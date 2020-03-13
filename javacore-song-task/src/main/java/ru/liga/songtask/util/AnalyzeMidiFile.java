package ru.liga.songtask.util;

import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Lyrics;
import com.leff.midi.event.meta.Text;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

enum EnumArgAnalyze {

    MAJOR_LYRICS(0, ""),
    MAJOR_TEXT(1, ""),
    MAJOR_INTERSECTION(2, ""),
    SHOW_MULTIPLY_ALL(3, ""),
    SHOW_MULTIPLY_TEXT(4, ""),
    SHOW_MULTIPLY_LYRICS(5, ""),
    SHOW_MULTIPLY_INTERSECTION(6, ""),
    SHOW_TEXT(7, ""),
    SHOW_TEXT_NOT_FOUND(8, ""),
    SHOW_LYRICS(9, ""),
    SHOW_LYRICS_IF_NOT_FOUND(10, ""),
    SHOW_INTERSECTION(11, ""),
    SHOW_INTERSECTION_IF_NOT_FOUND(12, ""),
    SAVE_RESULT_TO_FILE(13, ""),
    PRINT_INTO_LOGGER_RESULT(14, "");

    private final int id;
    private final String name;

    EnumArgAnalyze(final int id, final String name) {
        this.id = id;
        this.name = "";
    }


    public int getId() {
        return id;
    }


    public String getName() {
        return EnumArgAnalyze.values()[id].toString();
    }
}

public class AnalyzeMidiFile extends MidiOperation {

    private static Predicate<EnumArgAnalyze> predicateAnalyzeFileOnTextNote = enumArgAnalyze -> enumArgAnalyze == EnumArgAnalyze.MAJOR_TEXT
            || enumArgAnalyze == EnumArgAnalyze.SHOW_TEXT
            || enumArgAnalyze == EnumArgAnalyze.SHOW_MULTIPLY_ALL
            || enumArgAnalyze == EnumArgAnalyze.SHOW_MULTIPLY_TEXT
            || enumArgAnalyze == EnumArgAnalyze.SHOW_TEXT_NOT_FOUND;

    private static Predicate<EnumArgAnalyze> predicateAnalyzeFileOnLyricsNote = enumArgAnalyze -> enumArgAnalyze == EnumArgAnalyze.MAJOR_LYRICS
            || enumArgAnalyze == EnumArgAnalyze.SHOW_LYRICS
            || enumArgAnalyze == EnumArgAnalyze.SHOW_MULTIPLY_ALL
            || enumArgAnalyze == EnumArgAnalyze.SHOW_MULTIPLY_LYRICS
            || enumArgAnalyze == EnumArgAnalyze.SHOW_LYRICS_IF_NOT_FOUND;

    private static Predicate<EnumArgAnalyze> predicateAnalyzeFileIntersection = enumArgAnalyze -> enumArgAnalyze == EnumArgAnalyze.MAJOR_INTERSECTION
            || enumArgAnalyze == EnumArgAnalyze.SHOW_INTERSECTION
            || enumArgAnalyze == EnumArgAnalyze.SHOW_MULTIPLY_ALL
            || enumArgAnalyze == EnumArgAnalyze.SHOW_MULTIPLY_INTERSECTION
            || enumArgAnalyze == EnumArgAnalyze.SHOW_INTERSECTION_IF_NOT_FOUND;

    private static Predicate<MidiEvent> predicateFindTextNode = midiEvent -> midiEvent.getTick() != 0 && midiEvent.getDelta() != 0 && midiEvent instanceof Text;
    private static Predicate<MidiEvent> predicateWithLyrics = midiEvent -> midiEvent.getTick() != 0 && midiEvent.getDelta() != 0 && midiEvent instanceof Lyrics;
    private static Predicate<EnumArgAnalyze> checkMajorAlgorithm = arg -> arg.ordinal() > 0 && arg.ordinal() < 3;
    private static Predicate<EnumArgAnalyze> checkShowMethod = arg -> arg.ordinal() > 2 && arg.ordinal() <= 12;


    private static List<EnumArgAnalyze> argumentsList;
    private HashMap<MidiTrack, List<Note>> hashMapMidiTracksListNotes = new HashMap<>();

    private AnalyzeMidiFile(List<EnumArgAnalyze> argumentList, String pathToMidFile) {
        super(pathToMidFile);
        setArgumentList(argumentList.stream()
                .map(EnumArgAnalyze::getName)
                .collect(Collectors.toList()));
        transformMidiTracksToNotes();
    }

    public static void main(String[] args) {
//        String[] arg1 = {"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Wrecking Ball.mid\"", "-analyze", "SHOW_LYRICS", "SHOW_TEXT", "SHOW_INTERSECTION"};
//        AnalyzeMidiFile analyzeMidiFile = AnalyzeMidiFile.getInstance(arg1, "D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Wrecking Ball.mid");

//        String[] arg1 = {"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Underneath Your Clothes.mid\"", "-analyze", "SHOW_LYRICS", "SHOW_TEXT", "SHOW_INTERSECTION"};
//        AnalyzeMidiFile analyzeMidiFile = AnalyzeMidiFile.getInstance(arg1, "D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Underneath Your Clothes.mid");

        String[] arg1 = {"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid\"", "-analyze", "SHOW_LYRICS", "SHOW_TEXT", "SHOW_INTERSECTION"};
        AnalyzeMidiFile analyzeMidiFile = AnalyzeMidiFile.getInstance(arg1, "D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid");
        analyzeMidiFile.makeOperation();
    }

    public static AnalyzeMidiFile getInstance(String[] args, String pathToMidiFile) {
        String[] newArgs = Arrays.copyOfRange(args, 2, args.length);
        List<EnumArgAnalyze> tempEnumList = parseArguments(newArgs);
        return new AnalyzeMidiFile(tempEnumList, pathToMidiFile);
    }

    private static List<EnumArgAnalyze> parseArguments(String[] args) {
        argumentsList = Arrays.stream(args).
                filter(AnalyzeMidiFile::checkArgumentFromEnum).
                map(EnumArgAnalyze::valueOf).
                collect(Collectors.toList());

        if (!argumentsList.stream().anyMatch(checkMajorAlgorithm)) {
            argumentsList.add(EnumArgAnalyze.MAJOR_TEXT);
        }
        if (!argumentsList.stream().anyMatch(checkShowMethod)) {
            argumentsList.add(EnumArgAnalyze.SHOW_TEXT);
        }

        return argumentsList;
    }

    private static boolean checkArgumentFromEnum(String argument) {
        try {
            EnumArgAnalyze.valueOf(argument.toUpperCase());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean intersectionsNotes(Note note1, Note note2) {
//        logger.debug("Enter to {}", "intersectionsNotes");

        if (note1.startTick().compareTo(note2.startTick()) > 0) {
            Note temp = note2;
            note2 = note1;
            note1 = temp;
        }

        return note1.endTickInclusive() > note2.startTick();
    }

    // Generic function to concatenate multiple lists in Java
    public static <T> List<T> concatenate(List<T>... lists) {
        List<T> result = new ArrayList<>();
        Stream.of(lists).forEach(result::addAll);

        return result;
    }

    public List<String> getArgumentList() {
        return super.getArgumentList();
    }

    private void transformMidiTracksToNotes() {
        for (MidiTrack midiTrack : getMidiFile().getTracks()) {
            hashMapMidiTracksListNotes.put(midiTrack, SongUtils.eventsToNotes(midiTrack.getEvents()));
        }
    }

    private void printAnalyzeMidTrack(List<Note> noteList) {

        Map<Integer, Long> frequencyMap = noteList.stream()
                .collect(Collectors.groupingBy(note -> note.sign().getMidi(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));

        Map<Integer, Long> timingMap = noteList.stream()
                .collect(Collectors.groupingBy(note -> SongUtils.tickToMs(getTempo().getBpm(), getMidiFile().getResolution(), note.durationTicks()), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, newVal) -> oldVal, LinkedHashMap::new));

        int upperNote = frequencyMap.entrySet().iterator().next().getKey();
        int lowerNote = frequencyMap.entrySet().stream().reduce((first, second) -> second).orElse(null).getKey();


        System.out.println("Analyze File");
        System.out.println("");

        System.out.println("Range notes");
        System.out.println("    " + "Upper: " + NoteSign.fromMidiNumber(upperNote).fullName());
        System.out.println("    " + "Lower: " + NoteSign.fromMidiNumber(lowerNote).fullName());
        System.out.println("    " + "Range: " + (lowerNote - upperNote));

        System.out.println("Number of notes by duration");
        timingMap.forEach((key, value) -> System.out.println("    " + key + "ms :" + value));

        System.out.println("List of notes with the number of occurrences");
        frequencyMap.forEach((key, value) -> System.out.println("    " + NoteSign.fromMidiNumber(key).fullName() + ":" + value));

    }

    @Override
    public void makeOperation() {
        makeOperation2();
    }

    private void makeOperation2() {
        HashMap<MidiTrack, Integer> counterMidiTrack = new HashMap<>();
        List<MidiTrack> listTracksWithTextNote;
        List<MidiTrack> listTracksWithLyricsNote;
        List<MidiTrack> listTracksWithoutIntersection = new ArrayList<>();
        HashMap<MidiTrack, List<MidiTrack>> hashMapMidiTrackTextNote = new HashMap<>();
        HashMap<MidiTrack, List<MidiTrack>> hashMapMidiTrackLyricsNote = new HashMap<>();

        for (Map.Entry<MidiTrack, List<Note>> listEntry : hashMapMidiTracksListNotes.entrySet()) {
            counterMidiTrack.put(listEntry.getKey(), 0);

            if (argumentsList.stream().anyMatch(predicateAnalyzeFileOnTextNote)) {
                listTracksWithTextNote = findTracksWithConditionFromMidFile(predicateFindTextNode);
                hashMapMidiTrackTextNote = getTrackLikeVoiceText(listTracksWithTextNote);
            }
            if (argumentsList.stream().anyMatch(predicateAnalyzeFileOnLyricsNote)) {
                listTracksWithLyricsNote = findTracksWithConditionFromMidFile(predicateWithLyrics);
                hashMapMidiTrackLyricsNote = getTrackLikeVoiceLyrics(listTracksWithLyricsNote);
            }
            if (argumentsList.stream().anyMatch(predicateAnalyzeFileIntersection)) {
                listTracksWithoutIntersection = findTrackWithoutIntersectionFromMidFile();
            }
        }

        int coffText = 26;
        int coffLyrics = 26;
        int coffIntersection = 26;

        if (argumentsList.contains(EnumArgAnalyze.MAJOR_TEXT)) {
            coffText = 48;
        } else if (argumentsList.contains(EnumArgAnalyze.MAJOR_LYRICS)) {
            coffLyrics = 48;
        } else if (argumentsList.contains(EnumArgAnalyze.MAJOR_INTERSECTION)) {
            coffIntersection = 48;
        }

        HashMap<MidiTrack, HashMap<MidiTrack, Integer>> tempMap1 = new HashMap<>();

        for (Map.Entry<MidiTrack, Integer> entry : counterMidiTrack.entrySet()) {

            int coff = 0;
            List<MidiTrack> tempListTrack = new ArrayList<>();

            if (hashMapMidiTrackLyricsNote.get(entry.getKey()) != null) {
                tempListTrack.addAll(hashMapMidiTrackLyricsNote.get(entry.getKey()));
            }

            if (hashMapMidiTrackTextNote.get(entry.getKey()) != null) {
                tempListTrack.addAll(hashMapMidiTrackTextNote.get(entry.getKey()));
            }

            tempListTrack.addAll(listTracksWithoutIntersection);
            Set<MidiTrack> setMidiTracks = new HashSet<>(tempListTrack);
            HashMap<MidiTrack, Integer> tempMap = new HashMap<>();

            for (MidiTrack midiTrack : setMidiTracks) {
                coff = 0;

                if (hashMapMidiTrackTextNote.get(entry.getKey()) != null && hashMapMidiTrackTextNote.get(entry.getKey()).contains(midiTrack)) {
                    coff += coffText;
                }
                if (hashMapMidiTrackLyricsNote.get(entry.getKey()) != null && hashMapMidiTrackLyricsNote.get(entry.getKey()).contains(midiTrack)) {
                    coff += coffLyrics;
                }
                if (listTracksWithoutIntersection.contains(midiTrack)) {
                    coff += coffIntersection;
                }

                if (tempMap.get(midiTrack) != null) {
                    if (tempMap.get(midiTrack) < coff)
                        tempMap.put(midiTrack, coff);
                } else {
                    tempMap.put(midiTrack, coff);
                }
            }
            tempMap1.put(entry.getKey(), tempMap);
        }

        final MidiTrack[] midiTrackMaxChance = {null};
        final int[] maxChance = {Integer.MIN_VALUE};

        for (Map.Entry<MidiTrack, HashMap<MidiTrack, Integer>> entry : tempMap1.entrySet()) {
            System.out.println("Track " + entry.getKey());
            entry.getValue().forEach((k, v) -> {
                if (maxChance[0] < v){
                    midiTrackMaxChance[0] = k;
                    maxChance[0] =v;
                }
                System.out.println("   MidiTrack: " + k + ";  Chance: " + v);
            });
        }

        hashMapMidiTrackTextNote.entrySet().forEach(entry ->{
            System.out.println("Track "+entry.getKey());
            entry.getValue().stream().forEach(ent1-> System.out.println("   "+ent1));
        });
        System.out.println("Max track chance"+midiTrackMaxChance[0]+"; chance: "+ maxChance[0]);
        printAnalyzeMidTrack(SongUtils.eventsToNotes(midiTrackMaxChance[0].getEvents()));
    }

    private void makeOperation1() {

        List<MidiTrack> listTracksWithTextNote;
        List<MidiTrack> listTracksWithLyricsNote;
        List<MidiTrack> listTracksWithoutIntersection = new ArrayList<>();
        HashMap<MidiTrack, List<MidiTrack>> hashMapMidiTrackTextNote = new HashMap<>();
        HashMap<MidiTrack, List<MidiTrack>> hashMapMidiTrackLyricsNote = new HashMap<>();

        if (argumentsList.stream().anyMatch(predicateAnalyzeFileOnTextNote)) {
            listTracksWithTextNote = findTracksWithConditionFromMidFile(predicateFindTextNode);
            hashMapMidiTrackTextNote = getTrackLikeVoiceText(listTracksWithTextNote);
        }
        if (argumentsList.stream().anyMatch(predicateAnalyzeFileOnLyricsNote)) {
            listTracksWithLyricsNote = findTracksWithConditionFromMidFile(predicateWithLyrics);
            hashMapMidiTrackLyricsNote = getTrackLikeVoiceLyrics(listTracksWithLyricsNote);
        }
        if (argumentsList.stream().anyMatch(predicateAnalyzeFileIntersection)) {
            listTracksWithoutIntersection = findTrackWithoutIntersectionFromMidFile();
        }
        int limit = argumentsList.contains(EnumArgAnalyze.SHOW_MULTIPLY_ALL) ? Integer.MAX_VALUE : 1;

        getTrackWithTextEvent(hashMapMidiTrackTextNote, limit);

        getTracksWithLyricsEvents(hashMapMidiTrackLyricsNote, limit);

        getTracksWithoutIntersection(listTracksWithoutIntersection, limit);
    }

    private void getTracksWithoutIntersection(List<MidiTrack> listTracksWithoutIntersection, int limit) {
        if (argumentsList.contains(EnumArgAnalyze.MAJOR_INTERSECTION) || argumentsList.contains(EnumArgAnalyze.SHOW_MULTIPLY_ALL) || argumentsList.contains(EnumArgAnalyze.SHOW_INTERSECTION)) {
            System.out.println("Intersection " + listTracksWithoutIntersection.size());
            System.out.println();
            int limitLocal = argumentsList.contains(EnumArgAnalyze.SHOW_MULTIPLY_LYRICS) ? Integer.MAX_VALUE : 1;
            limitLocal = Math.max(limit, limitLocal);

            listTracksWithoutIntersection.stream().limit(limitLocal).forEachOrdered(midiTrack -> {
                System.out.println("Track" + midiTrack.toString());
                printAnalyzeMidTrack(hashMapMidiTracksListNotes.get(midiTrack));
            });
        }
    }

    private void getTrackWithTextEvent(HashMap<MidiTrack, List<MidiTrack>> hashMapMidiTrackTextNote, int limit) {
        if (argumentsList.contains(EnumArgAnalyze.MAJOR_TEXT) || argumentsList.contains(EnumArgAnalyze.SHOW_MULTIPLY_ALL) || argumentsList.contains(EnumArgAnalyze.SHOW_TEXT)) {
            int limitLocal = argumentsList.contains(EnumArgAnalyze.SHOW_MULTIPLY_TEXT) ? Integer.MAX_VALUE : 1;
            limitLocal = Math.max(limit, limitLocal);
            System.out.println("Text " + hashMapMidiTrackTextNote.size());
            System.out.println();
            hashMapMidiTrackTextNote.entrySet().stream()
                    .limit(limitLocal)
                    .forEachOrdered(midiTrackListEntry -> {
                        System.out.println("Track" + midiTrackListEntry.getKey().toString());
                        midiTrackListEntry.getValue().stream()
                                .forEachOrdered(midiTrack -> printAnalyzeMidTrack(hashMapMidiTracksListNotes.get(midiTrack)));
                    });

        }
    }

    private void getTracksWithLyricsEvents(HashMap<MidiTrack, List<MidiTrack>> hashMapMidiTrackLyricsNote, int limit) {
        if (argumentsList.contains(EnumArgAnalyze.MAJOR_LYRICS) || argumentsList.contains(EnumArgAnalyze.SHOW_MULTIPLY_ALL) || argumentsList.contains(EnumArgAnalyze.SHOW_LYRICS)) {
            System.out.println("Lyrics " + hashMapMidiTrackLyricsNote.size());
            System.out.println();
            int limitLocal = argumentsList.contains(EnumArgAnalyze.SHOW_MULTIPLY_LYRICS) ? Integer.MAX_VALUE : 1;
            limitLocal = Math.max(limit, limitLocal);

            hashMapMidiTrackLyricsNote.entrySet().stream()
                    .limit(limitLocal)
                    .forEachOrdered(midiTrackListEntry -> {
                        System.out.println("Track" + midiTrackListEntry.getKey().toString());
                        midiTrackListEntry.getValue().stream()
                                .forEachOrdered(midiTrack -> printAnalyzeMidTrack(hashMapMidiTracksListNotes.get(midiTrack)));
                    });
        }
    }

    public List<MidiTrack> findTracksWithConditionFromMidFile(Predicate<MidiEvent> condition) {
        return getMidiFile().getTracks().stream().
                filter(midiTrack -> midiTrack.getEvents().stream()
                        .anyMatch(condition))
                .collect(Collectors.toList());
    }

    public HashMap<MidiTrack, List<MidiTrack>> getTrackLikeVoiceText(List<MidiTrack> listTracksWithTextNote) {

        HashMap<MidiTrack, List<MidiTrack>> hashMapMidiTrackLikeHuman = new HashMap<>();

        TreeSet<MidiEvent> listMidiEvent = listTracksWithTextNote.get(0).getEvents();
        for (MidiTrack midiTrack : listTracksWithTextNote) {
            for (Map.Entry<MidiTrack, List<Note>> midiTrackListNoteEntry : hashMapMidiTracksListNotes.entrySet()) {
                boolean isTrackLikeHuman = true;
                int index = 0;
                if (midiTrackListNoteEntry.getValue().size() > 0) {
                    for (MidiEvent eventText : listMidiEvent) {
                        if (eventText.getDelta() != 0 && eventText.getTick() != 0 && eventText instanceof Text) {
                            if (eventText.getTick() == midiTrackListNoteEntry.getValue().get(index).startTick()) {
                                index++;
                            } else {
                                isTrackLikeHuman = false;
                            }
                        }
                    }
                } else {
                    isTrackLikeHuman = false;
                }

                if (isTrackLikeHuman) {
                    putOnHashMap(hashMapMidiTrackLikeHuman, midiTrack, midiTrackListNoteEntry.getKey());
                }
            }
        }

        return hashMapMidiTrackLikeHuman;
    }

    public HashMap<MidiTrack, List<MidiTrack>> getTrackLikeVoiceLyrics(List<MidiTrack> listTracksWithLyricsNote) {
        HashMap<MidiTrack, List<MidiTrack>> hashMapMidiTrackLikeHuman = new HashMap<>();

        for (MidiTrack midiTrack : listTracksWithLyricsNote) {
            for (Map.Entry<MidiTrack, List<Note>> midiTrackListNoteEntry : hashMapMidiTracksListNotes.entrySet()) {
                if (midiTrackListNoteEntry.getKey().equals(midiTrack)) {
                    putOnHashMap(hashMapMidiTrackLikeHuman, midiTrack, midiTrackListNoteEntry.getKey());
                }
            }
        }
        return hashMapMidiTrackLikeHuman;
    }

    private HashMap<MidiTrack, List<MidiTrack>> putOnHashMap(HashMap<MidiTrack, List<MidiTrack>> hashMap, MidiTrack midiTrackKey, MidiTrack midiTrackValue) {
        if (hashMap.get(midiTrackKey) == null) {
            List<MidiTrack> tempListTrack = new ArrayList<>();
            tempListTrack.add(midiTrackValue);
            hashMap.put(midiTrackKey, tempListTrack);
        } else {
            hashMap.get(midiTrackKey).add(midiTrackValue);
        }
        return hashMap;
    }

    public List<MidiTrack> findTrackWithoutIntersectionFromMidFile() {
        List<MidiTrack> midiTrackList = new ArrayList<>();

        for (Map.Entry<MidiTrack, List<Note>> midiTrackListNoteEntry : hashMapMidiTracksListNotes.entrySet()) {
            boolean notIntersection = true;
            for (int i = 0; i < midiTrackListNoteEntry.getValue().size() - 1; i++) {
//                if (midiTrackListNoteEntry.getValue().get(i).endTickInclusive() > midiTrackListNoteEntry.getValue().get(i + 1).startTick()) {
                if (intersectionsNotes(midiTrackListNoteEntry.getValue().get(i), midiTrackListNoteEntry.getValue().get(i + 1))) {
                    notIntersection = false;
                    break;
                }
            }
            if (notIntersection && midiTrackListNoteEntry.getValue().size() > 0)
                midiTrackList.add(midiTrackListNoteEntry.getKey());
        }

        return midiTrackList;
    }
}
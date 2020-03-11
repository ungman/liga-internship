package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.ChannelEvent;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Lyrics;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.Text;
import org.slf4j.Logger;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AnalyzeMidiTrack {

    private static final List<MidiTrack> listTrackLyrics = new ArrayList<>();
    private static final List<MidiTrack> listTrackText = new ArrayList<>();
    private static final List<MidiTrack> listTrackIntersection = new ArrayList<>();
    private static final HashMap<String, List<List<Note>>> mapTrackForAnalyze = new HashMap<>();
    public static Logger logger = null;
    private static Tempo tempo = null;
    private static boolean showLyrics = false;
    private static boolean showIntersection = false;
    private static boolean showMultiple = false;

    public static void analyze(String[] args, Logger logger1) {
        //setting
        logger = logger1;
        logger.debug("Enter to {}", "analyze");
        for (int i = 2; i < args.length; i++) {
            if (args[i].trim().equalsIgnoreCase("lyric")) {
                showLyrics = true;
            }
            if (args[i].trim().equalsIgnoreCase("intersection")) {
                showIntersection = true;
            }
            if (args[i].trim().equalsIgnoreCase("multiple")) {
                showMultiple = true;
            }
        }
        logger.info("Show lyrics {} , show intersection {}, show multiple {}", new Object[]{showLyrics, showIntersection, showMultiple});
        findVocalNoteTrack(args[0]);
        logger.debug("Exit from {} ","analyze");
    }

    private static void findVocalNoteTrack(String path1) {
        logger.debug("Enter to {}", "findVocalNoteTrack");
        try {
            logger.info("Try read mid file in path: {}", path1);
            MidiFile midiFile = new MidiFile((new FileInputStream(path1)));
            logger.info("Open file successfully");
            getTrackWithTextEvents(midiFile);

            if (listTrackText.size() > 0) {
                logger.debug("List track with text events:{}", listTrackText.size());
                int size = showMultiple ? listTrackText.size() : 1;

                for (int i = 0; i < size; i++) {
                    mapTrackForAnalyze.put("Text" + i, getTrackNotesVocal(midiFile, listTrackText.get(i)));
                }
            }
            //analyze listTrackText
            if (showLyrics) {
                logger.debug("List track with Lyrics events:{}", listTrackLyrics.size());
                List<List<Note>> listTrackNotesList = new ArrayList<>();
                for (MidiTrack midiTrack : listTrackLyrics) {
                    listTrackNotesList.add(SongUtils.eventsToNotes(midiTrack.getEvents()));
                }
                mapTrackForAnalyze.put("Lyric", listTrackNotesList);
            }

            if (showIntersection) {
                analyzeIsLeatherBastard(midiFile);
                logger.debug("List track with Intersections events:{}", listTrackIntersection.size());
                List<List<Note>> listTrackNotesList = new ArrayList<>();
                for (MidiTrack midiTrack : listTrackIntersection) {
                    listTrackNotesList.add(SongUtils.eventsToNotes(midiTrack.getEvents()));
                }
                mapTrackForAnalyze.put("Intersection", listTrackNotesList);
            }

            int intro = 0;

            for (Map.Entry<String, List<List<Note>>> entry : mapTrackForAnalyze.entrySet()) {
                intro++;
//                System.out.println(entry.getKey());
                logger.debug("Analyze ({})", entry);
                for (List<Note> notes : entry.getValue()) {
                    analyzeVoiceLeatherBastard(notes, tempo, midiFile.getResolution());
                }
            }

            if (intro == 0) {
                logger.debug("Find track 0");
                logger.info("Not found vocal track; You can argument with run etc( java -jar ... analyze intersection)");
                logger.info("Program analyze track, and found without intersection on time note");
            }

        } catch (IOException e) {
            logger.debug(e.getMessage());
            e.printStackTrace();
        }
        logger.debug("Exit from {} ","findVocalNoteTrack");
    }

    private static void getTrackWithTextEvents(MidiFile midiFile) {
        logger.debug("Enter to {}", "getTrackWithTextEvents");
        for (MidiTrack midiTrack : midiFile.getTracks()) {
            boolean isOnlyText = true;
            boolean isMetaInfo = true;

            for (MidiEvent event : midiTrack.getEvents()) {
                if (event instanceof Lyrics && showLyrics) {
                    listTrackLyrics.add(midiTrack);
                    logger.debug("Founded track with lyric and added to list");
                } else if (event instanceof Text) {
                    if (event.getTick() != 0 && event.getDelta() != 0) {
                        isMetaInfo = false;
                    }
                } else if (event instanceof ChannelEvent) {
                    isOnlyText = false;
                }

                if (event instanceof Tempo) {
                    logger.debug("Founded tempo");
                    tempo = (Tempo) event;
                }
            }

            if (isOnlyText && !isMetaInfo) {
                logger.debug("found track with text and added to list");
                listTrackText.add(midiTrack);
            }
        }
        if (tempo == null) {
            logger.debug("Tempo is null!");
            logger.debug("Exit from {} ","getTrackWithTextEvents");
            throw new RuntimeException("Tempo is null!");
        }
        logger.debug("Exit from {} ","getTrackWithTextEvents");
    }

    private static List<List<Note>> getTrackNotesVocal(MidiFile midiFile, MidiTrack trackVocalEvents) {
        logger.debug("Enter to {}", "getTrackNotesVocal");
        List<List<Note>> listTrackOfListNode = new ArrayList<>();
        for (MidiTrack midiTrack : midiFile.getTracks()) {
            List<Note> notes = SongUtils.eventsToNotes(midiTrack.getEvents());
            int index = 0;
            boolean isHumanBastardVocal = true;
            if (notes.size() > 0) {
                for (MidiEvent eventText : trackVocalEvents.getEvents()) {
                    if (eventText.getDelta() != 0 && eventText.getTick() != 0 && eventText instanceof Text) {
                        if (eventText.getTick() == notes.get(index).startTick()) {
                            index++;
                        } else {
                            isHumanBastardVocal = false;
                        }
                    }
                }
            } else {
                isHumanBastardVocal = false;
            }

            if (isHumanBastardVocal) {
                logger.debug("Founded track note for text events");
                listTrackOfListNode.add(notes);
            }
        }
        logger.debug("Exit from {} ","getTrackNotesVocal");
        return listTrackOfListNode;
    }

    private static void analyzeVoiceLeatherBastard(List<Note> midiTrackLB, Tempo tempo, int resFile) {
        logger.debug("Enter to {}", "analyzeVoiceLeatherBastard");
        logger.info("Started analyze track notes");
        List<Note> vhb = midiTrackLB;
        HashMap<Integer, Integer> mapVH = new HashMap<>();
        HashMap<Float, Integer> mapDL = new HashMap<>();

        int maxNote = Integer.MIN_VALUE;
        int minNote = Integer.MAX_VALUE;
        int countNote = 0;

        for (Note note : vhb) {
            // counter notes
            countNote++;
            int nodeNumbers = note.sign().ordinal();
            if (mapVH.get(nodeNumbers) == null) {
                mapVH.put(nodeNumbers, 1);
            } else {
                Integer noteCounter = mapVH.get(nodeNumbers);
                noteCounter++;
                mapVH.put(nodeNumbers, noteCounter);
            }

            float interval = SongUtils.tickToMs(tempo.getBpm(), resFile, note.durationTicks());
            if (mapDL.get(interval) == null) {
                mapDL.put(interval, 1);
            } else {
                Integer temp = mapDL.get(interval);
                temp++;
                mapDL.put(interval, temp);
            }

            maxNote = Math.max(maxNote, note.sign().ordinal());
            minNote = Math.min(minNote, note.sign().ordinal());

        }

        logger.debug("Analyze file");
        logger.info("Count note: " + vhb.size());


        System.out.println();
        logger.info("Range");
        logger.info("    " + "Upper: " + NoteSign.values()[minNote].fullName());
        logger.info("    " + "Lower: " + NoteSign.values()[maxNote].fullName());
        logger.info("    " + "Range: " + (maxNote - minNote));


        System.out.println();
//        System.out.println("Number of notes by duration");
        logger.info("Number of notes by duration");
//        mapDL.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(x -> System.out.println("    " + x.getKey() + "мс: " + x.getValue()));
        mapDL.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(x -> logger.info("    " + x.getKey() + "ms: " + x.getValue()));


        System.out.println();
        System.out.println("List of notes with the number of occurrences");
        logger.info("List of notes with the number of occurrences");
//        mapVH.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(x -> System.out.println("    " + NoteSign.values()[x.getKey()].fullName() + ": " + x.getValue()));
        mapVH.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(x -> logger.info("    " + NoteSign.values()[x.getKey()].fullName() + ": " + x.getValue()));
        logger.debug("Exit from {} ","analyzeVoiceLeatherBastard");
    }

    private static void analyzeIsLeatherBastard(MidiFile midiFile) {
        logger.debug("Enter to {}", "analyzeIsLeatherBastard");
        logger.info("Analyze File for intersection");
        for (MidiTrack midiTrack : midiFile.getTracks()) {
            List<Note> listNote = SongUtils.eventsToNotes(midiTrack.getEvents());
            boolean isLeatherBastard = true;
            for (int i = 0; i < listNote.size(); i++) {
                Note currentNode = listNote.get(i);
                for (int j = 0; j < listNote.size(); j++) {
                    if (i != j && intersectionsNotes(currentNode, listNote.get(j))) {
                        isLeatherBastard = false;
                    }
                }
            }

            if (isLeatherBastard)
                listTrackIntersection.add(midiTrack);
        }
        logger.debug("Exit to {}", "analyzeIsLeatherBastard");
    }

    private static boolean intersectionsNotes(Note note1, Note note2) {
//        logger.debug("Enter to {}", "intersectionsNotes");
        if (note1.startTick().compareTo(note2.startTick()) == 0) {

               return  true;
        }

        if (note1.startTick().compareTo(note2.startTick()) > 0) {
            Note temp = note2;
            note2 = note1;
            note1 = temp;
        }

        return note1.endTickInclusive().compareTo(note2.startTick()) > 0;
    }
}

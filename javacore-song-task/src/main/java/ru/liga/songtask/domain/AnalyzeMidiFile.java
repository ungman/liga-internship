package ru.liga.songtask.domain;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Lyrics;
import com.leff.midi.event.meta.Text;
import ru.liga.songtask.util.AnalyzeMidFile;
import ru.liga.songtask.util.FileOperation;
import ru.liga.songtask.util.SongUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class AnalyzeMidiFile extends FileOperation {

    private static Predicate<MidiEvent> predicateFindTextNode = midiEvent -> midiEvent.getTick() != 0 && midiEvent.getDelta() != 0 && midiEvent instanceof Text;
    private static Predicate<MidiEvent> predicateWithLyrics = midiEvent -> midiEvent.getTick() != 0 && midiEvent.getDelta() != 0 && midiEvent instanceof Lyrics;

    private HashMap<MidiTrack, List<Note>> hashMapMidiTracksListNotes =new LinkedHashMap<>();
    private HashMap<MidiTrack, Integer> hashMapMidiTrackIndex = new LinkedHashMap<>();
    private List<AnalyzeMidFile> analyzeMidFiles = new ArrayList<>();

    private AnalyzeMidiFile(String[] args, String pathToMidFile) {

        super(pathToMidFile);
        Controller.logger.debug("Enter to constructor {}", "AnalyzeMidiFile(List<EnumArgAnalyze> argumentList, String pathToMidFile,Logger logger)");

        transformMidiTracksToNotes();
    }


    public static AnalyzeMidiFile getInstance(String[] args, String pathToMidiFile) {
        Controller.logger.debug("Enter to  method {}", "getInstance()");
        return new AnalyzeMidiFile(args, pathToMidiFile);

    }

    private void transformMidiTracksToNotes() {
        Controller.logger.debug("Enter to {}", "transformMidiTracksToNotes");
        int i = 0;
        for (MidiTrack midiTrack : getMidiFile().getTracks()) {
            hashMapMidiTracksListNotes.put(midiTrack, SongUtils.eventsToNotes(midiTrack.getEvents()));
            hashMapMidiTrackIndex.put(midiTrack, i);
            i++;
        }
    }

    private void printAnalyzeMidTrack(List<Note> noteList) {

        Controller.logger.debug("Enter to {}", "printAnalyzeMidTrack");

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

        int lowerNote = frequencyMap.entrySet().iterator().next().getKey();
        int upperNote = Objects.requireNonNull(frequencyMap.entrySet().stream().reduce((first, second) -> second).orElse(null)).getKey();


        Controller.logger.info("Analyze File");

        Controller.logger.info("Range notes");
        Controller.logger.info("    " + "Upper: " + NoteSign.fromMidiNumber(upperNote).fullName());
        Controller.logger.info("    " + "Lower: " + NoteSign.fromMidiNumber(lowerNote).fullName());
        Controller.logger.info("    " + "Range: " + Math.abs(lowerNote - upperNote));

        Controller.logger.info("Number of notes by duration");
        timingMap.forEach((key, value) -> Controller.logger.info("    {} ms : {}", key, value));

        Controller.logger.info("List of notes with the number of occurrences");
        frequencyMap.forEach((key, value) -> Controller.logger.info("    {} : {}", NoteSign.fromMidiNumber(key).fullName(), value));
    }
    public MidiTrack getMaxRateTrack(){
        analyzeMidFiles.add(new AnalyzeMidiFileWithTextEvent(48, predicateFindTextNode));
        analyzeMidFiles.add(new AnalyzeFileWithLyricsEvent(26, predicateWithLyrics));
        analyzeMidFiles.add(new AnalyzeMidiFileWithoutIntersection(28));

        int maxCoff = Integer.MIN_VALUE;
        MidiTrack maxRateTrack = null;
        for (Map.Entry<MidiTrack, List<Note>> entry : hashMapMidiTracksListNotes.entrySet()) {
            int coff = 0;
            for (AnalyzeMidFile analyzeMidFile : analyzeMidFiles) {
                if (analyzeMidFile.getTracks(hashMapMidiTracksListNotes).contains(entry.getKey())) {
                    coff += analyzeMidFile.getCoefficient();
                }
                if (coff > maxCoff) {
                    maxCoff = coff;
                    maxRateTrack = entry.getKey();
                }
            }
        }
        return maxRateTrack;
    }
    @Override
    public void makeOperation() {
        Controller.logger.debug("Enter to {}", "makeOperation");
        MidiTrack maxRateTrack=getMaxRateTrack();
        Controller.logger.info("Number midi track {}",hashMapMidiTrackIndex.get(maxRateTrack));
        printAnalyzeMidTrack(hashMapMidiTracksListNotes.get(maxRateTrack));
    }

}

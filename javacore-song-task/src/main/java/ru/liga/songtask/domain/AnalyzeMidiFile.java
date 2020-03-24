package ru.liga.songtask.domain;

import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Lyrics;
import com.leff.midi.event.meta.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.util.AnalyzeMidFile;
import ru.liga.songtask.util.MidiFileBaseOperationHelper;
import ru.liga.songtask.util.SongUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class AnalyzeMidiFile extends MidiFileBaseOperationHelper {

    public static final Logger logger = LoggerFactory.getLogger(AnalyzeMidiFile.class);
    private static Predicate<MidiEvent> predicateFindTextNode = midiEvent -> midiEvent.getTick() != 0 && midiEvent.getDelta() != 0 && midiEvent instanceof Text;
    private static Predicate<MidiEvent> predicateWithLyrics = midiEvent -> midiEvent.getTick() != 0 && midiEvent.getDelta() != 0 && midiEvent instanceof Lyrics;
    private HashMap<MidiTrack, List<Note>> hashMapMidiTracksListNotes = new LinkedHashMap<>();
    private HashMap<MidiTrack, Integer> hashMapMidiTrackIndex = new LinkedHashMap<>();
    private List<AnalyzeMidFile> analyzeMidFiles = new ArrayList<>();

    private AnalyzeMidiFile(String[] args, String pathToMidFile) {
        super(pathToMidFile);
        logger.debug("Enter to constructor {}", "AnalyzeMidiFile(List<EnumArgAnalyze> argumentList, String pathToMidFile,Logger logger)");
        transformMidiTracksToNotes();
    }


    public static AnalyzeMidiFile getInstance(String[] args, String pathToMidiFile) {
        logger.debug("Enter to  method {}", "getInstance()");
        return new AnalyzeMidiFile(args, pathToMidiFile);
    }

    private void transformMidiTracksToNotes() {
        logger.debug("Enter to {}", "transformMidiTracksToNotes");
        int i = 0;
        for (MidiTrack midiTrack : getMidiFile().getTracks()) {
            hashMapMidiTracksListNotes.put(midiTrack, SongUtils.eventsToNotes(midiTrack.getEvents()));
            hashMapMidiTrackIndex.put(midiTrack, i);
            i++;
        }
    }

    private void printAnalyzeMidTrack(List<Note> noteList) {
        logger.debug("Enter to {}", "printAnalyzeMidTrack");
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
        logger.info("Analyze File");
        logger.info("Range notes");
        logger.info("    " + "Upper: " + NoteSign.fromMidiNumber(upperNote).fullName());
        logger.info("    " + "Lower: " + NoteSign.fromMidiNumber(lowerNote).fullName());
        logger.info("    " + "Range: " + Math.abs(lowerNote - upperNote));
        logger.info("Number of notes by duration");
        timingMap.forEach((key, value) -> logger.info("    {} ms : {}", key, value));
        logger.info("List of notes with the number of occurrences");
        frequencyMap.forEach((key, value) -> logger.info("    {} : {}", NoteSign.fromMidiNumber(key).fullName(), value));
    }

    public MidiTrack getMaxRateTrack() {
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
        logger.debug("Enter to {}", "makeOperation");
        MidiTrack maxRateTrack = getMaxRateTrack();
        logger.info("Number midi track {}", hashMapMidiTrackIndex.get(maxRateTrack));
        printAnalyzeMidTrack(hashMapMidiTracksListNotes.get(maxRateTrack));
    }

}

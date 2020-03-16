package ru.liga.songtask.domain;

import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import ru.liga.songtask.util.AnalyzeMidFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AnalyzeFileWithLyricsEvent implements AnalyzeMidFile {

    public int coff;
    public List<MidiTrack> listTrack;
    private Predicate<MidiEvent> predicateFindLyricsEvent;

    public AnalyzeFileWithLyricsEvent(int coff, Predicate<MidiEvent> predicateFindLyricsEvent) {
        Controller.logger.debug("Enter to {} ", "AnalyzeFileWithLyricsEvent(int, Predicate<MidiEvent>)");
        this.coff = coff;
        this.predicateFindLyricsEvent = predicateFindLyricsEvent;
    }

    @Override
    public List<MidiTrack> getTracks(Map<MidiTrack, List<Note>> mapMidiTracksListNote) {
        Controller.logger.debug("Enter to {} ", "getTracks");
        if (listTrack != null)
            return listTrack;

        Controller.logger.debug("Enter to {}", "getTrackLikeVoiceLyrics");
        List<MidiTrack> ListTrackLikeHumanFromLyricsEvent = new ArrayList<>();
        List<MidiTrack> listTrackWithLyricsNote = new ArrayList<>();

        listTrackWithLyricsNote = findTracksWithConditionFromMidFile(mapMidiTracksListNote, predicateFindLyricsEvent);

        List<MidiTrack> finalAddCondition = listTrackWithLyricsNote;
        ListTrackLikeHumanFromLyricsEvent = mapMidiTracksListNote.keySet().stream()
                .filter(entry -> finalAddCondition.stream().anyMatch(midiTrack -> midiTrack.equals(entry)))
                .collect(Collectors.toList());

        listTrack = new ArrayList<>(new HashSet<>(ListTrackLikeHumanFromLyricsEvent));
        return listTrack;
    }

    @Override
    public int getCoefficient() {
        return coff;
    }

    private List<MidiTrack> findTracksWithConditionFromMidFile(Map<MidiTrack, List<Note>> mapMidiTracksListNot, Predicate<MidiEvent> condition) {
        Controller.logger.debug("Enter to {} ", "findTracksWithConditionFromMidFile");
        return mapMidiTracksListNot.keySet().stream().
                filter(midiTrack -> midiTrack.getEvents().stream()
                        .anyMatch(condition))
                .collect(Collectors.toList());
    }
}

package ru.liga.songtask.domain;

import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Text;
import ru.liga.songtask.util.AnalyzeMidFile;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class AnalyzeMidiFileWithTextEvent implements AnalyzeMidFile {

    public int coff;
    public List<MidiTrack> listTrack;
    private Predicate<MidiEvent> predicateFindTextEvent;

    public AnalyzeMidiFileWithTextEvent(int coff, Predicate<MidiEvent> predicateFindTextEvent) {
        Controller.logger.debug("Enter to {} ", " AnalyzeMidiFileWithTextEvent(int , Predicate<MidiEvent> )");
        this.coff = coff;
        this.predicateFindTextEvent = predicateFindTextEvent;
    }

    @Override
    public List<MidiTrack> getTracks(Map<MidiTrack, List<Note>> mapMidiTracksListNotes) {
        Controller.logger.debug("Enter to {} ", "getTracks");
        if (listTrack != null)
            return listTrack;
        List<MidiTrack> listTrackWithTextNote = new ArrayList<>();

        listTrackWithTextNote = findTracksWithConditionFromMidFile(mapMidiTracksListNotes, predicateFindTextEvent);

        List<MidiTrack> midiTrackListLikeHumanFromTextEvents = new ArrayList<>();

        for (MidiTrack midiTrack1 : listTrackWithTextNote) {

            TreeSet<MidiEvent> listMidiEvent = midiTrack1.getEvents();
            for (Map.Entry<MidiTrack, List<Note>> midiTrackListNoteEntry : mapMidiTracksListNotes.entrySet()) {
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
                    midiTrackListLikeHumanFromTextEvents.add(midiTrackListNoteEntry.getKey());
                }
            }
        }
        listTrack = new ArrayList<>(new HashSet<>(midiTrackListLikeHumanFromTextEvents));
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

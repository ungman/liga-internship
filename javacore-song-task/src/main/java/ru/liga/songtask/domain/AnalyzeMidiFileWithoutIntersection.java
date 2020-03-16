package ru.liga.songtask.domain;

import com.leff.midi.MidiTrack;
import ru.liga.songtask.util.AnalyzeMidFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AnalyzeMidiFileWithoutIntersection implements AnalyzeMidFile {
    public int coff;
    public List<MidiTrack> listTrack;

    public AnalyzeMidiFileWithoutIntersection(int coff) {
        Controller.logger.debug("Enter to {}", "AnalyzeMidiFileWithoutIntersection(int)");
        this.coff = coff;
    }

    @Override
    public List<MidiTrack> getTracks(Map<MidiTrack, List<Note>> mapMidiTracksListNote) {
        Controller.logger.debug("Enter to {}", "getTracks");
        if(listTrack!=null)
            return listTrack;

        List<MidiTrack> listTrackLikeHumanWithoutIntersection = new ArrayList<>();
        for (Map.Entry<MidiTrack, List<Note>> midiTrackListNoteEntry : mapMidiTracksListNote.entrySet()) {
            boolean notIntersection = true;
            for (int i = 0; i < midiTrackListNoteEntry.getValue().size() - 1; i++) {
                if (intersectionsNotes(midiTrackListNoteEntry.getValue().get(i), midiTrackListNoteEntry.getValue().get(i + 1))) {
                    notIntersection = false;
                    break;
                }
            }

            if (notIntersection && midiTrackListNoteEntry.getValue().size() > 0)
                listTrackLikeHumanWithoutIntersection.add(midiTrackListNoteEntry.getKey());
        }
        listTrack=listTrackLikeHumanWithoutIntersection;
        return listTrack;

    }

    private boolean intersectionsNotes(Note note1, Note note2) {

        if (note1.startTick().compareTo(note2.startTick()) > 0) {
            Note temp = note2;
            note2 = note1;
            note1 = temp;
        }
        return note1.endTickInclusive() > note2.startTick();
    }

    @Override
    public int getCoefficient() {
        return coff;
    }

}

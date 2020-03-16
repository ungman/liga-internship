package ru.liga.songtask.util;

import com.leff.midi.MidiTrack;
import ru.liga.songtask.domain.Note;

import java.util.List;
import java.util.Map;

public interface AnalyzeMidFile {

    int coff = 0;
    List<MidiTrack> listTrack = null;

    List<MidiTrack> getTracks(Map<MidiTrack, List<Note>> mapMidiTracksListNotes);

    int getCoefficient();
}

package test;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;

import org.junit.Before;
import org.junit.Test;
import ru.liga.songtask.domain.AnalyzeMidiFileWithoutIntersection;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.util.SongUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;


public class AnalyzeMidiFileWithoutIntersectionTest {
    public AnalyzeMidiFileWithoutIntersection rightInit;
    private HashMap<MidiTrack, List<Note>> hashMapMidiTracksListNotes =new LinkedHashMap<>();
    private MidiFile midiFile;

    @Before
    public void init() throws IOException {
        rightInit=new AnalyzeMidiFileWithoutIntersection(28);
        String path="D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid";
        midiFile=new MidiFile(new FileInputStream(path));

        for (MidiTrack midiTrack : midiFile.getTracks()) {
            hashMapMidiTracksListNotes.put(midiTrack, SongUtils.eventsToNotes(midiTrack.getEvents()));
        }
    }

    @Test
    public void testMotEmptyList(){
        assertThat(rightInit.getTracks(hashMapMidiTracksListNotes).size()).isGreaterThan(0);
    }

}
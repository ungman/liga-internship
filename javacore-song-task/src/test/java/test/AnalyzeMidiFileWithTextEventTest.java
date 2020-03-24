package test;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.meta.Text;
import org.junit.Before;
import org.junit.Test;
import ru.liga.songtask.domain.AnalyzeMidiFileWithTextEvent;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.util.SongUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

public class AnalyzeMidiFileWithTextEventTest {
    public AnalyzeMidiFileWithTextEvent rightInit;
    private static Predicate<MidiEvent> predicateFindTextNode = midiEvent -> midiEvent.getTick() != 0 && midiEvent.getDelta() != 0 && midiEvent instanceof Text;
    private HashMap<MidiTrack, List<Note>> hashMapMidiTracksListNotes = new LinkedHashMap<>();
    private MidiFile midiFile;

    @Before
    public void init() throws IOException {
        rightInit = new AnalyzeMidiFileWithTextEvent(48, predicateFindTextNode);
        String path = "D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid";
        midiFile = new MidiFile(new FileInputStream(path));

        for (MidiTrack midiTrack : midiFile.getTracks()) {
            hashMapMidiTracksListNotes.put(midiTrack, SongUtils.eventsToNotes(midiTrack.getEvents()));
        }
    }

    @Test
    public void notEmptyListTrack() {
        rightInit.getTracks(hashMapMidiTracksListNotes);
        assertThat(rightInit.getTracks(hashMapMidiTracksListNotes).size()).isGreaterThan(0);
    }
}
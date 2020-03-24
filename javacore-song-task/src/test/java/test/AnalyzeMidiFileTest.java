package test;

import com.leff.midi.MidiTrack;
import org.junit.Test;
import ru.liga.songtask.domain.AnalyzeMidiFile;
import ru.liga.songtask.util.MidiFileBaseOperationHelper;

import static org.assertj.core.api.Assertions.assertThat;


public class AnalyzeMidiFileTest {
    @Test
    public void testGetInstance() {
        String[] args = new String[]{"D:\\\\trash\\\\liga-internship\\\\javacore-song-task\\\\src\\\\main\\\\resources\\\\Belle.mid", "analyze",};
        MidiFileBaseOperationHelper midiFileBaseOperationHelper = AnalyzeMidiFile.getInstance(args, args[0]);
        assertThat(midiFileBaseOperationHelper).isInstanceOf(AnalyzeMidiFile.class);
    }

    @Test
    public void testMakeOperation() {
        String[] args = new String[]{"D:\\\\trash\\\\liga-internship\\\\javacore-song-task\\\\src\\\\main\\\\resources\\\\Belle.mid", "analyze",};
        AnalyzeMidiFile amf = AnalyzeMidiFile.getInstance(args, args[0]);

        assertThat(amf.getMaxRateTrack()).isEqualTo(amf.getMaxRateTrack());

        MidiTrack answer = amf.getMidiFile().getTracks().get(9);
        assertThat(amf.getMaxRateTrack()).isEqualTo(answer);

        assertThat(amf.getMaxRateTrack()).isNotEqualTo(amf.getMidiFile().getTracks().get(0));

    }
}
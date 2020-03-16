package test;


import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import org.junit.Test;
import ru.liga.songtask.domain.ChangeMidiFile;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.util.FileOperation;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeMidiFileTest {


    @Test
    public void testGetInstance(){
        FileOperation fileOperation= ChangeMidiFile.getInstance(new String[]{"","","-trans","0","-tempo","0"},"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid");
        assertThat(fileOperation).isInstanceOf(ChangeMidiFile.class);
    }

    @Test(expected = RuntimeException.class)
    public void testWrongPathToFile(){
        String[] args=new String[]{"D:\\\\trash\\\\liga-internship\\\\javacore-song-task\\\\src\\\\main\\\\resources\\\\Bell.mid","change","-trans","0","-tempo","0"};
        ChangeMidiFile.getInstance(args,args[0]).parseArgument(args);
    }

    @Test
    public void testParseArgs(){
        String[] args=new String[]{"D:\\\\trash\\\\liga-internship\\\\javacore-song-task\\\\src\\\\main\\\\resources\\\\Belle.mid","change","-trans","0","-tempo","0"};
        assertThat(ChangeMidiFile.getInstance(args,args[0]).parseArgument(args)).isTrue();
    }

    @Test (expected = RuntimeException.class)
    public void testWrongParseArgs(){
        String[] args=new String[]{"D:\\\\trash\\\\liga-internship\\\\javacore-song-task\\\\src\\\\main\\\\resources\\\\Belle.mid","change","-trans","test","-tempo","0"};
        ChangeMidiFile.getInstance(args,args[0]).parseArgument(args);
    }

    @Test
    public void testChangeMidiFile() {
        String[] args=new String[]{"D:\\\\trash\\\\liga-internship\\\\javacore-song-task\\\\src\\\\main\\\\resources\\\\Belle.mid","change","-trans","1","-tempo","0"};
        FileOperation fileOperation= ChangeMidiFile.getInstance(args,args[0]);
        MidiFile midiFile=null;
        try {
            midiFile=new MidiFile(new FileInputStream(args[0]));

        } catch (IOException e) {
            e.printStackTrace();
        }

        fileOperation.makeOperation();
        List<MidiEvent> collectChanged = fileOperation.getMidiFile().getTracks().stream()
                        .flatMap(midiTrack -> midiTrack.getEvents().stream()
                        .filter(midiEvent -> midiEvent instanceof NoteOn)).collect(Collectors.toList());

        List<MidiEvent> collectOrig = midiFile.getTracks().stream()
                .flatMap(midiTrack -> midiTrack.getEvents().stream()
                        .filter(midiEvent -> midiEvent instanceof NoteOn)).collect(Collectors.toList());

        if(collectOrig.get(0)==collectChanged.get(0)){
            assertThat(((NoteOn)collectOrig.get(0)).getNoteValue()+1).isEqualTo(((NoteOn)collectChanged.get(0)).getNoteValue());
        }
    }

}
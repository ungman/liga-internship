package test;


import org.junit.Test;
import ru.liga.songtask.domain.CheckerArgumentChangeMidiFile;
import ru.liga.songtask.util.CheckerArguments;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckerArgumentChangeMidiFileTest {

    @Test
    public void testGetInstance(){
        CheckerArguments checkerArguments= CheckerArgumentChangeMidiFile.getInstance();
        assertThat(checkerArguments).isInstanceOf(CheckerArgumentChangeMidiFile.class);
    }

    @Test
    public void testCheckArgument(){
        String[] arrArg=new String[]{"path to mid file","type","-trans","0","-tempo","0"};
        assertThat(CheckerArgumentChangeMidiFile.getInstance().checkArguments(arrArg)).isTrue();

        String[] wrongArrArg=new String[]{"path to mid file","type","-trans","0"};
        assertThat(CheckerArgumentChangeMidiFile.getInstance().checkArguments(wrongArrArg)).isFalse();
        String[] wrongArrArg1=new String[]{"path to mid file","type","-trans","0","-tempo"};
        assertThat(CheckerArgumentChangeMidiFile.getInstance().checkArguments(wrongArrArg)).isFalse();
        wrongArrArg1=new String[]{"path to mid file","type","-trans","-tempo","0"};
        assertThat(CheckerArgumentChangeMidiFile.getInstance().checkArguments(wrongArrArg1)).isFalse();
    }
}
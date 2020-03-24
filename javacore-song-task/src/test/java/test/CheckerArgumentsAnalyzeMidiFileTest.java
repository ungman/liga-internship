package test;


import org.junit.Test;
import ru.liga.songtask.domain.CheckerArgumentsAnalyzeMidiFile;
import ru.liga.songtask.util.CheckerArguments;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckerArgumentsAnalyzeMidiFileTest {

    @Test
    public void testGetInstance() {
        CheckerArguments checkerArguments = CheckerArgumentsAnalyzeMidiFile.getInstance();
        assertThat(checkerArguments).isInstanceOf(CheckerArgumentsAnalyzeMidiFile.class);
    }

    @Test
    public void testArguments() {
        String[] args = {"This", "check", "dont", "needs", "analyze", "dont", "have", "argument"};
        CheckerArguments checkerArguments = CheckerArgumentsAnalyzeMidiFile.getInstance();
        assertThat(checkerArguments.checkArguments(args)).isTrue();

        args = new String[]{};
        assertThat(checkerArguments.checkArguments(args)).isFalse();
    }
}
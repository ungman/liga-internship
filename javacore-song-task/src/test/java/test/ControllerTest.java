package test;

import org.junit.Test;
import ru.liga.songtask.util.AnalyzeMidiFile;
import ru.liga.songtask.util.Controller;

import static org.assertj.core.api.Assertions.assertThat;


public class ControllerTest {
    @Test
    public void whenPathToMidFileWrong() {
        assertThat(Controller.checkCorrectPath("test.mid")).isFalse();
        assertThat(Controller.checkCorrectPath("test.md")).isFalse();
        assertThat(Controller.checkCorrectPath("D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\logback.xml")).isFalse();
    }

    @Test
    public void testSetName() {
        Controller.setPathToMidFile("test.mid");
        assertThat(Controller.getPathToMidFile()).isEqualToIgnoringCase("test.mid");
        Controller.setPathToMidFile("test1.mid");
        assertThat(Controller.getPathToMidFile()).isNotEqualToIgnoringCase("test.mid");
    }

    @Test
    public void whenPathToMidFileCorrect() {
        assertThat(Controller.checkCorrectPath("D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid")).isTrue();
    }

    @Test
    public void whenGetNameMidFileCorrect() {
        Controller.setPathToMidFile("D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid");
        assertThat(Controller.geNameMidFile()).isEqualToIgnoringCase("Belle.mid");
    }

    @Test
    public void whenGetNameMidiFileWrong() {
        Controller.setPathToMidFile("D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\logback.xml");
        assertThat(Controller.geNameMidFile()).isNotEqualToIgnoringCase("logback.xml");
    }

    @Test
    public void testDeletingQuotes() {
        String[] argsWithQuotes = {"\"testDeletingQuotes\"", "testDeletingQuotes"};
        String[] argsWithDoubleQuotes = {"\"\"testDeletingQuotes\"\"", "testDeletingQuotes"};
        String[] argsWithoutQuotes = {"testDeletingQuotes", "testDeletingQuotes"};
        assertThat(Controller.deleteAllQuotes(argsWithQuotes[0])).isEqualToIgnoringCase(argsWithQuotes[1]);
        assertThat(Controller.deleteAllQuotes(argsWithDoubleQuotes[0])).isEqualToIgnoringCase(argsWithDoubleQuotes[1]);
        assertThat(Controller.deleteAllQuotes(argsWithoutQuotes[0])).isEqualToIgnoringCase(argsWithoutQuotes[1]);
    }

    @Test
    public void testArgumentsAnalyze() {
        String[] args = {"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid\"", "-analyze"};
        Controller.checkArgument(args);
        assertThat(Controller.classBaseOperation).isNotNull();
        args = new String[]{"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid\"", "-analyze", "MAJOR_LYRICS","SHOW_TEXT"};
        Controller.checkArgument(args);
        assertThat(Controller.classBaseOperation).isNotNull();
        args = new String[]{"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid\"", "-analyze", "MAJOR_LYRIC","SHOW_TEXT"};
        Controller.checkArgument(args);
        assertThat(Controller.classBaseOperation).isNotNull();
        assertThat(Controller.classBaseOperation instanceof AnalyzeMidiFile).isTrue();


    }

}
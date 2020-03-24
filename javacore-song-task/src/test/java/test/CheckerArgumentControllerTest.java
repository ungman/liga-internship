package test;

import org.junit.Test;
import ru.liga.songtask.domain.CheckerArgumentController;
import ru.liga.songtask.util.CheckerArguments;

import static org.assertj.core.api.Assertions.assertThat;

public class CheckerArgumentControllerTest {
    @Test
    public void testGetInstance() {
        CheckerArguments checkerArguments = CheckerArgumentController.getInstance();
        assertThat(checkerArguments).isInstanceOf(CheckerArgumentController.class);
    }

    @Test
    public void testCheckArguments() {
        CheckerArgumentController checkerArguments = CheckerArgumentController.getInstance();
        String[] rightArrArg = new String[]{"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid", "analyze"};
        assertThat(checkerArguments.checkTypeAction(rightArrArg) && checkerArguments.checkCorrectPath(rightArrArg[0])).isTrue();


        rightArrArg[0] = "Wrong path";
        assertThat(checkerArguments.checkCorrectPath(rightArrArg[0])).isFalse();
    }

    @Test
    public void testCheckType() {
        CheckerArgumentController checkerArguments = CheckerArgumentController.getInstance();
        String[] rightArrArg = new String[]{"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid\"", "analyze"};
        assertThat(checkerArguments.checkTypeAction(rightArrArg)).isTrue();
        rightArrArg[1] = "change";
        assertThat(checkerArguments.checkTypeAction(rightArrArg)).isTrue();

        rightArrArg[1] = "wrong type";
        assertThat(checkerArguments.checkTypeAction(rightArrArg)).isFalse();
    }

    @Test
    public void testCheckPath() {
        CheckerArgumentController checkerArguments = CheckerArgumentController.getInstance();
        String[] rightArrArg = new String[]{"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid", "analyze"};
        assertThat(checkerArguments.checkCorrectPath(rightArrArg[0])).isTrue();


        rightArrArg[0] = "D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.midi";
        assertThat(checkerArguments.checkCorrectPath(rightArrArg[0])).isFalse();
        rightArrArg[0] = "D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\Belle.mid";
        assertThat(checkerArguments.checkCorrectPath(rightArrArg[0])).isFalse();
    }

}
package test;

import org.junit.Test;
import ru.liga.songtask.domain.AnalyzeMidiFile;
import ru.liga.songtask.domain.ChangeMidiFile;
import ru.liga.songtask.domain.Controller;
import ru.liga.songtask.util.AnalyzeMidFile;

import static org.assertj.core.api.Assertions.assertThat;


public class ControllerTest {

    String[] rightArgsAnalyze =new String[]{"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid\"","analyze"};
    String[] getRightArgsChange=new String[]{"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Underneath Your Clothes.mid\"","change","-trans","1","-tempo","0"};

    @Test
    public  void testPath()
    {
        Controller controller=new Controller(rightArgsAnalyze);
        assertThat(controller.getNameMidFile()).isEqualToIgnoringCase("Belle.mid");
        assertThat(controller.getPathToMidFile()).isEqualToIgnoringCase("D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid");
        rightArgsAnalyze[0]="\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Underneath Your Clothes.mid\"";
        controller=new Controller(rightArgsAnalyze);
        assertThat(controller.getNameMidFile()).isNotEqualToIgnoringCase("Belle.mid");
        assertThat(controller.getNameMidFile()).isEqualToIgnoringCase("Underneath Your Clothes.mid");
    }

    @Test
    public  void testBaseOperationClassIsAnalyze()
    {
        Controller controller=new Controller(rightArgsAnalyze);
        assertThat(controller.classBaseOperation).isInstanceOf(AnalyzeMidiFile.class);
    }

    @Test
    public  void testBseOperationClassIsChange(){
        Controller controller=new Controller(getRightArgsChange);
        assertThat(controller.classBaseOperation).isInstanceOf(ChangeMidiFile.class);
    }




}
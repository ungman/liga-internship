package util.asserts;

import util.annotation.StatisticsTestLevel;

import java.util.Date;

public interface TestsAssertCounter {
    void init();
    void addMethodResult(String nameMethod, String nameMethodAnn, Date date, Boolean result,Object...objects);
    void showResult(StatisticsTestLevel testLevel);
//    void printingMessage(StatisticsTestLevel testLevel);
}

package util.asserts;


import util.annotation.PrintTestResult;
import util.annotation.StatisticsTestLevel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.BiConsumer;

public class AssertCounter implements TestsAssertCounter {

    private static LinkedHashMap<String, HashMap<String, HashMap<Date, List<Object>>>> methodsTestCounter;
    private static int countPassedTest = 0;
    private static int countFailedTest = 0;
    private static AssertCounter instance;

    public static StatisticsTestLevel kostil=null;
    public static PrintTestResult kostil2=null;

    private AssertCounter() {
    }
    public static AssertCounter getInstance() {
        if (instance == null)
            instance = new AssertCounter();
        return instance;
    }

    public void init() {
//        kostil=StatisticsTestLevel.All;
//        kostil2=PrintTestResult.AFTER_ASSERT;

        methodsTestCounter = new LinkedHashMap<>();
        countFailedTest = 0;
        countPassedTest = 0;
    }

    public void addMethodResult(String nameMethod, String nameMethodAnn, Date date, Boolean result, Object... objects) {

        date = new Date(date.getTime() + (countPassedTest + countFailedTest));
        ArrayList objectArrayList = new ArrayList<Object>() {{
            add(result);
            addAll(Arrays.asList(objects));
        }};
        Date finalDate = date;
        if (methodsTestCounter.get(nameMethod) == null) {
            methodsTestCounter.put(nameMethod, new HashMap<String, HashMap<Date, List<Object>>>() {{
                put(nameMethodAnn, new HashMap<Date, List<Object>>() {{
                    put(finalDate, objectArrayList);
                }});
            }});
        } else if (methodsTestCounter.get(nameMethod).get(nameMethodAnn) == null) {
            methodsTestCounter.get(nameMethod)
                    .put(nameMethodAnn, new HashMap<Date, List<Object>>() {{
                        put(finalDate, objectArrayList);
                    }});
        } else {
            methodsTestCounter.get(nameMethod).get(nameMethodAnn).put(finalDate, objectArrayList);
        }

        if (result)
            countPassedTest++;
        else
            countFailedTest++;

    }

    public void showResult(StatisticsTestLevel levelPrint) {

        if(kostil!=null)
            levelPrint=kostil;
        BiConsumer<Date, List<Object>> printLevel = getPrintLevelConsumer(levelPrint);
        if(printLevel==null)
            return;

        if (methodsTestCounter != null) {
            for (Map.Entry<String, HashMap<String, HashMap<Date, List<Object>>>> entry : methodsTestCounter.entrySet()) {
                System.out.println("Method name: " + entry.getKey());
                BiConsumer<Date, List<Object>> finalPrintLevel = printLevel;
                entry.getValue().forEach(((nameAssert, dateBooleanHashMap) -> {
                    System.out.println("  Name asserts: " + nameAssert);
                    dateBooleanHashMap
                            .forEach(finalPrintLevel);
                }));

                System.out.println();
            }

            System.out.println("Test " + (countFailedTest + countPassedTest) + "; Passed: " + countPassedTest + "; Failed: " + countFailedTest);
            System.out.println();

        }
    }

    private BiConsumer<Date, List<Object>> getPrintLevelConsumer(StatisticsTestLevel levelPrint) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:ms");

        BiConsumer<Date, List<Object>> printLevel = null;
        if (levelPrint.equals(StatisticsTestLevel.NONE))
            printLevel = null;

        if (levelPrint.equals(StatisticsTestLevel.ONLY_COUNTS))
            printLevel = (k, v) -> { };

        if (levelPrint.equals(StatisticsTestLevel.SHOW_NAME_RESULT))
            printLevel = (date, result) -> System.out.println("     " + dateFormat.format(date) + "; Expected:" + result.get(0));

        if (levelPrint.equals(StatisticsTestLevel.All))
            printLevel = (date, result) -> {
                System.out.println("     " + dateFormat.format(date) + "; Expected:" + result.get(0));
                System.out.print("     Data: ");
                result.stream().skip(1).forEach(data -> System.out.print(data + ";   "));
                System.out.println();
            };

        return printLevel;
    }
}

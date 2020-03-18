package util.asserts;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class AssertCounter {

    private static LinkedHashMap<String,HashMap<String, HashMap<Date,Boolean>>> methodsTestCounter;
    private  static int countPassedTest=0;
    private  static int countFailedTest=0;

    public static void  init(){

        methodsTestCounter=new LinkedHashMap<>();
        countFailedTest=0;
        countPassedTest=0;
    }

    public static void addMethodResult(String nameMethod,String nameMethodAnn,Date date, Boolean result){
        date=new Date(date.getTime()+(countPassedTest+countFailedTest));
        Date finalDate = date;
        if(methodsTestCounter.get(nameMethod)==null){
            methodsTestCounter.put(nameMethod,new HashMap<String, HashMap<Date, Boolean>>(){{put(nameMethodAnn,new HashMap<Date, Boolean>(){{put(finalDate,result);}});}});
        }else if(methodsTestCounter.get(nameMethod).get(nameMethodAnn)==null){
            methodsTestCounter.get(nameMethod)
                   .put(nameMethodAnn,new HashMap<Date, Boolean>(){{put(finalDate,result);}});
        }else{
            methodsTestCounter.get(nameMethod).get(nameMethodAnn).put(finalDate,result);
        }

        if(result)
            countPassedTest++;
        else
            countFailedTest++;

    }

    public static void showResult(){

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:ms");

        if(methodsTestCounter!=null) {
            for (Map.Entry<String,HashMap<String,HashMap<Date,Boolean>>> entry: methodsTestCounter.entrySet()) {
                System.out.println("Method name: "+entry.getKey());

                entry.getValue().forEach(((nameAssert, dateBooleanHashMap) -> {
                    System.out.println("  nameAssert: "+nameAssert);
                    dateBooleanHashMap
                            .forEach((date,result)-> System.out.println("     " + dateFormat.format(date) + "; Expected:" + result));}));

                System.out.println();
            }
            System.out.println("Test " + (countFailedTest + countPassedTest) + "; Passed: " + countPassedTest + "; Failed: " + countFailedTest);
            System.out.println();

        }
    }
}

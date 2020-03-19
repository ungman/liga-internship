package util.asserts;

import util.annotation.PrintTestResult;

import java.util.Date;
import java.util.List;

public class Assert {

    public static Object object;
    public static Assert instanceAssert;
    private  static final TestsAssertCounter testAssertCounters=AssertCounter.getInstance();


    private Assert() {
    }

    public static Assert ownAssert(Object... object1) {

        if (object1==null || object1.length < 1) {
            object = null;
        } else {
            object = object1[0];
        }

        if(instanceAssert==null){
            instanceAssert = new Assert();
        }
        if(AssertCounter.kostil2!=null && AssertCounter.kostil2.equals(PrintTestResult.AFTER_ASSERT))
            testAssertCounters.init();
        return  instanceAssert;
    }

    public void isTrue() {

        try {
            Boolean cast = (Boolean) object;
            if (cast) {
                testAssertCounters.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(), Thread.currentThread().getStackTrace()[1].getMethodName(),new Date(), true,object);
            } else {
                testAssertCounters.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), false,object);
            }
        } catch (Exception e) {
            testAssertCounters.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), false,object);
        }

        if(AssertCounter.kostil2!=null&&AssertCounter.kostil2.equals(PrintTestResult.AFTER_ASSERT)){
            testAssertCounters.showResult(AssertCounter.kostil);
        }
        //notify?
    }


    public void isEquals(Object object1) {
        if (object.equals(object1)) {
            testAssertCounters.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), true,object,object1);
        } else {
            testAssertCounters.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), false,object,object1);
        }

        if(AssertCounter.kostil2!=null&&AssertCounter.kostil2.equals(PrintTestResult.AFTER_ASSERT)){
            testAssertCounters.showResult(AssertCounter.kostil);
        }
    }


    public void isNotNull() {
        if (object != null)
            testAssertCounters.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), true,object);
        else
            testAssertCounters.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), false,object);

        if(AssertCounter.kostil2!=null&&AssertCounter.kostil2.equals(PrintTestResult.AFTER_ASSERT)){
            testAssertCounters.showResult(AssertCounter.kostil);

        }
    }

    private  static String getCallerClassName(StackTraceElement[] ste){
        String callerClassName = null;
        int i = 1;
        while (i < ste.length && ste[i].getMethodName().startsWith("access$")) {
            ++i;
        }
        if (i < ste.length) {
            callerClassName = ste[i].getClassName();
        }
        return callerClassName;
    }

}

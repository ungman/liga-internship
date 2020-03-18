package util.asserts;

import java.util.Date;

public class Assert {

    public static Object object;
    public static Assert instanceAssert;
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

        return  instanceAssert;
    }

    public void isTrue() {
        try {
            Boolean cast = (Boolean) object;
            if (cast) {
                AssertCounter.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(), Thread.currentThread().getStackTrace()[1].getMethodName(),new Date(), true);
            } else {
                AssertCounter.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), false);
            }
        } catch (Exception e) {
            AssertCounter.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), false);
        }
    }

    public void isEquals(Object object1) {
        if (object.equals(object1)) {
            AssertCounter.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), true);
        } else {
            AssertCounter.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), false);
        }
    }

    public void isNotNull() {
        if (object != null)
            AssertCounter.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), true);
        else
            AssertCounter.addMethodResult(Thread.currentThread().getStackTrace()[2].getMethodName(),Thread.currentThread().getStackTrace()[1].getMethodName(), new Date(), false);

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

package util;

import util.annotation.*;
import util.asserts.AssertCounter;
import util.asserts.TestsAssertCounter;
import util.marker.IgnoreTestException;
import util.reflection.ReflectionHelper;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CustomTestsRunner {

    private static final Class[] defaultAnnotation = new Class[]{After.class, Before.class, Test.class};

    private  static  final  boolean isMoreOneBeforeOrAfterException=true;
    private  static  final boolean isMoreOneBeforeOrAfterAlert=true;

    private  static  final  MapClassMethodsWithAnnotations MapClassMethodsWithAnnotations= ReflectionHelper.getInstance();

    private static final TestsAssertCounter testAssertCounter=AssertCounter.getInstance();

    public static void runTest(String packageName) throws  IllegalAccessException, InvocationTargetException, InstantiationException {

        Map<Class<?>, List<Method>> mapClassMethods = MapClassMethodsWithAnnotations.getMapClassMethodsWithAnnotations(packageName,defaultAnnotation);

        for (Map.Entry<Class<?>, List<Method>> entry : mapClassMethods.entrySet()) {

            entry.getKey().getDeclaredConstructors();
            Object invokedObject = entry.getKey().getDeclaredConstructors()[0].newInstance();
            System.out.println("Test ran for " + entry.getKey());

            ArrayList<Method> methodsBefore = entry.getValue().stream()
                    .filter(method -> method.getAnnotation(Before.class) != null)
                    .collect(Collectors.toCollection(ArrayList::new));

            ArrayList<Method> methodsAfter = entry.getValue().stream()
                    .filter(method -> method.getAnnotation(After.class) != null)
                    .collect(Collectors.toCollection(ArrayList::new));

            LinkedList<Method> methodsTest = entry.getValue().stream()
                    .filter(method -> method.getAnnotation(Test.class) != null)
                    .collect(Collectors.toCollection(LinkedList::new));

            if( isMoreOneBeforeOrAfterException && !(invokedObject instanceof IgnoreTestException))  {
                if(methodsBefore.size()>1)
                    throw  new RuntimeException("More than one method with annotation @Before in class: "+ entry.getKey().getName()+"\n"+ Arrays.toString(Thread.currentThread().getStackTrace())
                    );
                if(methodsAfter.size()>1)
                    throw  new RuntimeException("More than one method with annotation @After in class: "+ entry.getKey().getName()+"\n"+ Arrays.toString(Thread.currentThread().getStackTrace()));
            }

            if(isMoreOneBeforeOrAfterAlert){
                if(methodsBefore.size()>1)
                    System.out.println("    More than one method with annotation @Before in class:"+ entry.getKey().getName());
                if(methodsAfter.size()>1)
                    System.out.println("    More than one method with annotation @After in class:"+ entry.getKey().getName());
            }

            runTest(invokedObject, methodsTest, methodsBefore, methodsAfter);

        }
    }

    private static void runTest(Object invoked, List<Method> listTest, List<Method> listBefore, List<Method> listAfter) {

        ArrayList<Method> showAfterAssert=listTest.stream()
                .filter(method -> method.getAnnotation(Test.class).printTestResult().equals(PrintTestResult.AFTER_ASSERT))
                .collect(Collectors.toCollection(ArrayList::new));
        ArrayList<Method> showAfterMethod=listTest.stream()
                .filter(method -> method.getAnnotation(Test.class).printTestResult().equals(PrintTestResult.AFTER_METHOD))
                .collect(Collectors.toCollection(ArrayList::new));

        testAssertCounter.init();
        showAfterAssert.forEach(method -> {
            try {
                AssertCounter.kostil=method.getAnnotation(Test.class).statisticsTestLevel();
                AssertCounter.kostil2=method.getAnnotation(Test.class).printTestResult();
                runMethodList(invoked, listBefore);
                method.invoke(invoked);
                runMethodList(invoked, listAfter);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

        AssertCounter.kostil2=null;
        AssertCounter.kostil=null;

        showAfterMethod.forEach(method -> {
            try {
                testAssertCounter.init();
                runMethodList(invoked, listBefore);
                method.invoke(invoked);
                runMethodList(invoked, listAfter);
                testAssertCounter.showResult(method.getAnnotation(Test.class).statisticsTestLevel());
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });


    }

    private static void runMethodList(Object invoked, List<Method> list) {
        list.forEach(method -> {
            try {
                method.invoke(invoked);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }
}

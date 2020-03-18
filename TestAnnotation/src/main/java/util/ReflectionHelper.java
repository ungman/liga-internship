package util;

import util.annotation.After;
import util.annotation.Before;
import util.annotation.Test;
import util.asserts.AssertCounter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ReflectionHelper {

    private static final Class[] defaultAnnotation = new Class[]{After.class, Before.class, Test.class};


    public static void runTest(String packageName, Class... annClasses) throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        if (annClasses == null || annClasses.length < 1) {
            annClasses = defaultAnnotation;
        }
        Map<Class<?>, List<Method>> mapClassMethods = getMapClassMethods(packageName, annClasses);
        for (Map.Entry<Class<?>, List<Method>> entry : mapClassMethods.entrySet()) {

            entry.getKey().getDeclaredConstructors();
            Object invokedObject = entry.getKey().getDeclaredConstructors()[0].newInstance();
            System.out.println("Test ran for " +entry.getKey());
            AssertCounter.init();
            List<Method> methodsBefore = entry.getValue().stream()
                    .filter(method -> method.getAnnotation(Before.class) != null)
                    .collect(Collectors.toList());

            List<Method> methodsAfter = entry.getValue().stream()
                    .filter(method -> method.getAnnotation(After.class) != null)
                    .collect(Collectors.toList());

            List<Method> methodsTest = entry.getValue().stream()
                    .filter(method -> method.getAnnotation(Test.class) != null)
                    .collect(Collectors.toList());

            runTest(invokedObject,methodsTest,methodsBefore,methodsAfter);

            AssertCounter.showResult();
        }
    }

    private  static  void runTest(Object invoked, List<Method> listT,List<Method> listB,List<Method> listA){

        listT.stream().forEachOrdered(method -> {
            try {
                runMethodList(invoked,listB);
                method.invoke(invoked);
                runMethodList(invoked,listA);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }
    private static void runMethodList(Object invoked, List<Method> list) {

        list.stream().forEachOrdered(method -> {
            try {
                method.invoke(invoked);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    public static Map<Class<?>, List<Method>> getMapClassMethods(String packageName, Class[] annClasses) throws IOException, ClassNotFoundException {
        return getClasses(packageName).stream()
                .filter(clazz -> Arrays.stream(clazz.getDeclaredMethods())
                        .anyMatch(method -> Stream.of(annClasses).anyMatch(clazzAnn -> method.getAnnotation(clazzAnn) != null)))
                .collect(Collectors.toMap(
                        clazz -> clazz,
                        clazz -> Arrays.stream(clazz.getDeclaredMethods())
                                .filter(method -> Stream.of(annClasses)
                                        .anyMatch(clazzAnn -> method.getAnnotation(clazzAnn) != null))
                                .collect(Collectors.toList())));
    }


    private static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            throw new RuntimeException("Error! not found classloader");
        }
        String path = packageName.replace('.', '/');
        Enumeration resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList();
        while (resources.hasMoreElements()) {
            URL resource = (URL) resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File dir : dirs) {
            classes.addAll(findClasses(dir, packageName));
        }
        return classes;
    }

    private static List findClasses(File directory, String packageName) throws ClassNotFoundException {
        List classes = new ArrayList();
        if (!directory.exists())
            return classes;

        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory() && file.getName().contains(".")) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}

package util.reflection;

import util.MapClassMethodsWithAnnotations;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class ReflectionHelper implements MapClassMethodsWithAnnotations {

    private static ReflectionHelper instance;
    private ReflectionHelper(){};
    public static ReflectionHelper getInstance() {
        if (instance == null) {
            instance = new ReflectionHelper();
        }
        return instance;
    }

    @Override
    public LinkedHashMap<Class<?>, List<Method>> getMapClassMethodsWithAnnotations(String packageName,Class[] annClasses) {
        LinkedHashMap<Class<?>, List<Method>> test = null;
        try {
            test = getClasses(packageName).stream()
                    .filter(clazz -> Arrays.stream(clazz.getDeclaredMethods())
                            .anyMatch(method -> Stream.of(annClasses).anyMatch(clazzAnn -> method.getAnnotation(clazzAnn) != null)))
                    .collect(Collectors.toMap(
                            clazz -> clazz,
                            clazz -> Arrays.stream(clazz.getDeclaredMethods())
                                    .filter(method -> Stream.of(annClasses)
                                            .anyMatch(clazzAnn -> method.getAnnotation(clazzAnn) != null))
                                    .collect(Collectors.toCollection(LinkedList::new)),
                            (u, v) -> {
                                throw new IllegalStateException(String.format("Duplicate key %s", u));
                                },
                            LinkedHashMap::new
                            ));
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return test;
    }

    private static List<Class<?>> getClasses(String packageName) throws IOException, ClassNotFoundException {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            throw new RuntimeException("Error! not found classloader");
        }
        String path = packageName.replace('.', '/');
        Enumeration resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
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

        boolean enterInPackage=true; // enter to sub folder
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                if(enterInPackage)
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));//assert !file.getName().contains(".");
                else if(file.getName().contains("."))
                    classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}

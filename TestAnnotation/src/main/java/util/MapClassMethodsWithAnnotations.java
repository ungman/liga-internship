package util;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface MapClassMethodsWithAnnotations{
     LinkedHashMap<Class<?>, List<Method>> getMapClassMethodsWithAnnotations(String packageName, Class[] annClasses);
}

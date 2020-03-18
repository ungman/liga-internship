import util.ReflectionHelper;
import util.annotation.After;
import util.annotation.Before;
import util.annotation.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
        ReflectionHelper.runTest("tests",true);
    }
}

package ru.liga.songtask.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.util.CheckerArguments;
import ru.liga.songtask.util.FileOperation;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Controller {

    public static final Logger logger = LoggerFactory.getLogger(Controller.class);
    public  FileOperation classBaseOperation;
    private  String pathToMidFile = "";
    private  String nameMidFile = "";

    public Controller(String[] args) {
        Controller.logger.debug("Enter to {}","Controller()");

        CheckerArguments checkerArgumentsController = CheckerArgumentController.getInstance();

        if (checkerArgumentsController.checkArguments(args)) {
            if (!parseArguments(args)) {
                logger.info("Cant parse argument");
                throw new RuntimeException("Cant parse argument");
            }
        } else {
            logger.info("Cant check argument");
            throw new RuntimeException("Cant check argument");
        }

    }


    public  boolean parseArguments(String[] args) {

        pathToMidFile = deleteAllQuotes(args[0]);
        nameMidFile = geNameMidFile();

        String typeAction = getTypeAction(args);


        if (typeAction.equals("analyze")) {
            logger.info("Chosen analyze {}",nameMidFile);
            if (CheckerArgumentsAnalyzeMidiFile.getInstance().checkArguments(args)) ;
            classBaseOperation = AnalyzeMidiFile.getInstance(args, pathToMidFile);
        }

        if (typeAction.equals("change")) {
            logger.info("Chosen change {}",nameMidFile);
            if (CheckerArgumentChangeMidiFile.getInstance().checkArguments(args)) ;
            classBaseOperation = ChangeMidiFile.getInstance(args, pathToMidFile);
        }

        if (classBaseOperation == null) {
            logger.debug("Dont create class for operation {}", typeAction);
            throw new RuntimeException("Dont create class for operation" + typeAction);
        }

        classBaseOperation.makeOperation();
        logger.debug("checkArgument: OK");
        return true;
    }

    private static String getTypeAction(String[] args) {

        String typeAction = null;
        if (args[1].equals("analyze")) {
            typeAction = "analyze";
        }
        if (args[1].equals("change")) {
            typeAction = "change";
        }
        return typeAction;
    }

    public  String getPathToMidFile() {
        return pathToMidFile;
    }

    public String getNameMidFile() {
        return nameMidFile;
    }

    public static String deleteAllQuotes(String string) {
        if (string.contains("\""))
            string = string.replaceAll("\"", "");
        return string;
    }

    public static boolean checkCorrectPath(String pathToMidFile1) {
        return pathToMidFile1.endsWith(".mid") && Files.isRegularFile(Paths.get(pathToMidFile1));
    }

    private String geNameMidFile() {
        if (checkCorrectPath(pathToMidFile)) {
            nameMidFile = pathToMidFile.split("\\\\")[pathToMidFile.split("\\\\").length - 1];
        }
        return nameMidFile;
    }
}

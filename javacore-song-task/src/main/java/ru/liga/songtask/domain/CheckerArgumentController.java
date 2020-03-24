package ru.liga.songtask.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.util.CheckerArguments;

import java.nio.file.Files;
import java.nio.file.Paths;

public class CheckerArgumentController implements CheckerArguments {

    private static CheckerArgumentController single_instance = null;
    public static final Logger logger = LoggerFactory.getLogger(CheckerArgumentController.class);

    private CheckerArgumentController() {
        logger.debug("Enter to {} ", "CheckerArgumentController()");
    }

    public static CheckerArgumentController getInstance() {
        logger.debug("Enter to {} ", "getInstance");
        if (single_instance == null)
            single_instance = new CheckerArgumentController();
        return single_instance;
    }

    public boolean checkTypeAction(String[] args) {
        logger.debug("Enter to {} ", "checkTypeAction");
        boolean isArgumentRight = true;
        if (args.length < 2) {
            logger.info("Arguments less than 2. Please enter: ");
            logger.info("   java -jar pathToJar.jar \"pathToMidFile.mid\" analyze");
            logger.info("   java -jar pathToJar.jar \"pathToMidFile.mid\" change -tempo x -trans y");
            isArgumentRight = false;
        }
        if (isArgumentRight && !args[1].equals("analyze") && !args[1].equals("change")) {
            logger.info("Wrong arguments. Please enter: ");
            logger.info("   java -jar pathToJar.jar \"pathToMidFile.mid\" analyze");
            logger.info("   java -jar pathToJar.jar \"pathToMidFile.mid\" change -tempo x -trans y");
            isArgumentRight = false;
        }
        return isArgumentRight;
    }

    public boolean checkCorrectPath(String pathToMidFile1) {
        boolean isCorrectPath = pathToMidFile1.endsWith(".mid") && Files.isRegularFile(Paths.get(pathToMidFile1));
        if (!isCorrectPath) {
            logger.info("Wrong path to file. Please enter: ");
            logger.info("   java -jar pathToJar.jar \"pathToMidFile.mid\" analyze");
            logger.info("   java -jar pathToJar.jar \"pathToMidFile.mid\" change -tempo x -trans y");
        }
        return isCorrectPath;

    }

    @Override
    public boolean checkArguments(String[] args) {
        return checkTypeAction(args) && checkCorrectPath(Controller.deleteAllQuotes(args[0]));
    }
}

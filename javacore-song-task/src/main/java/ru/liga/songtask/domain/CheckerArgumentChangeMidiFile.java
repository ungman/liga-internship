package ru.liga.songtask.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.util.CheckerArguments;


public class CheckerArgumentChangeMidiFile implements CheckerArguments {

    private static CheckerArgumentChangeMidiFile single_instance = null;
    public static final Logger logger = LoggerFactory.getLogger(CheckerArgumentChangeMidiFile.class);

    private CheckerArgumentChangeMidiFile() {
        logger.debug("Enter to {}", "CheckerArgumentChangeMidiFile()");
    }

    public static CheckerArgumentChangeMidiFile getInstance() {
        logger.debug("Enter to {}", "getInstance");
        if (single_instance == null)
            single_instance = new CheckerArgumentChangeMidiFile();
        return single_instance;
    }

    @Override
    public boolean checkArguments(String[] args) {
        logger.debug("Enter to {} ", "checkArguments");
        boolean isValueTranspose = false;
        boolean isValueTempo = false;
        for (int i = 2; i < args.length; i += 2) {
            if (args[i].trim().equalsIgnoreCase("-trans")) {
                try {
                    Integer.parseInt(args[i + 1].trim());
                    isValueTranspose = true;
                } catch (Exception e) {
                    isValueTranspose = false;
                    logger.info("Please enter right number for trans");
                    logger.info("For change:  java -jar pathToJar.jar pathToMidiFile.mid  change -trans x -tempo y");
                }
            }
            if (args[i].trim().equalsIgnoreCase("-tempo")) {
                try {
                    Integer.parseInt(args[i + 1].trim());
                    isValueTempo = true;
                } catch (Exception e) {
                    isValueTempo = false;
                    logger.info("Please enter right number for tempo");
                    logger.info("For change:  java -jar pathToJar.jar pathToMidiFile.mid  change -trans x -tempo y");

                }
            }
        }
        if (!isValueTranspose || !isValueTempo) {
            String name = !isValueTranspose ? " -trans" : "-tempo";
            logger.info("Please input argument for " + name);
            logger.info("For change:  java -jar pathToJar.jar pathToMidiFile.mid  change -trans x -tempo y");
        } else {
            logger.info("Arguments it right");
        }
        logger.debug("check arguments {} {}", isValueTempo, isValueTranspose);
        return isValueTempo && isValueTranspose;
    }

}

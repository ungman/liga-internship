package ru.liga.songtask.domain;

import ru.liga.songtask.util.CheckerArguments;



public class CheckerArgumentChangeMidiFile implements CheckerArguments {
    private static CheckerArgumentChangeMidiFile single_instance = null;

    private CheckerArgumentChangeMidiFile() {
        Controller.logger.debug("Enter to {}","CheckerArgumentChangeMidiFile()");
    }

    public static CheckerArgumentChangeMidiFile getInstance() {
        Controller.logger.debug("Enter to {}","getInstance");
        if (single_instance == null)
            single_instance = new CheckerArgumentChangeMidiFile();
        return single_instance;
    }

    @Override
    public boolean checkArguments(String[] args) {
        Controller.logger.debug("Enter to {} ", "checkArguments");

        boolean isValueTranspose = false;
        boolean isValueTempo = false;

        for (int i = 2; i < args.length; i += 2) {
            if (args[i].trim().equalsIgnoreCase("-trans")) {
                try {
                    Integer.parseInt(args[i + 1].trim());
                    isValueTranspose = true;
                } catch (Exception e) {
                    isValueTranspose=false;
                    Controller.logger.info("Please enter right number for trans");
                    Controller.logger.info("For change:  java -jar pathToJar.jar pathToMidiFile.mid  change -trans x -tempo y");
                }
            }

            if (args[i].trim().equalsIgnoreCase("-tempo")) {
                try {
                    Integer.parseInt(args[i + 1].trim());
                    isValueTempo = true;
                } catch (Exception e) {
                    isValueTempo=false;
                    Controller.logger.info("Please enter right number for tempo");
                    Controller.logger.info("For change:  java -jar pathToJar.jar pathToMidiFile.mid  change -trans x -tempo y");

                }
            }
        }

        if (!isValueTranspose || !isValueTempo) {
            String name = !isValueTranspose ? " -trans" : "-tempo";
            Controller.logger.info("Please input argument for " + name);
            Controller.logger.info("For change:  java -jar pathToJar.jar pathToMidiFile.mid  change -trans x -tempo y");
        } else {
            Controller.logger.info("Arguments it right");
        }

        Controller.logger.debug("check arguments {} {}",isValueTempo,isValueTranspose);
        return isValueTempo && isValueTranspose;
    }

}

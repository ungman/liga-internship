package ru.liga.songtask.util;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Controller {

    public static MidiOperation classBaseOperation;
    private static String pathToMidFile = "";
    private static String nameMidFile = "";

    public static void  main(String[] args){
        String[] arg1={"\"D:\\trash\\liga-internship\\javacore-song-task\\src\\main\\resources\\Belle.mid\"", "-analyze"};
        checkArgument(arg1);
    }

    public static boolean checkArgument(String[] args) {

        if (args.length < 2) {
            return false;
        }

        if (!args[1].equals("-analyze") && args.length == 2) {
            return false;
        }

        String pathToFile = deleteAllQuotes(args[0]);
        String typeAction = args[1];

        if (!checkCorrectPath(pathToFile)) {
            return false;
        } else {
            pathToMidFile = pathToFile;
            nameMidFile = geNameMidFile();
        }

        if (!typeAction.equals("-change") && !typeAction.equals("-analyze")) {
            return false;
        }

        if (typeAction.equals("-analyze")) {
            classBaseOperation = AnalyzeMidiFile.getInstance(args,pathToMidFile);
        }
        return true;
    }

    public static String getPathToMidFile() {
        return pathToMidFile;
    }

    public static void setPathToMidFile(String pathToMidFile1) {
        pathToMidFile = pathToMidFile1;
    }

    public static String getNameMidFile() {
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

    public static String geNameMidFile() {
        if (checkCorrectPath(pathToMidFile)) {
            nameMidFile = pathToMidFile.split("\\\\")[pathToMidFile.split("\\\\").length - 1];
        }
        return nameMidFile;
    }
}

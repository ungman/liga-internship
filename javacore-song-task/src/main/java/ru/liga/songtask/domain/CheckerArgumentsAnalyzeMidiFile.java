package ru.liga.songtask.domain;

import ru.liga.songtask.util.CheckerArguments;

public class CheckerArgumentsAnalyzeMidiFile implements CheckerArguments {

    private static CheckerArgumentsAnalyzeMidiFile single_instance = null;

    private CheckerArgumentsAnalyzeMidiFile() {
        Controller.logger.debug("Enter" +
                " to {}","CheckerArgumentsAnalyzeMidiFile()");
    }
    public static CheckerArgumentsAnalyzeMidiFile getInstance() {
        Controller.logger.debug("Enter to {}","getInstance");

        if (single_instance == null)
            single_instance = new CheckerArgumentsAnalyzeMidiFile();
        return single_instance;
    }

    @Override
    public boolean checkArguments(String[] args) {
        return args != null && args.length > 1;
    }
}

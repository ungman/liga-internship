package ru.liga.songtask.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.util.CheckerArguments;

public class CheckerArgumentsAnalyzeMidiFile implements CheckerArguments {

    private static CheckerArgumentsAnalyzeMidiFile single_instance = null;
    public static final Logger logger = LoggerFactory.getLogger(CheckerArgumentsAnalyzeMidiFile.class);

    private CheckerArgumentsAnalyzeMidiFile() {
        logger.debug("Enter to {}", "CheckerArgumentsAnalyzeMidiFile()");
    }

    public static CheckerArgumentsAnalyzeMidiFile getInstance() {
        logger.debug("Enter to {}", "getInstance");
        if (single_instance == null)
            single_instance = new CheckerArgumentsAnalyzeMidiFile();
        return single_instance;
    }

    @Override
    public boolean checkArguments(String[] args) {
        return args != null && args.length > 1;
    }
}

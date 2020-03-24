package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.event.meta.Tempo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.liga.songtask.domain.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public abstract class MidiFileBaseOperationHelper {
    public static final Logger logger = LoggerFactory.getLogger(MidiFileBaseOperationHelper.class);
    private MidiFile midiFile;
    private Tempo tempo;
    private String pathToJar;

    public MidiFileBaseOperationHelper(String path) {
        try {
            pathToJar = new File(MidiFileBaseOperationHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getPath();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        logger.debug("Enter to {}", "MidiFileBaseOperationHelper(String path,Logger logger)");
        setMidiFile(path);
        setTempo();
    }

    public String getPathToJar() {
        return pathToJar;
    }

    private void setTempo() {
        logger.debug("Enter to {}", "setTempo");
        if (midiFile == null) {
            return;
        }
        tempo = (Tempo) midiFile.getTracks().stream()
                .flatMap(midiTrack -> midiTrack.getEvents().stream())
                .filter(midiEvent -> midiEvent instanceof Tempo)
                .findFirst()
                .orElse(null);
    }

    public MidiFile getMidiFile() {
        return midiFile;
    }

    private void setMidiFile(String path) {
        Controller.logger.debug("Enter to {}", "setMidiFile");
        try {
            midiFile = new MidiFile(new FileInputStream(path));
        } catch (IOException e) {
//            e.printStackTrace();
            Controller.logger.info("Cant open file on path: {}", path);
            Controller.logger.debug(e.getMessage());
            throw new RuntimeException("Cant open file on path: " + path);
        }
    }

    abstract public void makeOperation();

    public Tempo getTempo() {
        return tempo;
    }


}

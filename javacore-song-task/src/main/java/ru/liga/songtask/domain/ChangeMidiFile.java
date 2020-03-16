package ru.liga.songtask.domain;

import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import ru.liga.songtask.util.FileOperation;

import java.io.File;
import java.io.IOException;

public class ChangeMidiFile extends FileOperation {

    private static String pathToNewFile;
    private static int maxMidiNumber = NoteSign.values()[0].getMidi();
    private int valueTranspose;
    private float valueTempo;
    private int oldValueTempo;

    private ChangeMidiFile(String[] args, String pathToFile) {
        super(pathToFile);

        if (!parseArgument(args)) {
            Controller.logger.info("Cant parse argument");
            throw new RuntimeException("Cant parse argument");
        }
    }

    public static ChangeMidiFile getInstance(String[] args, String pathToFile) {
        Controller.logger.debug("Enter to {} ", "getInstance");
        return new ChangeMidiFile(args, pathToFile);
    }

    public boolean parseArgument(String[] args) {
        Controller.logger.debug("Enter to {} ", "parseArgument");

        for (int i = 2; i < args.length; i += 2) {
            if (args[i].trim().equalsIgnoreCase("-trans")) {
                try {
                    valueTranspose = Integer.parseInt(args[i + 1].trim());
                } catch (Exception e) {
                    Controller.logger.info("Please enter number for trans");
                    Controller.logger.info("For analyze: java -jar " + getPathToJar() + " pathToMidiFile.mid  analyze");
                    Controller.logger.info("For change:  java -jar " + getPathToJar() + " pathToMidiFile.mid  change -trans x -tempo y");

                    throw new RuntimeException("Not correct argument for -trans");
                }
            }

            if (args[i].trim().equalsIgnoreCase("-tempo")) {
                try {
                    oldValueTempo = Integer.parseInt(args[i + 1].trim());
                } catch (Exception e) {
                    Controller.logger.info("Please enter number for tempo");
                    Controller.logger.info("For analyze: java -jar " + getPathToJar() + " pathToMidiFile.mid  analyze");
                    Controller.logger.info("For change:  java -jar " + getPathToJar() + " pathToMidiFile.mid  change -trans x -tempo y");
                    throw new RuntimeException("Not correct argument for -trans");
                }
            }
        }

        valueTempo = oldValueTempo == 0 ? 1 : (valueTempo * 1.0f) / 100;

        String nameMidFile = args[0].replace("\"", "").split("\\\\")[args[0].split("\\\\").length - 1];
        nameMidFile = nameMidFile.split("\\.")[0] + " -trans " + valueTranspose + " -tempo " + oldValueTempo + ".mid";
        String[] arrJarPath = getPathToJar().split("\\\\");
        StringBuilder newPath = new StringBuilder();

        for (int i = 0; i < arrJarPath.length - 1; i++) {
            newPath.append(arrJarPath[i]).append("\\");
        }
        pathToNewFile = newPath.append(nameMidFile).toString();
        return true;
    }

    @Override
    public void makeOperation() {
        changeMidiFile(pathToNewFile);
    }

    private void changeMidiFile(String newPath) {
        Controller.logger.debug("Enter to {} ", "changeMidiFile");

        MidiFile midiFile1 = getMidiFile();

        File file = new File(newPath);
        Controller.logger.info("New File name: {}", newPath);

        boolean isCreateFile;
        try {
            isCreateFile = file.createNewFile();
            if (!isCreateFile) {
                Controller.logger.info("File is existing");
            }
        } catch (IOException ex) {
            Controller.logger.info("Cant create new midiFile");
            Controller.logger.debug(ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        }
        Controller.logger.info("New File name: {}", newPath);

        midiFile1.getTracks().forEach(midiTrack -> midiTrack.getEvents().
                forEach(this::changeEvent));

        try {
            midiFile1.writeToFile(file);
        } catch (IOException e) {
            Controller.logger.info("Cant write new file");
            Controller.logger.debug(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        Controller.logger.debug("Exit from {} ", "changeMidiFile");
    }

    private void changeEvent(MidiEvent midiEvent) {
        if (midiEvent instanceof Tempo) {
            changeTempo(midiEvent);
            return;
        }
        if (midiEvent instanceof NoteOn || midiEvent instanceof NoteOff)
            changeNote(midiEvent);
    }

    private void changeNote(MidiEvent midiEvent) {

        if (midiEvent instanceof NoteOff) {
            if (checkNewNumber(((NoteOff) midiEvent).getNoteValue() + valueTranspose))
                ((NoteOff) midiEvent).setNoteValue(((NoteOff) midiEvent).getNoteValue() + valueTranspose);
            else {
                Controller.logger.info("Note higher or lower then needly {}", ((NoteOff) midiEvent).getNoteValue() + valueTranspose);
                throw new RuntimeException("Note higher or lower then needly");
            }

        }
        if (midiEvent instanceof NoteOn) {
            if (checkNewNumber(((NoteOn) midiEvent).getNoteValue() + valueTranspose))
                ((NoteOn) midiEvent).setNoteValue(((NoteOn) midiEvent).getNoteValue() + valueTranspose);
            else {
                Controller.logger.info("Note higher or lower then needly {} ",((NoteOn) midiEvent).getNoteValue() + valueTranspose);
                throw new RuntimeException("Note higher or lower then needly");
            }
        }
    }

    private boolean checkNewNumber(int number) {
        return number < maxMidiNumber && number >= 0;
    }

    private void changeTempo(MidiEvent midiEvent) {
        ((Tempo) midiEvent).setBpm(((Tempo) midiEvent).getBpm() / valueTempo);
    }
}

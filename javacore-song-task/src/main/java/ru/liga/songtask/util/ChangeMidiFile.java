package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import org.slf4j.Logger;
import ru.liga.songtask.domain.NoteSign;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

public class ChangeMidiFile {
    public static Logger logger = null;

    public static boolean change(String[] args, String jarPath, Logger logger1) throws IOException {

        //log(e)
        logger = logger1;
        logger.debug("Enter to {} ", "change");
        logger.info("Start changing");
        logger.info("Checking Arguments");
        int valueTranspose = 0;
        int valueTempo = 0;
        boolean isHalftone = false;
        boolean isValueTempo = false;
        for (int i = 2; i < args.length; i += 2) {

            if (args[i].trim().equalsIgnoreCase("-trans")) {
                try {
                    valueTranspose = Integer.parseInt(args[i + 1].trim());
                    isHalftone = true;
                } catch (Exception e) {
                    logger.info("Please enter number for trans");
                    logger.info("For analyze: java -jar " + jarPath + " pathToMidiFile.mid  analyze");
                    logger.info("For change:  java -jar " + jarPath + " pathToMidiFile.mid  change -trans x -tempo y");
                    logger.debug("Exit from {} ","change");
                    //log e;
                    return false;
                }
            }

            if (args[i].trim().equalsIgnoreCase("-tempo")) {
                try {
                    valueTempo = Integer.parseInt(args[i + 1].trim());
                    isValueTempo = true;
                } catch (Exception e) {
                    logger.info("Please enter number for tempo");
                    logger.info("For analyze: java -jar " + jarPath + " pathToMidiFile.mid  analyze");
                    logger.info("For change:  java -jar " + jarPath + " pathToMidiFile.mid  change -trans x -tempo y");
                    logger.debug("Exit from {} ","change");
                    //log e;
                    return false;

                }
            }
        }

        if (!isHalftone || !isValueTempo) {
            String name = !isHalftone ? " -trans" : "-tempo";
            logger.info("Please input argument for " + name);
            logger.info("For change:  java -jar " + jarPath + " pathToMidiFile.mid  change -trans x -tempo y");
            logger.debug("Exit from {} ","change");
            //log e;
            return false;
        }
        logger.debug("Successfully checked arguments");
        logger.info("Successfully checked arguments");
        String[] arrJarPath = jarPath.split("\\\\");
        logger.debug("Name file {} ",args[0].trim());
        String nameMidFile = args[0].replace("\"", "").split("\\\\")[args[0].split("\\\\").length - 1];
        String newPath = "";
        nameMidFile = nameMidFile.split("\\.")[0] + "-trans " + valueTranspose + " -tempo " + valueTempo + ".mid";
        for (int i = 0; i < arrJarPath.length - 1; i++) {
            newPath = newPath + arrJarPath[i] + "\\";
        }
        logger.debug("Name new file {} ",newPath + nameMidFile);
        transpose(args[0].trim().replace("\"", ""), newPath + nameMidFile, valueTranspose, valueTempo);
        logger.debug("Exit from {} ","change");
        logger.info("Exit from {} ","change");
        return true;
    }


    private static void transpose(String path, String newPath, int valueTranspose, int valueTempo) throws IOException {
        logger.debug("Enter to {} ","transpose");
        logger.info("Started transform");
        MidiFile midiFile1 = new MidiFile((new FileInputStream(path)));


        File file = new File(newPath);
        file.createNewFile();
        float newValTempo = valueTempo == 0 ? 1 : (valueTempo * 1.0f) / 100;
        for (int i = 0; i < midiFile1.getTrackCount(); i++) {
            transposeMidiTrack(midiFile1.getTracks().get(i).getEvents(), valueTranspose, newValTempo);
        }
        midiFile1.writeToFile(file);
        logger.debug("Exit from {} ","transpose");
    }

    private static void transposeMidiTrack(TreeSet<MidiEvent> events, int valueTranspose, float valueTempo) {
        logger.debug("Enter to {} ","transposeMidiTrack");
        Queue<NoteOn> noteOnQueue = new LinkedBlockingQueue<>();
        for (MidiEvent event : events) {
            if (event instanceof NoteOn || event instanceof NoteOff) {
                if (SongUtils.isEndMarkerNote(event)) {
                    NoteSign noteSign = NoteSign.fromMidiNumber(SongUtils.extractNoteValue(event));
                    if (noteSign != NoteSign.NULL_VALUE) {
                        NoteOn noteOn = noteOnQueue.poll();
                        if (noteOn != null) {
                            if (event instanceof NoteOff)
                                ((NoteOff) event).setNoteValue(transposeNote(noteSign.getMidi(), valueTranspose));
                            if (event instanceof NoteOn)
                                noteOn.setNoteValue(transposeNote(noteSign.getMidi(), valueTranspose));
                        }
                    }
                } else {
                    noteOnQueue.offer((NoteOn) event);
                }
            }
            if (event instanceof Tempo) {
                ((Tempo) event).setBpm(((Tempo) event).getBpm() * valueTempo);
            }
        }
        logger.debug("Exit from {} ","transposeMidiTrack");
    }

    private static int transposeNote(int noteSign, int valueTranspose) {
        int valueNote = noteSign + valueTranspose;

        if (valueNote < 0) {
            logger.info("Transpose Note value lower then NoteSign length {}" , valueNote);
            throw new RuntimeException("Transpose Note value lower then NoteSign length " + valueNote);
        }
        int maxMidi=107;
        if (valueNote > maxMidi) {
            logger.info("Transpose Note value higher then NoteSign length {}" , valueNote);
            throw new RuntimeException("Transpose Note value higher then NoteSign length " + valueNote);
        }

        return valueNote;
    }
}

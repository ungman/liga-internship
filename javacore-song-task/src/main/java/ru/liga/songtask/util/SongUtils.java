package ru.liga.songtask.util;

import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import ru.liga.songtask.domain.Note;
import ru.liga.songtask.domain.NoteSign;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingQueue;

public class SongUtils {

    /**
     * Перевод тиков в миллисекунды
     *
     * @param bpm          - количество ударов в минуту (темп)
     * @param resolution   - midiFile.getResolution()
     * @param amountOfTick - то что переводим в миллисекунды
     * @return
     */
    public static int tickToMs(float bpm, int resolution, long amountOfTick) {
        return (int) (((60 * 1000) / (bpm * resolution)) * amountOfTick);
    }

    public static List<Note> eventsToNotes(TreeSet<MidiEvent> events) {
        List<Note> vbNotes = new ArrayList<>();
        Queue<NoteOn> noteOnQueue = new LinkedBlockingQueue<>();
        for (MidiEvent event : events) {
            if (event instanceof NoteOn || event instanceof NoteOff) {
                if (isEndMarkerNote(event)) {
                    NoteSign noteSign = NoteSign.fromMidiNumber(extractNoteValue(event));
                    if (noteSign != NoteSign.NULL_VALUE) {
                        NoteOn noteOn = noteOnQueue.poll();
                        if (noteOn != null) {
                            long start = noteOn.getTick();
                            long end = event.getTick();
                            vbNotes.add(
                                    new Note(noteSign, start, end - start));
                        }
                    }
                } else {
                    noteOnQueue.offer((NoteOn) event);
                }
            }
        }
        return vbNotes;
    }

    public static boolean isEndMarkerNote(MidiEvent event) {
        if (event instanceof NoteOff) {
            return true;
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getVelocity() == 0;
        } else {
            return false;
        }
    }

    public static Integer extractNoteValue(MidiEvent event) {
        if (event instanceof NoteOff) {
            return ((NoteOff) event).getNoteValue();
        } else if (event instanceof NoteOn) {
            return ((NoteOn) event).getNoteValue();
        } else {
            return null;
        }
    }

    public static void playTrack(String path) throws Exception {
        File midiFile = new File(path);
        Sequencer sequencer = MidiSystem.getSequencer();
        sequencer.setSequence(MidiSystem.getSequence(midiFile));
        sequencer.open();
        sequencer.start();
        while (true) {
            if (sequencer.isRunning()) {
                System.out.println("isPLaying");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignore) {
                    break;
                }
            } else {
                break;
            }
        }
        sequencer.stop();
        sequencer.close();

    }

}

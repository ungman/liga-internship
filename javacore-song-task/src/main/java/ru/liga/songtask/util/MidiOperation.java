package ru.liga.songtask.util;

import com.leff.midi.MidiFile;
import com.leff.midi.event.meta.Tempo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

abstract class MidiOperation {

    private List<String> argumentList = new ArrayList<>();
    private MidiFile midiFile;
    private Tempo tempo;


    MidiOperation(String path) {
        setMidiFile(path);
        setTempo();
    }

    public List<String> getArgumentList() {
        return argumentList;
    }

    public void setArgumentList(List<String> argumentList) {
        this.argumentList = argumentList;
    }

    private void setTempo() {

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
        try {
            midiFile = new MidiFile(new FileInputStream(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    abstract public void makeOperation();

    public Tempo getTempo() {
        return tempo;
    }
}

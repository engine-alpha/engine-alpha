package ea.internal.sound;

import ea.DateiManager;
import ea.internal.util.Logger;

import javax.sound.midi.*;
import java.io.File;
import java.io.IOException;

/**
 * @author Niklas Keller <me@kelunik.com>
 */
public class MidiSound extends BasicSound {
	private static Synthesizer synth;

	static {
		try {
			synth = MidiSystem.getSynthesizer();
		} catch (Exception e) {
			Logger.error("MIDI wird von diesem System nicht unterstützt!");
		}
	}

	/**
	 * Der Sequencer bei einer MIDI-Datei.<br />
	 * Bei einem Sound, der nicht vom Typ ".midi" ist, hat diese Referenz logischerweise keine Bedeutung.
	 */
	private Sequencer sequencer;

	public MidiSound(String path, int bpm) {
		this.sequencer = sequencerLaden(path, bpm);
	}

	/**
	 * Laedt den zum Abspielen einer MIDI-Datei fertigen Sequencer.
	 *
	 * @param verzeichnis
	 *            Das Verzeichnis der MIDI-Datei
	 * @param tempo
	 *            Das Endtempo der Datei in BPM
	 */
	private Sequencer sequencerLaden(String verzeichnis, int tempo) {
		Sequencer sequencer = null;
		try {
			// Sequencer und Synthesizer initialisieren
			sequencer = MidiSystem.getSequencer();
			Transmitter trans = sequencer.getTransmitter();
			Receiver rcvr = synth.getReceiver();
			// Beide öffnen und verbinden
			sequencer.open();
			synth.open();
			trans.setReceiver(rcvr);
			// Sequence lesen
			Sequence seq = MidiSystem.getSequence(new File(verzeichnis));
			sequencer.setSequence(seq);
			sequencer.setTempoInBPM(tempo);
		} catch (MidiUnavailableException e) {
			System.err.println("Achtung! Auf diesem PC ist MIDI nicht vorhanden!");
			sequencer = null;
		} catch (InvalidMidiDataException e) {
			System.err.println("Achtung! Die MIDI-Daten der Datei sind nicht einlesbar und beschaedigt!");
			sequencer = null;
		} catch (IOException e) {
			System.out.println("Fehler beim Einlesen der Datei! Existiert sie wirklich? Ist der Name korrekt eingegeben?" + DateiManager.bruch +
					verzeichnis);
			sequencer = null;
		}
		return sequencer;
	}

	public void schliessen() {

	}
}

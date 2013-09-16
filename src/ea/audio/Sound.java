/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea.audio;

import ea.io.DateiManager;

import javax.sound.sampled.*;
import javax.sound.midi.*;
import java.io.*;
/**
 * Sound beschreibt eine Sounddatei.<br />
 * Diese kann unterschiedlichen Typs sein. Bis jetzt werden folgende Formate unterstuetzt:<br /><br />
 * -MIDI-Format (.midi-Dateien): Musikalisches Notenpapier fuer einen MIDI-Synthesizer<br />
 * -WAVE-Sounds (.wav-Dateien): Gesampleter Sound.<br /><br />
 * -MP3-Sounds (.mp3-Dateien): Klassisches AudioFormat. Da JAVA allerdings dies nicht Standartmaessig unterstuetzt werden MP3-Dateien nur 
 * <b>in einer einfachen aber Speicherlastigen raw-Play-Methode wiedergegeben!!!!!! Diese empfiehlt sich besonders dann nicht, wenn der Sound in einer Wiederholungsschleife
 * abgespielt werden soll, da laengere Wartezeiten zwischen Ende des Loops und anfangs des neuen bestehen.</b>.<br /><br /><br />
 * 
 * Als beste Moeglichkeit fuer Sound in der EngineAlpha ist das .wav-Format zu empfehlen
 * 
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Sound
{
    /**Die Konstante fuer nicht abspielbare Dateien*/
    public static final int XXX = -1;
    
    /**Die Konstante fuer MIDI-Dateien*/
    public static final int MIDI = 0;
    
    /**Die Konstante fuer WAV-Dateien*/
    public static final int WAV = 1;
    
    /**Die Konstante fuer MP3-Dateien*/
    public static final int MP3 = 2;

    /**Konstante fuer geschlossene Dateien*/
    public static final int GESCHLOSSEN = -2;
    
    /**Der Synthesizer fuer MIDIs*/
    public static Synthesizer synth;
    
    static {
        try {
            synth = MidiSystem.getSynthesizer();
        }catch(Exception e) {
            System.err.println("MIDI wird von diesem PC nicht unterstuetzt!");
        }
    }
    
    /**
     * Das verzeichnis der Sound-Datei.
     */
    private String verzeichnis;
    
    /**
     * Der Typ des Sounds.<br />
     * Gibt an, ob der Sound MIDI, WAV, oder nicht abspielbar ist.
     */
    private int typ;
    
    /**
     * Der Soundclip bei einer Sampled-Datei.<br />
     * Bei einem Sound, der nicht vom Typ ".wav" ist, hat diese Referenz logischerweise keine Bedeutung.
     */
    private Clip clip;
    
    /**
     * Der Sequencer bei einer MIDI-Datei.<br />
     * Bei einem Sound, der nicht vom Typ ".midi" ist, hat diese Referenz logischerweise keine Bedeutung.
     */
    private Sequencer sequencer;
    
    /**
     * Das Audio-Format, das als mp3-Datei benutzt wird
     */
    private AudioFormat format;
    
    /**
     * Konstruktor f�r Objekte der Klasse Sound
     * @param   verzeichnis Das Verzeichnis der Sound-Datei. Mit Endung angeben! (z.B. "meinSound.wav")
     * @param   tempoBPM    Das Abspieltempo in Beats per Minute; nur fuer MIDI-Dateien relevant.
     */
    public Sound(String verzeichnis, int tempoBPM){
        if(!verzeichnis.contains(".")) {
            System.err.println("Das eingegebene Verzeichnis hat keine Dateiendung!");
            return;
        }
        if(verzeichnis.endsWith(".mid")) {
            typ = MIDI;
            sequencer = sequencerLaden(verzeichnis, tempoBPM);
        } else if(verzeichnis.endsWith(".wav")) {
            typ = WAV;
            clip = clipHolen(verzeichnis);
            if(clip == null){
                typ = -1;
            }
        } else if(verzeichnis.endsWith(".mp3")) {
            typ = MP3;
            format = formatHolen(verzeichnis);
        } else {
            System.err.println("Das angegebene Verzeichnis wird in der Engine Alpha nicht als Sound unterstuetzt. (Moeglich: .mid/.wav/.mp3");
            typ = -1;
        }
        this.verzeichnis = verzeichnis;
    }
    
    /**
     * Standartisierter Konstruktor. Dieser hier ist voll nutzwuerdig, ausser bei MIDI-Dateien!<br />
     * Hierfuer bietet sich eher der alterntive Konstruktor an!
     * @param   verzeichnis Das Verzeichnis der Sound-Datei. Mit Endung angeben! (z.B. "meinSound.wav")
     * @see Sound(String, int)
     * 
     */
    public Sound(String verzeichnis) {
        this(verzeichnis, 120);
    }
    
    /**
     * Schliesst den Sound.<br />
     * Diese Methode sollte ausgefuehrt werden, sobald der Sound nicht mehr gebraucht wird,
     * <b>um den vollen Speicher der geoeffneten Datei wieder frei zu geben</b>.
     */
    public void schliessen() {
        switch(typ) {
            case MIDI:
                sequencer.close();
                break;
            case WAV:
                clip.close();
                break;
            default:
                System.err.println("Der Sound ist fehlerhaft, er konnte weder geoeffnet noch jetzt geschlossen werden!");
                break;
        }
        this.typ = GESCHLOSSEN;
    }
    
    /**
     * Methode zum Holen eines WAV-Clips.
     * @param   verzeichnis Das Verzeichnis der Datei
     * @return  Der Clip. Ist bei Fehlkern null
     */
    private Clip clipHolen(String verzeichnis) {
        Clip clip = null;
        try {
            //AudioInputStream Oeffnen
            AudioInputStream ais = AudioSystem.getAudioInputStream(
                new File(verzeichnis)
            );
            AudioFormat format = ais.getFormat();
            //ALAW/ULAW samples in PCM konvertieren
            if ((format.getEncoding() == AudioFormat.Encoding.ULAW) || (format.getEncoding() == AudioFormat.Encoding.ALAW)) {
                AudioFormat tmp = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    format.getSampleRate(),
                    format.getSampleSizeInBits() * 2,
                    format.getChannels(),
                    format.getFrameSize() * 2,
                    format.getFrameRate(),
                    true
                );
                ais = AudioSystem.getAudioInputStream(tmp, ais);
                format = tmp;
            }
            //Clip erzeugen und Oeffnen
            DataLine.Info info = new DataLine.Info(
                Clip.class,
                format,
                ((int) ais.getFrameLength() * format.getFrameSize())
            );
            clip = (Clip)AudioSystem.getLine(info); 
            clip.open(ais);
        } catch(UnsupportedAudioFileException e) {
            System.err.println("Dies haette nicht passieren duerfen. Das MIDI-File wird nicht unterstuetzt.");
            clip = null;
        } catch(LineUnavailableException e) {
            e.printStackTrace();
            clip = null;
        } catch(IOException e) {
            System.err.println("Fehler beim Lesen der Datei! Existiert das Verzeichnis wirklich?" + DateiManager.bruch +
                    verzeichnis);
            clip = null;
        }
        return clip;
    }
    
    /**
     * Laedt den zum Abspielen einer MIDI-Datei fertigen Sequencer.
     * @param   verzeichnis Das Verzeichnis der MIDI-Datei
     * @param   tempo   Das Endtempo der Datei in BPM
     */
    private Sequencer sequencerLaden(String verzeichnis, int tempo) {
        Sequencer sequencer = null;
        try {
            //Sequencer und Synthesizer initialisieren
            sequencer = MidiSystem.getSequencer();
            Transmitter trans = sequencer.getTransmitter();
            Receiver rcvr = synth.getReceiver();
            //Beide �ffnen und verbinden
            sequencer.open();
            synth.open();
            trans.setReceiver(rcvr);
            //Sequence lesen
            Sequence seq = MidiSystem.getSequence(new File(verzeichnis));
            sequencer.setSequence(seq);
            sequencer.setTempoInBPM(tempo);
        }catch(MidiUnavailableException e) {
            System.err.println("Achtung! Auf diesem PC ist MIDI nicht vorhanden!");
            sequencer = null;
        }catch(InvalidMidiDataException e) {
            System.err.println("Achtung! Die MIDI-Daten der Datei sind nicht einlesbar und beschaedigt!");
            sequencer = null;
        }catch(IOException e) {
            System.out.println("Fehler beim Einlesen der Datei! Existiert sie wirklich? Ist der Name korrekt eingegeben?" + DateiManager.bruch +
                    verzeichnis);
            sequencer = null;
        }
        return sequencer;
    }
    
    /**
     * Methode zum generieren des codierten Audioformats bei einer mp3-Datei
     * @param   verzeichnis Das Verzeichnis der mp3-Datei
     */
    private AudioFormat formatHolen(String verzeichnis) {
        AudioFormat ret = null;
        try {
        AudioInputStream in= AudioSystem.getAudioInputStream(new File(verzeichnis));
        AudioFormat baseFormat = in.getFormat();
        ret = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
        baseFormat.getSampleRate(),
                                                                                  16,
                                                                                  baseFormat.getChannels(),
                                                                                  baseFormat.getChannels() * 2,
                                                                                  baseFormat.getSampleRate(),
                                                                                  false);
        } catch(UnsupportedAudioFileException e) {
            System.err.println("Interner Fehler das Format innerhalb der .mp3-Datei wird nicht unterstuetzt. Es sollte zu einer .wav-Datei konvertiert werden.");
        } catch(IOException e) {
            System.err.println("Fehler beim Lesen der Datei. Ist der Name/Verzeichnis korrekt?");
        }
        return ret;
    }
    
    /**
     * Erstellt ein neues InputStream-Objekt, das fuer das Wiedergeben einer mp3-Datei zustaendig ist.
     * @return  Der AudioInputStream fuer diese Datei
     */
    public AudioInputStream stream() {
        AudioInputStream ret = null;
        try {
            AudioInputStream in= AudioSystem.getAudioInputStream(new File(this.verzeichnis));

            AudioFormat baseFormat = in.getFormat();
            AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
            baseFormat.getSampleRate(),
                                                                                  16,
                                                                                  baseFormat.getChannels(),
                                                                                  baseFormat.getChannels() * 2,
                                                                                  baseFormat.getSampleRate(),
                                                                                  false);
            ret = AudioSystem.getAudioInputStream(decodedFormat, in);
        } catch(UnsupportedAudioFileException e) {
            System.err.println("Dieses Audioformati wird bei mp3 so nicht unterstuetzt. Es bietet sich an, in .wav zu konvertieren");
        } catch(IOException e) {
            System.err.println("Fehler beim Lesen der Datei. Ist sie dort wirklich voll vorhanden?");
        }
        return ret;
    }

    /**
     * Gibt den Typ des Sound-Objekts aus.<br />
     * Diese Methode ist im Grunde nur relevant fuer die Maschine intern. Zum abspielen muss man hier nichts
     * beachten.
     * @return Der Typ des Sound-Objekts
     */
    public int typ() {
        return typ;
    }
    
    /**
     * Gibt den Clip der wave-Datei zurueck.<br />
     * Diese Methode wird innerhalb der Maschine zum abspielen der Sounds ausgefuehrt.
     */
    public Clip clip() {
        return clip;
    }
    
    /**
     * Gibt den Sequencer der midi-Datei zurueck.<br />
     * Diese Methode wird innerhalb der Maschine zum abspielen der Sounds ausgefuehrt.
     */
    public Sequencer sequencer() {
        return sequencer;
    }
    
    /**
     * Gibt das Format der mp3-Datei zurueck.<br />
     * Diese Methode wird innerhalb der Maschine zum abspielen der Sounds ausgefuehrt.
     */
    public AudioFormat format() {
        return format;
    }
    
    /**
     * Errechnet die SourceDataLine fertig zum Abspielen der mp3-Dateien.
     */
    public SourceDataLine getLine() {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        try {
            res = (SourceDataLine) AudioSystem.getLine(info);
            res.open(format);
        } catch(LineUnavailableException e) {
            System.err.println("Die DataLine zum mp3-Abspielen ist nicht zugreifbar. Es empfiehlt sich auf .wav umzusteigen.");
        }
        return res;
    }
    
    /**
     * Zeigt ob dieses SoundObjekt abspielbar ist.<br />
     * Diese Methode wird z.B. vor dem Abspielen verwendet, um zu testen, ob das Argument auch
     * tatsaechlich abgespielt werden kann.
     * @return  <code>true</code>, wenn die Datei abspielbar ist.
     */
    public boolean funktioniert() {
        return (typ != -1);
    }
    
    /**
     * Die Destruktor-Methode.<br />
     * In ihr werden die Ressourcen des Clips wieder freigegeben, sofern nicht schon geschehen!<br />
     * Dies ist wichtig, da bei Nichtgeschehen, die Speicherressourcen offen bleiben und den Speicher belasten.<br />
     * <b>Dennoch:  Die Ressourcen sollten im Quelltext ueber die Methode <code>schliessen()</code> freigegeben werden!</b> 
     * Dies garantiert Kontrolle ueber die eigenen Soundeinstellungen.
     * @see schliessen()
     */
    @Override
    protected void finalize() {
        if(typ != GESCHLOSSEN) {
            switch(typ) {
                case MIDI:
                    sequencer.close();
                    break;
                case WAV:
                    clip.close();
                    break;
            }
        }
        try {
            super.finalize();
        } catch(Throwable e) {
            e.printStackTrace();
        }
    }
}

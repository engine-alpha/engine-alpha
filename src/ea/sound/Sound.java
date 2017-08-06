/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2017 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea.sound;

import ea.internal.ano.API;
import ea.internal.io.ResourceLoader;
import ea.internal.util.Logger;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Diese Klasse dient zum Abspielen kurzer Sounds.
 *
 * @see Music Zum Abspielen längerer Sounds wie z.B. Hintergrundmusik.
 */
@API
public class Sound {
    private static final int BUFFER_SIZE = 8192;
    private static final int MAX_CONCURRENT_PLAYS = 32;
    private static Executor executor;

    static {
        executor = Executors.newCachedThreadPool();
    }

    /**
     * Komplette Sounddaten.
     */
    private final byte[] data;

    /**
     * Anzahl der aktuellen Abspiel-Threads dieses Sounds.
     */
    private int currentPlaybackCount = 0;

    /**
     * Erstellt ein neues Soundobjekt. Dieses kann den gleichen Sound mehrmals abpsielen.
     *
     * @param filename Dateiname.
     */
    @API
    public Sound(String filename) {
        try {
            this.data = ResourceLoader.load(filename);
        } catch (IOException e) {
            throw new RuntimeException("Sound-Datei wurde nicht gefunden: " + filename);
        }
    }

    /**
     * Spielt den Sound mit normaler Lautstärke ab.
     *
     * @see #play(float)
     * @see #play(float, float)
     */
    @API
    public void play() {
        this.play(1);
    }

    /**
     * Spielt den Sound mit der gegebenen Lautstärke ab.
     *
     * @param volume Wert zwischen 0 (leise) und 1 (laut).
     *
     * @see #play()
     * @see #play(float, float)
     */
    @API
    public void play(float volume) {
        this.play(volume, 0);
    }

    /**
     * Spielt den Sound mit der gegebenen Lautstärke und der gegebenen Balance ab.
     *
     * Spielt den Sound mehrfach zeitgleich ab, wenn diese Methode mehrmals aufgerufen wird, aber
     * maximal {@link #MAX_CONCURRENT_PLAYS} mal gleichzeitig.
     *
     * @param volume Wert zwischen 0 (leise) und 1 (laut).
     * @param balance Wert zwischen -1 (nur links) und 1 (nur rechts).
     *
     * @see #play()
     * @see #play(float)
     */
    @API
    public void play(float volume, float balance) {
        if (currentPlaybackCount > MAX_CONCURRENT_PLAYS) {
            return;
        }

        executor.execute(() -> {
            currentPlaybackCount++;

            try {
                InputStream is = new ByteArrayInputStream(data);
                AudioInputStream ais = AudioSystem.getAudioInputStream(is);
                AudioFormat format = ais.getFormat();

                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), format.getSampleSizeInBits(), 2, 4, format.getFrameRate(), false);
                AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, ais);

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

                line.open(decodedFormat, BUFFER_SIZE * 4);

                ((FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN)).setValue(20 * (float) Math.log10(volume));
                ((FloatControl) line.getControl(FloatControl.Type.BALANCE)).setValue(balance);

                line.start();

                int num = 0;
                byte[] buffer = new byte[BUFFER_SIZE];

                while (num != -1) {
                    try {
                        num = decodedStream.read(buffer, 0, buffer.length);
                    } catch (IOException e) {
                        line.drain();
                        line.close();

                        return;
                    }

                    if (num >= 0) {
                        line.write(buffer, 0, num);
                    }
                }

                line.drain();
                line.close();
            } catch (LineUnavailableException | IOException e) {
                // ignore and skip sound
            } catch (UnsupportedAudioFileException e) {
                Logger.error("Sound", "Sound-Format wird nicht unterstützt: " + e.getMessage());
            } finally {
                currentPlaybackCount--;
            }
        });
    }
}
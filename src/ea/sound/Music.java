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

import ea.internal.annotations.API;
import ea.internal.io.ResourceLoader;
import ea.internal.util.Logger;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Diese Klasse dient zum Abspielen längerer Sounds wie (Hintergrund-)Musik.
 *
 * @see Sound Zum Abspielen kurzer Sounds wie z.B. Kollisionstöne.
 */
@API
public class Music {
    private static final int BUFFER_SIZE = 8192;
    private static Executor executor;

    static {
        executor = Executors.newCachedThreadPool();
    }

    /**
     * Dateiname.
     */
    private final String filename;

    /**
     * Lautstärke von 0 (leise) bis 1 (laut).
     */
    private float volume;

    /**
     * Balance zwischen -1 (nur links) und 1 (nur rechts).
     */
    private float balance;

    /**
     * Playback, falls aktuell abgespielt wird, sonst {@code null}.
     */
    private Playback playback;

    /**
     * Erstellt eine neue Instanz. Lautstärke ist per Default auf 1 und die Balance auf 0.
     *
     * @param filename Dateiname.
     */
    @API
    public Music(String filename) {
        this.filename = filename;
        this.volume = 1;
        this.balance = 0;
    }

    /**
     * Startet ein einmaliges Abspielen. Wenn bereits abgespielt wird, wird die bisherige Wiedergabe
     * abgebrochen.
     *
     * @see #loop()
     */
    @API
    public synchronized void play() {
        this.start(PlaybackType.ONCE);
    }

    /**
     * Startet ein wiederholendes Abspielen. Wenn bereits abgespielt wird, wird die bisherige
     * Wiedergabe abgebrochen.
     *
     * @see #play()
     */
    @API
    public synchronized void loop() {
        this.start(PlaybackType.LOOP);
    }

    /**
     * Interne Routine um das Abspielen zu starten.
     *
     * @param type Wiederholendes oder einmaliges Abspielen?
     */
    private synchronized void start(PlaybackType type) {
        if (playback != null) {
            this.stop();
        }

        playback = new Playback(type);
        executor.execute(playback);
    }

    /**
     * Stoppt die aktuelle Wiedergabe, falls gerade abgespielt wird. Tut sonst nichts.
     */
    @API
    public synchronized void stop() {
        if (playback != null) {
            playback.dispose();
        }
    }

    /**
     * Pausiert die aktuelle Wiedergabe oder tut nichts, wenn gerade nicht abgespielt wird.
     * @see #resume()
     */
    @API
    public synchronized void pause() {
        if (playback != null) {
            playback.pause();
        }
    }

    /**
     * Startet die aktuelle Wiedergabe vectorFromThisTo einem {@link #pause()}. Tut nichts, wenn nicht
     * abgespielt wird.
     * @see #pause()
     */
    @API
    public synchronized void resume() {
        if (playback != null) {
            playback.resume();
        }
    }

    /**
     * Setzt die Lautstärke. Wenn gerade abgespielt wird, wird die Lautstärke direkt übernommen,
     * sonst wird sie beim Starten der nächsten Wiedergabe gesetzt. Diese Einstellung ist
     * permanent, bis sie zum nächsten mal geändert wird.
     *
     * @param volume Lautstärke zwischen 0 (leise) und 1 (laut).
     */
    @API
    public synchronized void setVolume(float volume) {
        this.volume = volume;

            if (playback != null) {
            playback.setVolume(volume);
        }
    }

    /**
     * Setzt die Balance. Wenn gerade abgespielt wird, wird die Balance direkt übernommen,
     * sonst wird sie beim Starten der nächsten Wiedergabe gesetzt. Diese Einstellung ist
     * permanent, bis sie zum nächsten mal geändert wird.
     *
     * @param balance Balance zwischen -1 (nur links) nur 1 (nur rechts).
     */
    @API
    public synchronized void setBalance(float balance) {
        this.balance = balance;

        if (playback != null) {
            playback.setBalance(balance);
        }
    }

    /**
     * Playback-Type.
     */
    private enum PlaybackType {
        ONCE, LOOP
    }

    /**
     * Playback wird in einem eigenen Thread ausgeführt.
     */
    private class Playback implements Runnable {
        private SourceDataLine line;
        private PlaybackType type;
        private boolean paused;

        /**
         * Erstellt ein neues Objekt.
         *
         * @param type Type der Wiedergabe.
         */
        public Playback(PlaybackType type) {
            assert type != null;

            this.type = type;
        }

        /**
         * Eigentliches Abspielen.
         */
        public void run() {
            try {
                InputStream is = new BufferedInputStream(ResourceLoader.loadAsStream(filename));
                AudioInputStream ais = AudioSystem.getAudioInputStream(is);
                AudioFormat format = ais.getFormat();

                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, format.getSampleRate(), format.getSampleSizeInBits(), 2, 4, format.getFrameRate(), false);
                AudioInputStream decodedStream = AudioSystem.getAudioInputStream(decodedFormat, ais);

                DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
                SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);

                line.open(decodedFormat, BUFFER_SIZE * 4);
                line.start();

                this.paused = false;
                this.line = line;

                this.setVolume(volume);
                this.setBalance(balance);

                while (true) {
                    if (decodedStream.markSupported()) {
                        decodedStream.mark(decodedStream.available());
                    }

                    int num = 0;
                    byte[] buffer = new byte[BUFFER_SIZE];

                    while (num != -1) {
                        try {
                            num = decodedStream.read(buffer, 0, buffer.length);
                        } catch (IOException e) {
                            line.drain();

                            return;
                        }

                        if (num >= 0) {
                            synchronized (this) {
                                while (paused) {
                                    this.wait();
                                }
                            }

                            line.write(buffer, 0, num);
                        }
                    }

                    if (type == PlaybackType.ONCE) {
                        line.stop();
                        line.drain();

                        return;
                    }

                    if (decodedStream.markSupported()) {
                        decodedStream.reset();
                    } else {
                        // MP3 playback currently doesn't support mark. We simply use a new stream
                        // here, but this will result in a non-gapless playback. :-(
                        ResourceLoader.loadAsStream(filename);
                        ais = AudioSystem.getAudioInputStream(is);
                        decodedStream = AudioSystem.getAudioInputStream(decodedFormat, ais);
                    }
                }
            } catch (LineUnavailableException | InterruptedException e) {
                // ignore and skip sound
            } catch (IOException e) {
                Logger.error("Sound", "Sound konnte nicht geladen werden: " + e.getMessage());
            } catch (UnsupportedAudioFileException e) {
                Logger.error("Sound", "Sound-Format wird nicht unterstützt: " + e.getMessage());
            } finally {
                this.dispose();
            }
        }

        /**
         * Schließt die Line.
         */
        private synchronized void dispose() {
            if (line == null) {
                return;
            }

            line.stop();
            line.close();

            line = null;
        }

        /**
         * Pausiert die Wiedergabe.
         */
        public synchronized void pause() {
            this.paused = true;

            if (this.line != null) {
                this.line.stop();
            }
        }

        /**
         * Setzt die Wiedergabe fort.
         */
        public synchronized void resume() {
            this.paused = false;

            if (this.line != null) {
                this.line.start();
            }

            this.notifyAll();
        }

        /**
         * Setzt die Lautstärke.
         *
         * @param volume Wert zwischen 0 (leise) und 1 (laut).
         */
        public void setVolume(float volume) {
            if (line == null) {
                return;
            }

            ((FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN)).setValue(20 * (float) Math.log10(volume));
        }

        /**
         * Setzt die Balance.
         *
         * @param balance Wert zwischen -1 (nur links) und 1 (nur rechts).
         */
        public void setBalance(float balance) {
            if (line == null) {
                return;
            }

            ((FloatControl) line.getControl(FloatControl.Type.BALANCE)).setValue(balance);
        }
    }
}
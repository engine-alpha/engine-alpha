/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
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

package ea;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Sound {
    private Clip clip;
    private AudioInputStream ais;

    public Sound(String datei) {
        try {
            byte[] data = loadFromStream(new FileInputStream(datei));
            ais = AudioSystem.getAudioInputStream(new ByteArrayInputStream(data));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] loadFromStream(InputStream is) {
        byte[] bytes;

        if (is == null) {
            return null;
        }

        try {
            bytes = new byte[is.available()];

            int off = 0;
            int n;

            while (off < bytes.length && (n = is.read(bytes, off, bytes.length - off)) >= 0) {
                off += n;
            }

            is.close();

            return bytes;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                // ignore here...
            }
        }
    }

    public void play() {
        try {
            ais.reset();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            openClip();
            clip.start();
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loop() {
        try {
            ais.reset();
        } catch (IOException e) {
            return;
        }

        try {
            openClip();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (LineUnavailableException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (clip == null) {
            return;
        }

        clip.stop();
    }

    private void openClip() throws LineUnavailableException, IOException {
        clip = AudioSystem.getClip();
        clip.open(ais);
        clip.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent event) {
                if (event.getType().equals(LineEvent.Type.STOP)) {
                    event.getLine().close();
                }
            }
        });
    }
}

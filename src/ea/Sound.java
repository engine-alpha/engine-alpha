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

import ea.internal.sound.SampledSound;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@API
public class Sound {
	private byte[] data;

	private SampledSound ss;

    @API
	public Sound (String datei) {
		try {
			data = loadFromStream(new FileInputStream(datei));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static byte[] loadFromStream (InputStream is) {
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
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

    @API
	public void play () {
		if (ss != null) {
			ss.stopSound();

			try {
				ss.join();
			} catch (Exception e) {
				e.printStackTrace();
			}

			ss = null;
		}

		if (data != null) {
			ss = new SampledSound(data, false);
			ss.start();
		}
	}

    @API
	public void loop () {
		if (ss != null) {
			ss.stopSound();

			try {
				ss.join();
			} catch (Exception e) {
				e.printStackTrace();
			}

			ss = null;
		}

		if (data != null) {
			ss = new SampledSound(data, true);
			ss.start();
		}
	}

	public void pause () {
		if (ss == null) {
			return;
		}

		ss.pauseSound(true);
        clip = null;
    }

    @API
    public void pause() {
        if (clip == null || paused) {
            return;
        }

        if (clip.isRunning()) {
            paused = true;
            clip.stop();
        }
    }

    @API
    public void unpause() {
        if (clip == null || !paused) {
            return;
        }

        paused = false;
        clip.start();
	}

	public void unpause () {
		if (ss == null) {
			return;
		}

		ss.pauseSound(false);
	}

	public void stop () {
		if (ss == null) {
			return;
		}

		ss.stopSound();
	}
}

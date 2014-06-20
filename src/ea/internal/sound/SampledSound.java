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

package ea.internal.sound;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * plays a sound<br />
 * <big>This is a internal class, do not use it in your projects!</big>
 *
 * @author Niklas Keller
 * @version v3.0
 * @since v3.0
 */
public class SampledSound extends Thread {
	private enum State {
		PLAYING, PAUSED, STOPPED
	}

	private State state;
	private byte[] data;
	private boolean loop;

	private AudioInputStream ais;
	private SourceDataLine line;

	/**
	 * @param data data from soundfile
	 * @param loop if true, the sound is looped, otherwise only played once
	 */
	public SampledSound(byte[] data, boolean loop) {
		this.setDaemon(true);
		this.init(data, loop);
	}

	private void init(byte[] data, boolean loop) {
		this.data = data;
		this.loop = loop;

		if (this.ais != null) {
			try {
				this.ais.close();
				this.ais = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		InputStream is = new ByteArrayInputStream(this.data.clone());

		try {
			this.ais = AudioSystem.getAudioInputStream(is);

			AudioInputStream tmp;
			AudioFormat baseFormat = this.ais.getFormat();
			AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(),
					16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			tmp = AudioSystem.getAudioInputStream(decodedFormat, this.ais);

			this.ais = tmp;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		if(startLine()) {
			super.start();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		playSound();

		this.state = State.STOPPED;
	}

	private boolean startLine() {
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, ais.getFormat());

		try {
			this.line = (SourceDataLine) AudioSystem.getLine(info);
			this.line.open(ais.getFormat());
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		line.start();

		return true;
	}

	private synchronized void playSound() {
		int num = 0;
		byte[] buffer = new byte[1024];

		try {
			while (num != -1) {
				if (this.state == State.PAUSED) {
					try {
						synchronized (this) {
							wait();
						}
					} catch (InterruptedException e) {
						this.state = State.STOPPED;
					}
				}

				if (this.state == State.STOPPED) {
					line.start();
					line.flush();
					break;
				}

				num = this.ais.read(buffer, 0, buffer.length);

				if (num >= 0) {
					line.write(buffer, 0, num);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ais.close();
				ais = null;
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (loop && this.state != State.STOPPED) {
				this.init(data, true);
				this.playSound();
			} else {
				line.drain();
				line.close();
			}
		}
	}

	/**
	 * pauses / unpauses this sound
	 *
	 * @param pause true to pause, false to unpause
	 */
	public void pauseSound(boolean pause) {
		this.state = pause ? State.PAUSED : State.PLAYING;

		synchronized (this) {
			if (!pause) {
				this.notify();
			}
		}
	}

	/**
	 * stops this sound
	 */
	public void stopSound() {
		this.state = State.STOPPED;
	}
}
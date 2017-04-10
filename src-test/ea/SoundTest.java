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

package ea;

import org.junit.Before;
import org.junit.Test;

public class SoundTest {
    private Sound sound;

    @Before
    public void setUp() {
        this.sound = new Sound("res/assets/sounds/glass.wav");
    }

    @Test
    public void play() throws InterruptedException {
        this.sound.play();
        Thread.sleep(2000);
        this.sound.play();
        this.sound.stop();
        Thread.sleep(1000);
    }

    @Test
    public void pause() throws InterruptedException {
        this.sound.play();
        this.sound.pause();
        this.sound.unpause();
        Thread.sleep(2000);
    }

    @Test
    public void loop() throws InterruptedException {
        this.sound.loop();
        Thread.sleep(5000);
    }
}

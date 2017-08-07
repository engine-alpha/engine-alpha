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

package ea.raum;

import ea.internal.ano.API;
import ea.internal.io.ImageLoader;
import ea.internal.phy.WorldHandler;
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

@API
public class Animation extends Raum {
    private Frame[] frames;

    private int width;
    private int height;

    private int currentTime;
    private int currentIndex;

    private WorldHandler worldHandler;

    private Animation(Frame[] frames) {
        if (frames.length < 1) {
            throw new RuntimeException("Eine Animation kann nicht mit einem leeren Frames-Array initialisiert werden.");
        }

        for (Frame frame : frames) {
            if (frame.getDuration() < 1) {
                throw new RuntimeException("Ein Frame muss mindestens eine Millisekunde lang sein.");
            }
        }

        this.frames = frames;

        this.width = frames[0].getImage().getWidth();
        this.height = frames[0].getImage().getHeight();

        this.currentTime = 0;
        this.currentIndex = 0;
    }

    @Override
    public void updateWorld(WorldHandler worldHandler) {
        super.updateWorld(worldHandler);

        this.worldHandler = worldHandler;
    }

    @Override
    public void render(Graphics2D g) {
        if (worldHandler == null) {
            throw new RuntimeException("Eine Animation kann nicht gezeichnet werden, bevor sie an der Wurzel angemeldet wurde.");
        }

        this.currentTime += worldHandler.getWorldThread().getMaster().getLastFrameTime();

        Frame currentFrame = this.frames[currentIndex];

        while (this.currentTime > currentFrame.getDuration()) {
            this.currentTime -= currentFrame.getDuration();
            this.currentIndex = (this.currentIndex + 1) % this.frames.length;
            currentFrame = this.frames[this.currentIndex];
        }

        g.drawImage(this.frames[currentIndex].getImage(), 0, 0, null);
    }

    @Override
    public Shape createShape(float pixelPerMeter) {
        return this.berechneBoxShape(pixelPerMeter, width, height);
    }

    @API
    public static Animation createFromSpritesheet(int frameDuration, String filepath, int x, int y) {
        if (frameDuration < 1) {
            throw new RuntimeException("Frame-Länge kann nicht kleiner als 1 sein.");
        }

        BufferedImage image = ImageLoader.load(filepath);

        if (image.getWidth() % x != 0) {
            throw new RuntimeException(String.format("Spritesheet hat nicht die richtigen Maße (Breite: %d) um es auf %d Elemente in x-Richtung aufzuteilen.", image.getWidth(), x));
        }

        if (image.getHeight() % y != 0) {
            throw new RuntimeException(String.format("Spritesheet hat nicht die richtigen Maße (Höhe: %d) um es auf %d Elemente in y-Richtung aufzuteilen.", image.getHeight(), y));
        }

        int width = image.getWidth() / x;
        int height = image.getHeight() / y;

        java.util.List<Frame> frames = new LinkedList<>();

        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                frames.add(new Frame(image.getSubimage(i * width, j * height, width, height), frameDuration));
            }
        }

        return new Animation(frames.toArray(new Frame[frames.size()]));
    }

    public static Animation createFromImages(int frameDuration, String... filepaths) {
        if (frameDuration < 1) {
            throw new RuntimeException("Frame-Länge kann nicht kleiner als 1 sein.");
        }

        java.util.List<Frame> frames = new LinkedList<>();

        for (String filepath : filepaths) {
            frames.add(new Frame(ImageLoader.load(filepath), frameDuration));
        }

        return new Animation(frames.toArray(new Frame[frames.size()]));
    }

    private static class Frame {
        private BufferedImage image;
        private int duration;

        public Frame(BufferedImage image, int duration) {
            this.image = image;
            this.duration = duration;
        }

        public BufferedImage getImage() {
            return image;
        }

        public int getDuration() {
            return duration;
        }
    }
}

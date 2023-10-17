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

package ea.actor;

import ea.FrameUpdateListener;
import ea.event.EventListeners;
import ea.internal.FixtureBuilder;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.graphics.AnimationFrame;
import ea.internal.io.ImageLoader;
import ea.internal.io.ResourceLoader;
import ea.internal.util.GifDecoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * Eine Animation ist ein Actor-Objekt, das aus mehreren
 * <a href="https://de.wikipedia.org/wiki/Einzelbild_(Film)">Frames</a> besteht. Frames können auf verschiedene
 * Arten aus Bilddateien eingeladen werden:
 * <ul>
 * <li>Animierte GIFs</li>
 * <li><a href="https://de.wikipedia.org/wiki/Sprite_(Computergrafik)">Spritesheets</a></li>
 * <li>Einzelne Bilddateien</li>
 * </ul>
 *
 * @author Michael Andonie
 */
@API
public class Animation extends Actor implements FrameUpdateListener {

    private final AnimationFrame[] frames;

    private final float width;
    private final float height;

    private transient float currentTime;
    private transient int currentIndex;

    /**
     * Liste aller Runnable, die beim Abschließen des Loops ausgeführt werden.
     */
    private final EventListeners<Runnable> onCompleteListeners = new EventListeners<>();

    private Animation(AnimationFrame[] frames, float width, float height) {
        super(() -> {
            if (frames.length < 1) {
                throw new RuntimeException("Eine Animation kann nicht mit einem leeren Frames-Array initialisiert werden.");
            }

            return FixtureBuilder.createSimpleRectangularFixture(width, height);
        });

        for (AnimationFrame frame : frames) {
            if (frame.getDuration() <= 0) {
                throw new RuntimeException("Ein Frame muss länger als 0 Sekunden sein.");
            }
        }

        this.frames = frames.clone();
        this.width = width;
        this.height = height;

        this.currentTime = 0;
        this.currentIndex = 0;
    }

    /**
     * Copy-Konstruktor, damit Vererbung genutzt werden kann.
     *
     * @param animation Animation.
     */
    public Animation(Animation animation) {
        this(animation.frames, animation.width, animation.height);

        animation.onCompleteListeners.invoke(this::addOnCompleteListener);
    }

    /**
     * Gibt die Frames dieser Animation aus.
     *
     * @return Die Frames dieser Animation.
     */
    @Internal
    public AnimationFrame[] getFrames() {
        return frames.clone();
    }

    /**
     * Gibt die Breite der Animation in Metern aus.
     *
     * @return Die Breite der Animation in Pixel.
     * @see #getHeight()
     */
    @API
    public float getWidth() {
        return this.width;
    }

    /**
     * Gibt die Höhe der Animation in Metern aus.
     *
     * @return Die Höhe der Animation in Pixel
     * @see #getWidth()
     */
    @API
    public float getHeight() {
        return this.height;
    }

    /**
     * Fügt einen Listener hinzu. Die <code>run()</code>-Methode wird immer wieder ausgeführt, sobald der
     * <b>letzte Zustand der Animation abgeschlossen wurde</b>.
     *
     * @param listener Ein Runnable, dessen run-Methode ausgeführt werden soll, sobald die Animation abgeschlossen ist
     *                 (wird ausgeführt, bevor der Loop von Vorne beginnt).
     */
    @API
    public void addOnCompleteListener(Runnable listener) {
        onCompleteListeners.add(listener);
    }

    @Internal
    @Override
    public void onFrameUpdate(float deltaSeconds) {
        this.currentTime += deltaSeconds;

        AnimationFrame currentFrame = this.frames[currentIndex];

        while (this.currentTime > currentFrame.getDuration()) {
            this.currentTime -= currentFrame.getDuration();
            if (this.currentIndex + 1 == this.frames.length) {
                onCompleteListeners.invoke(Runnable::run);
                this.currentIndex = 0;
            } else {
                this.currentIndex += 1;
            }
        }
    }

    @Override
    public void render(Graphics2D g, float pixelPerMeter) {
        this.frames[currentIndex].render(g, width * pixelPerMeter, height * pixelPerMeter, false, false);
    }

    @API
    public static Animation createFromSpritesheet(float frameDuration, String filepath, int x, int y, float width, float height) {
        if (frameDuration <= 0) {
            throw new RuntimeException("Frame-Länge muss größer als 0 sein");
        }

        BufferedImage image = ImageLoader.load(filepath);

        if (image.getWidth() % x != 0) {
            throw new RuntimeException(String.format("Spritesheet hat nicht die richtigen Maße (Breite: %d) um es auf %d Elemente in getX-Richtung aufzuteilen.", image.getWidth(), x));
        }

        if (image.getHeight() % y != 0) {
            throw new RuntimeException(String.format("Spritesheet hat nicht die richtigen Maße (Höhe: %d) um es auf %d Elemente in getY-Richtung aufzuteilen.", image.getHeight(), y));
        }

        int imageWidth = image.getWidth() / x;
        int imageHeight = image.getHeight() / y;

        List<AnimationFrame> frames = new LinkedList<>();

        for (int j = 0; j < y; j++) {
            for (int i = 0; i < x; i++) {
                frames.add(new AnimationFrame(image.getSubimage(i * imageWidth, j * imageHeight, imageWidth, imageHeight), frameDuration));
            }
        }

        return new Animation(frames.toArray(new AnimationFrame[0]), width, height);
    }

    @API
    public static Animation createFromImages(float frameDuration, float width, float height, String... filepaths) {
        if (frameDuration <= 0) {
            throw new RuntimeException("Frame-Länge muss größer als 1 sein.");
        }

        Collection<AnimationFrame> frames = new LinkedList<>();

        for (String filepath : filepaths) {
            frames.add(new AnimationFrame(ImageLoader.load(filepath), frameDuration));
        }

        return new Animation(frames.toArray(new AnimationFrame[0]), width, height);
    }

    /**
     * Lädt alle Bilddateien mit einem bestimmten Präfix in einem bestimmten Verzeichnis in eine Animation.
     *
     * @param frameDuration Die Dauer (ms), die ein Frame aktiv bleibt.
     * @param directoryPath Der Pfad zum Verzeichnis, in dem die einzuladenden Bilder liegen.
     * @param prefix        Das Pfad-Präfix. Diese Funktion sucht <a>alle Dateien mit dem gegebenen Präfix</a> (im
     *                      angegebenen Ordner) und fügt sie in aufsteigender Reihenfolge der Animation hinzu.
     * @return Eine Animation aus allen Dateien, die mit dem Pfadpräfix beginnen.
     */
    @API
    public static Animation createFromImagesPrefix(float frameDuration, float width, float height, String directoryPath, String prefix) {
        // Liste mit den Pfaden aller qualifizierten Dateien
        ArrayList<String> allPaths = new ArrayList<>();

        File directory;
        try {
            directory = ResourceLoader.loadAsFile(directoryPath);
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Einladen des Verzeichnisses: " + e.getMessage());
        }
        if (!directory.isDirectory()) {
            throw new RuntimeException("Der angegebene Pfad war kein Verzeichnis: " + directoryPath);
        }

        File[] children = directory.listFiles();
        if (children != null) {
            for (File file : children) {
                if (!file.isDirectory() && file.getName().startsWith(prefix)) {
                    allPaths.add(file.getAbsolutePath());
                }
            }
        }

        allPaths.sort(Comparator.naturalOrder());

        if (allPaths.isEmpty()) {
            throw new RuntimeException("Konnte keine Bilder mit Präfix \"" + prefix + "\" im Verzeichnis \"" + directoryPath + "\" finden.");
        }

        return createFromImages(frameDuration, width, height, allPaths.toArray(new String[0]));
    }

    @API
    public static Animation createFromAnimatedGif(String filepath, float width, float height) {
        GifDecoder gifDecoder = new GifDecoder();
        gifDecoder.read(filepath);

        int frameCount = gifDecoder.getFrameCount();
        AnimationFrame[] frames = new AnimationFrame[frameCount];

        for (int i = 0; i < frameCount; i++) {
            BufferedImage frame = gifDecoder.getFrame(i);
            int durationInMillis = gifDecoder.getDelay(i);
            frames[i] = new AnimationFrame(frame, durationInMillis / 1000f);
        }

        return new Animation(frames, width, height);
    }
}

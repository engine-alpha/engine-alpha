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
import ea.internal.ano.API;
import ea.internal.io.ImageLoader;
import ea.internal.io.ResourceLoader;
import org.jbox2d.collision.shapes.Shape;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@API
public class Animation extends Actor implements FrameUpdateListener {

    private Frame[] frames;

    private final int width;
    private final int height;

    private int currentTime;
    private int currentIndex;

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

    /**
     * Gibt die Breite der Animation in Pixel aus.
     * @return  Die Breite der Animation in Pixel.
     * @see #getImageHeight()
     */
    @API
    public int getImageWidth() {
        return width;
    }

    /**
     * Gibt die Höhe der Animation in Pixel aus.
     * @return  Die Höhe der Animation in Pixel
     * @see #getImageWidth()
     */
    @API
    public int getImageHeight() {
        return height;
    }


    @Override
    public void onFrameUpdate(int frameDuration) {
        this.currentTime += frameDuration;

        Frame currentFrame = this.frames[currentIndex];

        while (this.currentTime > currentFrame.getDuration()) {
            this.currentTime -= currentFrame.getDuration();
            this.currentIndex = (this.currentIndex + 1) % this.frames.length;
            currentFrame = this.frames[this.currentIndex];
        }
    }

    @Override
    public void render(Graphics2D g) {
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
            throw new RuntimeException(String.format("Spritesheet hat nicht die richtigen Maße (Breite: %d) um es auf %d Elemente in getX-Richtung aufzuteilen.", image.getWidth(), x));
        }

        if (image.getHeight() % y != 0) {
            throw new RuntimeException(String.format("Spritesheet hat nicht die richtigen Maße (Höhe: %d) um es auf %d Elemente in getY-Richtung aufzuteilen.", image.getHeight(), y));
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

    @API
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

    public static Animation createFromAnimatedGif(int frameDuration, String filepath) {
        //Code happily adapted from StackExchange:
        //https://stackoverflow.com/questions/8933893/convert-each-animated-gif-frame-to-a-separate-bufferedimage?
        //utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
        try {
            String[] imageatt = new String[]{
                    "imageLeftPosition",
                    "imageTopPosition",
                    "imageWidth",
                    "imageHeight"
            };

            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            ImageInputStream ciis = ImageIO.createImageInputStream(ResourceLoader.loadAsStream(filepath));
            reader.setInput(ciis, false);

            int numImages = reader.getNumImages(true);
            BufferedImage master = null;

            Frame[] frames = new Frame[numImages];

            for (int i = 0; i < numImages; i++) {
                BufferedImage image = reader.read(i);
                IIOMetadata metadata = reader.getImageMetadata(i);

                Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
                NodeList children = tree.getChildNodes();

                for (int j = 0; j < children.getLength(); j++) {
                    Node nodeItem = children.item(j);

                    if(nodeItem.getNodeName().equals("ImageDescriptor")){
                        Map<String, Integer> imageAttr = new HashMap<>();

                        for (int k = 0; k < imageatt.length; k++) {
                            NamedNodeMap attr = nodeItem.getAttributes();
                            Node attnode = attr.getNamedItem(imageatt[k]);
                            imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
                        }
                        if(i==0){
                            master = new BufferedImage(imageAttr.get("imageWidth"), imageAttr.get("imageHeight"), BufferedImage.TYPE_INT_ARGB);
                        }
                        master.getGraphics().drawImage(image, imageAttr.get("imageLeftPosition"), imageAttr.get("imageTopPosition"), null);
                    }
                }
                frames[i] = new Frame(master, frameDuration);
            }
            return new Animation(frames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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

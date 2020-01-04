package ea.internal.io;

import ea.internal.util.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Diese IO Klasse schreibt Bilder.
 */
public class ImageWriter {

    public static void writeImage(BufferedImage image, String path) {
        String pathlowercase = path.toLowerCase();
        String formatname = null;
        if (pathlowercase.endsWith(".png")) {
            formatname = "png";
        } else if (pathlowercase.endsWith(".gif")) {
            formatname = "gif";
        } else if (pathlowercase.endsWith(".jpg")) {
            formatname = "jpg";
        } else {
            Logger.error("IO", "Nicht unterstütztes Format. Nur png, jpg, gif ist unterstützt");
            return;
        }
        try {
            ImageIO.write(image, formatname, new File(ResourceLoader.normalizePath(path)));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Fehler beim Schreiben des Bildes");
        }
    }
}

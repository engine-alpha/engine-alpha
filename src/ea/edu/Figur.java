package ea.edu;

import ea.actor.Actor;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;
import ea.internal.annotations.Internal;
import ea.internal.io.ImageLoader;
import ea.internal.io.ResourceLoader;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * EDU-Variante der {@link StatefulAnimation}.
 *
 * @author Michael Andonie
 */
public class Figur implements EduActor {

    public static final float DEFAULT_PIXEL_PRO_METER = 30f;
    public static final int DEFAULT_FRAME_DURATION = 250;

    private final StatefulAnimation statefulAnimation;
    private final float width, height;

    /**
     * Einführungskonstruktor. Erstellt eine Figur mit einem ersten Zustand.
     *
     * @param pixelProMeter Die Anzahl an Pixel in der Zieldatei, die einem Meter in der Engine entsprechen
     * @param zustandsName  Der Name für den ersten Zustand.
     * @param gifBildPfad   Pfad zu einem <b>GIF Bild</b>.
     */
    public Figur(float pixelProMeter, String zustandsName, String gifBildPfad) {
        assertPixelProMeter(pixelProMeter);
        statefulAnimation = new StatefulAnimation(width = ImageLoader.load(gifBildPfad).getWidth() / pixelProMeter, height = ImageLoader.load(gifBildPfad).getHeight() / pixelProMeter);

        zustandHinzufuegenVonGIF(zustandsName, gifBildPfad);
        eduSetup();
    }

    public Figur(String zustandsName, String gifBildPfad) {
        this(DEFAULT_PIXEL_PRO_METER, zustandsName, gifBildPfad);
    }

    /**
     * Einführungskonstruktor. Erstellt eine Figur mit einem ersten Zustand.
     *
     * @param pixelProMeter   Die Anzahl an Pixel in der Zieldatei, die einem Meter in der Engine entsprechen
     * @param zustandsName    Der Name für den ersten Zustand.
     * @param spriteSheetPfad Pfad zu einem <b>Spritesheet</b>.
     * @param anzahlX         Anzahl der Spritesheet-Kacheln in die X-Richtung.
     * @param anzahlY         Anzahl der Spritesheet-Kacheln in die Y-Richtung.
     */
    public Figur(float pixelProMeter, String zustandsName, String spriteSheetPfad, int anzahlX, int anzahlY) {
        assertPixelProMeter(pixelProMeter);
        statefulAnimation = new StatefulAnimation(width = (ImageLoader.load(spriteSheetPfad).getWidth() / anzahlX) / pixelProMeter, height = (ImageLoader.load(spriteSheetPfad).getHeight() / anzahlY) / pixelProMeter);
        zustandHinzufuegenVonSpritesheet(zustandsName, spriteSheetPfad, anzahlX, anzahlY);
        eduSetup();
    }

    public Figur(String zustandsName, String spriteSheetPfad, int anzahlX, int anzahlY) {
        this(DEFAULT_PIXEL_PRO_METER, zustandsName, spriteSheetPfad, anzahlX, anzahlY);
    }

    /**
     * Erstellt eine Figur mit einem ersten Zustand. Lädt dazu alle Bilder in einem Verzeichnis ein, die zu einem
     * bestimmten Präfix passen.
     *
     * @param pixelProMeter   Die Anzahl an Pixel in der Zieldatei, die einem Meter in der Engine entsprechen
     * @param zustandName     Name für den ersten Zustand.
     * @param verzeichnisPfad Pfad zum Verzeichnis, in dem alle einzuladenden Bilder liegen.
     * @param praefix         Das Präfix, das alle einzuladenden Bilder haben müssen.
     */
    public Figur(float pixelProMeter, String zustandName, String verzeichnisPfad, String praefix) {
        assertPixelProMeter(pixelProMeter);
        statefulAnimation = new StatefulAnimation(width = getWidthHeightFromPrefixed(verzeichnisPfad, praefix, true), height = getWidthHeightFromPrefixed(verzeichnisPfad, praefix, false));

        zustandHinzufuegenNachPraefix(zustandName, verzeichnisPfad, praefix);
        eduSetup();
    }

    private static void assertPixelProMeter(float pixelProMeter) {
        if (pixelProMeter <= 0) {
            throw new IllegalArgumentException("Die Pixel-Pro-Meter-Eingabe muss positiv sein. War " + pixelProMeter);
        }
    }

    private static final int getWidthHeightFromPrefixed(String directoryPath, String prefix, boolean width) {
        //Liste mit den Pfaden aller qualifizierten Dateien
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
        File[] childs = directory.listFiles();
        for (File file : childs) {
            if (!file.isDirectory() && file.getName().startsWith(prefix)) {
                return width ? ImageLoader.load(file.getAbsolutePath()).getWidth() : ImageLoader.load(file.getAbsolutePath()).getHeight();
            }
        }
        throw new RuntimeException("Es gab kein Bild im Verzeichnis " + directoryPath + " mit Präfix " + prefix);
    }

    public Figur(String zustandName, String verzeichnisPfad, String praefix) {
        this(1f, zustandName, verzeichnisPfad, praefix);
    }

    /**
     * Fügt einen Zustand mit GIF-Visualisierung ein.
     *
     * @param zustandsName Name des Zustands.
     * @param bildpfad     Pfad zum GIF, das zu diesem Zustand animiert wird.
     */
    public void zustandHinzufuegenVonGIF(String zustandsName, String bildpfad) {
        if (!bildpfad.toLowerCase().endsWith(".gif")) {
            throw new RuntimeException("Der agegebene Bildpfad muss eine GIF-Datei sein und auf \".gif\" enden. " + "Der angegebene Bildpfad war " + bildpfad);
        }
        Animation animation = Animation.createFromAnimatedGif(bildpfad, width, height);
        addState(zustandsName, animation);
    }

    /**
     * Fügt Zustand mit Spritesheet-Animation ein. Das Spritesheet muss <b>aus Kacheln gleicher Größe</b> bestehen.
     * "leere" Kacheln werden als leere Animationsframes mit einbezogen.
     *
     * @param zustandsName Der Name des Zustands.
     * @param bildpfad     Pfad zum Spritesheet.
     * @param anzahlX      Anzahl der Spritesheet-Kacheln in die X-Richtung.
     * @param anzahlY      Anzahl der Spritesheet-Kacheln in die Y-Richtung.
     */
    public void zustandHinzufuegenVonSpritesheet(String zustandsName, String bildpfad, int anzahlX, int anzahlY) {
        Animation animation = Animation.createFromSpritesheet(DEFAULT_FRAME_DURATION, bildpfad, anzahlX, anzahlY, width, height);
        addState(zustandsName, animation);
    }

    /**
     * Fügt einen Zustand über Einzelframes als Bilder ein.
     *
     * @param zustandsName Der Name des Zustands.
     * @param bildpfade    Die Pfade der Animationsframes in korrekter Reihenfolge.
     */
    public void zustandHinzufuegenVonBildern(String zustandsName, String... bildpfade) {
        Animation animation = Animation.createFromImages(DEFAULT_FRAME_DURATION, width, height, bildpfade);
        addState(zustandsName, animation);
    }

    /**
     * Fügt einen Zustand hinzu. Lädt dazu alle Bilder in einem Verzeichnis ein, die zu einem
     * bestimmten Präfix passen.
     *
     * @param zustandName     Name für den ersten Zustand.
     * @param verzeichnisPfad Pfad zum Verzeichnis, in dem alle einzuladenden Bilder liegen.
     * @param praefix         Das Präfix, das alle einzuladenden Bilder haben müssen.
     */
    public void zustandHinzufuegenNachPraefix(String zustandName, String verzeichnisPfad, String praefix) {
        Animation animation = Animation.createFromImagesPrefix(Spiel.getActiveScene(), DEFAULT_FRAME_DURATION, width, height, verzeichnisPfad, praefix);
        statefulAnimation.addState(zustandName, animation);
    }

    /**
     * Setzt den Zustand der Figur neu. In jedem Fall wird dabei der Animationsloop zurückgesetzt.
     *
     * @param zustandsName Der Name des zu setzenden Zustands. Unter diesem Namen muss ein Zustand in dieser
     *                     Figur existieren.
     */
    public void zustandSetzen(String zustandsName) {
        statefulAnimation.setState(zustandsName);
    }

    /**
     * Setzt einen automatischen Übergang von einem Zustand zu einem anderen.
     *
     * @param zustandVon  Der Von-Zustand.
     * @param zustandNach Der Zustand, zu dem die Figur automatisch übergehen soll, nachdem der Von-Zustand einmal
     *                    bis zum Ende durchgelaufen ist.
     */
    public void automatischenUebergangSetzen(String zustandVon, String zustandNach) {
        statefulAnimation.setStateTransition(zustandVon, zustandNach);
    }

    /**
     * Gibt den aktuellen Zustand aus.
     *
     * @return Der Name des aktuellen Zustands.
     */
    public String nenneAktuellenZustand() {
        return statefulAnimation.getCurrentState();
    }

    public void setzeAnimationsGeschwindigkeitVon(String zustandName, int frameDauerInMS) {
        statefulAnimation.setFrameDurationsOf(zustandName, frameDauerInMS);
    }

    @Internal
    @Override
    public Actor getActor() {
        return statefulAnimation;
    }

    private void addState(String stateName, Animation animation) {
        statefulAnimation.addState(stateName, animation);
    }

    /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     *
     * @param srcImg - source image to scale
     * @param scale  scaling factor
     *
     * @return - the new resized image
     */
    private static BufferedImage getScaledImage(BufferedImage srcImg, float scale) {
        int w = (int) (srcImg.getWidth() * scale), h = (int) (srcImg.getHeight() * scale);
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TRANSLUCENT);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }
}

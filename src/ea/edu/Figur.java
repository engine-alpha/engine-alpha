package ea.edu;

import ea.Camera;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;
import ea.internal.annotations.API;
import ea.internal.io.ImageLoader;
import ea.internal.io.ResourceLoader;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * EDU-Variante der {@link StatefulAnimation}.
 *
 * @author Michael Andonie
 */
@API
public class Figur extends EduActor<StatefulAnimation<String>> {
    private static final float DEFAULT_FRAME_DURATION = 0.25f;

    /**
     * Einführungskonstruktor. Erstellt eine Figur mit einem ersten Zustand.
     *
     * @param pixelProMeter Die Anzahl an Pixel in der Zieldatei, die einem Meter in der Engine entsprechen
     * @param zustandsName  Der Name für den ersten Zustand.
     * @param gifBildPfad   Pfad zu einem <b>GIF Bild</b>.
     */
    @API
    public Figur(float pixelProMeter, String zustandsName, String gifBildPfad) {
        super(new StatefulAnimation<>(ImageLoader.load(gifBildPfad).getWidth() / pixelProMeter, ImageLoader.load(gifBildPfad).getHeight() / pixelProMeter));

        fuegeZustandVonGifHinzu(zustandsName, gifBildPfad);
    }

    @API
    public Figur(String zustandsName, String gifBildPfad) {
        this(Camera.DEFAULT_ZOOM, zustandsName, gifBildPfad);
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
    @API
    public Figur(float pixelProMeter, String zustandsName, String spriteSheetPfad, int anzahlX, int anzahlY) {
        super(new StatefulAnimation<>(ImageLoader.load(spriteSheetPfad).getWidth() / pixelProMeter / anzahlX, ImageLoader.load(spriteSheetPfad).getHeight() / pixelProMeter / anzahlY));

        fuegeZustandVonSpritesheetHinzu(zustandsName, spriteSheetPfad, anzahlX, anzahlY);
    }

    @API
    public Figur(String zustandsName, String spriteSheetPfad, int anzahlX, int anzahlY) {
        this(Camera.DEFAULT_ZOOM, zustandsName, spriteSheetPfad, anzahlX, anzahlY);
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
    @API
    public Figur(float pixelProMeter, String zustandName, String verzeichnisPfad, String praefix) {
        super(new StatefulAnimation<>(getWidthHeightFromPrefixed(verzeichnisPfad, praefix).width, getWidthHeightFromPrefixed(verzeichnisPfad, praefix).height));

        fuegeZustandVonPraefixHinzu(zustandName, verzeichnisPfad, praefix);
    }

    @API
    public Figur(String zustandName, String verzeichnisPfad, String praefix) {
        this(1f, zustandName, verzeichnisPfad, praefix);
    }

    /**
     * Fügt einen Zustand mit GIF-Visualisierung ein.
     *
     * @param zustandsName Name des Zustands.
     * @param bildpfad     Pfad zum GIF, das zu diesem Zustand animiert wird.
     */
    @API
    public void fuegeZustandVonGifHinzu(String zustandsName, String bildpfad) {
        if (!bildpfad.toLowerCase().endsWith(".gif")) {
            throw new RuntimeException("Der agegebene Bildpfad muss eine GIF-Datei sein und auf \".gif\" enden. Der angegebene Bildpfad war: " + bildpfad);
        }

        addState(zustandsName, Animation.createFromAnimatedGif(bildpfad, getActor().getWidth(), getActor().getHeight()));
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
    @API
    public void fuegeZustandVonSpritesheetHinzu(String zustandsName, String bildpfad, int anzahlX, int anzahlY) {
        addState(zustandsName, Animation.createFromSpritesheet(DEFAULT_FRAME_DURATION, bildpfad, anzahlX, anzahlY, getActor().getWidth(), getActor().getHeight()));
    }

    /**
     * Fügt einen Zustand über Einzelframes als Bilder ein.
     *
     * @param zustandsName Der Name des Zustands.
     * @param bildpfade    Die Pfade der Animationsframes in korrekter Reihenfolge.
     */
    @API
    public void fuegeZustandVonEinzelbildernHinzu(String zustandsName, String... bildpfade) {
        addState(zustandsName, Animation.createFromImages(DEFAULT_FRAME_DURATION, getActor().getWidth(), getActor().getHeight(), bildpfade));
    }

    /**
     * Fügt einen Zustand hinzu. Lädt dazu alle Bilder in einem Verzeichnis ein, die zu einem
     * bestimmten Präfix passen.
     *
     * @param zustandName     Name für den ersten Zustand.
     * @param verzeichnisPfad Pfad zum Verzeichnis, in dem alle einzuladenden Bilder liegen.
     * @param praefix         Das Präfix, das alle einzuladenden Bilder haben müssen.
     */
    @API
    public void fuegeZustandVonPraefixHinzu(String zustandName, String verzeichnisPfad, String praefix) {
        getActor().addState(zustandName, Animation.createFromImagesPrefix(Spiel.getActiveScene(), DEFAULT_FRAME_DURATION, getActor().getWidth(), getActor().getHeight(), verzeichnisPfad, praefix));
    }

    /**
     * Setzt den Zustand der Figur neu. In jedem Fall wird dabei der Animationsloop zurückgesetzt.
     *
     * @param zustandsName Der Name des zu setzenden Zustands. Unter diesem Namen muss ein Zustand in dieser
     *                     Figur existieren.
     */
    @API
    public void setzeZustand(String zustandsName) {
        getActor().setState(zustandsName);
    }

    /**
     * Setzt einen automatischen Übergang von einem Zustand zu einem anderen.
     *
     * @param zustandVon  Der Von-Zustand.
     * @param zustandNach Der Zustand, zu dem die Figur automatisch übergehen soll, nachdem der Von-Zustand einmal
     *                    bis zum Ende durchgelaufen ist.
     */
    @API
    public void setzeAutomatischenUebergang(String zustandVon, String zustandNach) {
        getActor().setStateTransition(zustandVon, zustandNach);
    }

    /**
     * Gibt den aktuellen Zustand aus.
     *
     * @return Der Name des aktuellen Zustands.
     */
    @API
    public String nenneAktivenZustand() {
        return getActor().getCurrentState();
    }

    @API
    public void setzeAnimationsgeschwindigkeit(String zustandName, float dauerInSekunden) {
        getActor().setFrameDuration(zustandName, dauerInSekunden);
    }

    private void addState(String stateName, Animation animation) {
        getActor().addState(stateName, animation);
    }

    private static Dimension getWidthHeightFromPrefixed(String directoryPath, String prefix) {
        try {
            File directory = ResourceLoader.loadAsFile(directoryPath);
            if (!directory.isDirectory()) {
                throw new RuntimeException("Der angegebene Pfad war kein Verzeichnis: " + directoryPath);
            }

            File[] children = directory.listFiles();
            if (children != null) {
                for (File file : children) {
                    if (!file.isDirectory() && file.getName().startsWith(prefix)) {
                        BufferedImage image = ImageLoader.load(file.getAbsolutePath());
                        return new Dimension(image.getWidth(), image.getHeight());
                    }
                }
            }

            throw new RuntimeException("Es gab kein Bild im Verzeichnis " + directoryPath + " mit Präfix " + prefix);
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Einladen des Verzeichnisses: " + e.getMessage());
        }
    }
}

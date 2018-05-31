package ea.edu;

import ea.actor.Actor;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;
import ea.internal.ano.NoExternalUse;

/**
 * EDU-Variante der {@link StatefulAnimation}.
 * @author Michael Andonie
 */
public class Figur
extends StatefulAnimation
implements EduActor {


    /**
     * Einführungskonstruktor. Erstellt eine Figur mit einem ersten Zustand.
     * @param zustandsName  Der Name für den ersten Zustand.
     * @param gifBildPfad   Pfad zu einem <b>GIF Bild</b>.
     */
    public Figur(String zustandsName, String gifBildPfad) {
        zustandHinzufuegenVonGIF(zustandsName, gifBildPfad);
        eduSetup();
    }

    /**
     * Einführungskonstruktor. Erstellt eine Figur mit einem ersten Zustand.
     * @param zustandsName  Der Name für den ersten Zustand.
     * @param spriteSheetPfad   Pfad zu einem <b>Spritesheet</b>.
     * @param anzahlX       Anzahl der Spritesheet-Kacheln in die X-Richtung.
     * @param anzahlY       Anzahl der Spritesheet-Kacheln in die Y-Richtung.
     */
    public Figur(String zustandsName, String spriteSheetPfad, int anzahlX, int anzahlY) {
        zustandHinzufuegenVonSpritesheet(zustandsName, spriteSheetPfad, anzahlX, anzahlY);
        eduSetup();
    }

    /**
     * Fügt einen Zustand mit GIF-Visualisierung ein.
     * @param zustandsName  Name des Zustands.
     * @param bildpfad      Pfad zum GIF, das zu diesem Zustand animiert wird.
     */
    public void zustandHinzufuegenVonGIF(String zustandsName, String bildpfad) {
        if(!bildpfad.toLowerCase().endsWith(".gif")) {
            throw new RuntimeException("Der agegebene Bildpfad muss eine GIF-Datei sein und auf \".gif\" enden. "
                    + "Der angegebene Bildpfad war " + bildpfad);
        }
        Animation animation = Animation.createFromAnimatedGif(bildpfad);
        super.addState(zustandsName, animation);
    }

    /**
     * Fügt Zustand mit Spritesheet-Animation ein. Das Spritesheet muss <b>aus Kacheln gleicher Größe</b> bestehen.
     * "leere" Kacheln werden als leere Animationsframes mit einbezogen.
     * @param zustandsName  Der Name des Zustands.
     * @param bildpfad      Pfad zum Spritesheet.
     * @param anzahlX       Anzahl der Spritesheet-Kacheln in die X-Richtung.
     * @param anzahlY       Anzahl der Spritesheet-Kacheln in die Y-Richtung.
     */
    public void zustandHinzufuegenVonSpritesheet(String zustandsName, String bildpfad, int anzahlX, int anzahlY) {
        Animation animation = Animation.createFromSpritesheet(250, bildpfad, anzahlX, anzahlY);
        super.addState(zustandsName, animation);
    }

    /**
     * Fügt einen Zustand über Einzelframes als Bilder ein.
     * @param zustandsName  Der Name des Zustands.
     * @param bildpfade     Die Pfade der Animationsframes in korrekter Reihenfolge.
     */
    public void zustandHinzufuegenVonBildern(String zustandsName, String... bildpfade) {
        Animation animation = Animation.createFromImages(250, bildpfade);
        super.addState(zustandsName, animation);
    }

    /**
     * Setzt den Zustand der Figur neu. In jedem Fall wird dabei der Animationsloop zurückgesetzt.
     * @param zustandsName  Der Name des zu setzenden Zustands. Unter diesem Namen muss ein Zustand in dieser
     *                      Figur existieren.
     */
    public void zustandSetzen(String zustandsName) {
        super.setState(zustandsName);
    }

    /**
     * Setzt einen automatischen Übergang von einem Zustand zu einem anderen.
     * @param zustandVon    Der Von-Zustand.
     * @param zustandNach   Der Zustand, zu dem die Figur automatisch übergehen soll, nachdem der Von-Zustand einmal
     *                      bis zum Ende durchgelaufen ist.
     */
    public void automatischenUebergangSetzen(String zustandVon, String zustandNach) {
        super.setStateTransition(zustandVon, zustandNach);
    }

    /**
     * Gibt den aktuellen Zustand aus.
     * @return  Der Name des aktuellen Zustands.
     */
    public String nenneAktuellenZustand() {
        return super.getCurrentState();
    }

    @NoExternalUse
    @Override
    public Actor getActor() {
        return this;
    }
}

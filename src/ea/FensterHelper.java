package ea;

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.gui.Eingabe;
import ea.internal.gui.Frage;
import ea.internal.gui.HighScoreFenster;
import ea.internal.gui.Nachricht;

/**
 * Jedes <code>Game</code> hat eine Referenz auf ein Objekt dieser Klasse. Sie verwaltet
 * hierfür <b>alle Methoden, die mit dem Spiel-Fenster oder anderen Fenstern zu tun haben</b>.
 * Created by andonie on 06.09.15.
 */
public class FensterHelper {

    /**
     * Referenz auf das Game, für das dieses Objekt Fenster-Methoden bereitstellt.
     */
    @NoExternalUse
    private final Game game;

    /**
     * Erstellt ein FensterHelper-Objekt.
     * @param game  Das Spiel, für das dieser Helper arbeitet.
     */
    @NoExternalUse
    FensterHelper(Game game) {
        this.game = game;
    }

    /**
     * Setzt das übergebene Bild als Icon des Spiel-Fensters.
     *
     * @param icon
     * 		Ein <code>Bild</code>-Objekt, dessen Bild als Icon für das Spiel-Fenster
     * 	    verwendet werden soll. Die Position des Bildes (auf der Zeichenebene) ist irrelevant.
     */
    @API
    public void iconSetzen (Bild icon) {
        game.real_fenster.setIconImage(icon.bild());
    }

    /**
     * Fordert vom Benutzer eine Texteingabe (maximal 40 Zeichen) in einem neuen Fenster.<br />
     * Dieses Fenster muss erst geschlossen werden, damit das Spielfenster wieder in den
     * Vordergrund rücken kann. Diese Methode endet erst, wenn das Fenster geschlossen wurde.<br /> <br />
     * <b>Achtung:<br /> Bei dem Einsatz dieser Methode wird das Spiel angehalten.
     * Es ist empfohlen, vor dem Aufruf dieser Methode das Spiel darauf vorbereitet werden (z.B. Bewegungen
     * zurückgesetzt werden).
     *
     * @param nachricht
     * 		Ein Text, der angezeigt wird und als Eingabeaufforderung erläutern sollte,
     * 		wozu die Eingabe dient, und was eingegeben werden soll.
     *
     * @return  Die Eingabe des Benutzers.<br />
     *          Wurde das Dialogfenster über einen Knopf "gewaltsam"
     *          geschlossen, so ist die Rückgabe <code>null</code>.
     */
    @API
    public String eingabeFordern (String nachricht) {
        new Eingabe(game.real_fenster, nachricht, game.getFont());
        return Eingabe.ergebnis;
    }

    /**
     * Stellt eine Sicherheitsfrage, also eine Frage auf die mit "OK" oder "Abbrechen" geantwortet
     * werden kann, in einem neuen Fenster.<br />
     * Dieses Fenster muss erst geschlossen werden, damit das Spielfenster wieder in den
     * Vordergrund rücken kann. Diese Methode endet erst, wenn das Fenster geschlossen wurde.<br /> <br />
     * <b>Achtung:<br /> Bei dem Einsatz dieser Methode wird das Spiel angehalten.
     * Es ist empfohlen, vor dem Aufruf dieser Methode das Spiel darauf vorbereitet werden (z.B. Bewegungen
     * zurückgesetzt werden).
     *
     * @param frage
     * 		Der Fragetext, der im Dialogfenster angezeigt wird.
     *
     * @return
     * <ul>
     *     <li><code>true</code>, wenn die Frage mit "OK" beantwortet wurde</li>
     *     <li><code>false</code>, wenn die Frage mit "Abbrechen" beantwortet oder das Fenster anders
     *     geschlossen wurde.</li>
     * </ul>
     */
    @API
    public boolean sicherheitsFrage (String frage) {
        new Frage(game.real_fenster, frage, false, game.getFont());
        return Frage.ergebnis;
    }

    /**
     * Stellt eine einfache Frage, also eine Frage, auf die mit "Ja" oder "Nein" geantwortet werden
     * kann, in einem neuen Fenster.<br />
     * Dieses Fenster muss erst geschlossen werden, damit das Spielfenster wieder in den
     * Vordergrund rücken kann. Diese Methode endet erst, wenn das Fenster geschlossen wurde.<br /> <br />
     * <b>Achtung:<br /> Bei dem Einsatz dieser Methode wird das Spiel angehalten.
     * Es ist empfohlen, vor dem Aufruf dieser Methode das Spiel darauf vorbereitet werden (z.B. Bewegungen
     * zurückgesetzt werden).
     *
     * @param frage
     * 		Der Fragetext, der im Dialogfenster angezeigt wird.
     *
     * @return
     * <ul>
     *     <li><code>true</code>, wenn die Frage mit "OK" beantwortet wurde</li>
     *     <li><code>false</code>, wenn die Frage mit "Abbrechen" beantwortet oder das Fenster anders
     *     geschlossen wurde.</li>
     * </ul>
     */
    @API
    public boolean frage (String frage) {
        new Frage(game.real_fenster, frage, true, game.getFont());
        return Frage.ergebnis;
    }

    /**
     * Gibt eine einfache Textnachricht in einem Fenster wieder.<br />
     * Dieses Fenster muss erst geschlossen werden, damit das Spielfenster wieder in den
     * Vordergrund rücken kann. Diese Methode endet erst, wenn das Fenster geschlossen wurde.<br /> <br />
     * <b>Achtung:<br /> Bei dem Einsatz dieser Methode wird das Spiel angehalten.
     * Es ist empfohlen, vor dem Aufruf dieser Methode das Spiel darauf vorbereitet werden (z.B. Bewegungen
     * zurückgesetzt werden).
     *
     * @param nachricht
     * 		Die Textnachricht, die Angezeigt werden soll
     */
    @API
    public void nachrichtSchicken (String nachricht) {
        new Nachricht(game.real_fenster, true, nachricht, game.getFont());
    }

    /**
     * Öffnet ein titelloses Fenster, das die Highscores des Spiels anzeigt.
     *
     * @param namen
     * 		Die Namen der Liste als Array. Von <b>Index 0 als dem besten</b> bis zum schlechtesten auf
     * 		der Liste
     * @param punkte
     * 		Die Punktestaende der Liste als Array. Von <b>Index 0 als dem besten</b> bis zum
     * 		schlechtesten auf der Liste
     *
     * @see #highscoreAnzeigen(String[], int[])
     */
    @API
    public void highscoreAnzeigen (String[] namen, int[] punkte) {
        highscoreAnzeigen(namen, punkte, "");
    }

    /**
     * Öffnet ein Fenster, das die Highscores des Spiels anzeigt.
     *
     * @param namen
     * 		Die Namen der Liste als Array. Von <b>Index 0 als dem besten</b> bis zum schlechtesten auf
     * 		der Liste
     * @param punkte
     * 		Die Punktestaende der Liste als Array. Von <b>Index 0 als dem besten</b> bis zum
     * 		schlechtesten auf der Liste
     * @param fenstertitel
     * 		Der Titel des sich oeffnenden Fensters. Dieser Parameter kann weggelassen werden, hierfuer
     * 		gibt es eine alternative Methode, die diesen Titel nicht erwartet.
     */
    @API
    public void highscoreAnzeigen (String[] namen, int[] punkte, String fenstertitel) {
        new HighScoreFenster(game.real_fenster, fenstertitel, namen, punkte, game.getFont());
    }

    /**
     * Setzt den Font, der ab sofort von den Fenstern standartmaessig verwendet wird.
     *
     * @param fontname
     * 		Der Name des zu verwendenden Fonts.
     *
     * @see ea.Text
     */
    @API
    public void fensterFontSetzen (String fontname) {
        fensterFontSetzen(fontname, game.getFont().getSize());
    }

    /**
     * Setzt den Font, der ab sofort von den Fenstern standartmaessig verwendet wird.
     *
     * @param fontname
     * 		Der Name des zu verwendenden Fonts. <br /> Ein Blick auf das <b>Fontprotokoll</b> (in der
     * 		Klasse <code>Text</code> ist empfehlenswert!
     * @param schriftgroesse
     * 		Die Schriftgroesse, in der die texte dargestellt werden sollen.
     *
     * @see Text
     */
    public void fensterFontSetzen (String fontname, int schriftgroesse) {
        game.fontSetzen(Text.holeFont(fontname).deriveFont(0, schriftgroesse));
    }

    /**
     * Minimiert das Spiel-Fenster.<br /> Dadurch wird es in die Taskleiste hinein minimiert.
     */
    @API
    public void minimieren () {
        game.real_fenster.minimieren();
    }

    /**
     * Stellt das Spiel-Fenster wieder her.<br /> Dadurch wird es - sofern es sich in der Taskleiste minimiert
     * befindet - wieder angezeigt.
     */
    @API
    public void wiederherstellen () {
        game.real_fenster.wiederherstellen();
    }

}

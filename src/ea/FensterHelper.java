package ea;

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.gui.Eingabe;
import ea.internal.gui.Frage;
import ea.internal.gui.HighScoreFenster;
import ea.internal.gui.Nachricht;
import ea.internal.util.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

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
     * @param titel
     *      Der Titel für das Dialogfenster, in dem die Eingabeaufforderung dargestellt wird.
     * @param nachricht
     * 		Ein Text, der angezeigt wird und als Eingabeaufforderung erläutern sollte,
     * 		wozu die Eingabe dient, und was eingegeben werden soll.
     *
     * @return  Die Eingabe des Benutzers.<br />
     *          Wurde das Dialogfenster über einen Knopf "gewaltsam"
     *          geschlossen, so ist die Rückgabe <code>null</code>.
     */
    @API
    public String eingabeFordern (String titel, String nachricht) {
        new Eingabe(game.real_fenster, titel, nachricht, game.getFont());
        return Eingabe.ergebnis;
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
        return eingabeFordern("Eingabe", nachricht);
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
     * @param titel
     *      Der Titel für das Dialogfenster, in dem die Frage angezeigt wird.
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
    public boolean sicherheitsFrage (String titel, String frage) {
        new Frage(game.real_fenster, titel, frage, false, game.getFont());
        return Frage.ergebnis;
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
        return sicherheitsFrage("Frage", frage);
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
     * @param titel
     *      Der Titel für das Dialogfenster, in dem die Frage angezeigt wird.
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
    public boolean frage (String titel, String frage) {
        new Frage(game.real_fenster, titel, frage, true, game.getFont());
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
        return frage("Frage", frage);
    }

    /**
     * Gibt eine einfache Textnachricht in einem Fenster wieder.<br />
     * Dieses Fenster muss erst geschlossen werden, damit das Spielfenster wieder in den
     * Vordergrund rücken kann. Diese Methode endet erst, wenn das Fenster geschlossen wurde.<br /> <br />
     * <b>Achtung:<br /> Bei dem Einsatz dieser Methode wird das Spiel angehalten.
     * Es ist empfohlen, vor dem Aufruf dieser Methode das Spiel darauf vorbereitet werden (z.B. Bewegungen
     * zurückgesetzt werden).
     *
     * @param titel
     *      Der Titel für das Dialogfenster
     * @param nachricht
     * 		Die Textnachricht, die im Dialogfenster angezeigt werden soll
     */
    @API
    public void nachrichtSchicken (String titel, String nachricht) {
        new Nachricht(game.real_fenster, true, nachricht, titel, game.getFont());
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
     * 		Die Textnachricht, die im Dialogfenster angezeigt werden soll
     */
    @API
    public void nachrichtSchicken (String nachricht) {
        nachrichtSchicken("Nachricht", nachricht);
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
     * Öffnet einen Such-Dialog, der die Auswahl eines Pfades ermöglicht.
     *
     * @param akzeptierteEndungen
     * 		Eine Reihe beliebig vieler akzeptierter Endungen (Gross/Kleinschreibung vollkommen egal)<br
     * 		/> z.B. : <code>pfadAuswaehlen("jpg", "bmp", "gif");</code><br /> Wird <code>null</code>
     * 		als Parameter gegeben, so sind saemtliche Dateien waehlbar.<br /> z.B. :
     * 		<code>pfadAuswaehlen(null);</code>
     *
     * @return Der Pfad der ausgewaehlten Datei als String. Ist "null", wenn kein Pfad ausgewaehlt
     * wurde, sondern das Fenster manuell geschlossen wurde
     */
    @API
    public String pfadAuswaehlen (final String... akzeptierteEndungen) {
        FileFilter filter = new FileFilter() {
            public boolean accept (File pathname) {
                if (akzeptierteEndungen == null) {
                    return true;
                } else if (pathname.isDirectory()) {
                    return true;
                } else {
                    for (int i = 0; i < akzeptierteEndungen.length; i++) {
                        if (pathname.getName().toLowerCase().endsWith("." + akzeptierteEndungen[i].toLowerCase())) {
                            return true;
                        }
                    }
                    return false;
                }
            }

            @Override
            public String getDescription () {
                if (akzeptierteEndungen == null) {
                    return "Alle Dateien wählbar";
                } else {
                    String sel = "";
                    for (int i = 0; i < akzeptierteEndungen.length; i++) {
                        sel += "." + akzeptierteEndungen[i].toLowerCase() + " ";
                    }
                    return "Ausgewählte Formate (" + sel + ")";
                }
            }
        };
        JFileChooser ch = new JFileChooser();
        ch.setFileFilter(filter);
        int erg = ch.showOpenDialog(game.real_fenster);
        if (erg == JFileChooser.APPROVE_OPTION) {
            return ch.getSelectedFile().getPath();
        } else {
            return null;
        }
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

    /**
     * Gibt ein BoundingRechteck zurueck, dass die Masse des Fensters beschreibt.<br /> Die Hoehe
     * und Breite geben die Hoehe und Breite des Fensters wieder. Die Position ist immer (0|0), da
     * dies nicht relevant ist für die Maße des Fensters.
     *
     * @return  Ein <code>BoundingRechteck</code>, dass die Maße des Fensters in seiner Höhe und Breite
     *          beschreibt.
     */
    @API
    public BoundingRechteck fensterGroesse () {
        return game.real_fenster.fenstermasse();
    }

    /**
     * Macht vom aktuell sichtbaren Bereich (also dem von der Kamera derzeit erfassten Bereich)
     * einen Screenshot.
     *
     * @param pfad
     * 		Der Pfad, in dem das Bild gespeichert werden soll. Ein Wert wie {@code screenshot.jpg}
     * 		speichert den Screenshot im Projektordner. Für eingabeabhängige Pfade kann
     * 		<code>pfadAuswaehlen(String[])</code> benutzt werden.<br /> <br /> <b> ACHTUNG!! </b><br />
     * 		Als Endung wird bisher nur ".jpg" und ".png" unterstützt!
     */
    @API
    public void screenshot (String pfad) {
        screenshot(pfad, game.kamera.position());
    }

    /**
     * Macht einen Screenshot von einem bestimmten Bildbereich und speichert diesen ab.
     *
     * @param pfad
     * 		Der Pfad, in dem das Bild gespeichert werden soll. Ein Wert wie {@code screenshot.jpg}
     * 		speichert den Screenshot im Projektordner. Für eingabeabhängige Pfade kann
     * 		<code>pfadAuswaehlen(String[])</code> benutzt werden.<br /> <br /> <b> ACHTUNG!! </b><br />
     * 		Als Endung wird bisher nur ".jpg" und ".png" unterstützt!
     * @param ausschnitt
     * 		Der Ausschnitt aus der Zeichenebene, der als Bild gespeichert werden soll als
     * 		<code>BoundingRechteck</code>.
     *
     * @see #pfadAuswaehlen(java.lang.String[])
     * @see #screenshot(java.lang.String, int, int, int, int)
     */
    @API
    public void screenshot (final String pfad, final BoundingRechteck ausschnitt) {
        final String ext = pfad.toLowerCase().substring(pfad.length() - 3);

        if (!ext.equals("png") && !ext.equals("jpg")) {
            throw new IllegalArgumentException("Pfad muss auf .jpg oder .png enden!");
        }

        new Thread() {
            public void run () {
                BufferedImage img = new BufferedImage((int) ausschnitt.breite, (int) ausschnitt.hoehe, BufferedImage.TYPE_INT_RGB);
                Graphics2D g = img.createGraphics();

                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

                // TODO -> Camera-Translation einbauen!

                game.kamera.wurzel().render(g);

                try {
                    ImageIO.write(img, ext, new File(pfad));
                } catch (IOException e) {
                    Logger.error("IO", "Schreibfehler beim Speichern des Screenshots!");
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * Macht einen Screenshot von einem bestimmten Bildbereich und speichert diesen ab,
     *
     * @param pfad
     * 		Der Pfad, in dem das Bild gespeichert werden soll. Ein Wert wie {@code screenshot.jpg}
     * 		speichert den Screenshot im Projektordner. Für eingabeabhängige Pfade kann
     * 		<code>pfadAuswaehlen(String[])</code> benutzt werden.<br /> <br /> <b> ACHTUNG!! </b><br />
     * 		Als Endung wird bisher nur ".jpg" und ".png" unterstützt!
     * @param x
     * 		Die X-Koordinate der oberen linken Ecke des Bildausschnitts.
     * @param y
     * 		Die Y-Koordinate der oberen linken Ecke des Bildausschnitts.
     * @param breite
     * 		Die gewuenschte Breite des Bildes
     * @param hoehe
     * 		Die gewuenschte Laenge des Bildes
     *
     * @see #pfadAuswaehlen(java.lang.String[])
     * @see #screenshot(java.lang.String, ea.BoundingRechteck)
     */
    @API
    public void screenshot (String pfad, int x, int y, int breite, int hoehe) {
        screenshot(pfad, new BoundingRechteck(x, y, breite, hoehe));
    }
}

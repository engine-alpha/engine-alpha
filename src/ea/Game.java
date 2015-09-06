/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
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

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.gui.*;
import ea.internal.util.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Diese Klasse ist für die sofortige, einfache Verwendung der Engine verantwortlich.<br /> Aus ihr
 * sollte die Klasse abgleitet werden, die die Spielorganisation beinhaltet.
 *
 * @author Michael Andonie
 */
public abstract class Game
        implements TastenReagierbar, FrameUpdateReagierbar {

    /* _______________________ STATIC STUFF _______________________ */

	static {
		System.setProperty("sun.java2d.opengl", "true"); // ok
		System.setProperty("sun.java2d.d3d", "false"); // ok
		System.setProperty("sun.java2d.noddraw", "true"); // set false if possible, linux
		System.setProperty("sun.java2d.pmoffscreen", "false"); // set true if possible, linux
		System.setProperty("sun.java2d.ddoffscreen", "true"); // ok, windows
		System.setProperty("sun.java2d.ddscale", "true"); // ok, hardware accelerated image scaling on windows
	}


    /* _______________________ FIELDS _______________________ */


    // ~~~~~ Internal Stuff ~~~~~

    /**
     * An diesem Knoten angelegte Objekte werden immer im Vordergrund sein.<br /> Dies wird zB fuer
     * einen Abblendbildschirm verwendet.
     */
    @NoExternalUse
    private final Knoten superWurzel;

    /**
     * Das Spielfenster
     */
    @NoExternalUse
    final Fenster real_fenster;

    /**
     * Gibt an, ob bei Escape-Druck das Spiel beendet werden soll.
     */
    @NoExternalUse
    private final boolean exitOnEsc;

    /**
     * Der Font für die Fenstertexte
     */
    @NoExternalUse
    private Font font;


    // ~~~~~ Basic API References ~~~~~

	/**
	 * Der Wurzel-Knoten. An ihm muessen direkt oder indirekt (ueber weitere Knoten) alle
	 * <code>Raum</code>-Objekte angemeldet werden, die auch (normal) gezeichnet werden sollen.
	 */
    @API
	public final Knoten wurzel;

	/**
	 * Die statische Wurzel.<br /> Objekte, die an diesem Knoten angemeldet werden, werden ebenfalls
	 * gezeichnet, jedoch mit einem essentiellen Unterschied bei Verschiebung der
	 * <code>Kamera</code> werden diese nicht verschoben gezeichnet, sondern bleiben weiter
	 * (<b>statisch</b>) auf ihrer festen Position. Dies bietet sich zum Beispiel fuer eine
	 * Punkte-Anzeige etc an.
	 */
    @API
	public final Knoten statischeWurzel;

	/**
	 * Die <code>Kamera</code> des Spiels.<br /> Dieser kann ueber <code>setzeFokus(Raum m)</code>
	 * ein bestimmtes Raum-Objekt immer im Zentrum zeigen. Es gibt auch weitere interessante
	 * Methoden dieser Klasse.<br /> Hierzu siehe <b>Handbuch oder Doku</b>.
	 */
    @API
	public final Kamera kamera;

	/**
	 * Dieser String ist <b>immer das korrekte, Systemabhaengige Pfadtrenner</b>-Literal, das ganz
	 * einfach bei Pfadangaben verwendet werden kann:<br /> <br /> <code> String verzeichnis =
	 * "meinOrdner" + "meinDarinLiegenderUnterordner" + "meineDatei.eaf"; </code>
	 */
    @API
	public final String pfadtrenner = DateiManager.sep;



    // ~~~~~ Handles ~~~~~

    /**
     * Über diese Referenz kann die Maus des Spiels beeinflusst werden.
     */
    @API
    public final Maus maus;

    /**
     * Über dieses Objekt können alle Anmelde-Methoden aufgerufen werden. Zum Beispiel für:
     * <ul>
     *     <li>Tastendruck</li>
     *     <li>Mausklick / -bewegung</li>
     *     <li>Ticker</li>
     * </ul>
     */
    @API
    public final Anmelden anmelden;

    /**
     * Über dieses Objekt können alle Fenster-Funktionalitäten genutzt werden. Zum Beispiel:
     * <ul>
     *     <li>Eine Dialogfenster öffnen</li>
     *     <li>Eine Nutzereingabe in einem neuen Fenster anfordern</li>
     *     <li>Ein Highscore-Fenster anzeigen</li>
     *     <li>Das Spielfenster-Icon einstellen</li>
     *     <li>Das Spielfenster minimieren / wiederherstellen</li>
     * </ul>
     */
    @API
    public final FensterHelper fenster;






    /* _______________________ CONSTRUCTOR OVERKILL _______________________ */

    /**
     * Groesster Konstruktor fuer Objekte der Klasse Game.
     *
     * @param x
     * 		Die Breite des Fensters
     * @param y
     * 		Die Hoehe des Fensters
     * @param titel
     * 		Der Titel des Spielfensters
     * @param vollbild
     * 		Ob das Fenster ein echtes Vollbild sein soll.<br /> Manche Computer unterstuetzen kein
     * 		Vollbild, in diesem Fall wird ein moeglichst grosses Fenster erzeugt.<br /> Im
     * 		Vollbildmodus nimmt das Fenster den gesamten Bildschirm ein, die Eingabeparameter
     * 		<code>x</code> und <code>y</code> beschreiben dann die Masse dieses Fensters, <b>der
     * 		Bildschirm wird also an die gewuenschte X-Y-Groesse angepasst!!!</b>
     * @param exitOnEsc
     * 		Ist dieser Wert <code>true</code>, so wird das Spiel automatisch beendet, wenn die
     * 		"Escape"-Taste gedrueckt wurde. Dies bietet sich vor allem an, wenn das Spiel ein Vollbild
     * 		ist oder die Maus aufgrund der Verwendung einer Maus im Spiel nicht auf das "X"-Symbol des
     * 		Fensters geklickt werden kann, wodurch der Benutzer im Spiel "gefangen" waere und <b>dies
     * 		ist etwas unbeschreiblich aergerliches fuer den Spielbenutzer!!!!!!!!!!!</b>
     * @param fensterX
     * 		Die X-Koordinate der linken oberen Ecke des Fensters auf dem Computerbildschirm
     * @param fensterY
     * 		Die Y-Koordinate der linken oberen Ecke des Fensters auf dem Computerbildschirm
     */
    public Game (int x, int y, String titel, boolean vollbild, boolean exitOnEsc, int fensterX, int fensterY) {
        real_fenster = new Fenster(x, y, titel, vollbild, fensterX, fensterY);
        this.exitOnEsc = exitOnEsc;
        this.font = new Font("SansSerif", Font.PLAIN, 16);

        // ------------- Die Helper-Referenzen -------------
        kamera = real_fenster.getCam();
        kamera.wurzel().add(wurzel = new Knoten(), superWurzel = new Knoten());

        statischeWurzel = real_fenster.getStatNode();

        real_fenster.tastenReagierbarAnmelden(this);

        try {
            real_fenster.setIconImage(ImageIO.read(getClass().getResourceAsStream("/assets/favicon.png")));
        } catch (Exception e) {
            Logger.warning("IO", "Standard-Icon konnte nicht geladen werden.");
        }



        real_fenster.getFrameThread().gameHandshake(this);

        // ------------- Die Handles -------------
        this.anmelden = new Anmelden(this);
        this.fenster = new FensterHelper(this);
        this.maus = real_fenster.getMaus();
    }

	/**
	 * Erstellt ein Spiel, bei dem automatisch beim Drücken von ESC alles beendet wird und ohne
	 * Titel.
	 *
	 * @param x
	 * 		Die Breite des Fensters
	 * @param y
	 * 		Die Hoehe des Fensters
	 * @param vollbild
	 * 		Ob das Fenster ein echtes Vollbild sein soll.<br /> Manche Computer unterstuetzen kein
	 * 		Vollbild, in diesem Fall wird ein moeglichst grosses Fenster erzeugt.<br /> Im
	 * 		Vollbildmodus nimmt das Fenster den gesamten Bildschirm ein, die Eingabeparameter
	 * 		<code>x</code> und <code>y</code> beschreiben dann die Masse dieses Fensters, <b>der
	 * 		Bildschirm wird also an die gewuenschte X-Y-Groesse angepasst!!!</b>
	 */
	public Game (int x, int y, boolean vollbild) {
		this(x, y, "", vollbild, true);
	}

	/**
	 * Konstruktor fuer Objekte der Klasse Game. Erstellt unter anderem ein Fenster, das harmonisch
	 * im Spielbildschirm liegt.
	 *
	 * @param x
	 * 		Die Breite des Fensters
	 * @param y
	 * 		Die Hoehe des Fensters
	 * @param titel
	 * 		Der Titel des Spielfensters
	 * @param vollbild
	 * 		Ob das Fenster ein echtes Vollbild sein soll.<br /> Manche Computer unterstuetzen kein
	 * 		Vollbild, in diesem Fall wird ein moeglichst grosses Fenster erzeugt.<br /> Im
	 * 		Vollbildmodus nimmt das Fenster den gesamten Bildschirm ein, die Eingabeparameter
	 * 		<code>x</code> und <code>y</code> beschreiben dann die Masse dieses Fensters, <b>der
	 * 		Bildschirm wird also an die gewuenschte X-Y-Groesse angepasst!!!</b>
	 * @param exitOnEsc
	 * 		Ist dieser Wert <code>true</code>, so wird das Spiel automatisch beendet, wenn die
	 * 		"Escape"-Taste gedrueckt wurde. Dies bietet sich vor allem an, wenn das Spiel ein Vollbild
	 * 		ist oder die Maus aufgrund der Verwendung einer Maus im Spiel nicht auf das "X"-Symbol des
	 * 		Fensters geklickt werden kann, wodurch der Benutzer im Spiel "gefangen" waere und <b>dies
	 * 		ist etwas unbeschreiblich aergerliches fuer den Spielbenutzer!!!!!!!!!!!</b>
	 */
	public Game (int x, int y, String titel, boolean vollbild, boolean exitOnEsc) {
		this(x, y, titel, vollbild, exitOnEsc, -1, -1);
	}
	/**
	 * Vereinfachter Konstruktor.<br /> Hierbei gilt automatisch, dass das Fenster <b>beim
	 * Tastendruck auf "Escape" beendet wird</b>, sowie dass kein Vollbild aktiviert wird.<br /> Ist
	 * dies nicht gewuenscht, so muss der komplexere Konstruktor aufgerufen werden. Es wird kein
	 * Fenstertitel erstellt.
	 *
	 * @param x
	 * 		Die Breite des Fensters
	 * @param y
	 * 		Die Hoehe des Fenstsers
	 */
	public Game (int x, int y) {
		this(x, y, "");
	}

	/**
	 * Vereinfachter Konstruktor.<br /> Hierbei gilt automatisch, dass das Fenster <b>beim
	 * Tastendruck auf "Escape" beendet wird</b>, sowie dass kein Vollbild aktiviert wird.<br /> Ist
	 * dies nicht gewuenscht, so muss der komplexere Konstruktor aufgerufen werden.
	 *
	 * @param x
	 * 		Die Breite des Fensters
	 * @param y
	 * 		Die Hoehe des Fenstsers
	 * @param titel
	 * 		Der Titel des Spielfensters
	 */
	public Game (int x, int y, String titel) {
		this(x, y, titel, false, true);
	}

	/**
	 * Parameterloser Alternativkonstruktor.<br /> Hierbei wird automatisch ein Spielfenster der
	 * Groesse 500 auf 500 (kein Vollbild) erstellt, das bei Tastendruck auf "Escape" beendet wird.
	 */
	public Game () {
		this(500, 500, "", false);
	}

	/**
	 * Etwas vereinfachter Konstruktor.<br /> Hierbei gilt automatisch, dass das Fenster <b>beim
	 * Tastendruck auf "Escape" beendet wird</b>.<br /> Ist dies nicht gewuenscht, so muss der
	 * komplexere Konstruktor aufgerufen werden.
	 *
	 * @param x
	 * 		Die Breite des Fensters
	 * @param y
	 * 		Die Hoehe des Fenstsers
	 * @param titel
	 * 		Der Titel des Spielfensters
	 * @param vollbild
	 * 		Ob das Fenster ein echtes Vollbild sein soll.<br /> Manche Computer unterstuetzen kein
	 * 		Vollbild, in diesem Fall wird ein moeglichst grosses Fenster erzeugt.<br /> Im
	 * 		Vollbildmodus nimmt das Fenster den gesamten Bildschirm ein, die Eingabeparameter
	 * 		<code>x</code> und <code>y</code> beschreiben dann die Masse dieses Fensters, <b>der
	 * 		Bildschirm wird also an die gewuenschte X-Y-Groesse angepasst!!!</b>
	 */
	public Game (int x, int y, String titel, boolean vollbild) {
		this(x, y, titel, vollbild, true);
	}


    /* _______________________ ECHET API METHODEN _______________________ */

	/**
	 * Diese Methode beendet das Spiel gaenzlich.<br /> Das heisst, dass das Fenster geschlossen,
	 * alle belegten Ressourcen freigegeben und auch die virtuelle Maschine von JAVA beendet
	 * wird (sollten keine weiteren Fenster/Spiel-Instanzen existieren.
	 */
    @API
	public void beenden () {
		real_fenster.loeschen();
	}

	/**
	 * Prüft, ob eine bestimmte Taste gerade jetzt heruntergedrückt wird.
	 *
	 * @param code
	 * 		der Code der zu prüfenden Taste.
	 *
	 * @return <code>true</code>, falls die gewählte Taste gerade jetzt heruntergedrückt wird. Sonst
	 * <code>false</code>.
	 */
    @API
	public boolean tasteGedrueckt (int code) {
		return real_fenster.istGedrueckt(code);
	}

	/**
	 * Diese Methode kopiert eine beliebige Datei von einem Pfad in einen neuen.
	 *
	 * @param von
	 * 		Das Verzeichnis der Datei, die kopiert werden soll
	 * @param nach
	 * 		Das Verzeichnis, in das die Datei kopiert werden soll
	 * @param nameNeu
	 * 		Der Name der neuen Datei, die entstehen soll (z.B. "neuedatei.pdf")
	 *
	 * @return <code>true</code>, wenn das kopieren vollends erfolgreich war, sonst
	 * <code>false</code>.
	 */
    @API
	public boolean kopieren (String von, String nach, String nameNeu) {
		try {
			Files.copy(Paths.get(von), Paths.get(nach, nameNeu));
		} catch (FileNotFoundException e) {
			Logger.error("IO", "Die Datei konnte nicht gefunden werden!");
			return false;
		} catch (IOException e) {
			Logger.error("IO", "Fehler beim Lesen!");
			e.printStackTrace();
			return false;
		}

		return true;
	}

    public void ppmSetzen(float pixelprometer) {
        real_fenster.getWorldHandler().setPixelProMeter(pixelprometer);
    }



    /* _______________________ INTERNE METHODEN _______________________ */

    /**
     * Gibt den Font aus, der für Dialogfenster genutzt werden soll.
     * @return  der Font, der für Dialogfenster genutzt werden soll.
     */
    @NoExternalUse
    Font getFont() {
        return font;
    }

    /**
     * Setzt den Font für Dialogfenster neu.
     * @param font  Der Font für Dialogfenster.
     */
    @NoExternalUse
    void fontSetzen(Font font) {
        this.font = font;
    }

    /**
     * Die aus <code>TastenReagierbar</code> implemetierte Methode zum Reagieren auf einen
     * einfachen, einmaligen Tastendruck.
     *
     * @param code
     * 		Der Code dieser Taste zu den Codes:<br /> Siehe http://engine-alpha.org/wiki/Tastaturtabelle
     * 		für eine vollständige Tabelle
     *
     * @see ea.Taste
     */
    @NoExternalUse
    @Override
    public final void reagieren (int code) {
        if (exitOnEsc && code == Taste.ESCAPE) {
            beenden();
        }

        tasteReagieren(code);
    }

    /* _______________________ Kontrakt: Abstrakte und Überschreibbare Methoden _______________________ */

    /**
     * Diese Methode kann von der erbenden Klasse <i>überschrieben werden</i>. <br />
     * Diese Methode wird <i>in jedem Frame aufgerufen</i>. Möchte man <i>kontinuierlich wirkende
     * Änderungen</i> im Spiel implementieren, ist diese Methode der beste Ort dafür.
     * @param ts    Die tatsächliche Zeit <i>in Sekunden</i>, die seit dem letzten Frame
     *              vergangen ist. Bei 60 FPS (Frames pro Sekunde) ist also ein Durchschnittswert
     *              von <code>ts = 1/60 = 0.016666f</code> zu erwarten.
     */
    @API
    @Override
    public void frameUpdate(float ts) {
        //LEER - kann überschrieben werden.
    }

    /**
     * Diese Methode wird von der Klasse automatisch aufgerufen, sobald eine Taste einfach gedrueckt
     * wurde.<br /> Sie wird dann erst wieder aufgerufen, wenn die Taste erst losgelassen und dann
     * wieder gedreuckt wurde.<br /> Sollte allerdings eine Methode vonnoeten sein, die immer wieder
     * in Regelmaessigen abstaenden aufgerufen wird, solange die Taste <b>heruntergedrueckt ist, so
     * bietet sich dies im Interface <code>TasteGedruecktReagierbar</code> an</b>.
     *
     * @param code
     * 		Code der gedrückten Taste<br />Siehe http://engine-alpha.org/wiki/Tastaturtabelle für eine
     * 		vollständige Tabelle
     *
     * @see ea.TastenReagierbar
     */
    @API
    public abstract void tasteReagieren (int code);

    /**
     * Wird aufgerufen, sobald die <b>Initialisierung des Spiels</b> starten kann. <br/>
     * Diese Methode wird intern <i>einmalig aufgerufen, sobald die Spielumgebung initiiert werden soll</i>.
     * Das bedeutet, hierin werden die Operationen angesetzt, die klassischerweise in einem <i>Konstruktor</i>
     * durchgeführt werden.<br/>
     *
     * Um interne Fehler zu vermeiden, sollte die <b>gesamte Initiierung hier stattfinden</b> und nicht
     * im Konstruktor.
     */
    @API
    public abstract void initialisieren();
}

package ea.edu;

import ea.Game;
import ea.Vector;
import ea.actor.Actor;
import ea.edu.event.*;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.util.Logger;
import org.jbox2d.common.Vec2;

import java.awt.Color;
import java.util.HashMap;

/**
 * Diese Klasse steuert die EDU-Version. Sie ist Schnittstelle für:
 * <ul>
 * <li>Szenen-Management</li>
 * <li>Listener-Management</li>
 * </ul>
 *
 * @author Michael Andonie
 */
public class Spiel {

    private static final HashMap<String, Color> stringToColor = new HashMap<>();
    private static final HashMap<Color, String> colorToString = new HashMap<>();

    private static final HashMap<String, EduScene> sceneMap = new HashMap<>();

    public static final String DEFAULT_EDU_DIALOG_TITLE = "Engine Alpha - EDU Version";

    private static int frame_width = 800;
    private static int frame_height = 600;

    /* ~~~ EDU-UTILITY ~~~*/

    /**
     * Fügt eine String-awt/Color-Zuordnung zu.
     *
     * @param string Ein String (lowercase)
     * @param color  Eine Color
     */
    private static void addC(String string, Color color) {
        stringToColor.put(string, color);
        colorToString.put(color, string);
    }

    /**
     * Diese Methode ordnet einem String ein Color-Objekt zu.<br> Hierdurch ist in den Klassen
     * außerhalb der Engine keine awt-Klasse nötig.
     *
     * @param t Der Name der Farbe.
     *
     * @return Das Farbobjekt zum String; ist Color.black bei unzuordnembaren String
     */
    @Internal
    public static Color stringToColor(String t) {
        Color res = stringToColor.get(t.toLowerCase());
        if (res == null) {
            throw new IllegalArgumentException("Eine Farbe mit dem Namen " + t + " ist der Engine nicht bekannt.");
        } else {
            return res;
        }
    }

    /**
     * Reverse-Lookup für Farbzuordnung
     */
    public static String colorToString(Color color) {
        String res = colorToString.get(color);
        if (res == null) {
            return "unbekannt";
        } else {
            return res;
        }
    }


    /* ~~~ STEUERUNG & KAPSELUNG ~~~*/

    public static final Color COLOR_LILA = new Color(145, 19, 255);
    public static final Color COLOR_ORANGE = new Color(255, 116, 0);
    public static final Color COLOR_BRAUN = new Color(119, 77, 50);
    public static final Color COLOR_HELLBLAU = new Color(0, 194, 255);
    public static final Color COLOR_DUNKELBLAU = new Color(21, 0, 137);
    public static final Color COLOR_HELLGRUEN = new Color(157, 255, 0);
    public static final Color COLOR_DUNKELGRUEN = new Color(11, 71, 0);

    static {
        //Fülle alle Farbzuweisungen hinzu
        addC("gelb", Color.YELLOW);
        addC("weiss", Color.WHITE);
        stringToColor.put("weiß", Color.WHITE);
        addC("orange", COLOR_ORANGE);
        addC("grau", Color.GRAY);
        addC("gruen", Color.GREEN);
        stringToColor.put("grün", Color.GREEN);
        addC("blau", Color.BLUE);
        addC("rot", Color.RED);
        addC("pink", Color.PINK);
        addC("magenta", Color.MAGENTA);
        addC("lila", COLOR_LILA);
        addC("cyan", Color.CYAN);
        stringToColor.put("tuerkis", Color.CYAN);
        stringToColor.put("türkis", Color.CYAN);
        addC("dunkelgrau", Color.DARK_GRAY);
        addC("hellgrau", Color.LIGHT_GRAY);
        addC("braun", COLOR_BRAUN);
        addC("schwarz", Color.BLACK);
        addC("hellblau", COLOR_HELLBLAU);
        addC("dunkelblau", COLOR_DUNKELBLAU);
        addC("hellgruen", COLOR_HELLGRUEN);
        stringToColor.put("hellgrün", COLOR_HELLGRUEN);
        addC("dunkelgruen", COLOR_DUNKELGRUEN);
        stringToColor.put("dunkelgrün", COLOR_DUNKELGRUEN);

        //Startup-Game

    }

    /* ~~ Game Frame ~~ */

    /**
     * Setzt die Groesse des Engine-Fensters.
     *
     * @param breite Fenster-Breite
     * @param hoehe  Fenster-Hoehe
     */
    @API
    public static void setzeFensterGroesse(int breite, int hoehe) {
        if (activeScene != null) {
            throw new RuntimeException("setzeFensterGroesse kann nur aufgerufen werden, bevor " + "das erste grafische Objekt erzeugt wurde.");
        }
        if (breite < 0 || hoehe < 0) {
            throw new RuntimeException("Die Fenstergroesse (Breite sowie Höhe) muss jeweils > 0 sein. " + "Eingabe war: " + breite + " Breite und " + hoehe + " Höhe");
        }
        frame_width = breite;
        frame_height = hoehe;
    }

    /**
     * Stellt, ob das Hilfs-Raster, das die Koordinatenachsen visualisiert, dargestellt werden soll.
     *
     * @param sichtbar ist dieser Wert <code>true</code>, wird das Raster dargestellt. Ist er <code>false</code>,
     *                 wird das Raster deaktiviert.
     */
    @API
    public void rasterSichtbarSetzen(boolean sichtbar) {
        Game.setDebug(sichtbar);
    }

    /**
     * Setzt, ob die <b>aktive Szene</b> den Erkundungsmodus aktiv hat.
     * Ist der Erkundungsmodus aktiv, so kann man die aktuellen Szene navigieren mit Pfeiltasten (Kameraposition)
     * und Mausrad (Kamerazoom)
     *
     * @param aktiv Ob der Erkundungsmodus aktiv sein soll.
     */
    @API
    public void setzeErkundungsmodusAktiv(boolean aktiv) {
        getActiveScene().setExploreMode(aktiv);
    }

    /* ~~ Scene Transitions ~~ */

    private static EduScene activeScene;

    @Internal
    static EduScene getActiveScene() {
        if (activeScene == null) {
            activeScene = new EduScene();
            new Thread(() -> Game.start(frame_width, frame_height, activeScene)).start();
        }
        return activeScene;
    }

    @Internal
    static void setActiveScene(EduScene eduScene) {
        activeScene = eduScene;
        Game.transitionToScene(activeScene);
    }

    /**
     * Gibt der aktuellen Szene einen Namen. Eine Szene mit Name bleibt im System erhalten und kann wieder
     * aufgerufen werden.
     *
     * @param name Der Name für die Szene.
     *
     * @see #setzeSzene(String)
     */
    @API
    public void benenneSzene(String name) {
        if (getActiveScene().getSceneName() != null) {
            Logger.error("EDU", "Die Szene hat bereits einen Namen: " + getActiveScene().getSceneName());
            return;
        }
        activeScene.setSceneName(name);
        sceneMap.put(name, activeScene);
    }

    /**
     * Erstellt eine neue (leere) Szene und setzt diese aktiv.
     * Damit
     * <ul>
     * <li>Wird der Bildschirm "geleert"</li>
     * <li>Geht die vorige Szene "verloren", wenn sie nicht mit benannt wurde.</li>
     * <li>Werden alle grafischen Objekte, die ab sofort erstellt werden, in der neuen Szene eingesetzt.</li>
     * </ul>
     */
    @API
    public void neueSzene() {
        EduScene newScene = new EduScene();
        newScene.setGravity(new Vector(0, -9.81f));
        setActiveScene(newScene);
    }

    /**
     * Setzt die aktive Szene. Wurde eine Szene benannt, so bleibt sie gespeichert und kann wieder aktiv gesetzt
     * werden.
     *
     * @param szenenName der Name der aktiv zu setzenden Szene.
     *
     * @see #benenneSzene(String)
     */
    @API
    public void setzeSzene(String szenenName) {
        EduScene scene = sceneMap.get(szenenName);
        if (scene == null) {
            Logger.error("EDU", "Konnte keine Szene mit dem Namen " + szenenName + " finden.");
            return;
        }
        setActiveScene(scene);
    }

    /* _____________________________ LAYER CONTROLS _____________________________ */

    /**
     * Fügt der <b>derzeit aktiven Szene</b> eine neue Ebene hinzu.
     *
     * @param ebenenName     Der Name für die neue Ebene. Muss für die <b>derzeit aktive</b> Szene einzigartig sein.
     * @param ebenenPosition Die Position für die neue Ebene. Bestimmt, in welcher Reihenfolge Ebenen dargestellt
     *                       werden.
     *                       Ebenen mit einem größeren Positionswert werden vor Ebenen mit einem kleineren Positionswert
     *                       angezeigt.
     *                       Die Hauptebene hat die Position 0.
     */
    @API
    public void macheNeueEbene(String ebenenName, int ebenenPosition) {
        getActiveScene().addLayer(ebenenName, ebenenPosition);
    }

    @API
    public void setzeEbenenParallaxe(String ebenenName, float px, float py, float pz) {
        getActiveScene().setLayerParallax(ebenenName, px, py, pz);
    }

    @API
    public void setzeEbenenZeitVerzerrung(String ebenenName, float zeitverzerrung) {
        getActiveScene().setLayerTimeDistort(ebenenName, zeitverzerrung);
    }

    @API
    public void setzeAktiveEbene(String ebenenName) {
        getActiveScene().setActiveLayer(ebenenName);
    }

    @API
    public void setzeAufHauptebeneZurueck() {
        getActiveScene().resetToMainLayer();
    }

    /* ~~~ CAMERA CONTROL ~~~ */

    @API
    public void verschiebeKamera(float dX, float dY) {
        getActiveScene().getCamera().move(dX, dY);
    }

    @API
    public void setzeKameraZoom(float zoom) {
        getActiveScene().getCamera().setZoom(zoom);
    }

    @API
    public float nenneKameraZoom() {
        return getActiveScene().getCamera().getZoom();
    }

    @API
    public void setzeKameraFokus(Actor focus) {
        getActiveScene().getCamera().setFocus(focus);
    }

    @API
    public void rotiereKamera(float winkelInBogenmass) {
        getActiveScene().getCamera().rotate(winkelInBogenmass);
    }

    @API
    public void setzeKameraRotation(float winkelInBogenmass) {
        getActiveScene().getCamera().rotateTo(winkelInBogenmass);
    }

    /* ~~~ GLOBAL WORLD PHYSICS ~~~ */

    @API
    public void setzeSchwerkraft(float schwerkraft) {
        getActiveScene().getWorldHandler().getWorld().setGravity(new Vec2(0, -schwerkraft));
    }

    /* ~~~ Dialogues ~~~ */

    @API
    public void nachricht(String nachricht) {
        Game.showMessage(nachricht, DEFAULT_EDU_DIALOG_TITLE);
    }

    @API
    public boolean frageJaNein(String frage) {
        return Game.requestYesNo(frage, DEFAULT_EDU_DIALOG_TITLE);
    }

    @API
    public boolean nachrichtOkAbbrechen(String frage) {
        return Game.requestOkCancel(frage, DEFAULT_EDU_DIALOG_TITLE);
    }

    @API
    public String eingabe(String nachricht) {
        return Game.requestStringInput(nachricht, DEFAULT_EDU_DIALOG_TITLE);
    }

    /* ~~~ Listener Addition ~~~ */

    /**
     * Meldet ein Objekt an, das ab sofort auf Mausklicks reagieren wird.<br> Intern laesst sich
     * theoretisch ein Objekt <b>JEDER</b> Klasse anmelden!<br> Deshalb <i>sollten nur Objekte
     * angemeldet werden, die Instanzen eines interfaces EDU-<code>KLICKREAGIERBAR</code>-Interfaces
     * sind!!</i><br> <br> <br> Example:<br> <br> <code>KLICKREAGIERBAR { <br> //Eine
     * Methode diesen Namens MUSS existieren!!<br> public abstract void klickReagieren(int x, int
     * y);<br> }</code>
     *
     * @param client Das anzumeldende Objekt. Dieses wird ab sofort above jeden Mausklick informiert.
     */
    @API
    public void mausKlickReagierbarAnmelden(MausKlickReagierbar client) {
        getActiveScene().addEduClickListener(client);
    }

    @API
    public void mausKlickReagierbarAbmelden(MausKlickReagierbar klickReagierbar) {
        getActiveScene().removeEduClickListener(klickReagierbar);
    }

    /**
     * Meldet ein Objekt an, das ab sofort auf Tastendruck reagieren wird.<br> Intern laesst sich
     * theoretisch ein Objekt <b>JEDER</b> Klasse anmelden!<br> Deshalb <i>sollten nur Objekte
     * angemeldet werden, die Instanzen des EDU-<code>TASTENREAGIERBARANMELDEN</code>-Interfaces
     * sind!!</i>
     *
     * @param o Das anzumeldende Objekt. Dieses wird ab sofort above jeden Tastendruck informiert.
     */
    @API
    public void tastenReagierbarAnmelden(TastenReagierbar o) {
        getActiveScene().addEduKeyListener(o);
    }

    @API
    public void tasteReagierbarAbmelden(TastenReagierbar o) {
        getActiveScene().removeEduKeyListener(o);
    }

    /**
     * Meldet ein Objekt zum Ticken an. Intern laesst sich theoretisch ein Objekt <b>JEDER</b>
     * Klasse anmelden!<br> Deshalb <i>sollten nur Objekte angemeldet werden, die Instanzen des
     * EDU-<code>TICKER</code>-Interfaces sind!!</i>
     *
     * @param o                   Das anzumeldende Objekt, dessen Tickermethode aufgerufen werden soll.<br> Es
     *                            <b>MUSS</b>
     *                            eine Methode <code>tick()</code> haben.
     * @param intervallInSekunden Das Intervall in Sekunden, in dem das anzumeldende Objekt aufgerufen.
     */
    @API
    public void tickerAnmelden(Ticker o, float intervallInSekunden) {
        getActiveScene().addEduTicker(o, intervallInSekunden);
    }

    /**
     * Meldet einen "Ticker" ab.
     *
     * @param o Das Angemeldete "Ticker"-Objekt, das nun nicht mehr aufgerufen werden soll.
     *
     * @see #tickerAnmelden(Ticker, float)
     */
    @API
    public void tickerAbmelden(Ticker o) {
        getActiveScene().removeEduTicker(o);
    }

    /**
     * Meldet ein Objekt an, dass zu jedem Frame-Update <b>in der aktuell aktiven Szene</b> durch Aufruf der Methode
     * <code>frameUpdateReagieren(int ms)</code> informiert wird (Parameter gibt die Anzahl an Millisekunden an,
     * die seit dem letzten Frame-Update vergangen sind.
     *
     * @param o Ein beliebiges Objekt. Hat das Objekt keine Methode mit der Signatur
     *          <code>frameUpdateReagieren(int)</code>, so passiert nichts. Andernfalls wird ab sofort zu jedem
     *          Frame-Update der <b>aktuellen</b> Szene die Methode ausgeführt.
     *
     * @see #frameUpdateReagierbarAbmelden(FrameUpdateReagierbar)
     */
    @API
    public void frameUpdateReagierbarAnmelden(FrameUpdateReagierbar o) {
        getActiveScene().addEduFrameUpdateListener(o);
    }

    /**
     * Entfernt einen pro-forma Frameupdate-Listener von der <b>aktiven</b> Szene.
     *
     * @param o Das zu entfernende Objekt. War es nie angemeldet, so passiert nichts.
     *
     * @see #frameUpdateReagierbarAnmelden(FrameUpdateReagierbar)
     */
    @API
    public void frameUpdateReagierbarAbmelden(FrameUpdateReagierbar o) {
        getActiveScene().removeEduFrameUpdateListener(o);
    }

    /**
     * Meldet einen MausRad-Listener an der <b>aktiven</b> Szene an.
     *
     * @param o Ein Objekt mit einer Methode mit Signatur <code>mausRadReagieren(float)</code>
     *
     * @see #mausRadReagierbarAbmelden(MausRadReagierbar)
     */
    @API
    public void mausRadReagierbarAnmelden(MausRadReagierbar o) {
        getActiveScene().addEduMouseWheelListener(o);
    }

    /**
     * Meldet einen MausRad-Listener an der <b>aktiven</b> Szene ab.
     *
     * @param o Der abzumeldende Mausrad-Listener
     *
     * @see #mausRadReagierbarAnmelden(MausRadReagierbar)
     */
    @API
    public void mausRadReagierbarAbmelden(MausRadReagierbar o) {
        getActiveScene().removeEduMouseWheelListener(o);
    }

    /**
     * Gibt die X-Koordinate der Maus auf der Spielebene an.
     *
     * @return Die X-Koordinate der Maus auf der Spielebene (in Meter)
     *
     * @see #aktuelleMausPositionY()
     */
    @API
    public float aktuelleMausPositionX() {
        return getActiveScene().getMousePosition().x;
    }

    /**
     * Gibt die Y-Koordinate der Maus auf der Spielebene an.
     *
     * @return Die Y-Koordinate der Maus auf der Spielebene (in Meter)
     *
     * @see #aktuelleMausPositionX()
     */
    @API
    public float aktuelleMausPositionY() {
        return getActiveScene().getMousePosition().y;
    }
}

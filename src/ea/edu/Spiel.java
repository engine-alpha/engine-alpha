package ea.edu;

import ea.Game;
import ea.Vector;
import ea.actor.Actor;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.util.Logger;
import org.jbox2d.common.Vec2;

import java.awt.*;
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
            return Color.BLACK;
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

    static {
        //Fülle alle Farbzuweisungen hinzu
        addC("gelb", Color.YELLOW);
        addC("weiss", Color.WHITE);
        stringToColor.put("weiß", Color.WHITE);
        addC("orange", Color.ORANGE);
        addC("grau", Color.GRAY);
        addC("gruen", Color.GREEN);
        stringToColor.put("grün", Color.GREEN);
        addC("blau", Color.BLUE);
        addC("rot", Color.RED);
        addC("pink", Color.PINK);
        addC("magenta", Color.MAGENTA);
        stringToColor.put("lila", Color.MAGENTA);
        addC("cyan", Color.CYAN);
        stringToColor.put("tuerkis", Color.CYAN);
        stringToColor.put("türkis", Color.CYAN);
        addC("dunkelgrau", Color.DARK_GRAY);
        addC("hellgrau", Color.LIGHT_GRAY);
        addC("braun", new Color(110, 68, 14));

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

    /* ~~ Scene Transitions ~~ */

    private static EduScene activeScene;

    static EduScene getActiveScene() {
        if (activeScene == null) {
            activeScene = new EduScene();
            activeScene.setGravity(new Vector(0, -9.81f));
            new Thread(() -> Game.start(frame_width, frame_height, activeScene)).start();
        }
        return activeScene;
    }

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
    public void setzeSzene(String szenenName) {
        EduScene scene = sceneMap.get(szenenName);
        if (scene == null) {
            Logger.error("EDU", "Konnte keine Szene mit dem Namen " + szenenName + " finden.");
            return;
        }
        setActiveScene(scene);
    }

    /* ~~~ CAMERA CONTROL ~~~ */

    public void verschiebeKamera(float dX, float dY) {
        getActiveScene().getCamera().move(dX, dY);
    }

    public void setzeKameraZoom(float zoom) {
        getActiveScene().getCamera().setZoom(zoom);
    }

    public float nenneKameraZoom() {
        return getActiveScene().getCamera().getZoom();
    }

    public void setzeKameraFokus(Actor focus) {
        getActiveScene().getCamera().setFocus(focus);
    }

    public void rotiereKamera(float winkelInBogenmass) {
        getActiveScene().getCamera().rotate(winkelInBogenmass);
    }

    public void setzeKameraRotation(float winkelInBogenmass) {
        getActiveScene().getCamera().rotateTo(winkelInBogenmass);
    }

    /* ~~~ GLOBAL WORLD PHYSICS ~~~ */

    public void setzeSchwerkraft(float schwerkraft) {
        getActiveScene().getWorldHandler().getWorld().setGravity(new Vec2(0, -schwerkraft));
    }

    public void setzePixelProMeter(float pixelProMeter) {
        getActiveScene().getWorldHandler().setPixelPerMeter(pixelProMeter);
    }

    /* ~~~ Dialogues ~~~ */

    public void nachricht(String nachricht) {
        Game.showMessage(nachricht, DEFAULT_EDU_DIALOG_TITLE);
    }

    public boolean frageJaNein(String frage) {
        return Game.requestYesNo(frage, DEFAULT_EDU_DIALOG_TITLE);
    }

    public boolean nachrichtOkAbbrechen(String frage) {
        return Game.requestOkCancel(frage, DEFAULT_EDU_DIALOG_TITLE);
    }

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
     * @param client     Das anzumeldende Objekt. Dieses wird ab sofort ueber jeden Mausklick informiert.
     * @param linksklick Falls auf Linksklicks reagiert werden soll <code>true</code>, sonst <code>false</code>
     */
    public void klickReagierbarAnmeldenSzene(Object client, boolean linksklick) {
        getActiveScene().addEduClickListener(client, linksklick);
    }

    /**
     *
     * @param client
     * @param linksklick
     */
    public void klickReagierbarAnmelden(Object client, boolean linksklick) {
        klickReagierbarAnmeldenSzene(client, linksklick);
    }

    /**
     * Meldet ein Objekt an, das ab sofort auf Tastendruck reagieren wird.<br> Intern laesst sich
     * theoretisch ein Objekt <b>JEDER</b> Klasse anmelden!<br> Deshalb <i>sollten nur Objekte
     * angemeldet werden, die Instanzen des EDU-<code>TASTENREAGIERBARANMELDEN</code>-Interfaces
     * sind!!</i>
     *
     * @param o Das anzumeldende Objekt. Dieses wird ab sofort ueber jeden Tastendruck informiert.
     */
    public void tastenReagierbarAnmeldenSzene(Object o) {
        getActiveScene().addEduKeyListener(o);
    }

    public void tastenReagierbarAnmelden(Object o) {
        getActiveScene().addEduKeyListener(o);
    }

    /**
     * Meldet ein Objekt zum Ticken an. Intern laesst sich theoretisch ein Objekt <b>JEDER</b>
     * Klasse anmelden!<br> Deshalb <i>sollten nur Objekte angemeldet werden, die Instanzen des
     * EDU-<code>TICKER</code>-Interfaces sind!!</i>
     *
     * @param o         Das anzumeldende Objekt, dessen Tickermethode aufgerufen werden soll.<br> Es <b>MUSS</b>
     *                  eine Methode <code>tick()</code> haben.
     * @param intervall Das Intervall in Millisekunden, in dem das anzumeldende Objekt aufgerufen.
     */
    public void tickerAnmelden(Object o, int intervall) {
        getActiveScene().addEduTicker(o, intervall);
    }

    /**
     * Meldet einen "Ticker" ab.
     *
     * @param o Das Angemeldete "Ticker"-Objekt, das nun nicht mehr aufgerufen werden soll.
     *
     * @see #tickerAnmelden(Object, int)
     */
    public void tickerAbmelden(Object o) {
        getActiveScene().removeEduTicker(o);
    }
}

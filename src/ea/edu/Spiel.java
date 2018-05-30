package ea.edu;

import ea.EngineAlpha;
import ea.Game;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.util.Logger;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;

/**
 * Diese Klasse steuert die EDU-Version. Sie ist Schnittstelle für:
 * <ul>
 *     <li>Szenen-Management</li>
 *     <li>Listener-Management</li>
 * </ul>
 * @author Michael Andonie
 */
public class Spiel {

    private static final HashMap<String, Color> stringToColor = new HashMap<>();
    private static final HashMap<Color, String> colorToString = new HashMap<>();

    private static final HashMap<String, EduScene> sceneMap = new HashMap<>();

    /* ~~~ EDU-UTILITY ~~~*/

    /**
     * Fügt eine String-awt/Color-Zuordnung zu.
     * @param string    Ein String (lowercase)
     * @param color     Eine Color
     */
    private static void addC(String string, Color color) {
        stringToColor.put(string, color);
        colorToString.put(color, string);
    }

    /**
     * Diese Methode ordnet einem String ein Color-Objekt zu.<br /> Hierdurch ist in den Klassen
     * außerhalb der Engine keine awt-Klasse nötig.
     *
     * @param t
     * 		Der Name der Farbe.
     *
     * @return Das Farbobjekt zum String; ist Color.black bei unzuordnembaren String
     */
    @NoExternalUse
    public static Color stringToColor (String t) {
        Color res = stringToColor.get(t.toLowerCase());
        if(res == null) return Color.BLACK;
        else return res;
    }

    /**
     * Reverse-Lookup für Farbzuordnung
     * @param color
     * @return
     */
    public static String colorToString(Color color) {
        String res = colorToString.get(color);
        if(res==null) return "unbekannt";
        else return res;
    }


    /* ~~~ STEUERUNG & KAPSELUNG ~~~*/

    static {
        //Fülle alle Farbzuweisungen hinzu
        addC("gelb", Color.YELLOW);
        addC("weiss", Color.WHITE); stringToColor.put("weiß", Color.WHITE);
        addC("orange", Color.ORANGE);
        addC("grau", Color.GRAY);
        addC("gruen", Color.GREEN); stringToColor.put("grün", Color.GREEN);
        addC("blau", Color.BLUE);
        addC("rot", Color.RED);
        addC("pink", Color.PINK);
        addC("magenta", Color.MAGENTA); stringToColor.put("lila", Color.MAGENTA);
        addC("cyan", Color.CYAN); stringToColor.put("tuerkis", Color.CYAN); stringToColor.put("türkis", Color.CYAN);
        addC("dunkelgrau", Color.DARK_GRAY);
        addC("hellgrau", Color.LIGHT_GRAY);
        addC("braun", new Color(110,68,14));

        //Startup-Game

    }

    /* ~~ Game Frame ~~ */

    /**
     * Setzt die Groesse des Engine-Fensters.
     * @param breite    Fenster-Breite
     * @param hoehe     Fenster-Hoehe
     */
    @API
    public static void setzeFensterGroesse(int breite, int hoehe) {
        Game.setFrameSize(breite, hoehe);
    }

    /**
     * Stellt, ob das Hilfs-Raster, das die Koordinatenachsen visualisiert, dargestellt werden soll.
     * @param sichtbar  ist dieser Wert <code>true</code>, wird das Raster dargestellt. Ist er <code>false</code>,
     *                  wird das Raster deaktiviert.
     */
    @API
    public void rasterSichtbarSetzen(boolean sichtbar) {
        EngineAlpha.setDebug(sichtbar);
    }

    /* ~~ Scene Transitions ~~ */

    private static EduScene activeScene;

    static EduScene getActiveScene() {
        if(activeScene == null) {
            activeScene = new EduScene();
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
     * @param name Der Name für die Szene.
     * @see #szeneSetzen(String)
     */
    public static void szeneBennen(String name) {
        if(getActiveScene().getSceneName() != null) {
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
     *     <li>Wird der Bildschirm "geleert"</li>
     *     <li>Geht die vorige Szene "verloren", wenn sie nicht mit benannt wurde.</li>
     *     <li>Werden alle grafischen Objekte, die ab sofort erstellt werden, in der neuen Szene eingesetzt.</li>
     * </ul>
     */
    public static void szeneNeu() {
        setActiveScene(new EduScene());
    }

    /**
     * Setzt die aktive Szene. Wurde eine Szene benannt, so bleibt sie gespeichert und kann wieder aktiv gesetzt
     * werden.
     * @param szenenName    der Name der aktiv zu setzenden Szene.
     * @see #szeneBennen(String)
     */
    public static void szeneSetzen(String szenenName) {
        EduScene scene = sceneMap.get(szenenName);
        if(scene == null) {
            Logger.error("EDU", "Konnte keine Szene mit dem Namen " + szenenName + " finden.");
            return;
        }
        setActiveScene(scene);
    }

    /* ~~~ Listener Addition ~~~ */

    /**
     * Meldet ein Objekt an, das ab sofort auf Mausklicks reagieren wird.<br /> Intern laesst sich
     * theoretisch ein Objekt <b>JEDER</b> Klasse anmelden!<br /> Deshalb <i>sollten nur Objekte
     * angemeldet werden, die Instanzen eines interfaces EDU-<code>KLICKREAGIERBAR</code>-Interfaces
     * sind!!</i><br /> <br /> <br /> Example:<br /> <b /> <code>KLICKREAGIERBAR { <br /> //Eine
     * Methode diesen Namens MUSS existieren!!<br /> public abstract void klickReagieren(int x, int
     * y);<br /> }</code>
     *
     * @param client
     * 		Das anzumeldende Objekt. Dieses wird ab sofort ueber jeden Mausklick informiert.
     * @param linksklick
     * 		Falls auf Linksklicks reagiert werden soll <code>true</code>, sonst <code>false</code>
     */
    public void klickReagierbarAnmelden (Object client, boolean linksklick) {
        getActiveScene().addEduClickListener(client, linksklick);
    }

    /**
     * Meldet ein Objekt an, das ab sofort auf Tastendruck reagieren wird.<br /> Intern laesst sich
     * theoretisch ein Objekt <b>JEDER</b> Klasse anmelden!<br /> Deshalb <i>sollten nur Objekte
     * angemeldet werden, die Instanzen des EDU-<code>TASTENREAGIERBARANMELDEN</code>-Interfaces
     * sind!!</i>
     *
     * @param o
     * 		Das anzumeldende Objekt. Dieses wird ab sofort ueber jeden Tastendruck informiert.
     */
    public void tastenReagierbarAnmelden (Object o) {
        getActiveScene().addEduKeyListener(o);
    }

    /**
     * Meldet ein Objekt zum Ticken an. Intern laesst sich theoretisch ein Objekt <b>JEDER</b>
     * Klasse anmelden!<br /> Deshalb <i>sollten nur Objekte angemeldet werden, die Instanzen des
     * EDU-<code>TICKER</code>-Interfaces sind!!</i>
     *
     * @param o
     * 		Das anzumeldende Objekt, dessen Tickermethode aufgerufen werden soll.<br /> Es <b>MUSS</b>
     * 		eine Methode <code>tick()</code> haben.
     * @param intervall
     * 		Das Intervall in Millisekunden, in dem das anzumeldende Objekt aufgerufen.
     */
    public void tickerAnmelden (Object o, int intervall) {
        getActiveScene().addEduTicker(o, intervall);
    }
}

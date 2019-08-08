package ea.edu;

import ea.Game;
import ea.Vector;
import ea.actor.Actor;
import ea.edu.event.*;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Diese Klasse steuert die EDU-Version. Sie ist Schnittstelle für:
 * <ul>
 * <li>Szenen-Management</li>
 * <li>Listener-Management</li>
 * </ul>
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public class Spiel {

    public static final String STANDARD_TITEL = "Engine Alpha - EDU Version";
    public static final int STANDARD_BREITE = 800;
    public static final int STANDARD_HOEHE = 600;

    static final HashMap<Actor, EduActor> actorToInterfaceMap = new HashMap<>();

    private static final HashMap<String, Color> farben = new HashMap<>();
    private static final HashMap<String, EduScene> szenen = new HashMap<>();

    private static final Color COLOR_LILA = new Color(145, 19, 255);
    private static final Color COLOR_ORANGE = new Color(255, 116, 0);
    private static final Color COLOR_BRAUN = new Color(119, 77, 50);
    private static final Color COLOR_HELLBLAU = new Color(0, 194, 255);
    private static final Color COLOR_DUNKELBLAU = new Color(21, 0, 137);
    private static final Color COLOR_HELLGRUEN = new Color(157, 255, 0);
    private static final Color COLOR_DUNKELGRUEN = new Color(11, 71, 0);

    private static int fensterBreite = STANDARD_BREITE;
    private static int fensterHoehe = STANDARD_HOEHE;

    @API
    public static void parallel(Runnable runnable) {
        new Thread(runnable).start();
    }

    @API
    public static <T> void parallel(Consumer<T> runnable, T argument) {
        parallel(() -> runnable.accept(argument));
    }

    @API
    public static <T1, T2> void parallel(BiConsumer<T1, T2> runnable, T1 argument1, T2 argument2) {
        parallel(() -> runnable.accept(argument1, argument2));
    }

    /**
     * Fügt eine String-awt/Color-Zuordnung zu.
     *
     * @param string Ein String (lowercase)
     * @param color  Eine Color
     */
    private static void farbeHinzufuegen(String string, Color color) {
        farben.put(string, color);
    }

    /**
     * Diese Methode ordnet einem String ein Color-Objekt zu.<br> Hierdurch ist in den Klassen
     * außerhalb der Engine keine awt-Klasse nötig.
     *
     * @param farbname Der Name der Farbe.
     *
     * @return Das Farbobjekt zum String; ist Color.black bei unzuordnembaren String
     */
    @Internal
    public static Color konvertiereVonFarbname(String farbname) {
        Color color = farben.get(farbname.toLowerCase());
        if (color == null) {
            throw new IllegalArgumentException("Eine Farbe mit dem Namen " + farbname + " ist der Engine nicht bekannt");
        }

        return color;
    }

    /**
     * Reverse-Lookup für Farbzuordnung.
     */
    @Internal
    public static String konvertiereZuFarbname(Color color) {
        return farben.entrySet().stream() //
                .filter(entry -> entry.getValue().equals(color)) //
                .map(Map.Entry::getKey) //
                .findFirst() //
                .orElse("unbekannt");
    }

    static {
        // Fülle alle Farbzuweisungen hinzu
        farbeHinzufuegen("gelb", Color.YELLOW);
        farbeHinzufuegen("weiss", Color.WHITE);
        farbeHinzufuegen("weiß", Color.WHITE);
        farbeHinzufuegen("orange", COLOR_ORANGE);
        farbeHinzufuegen("grau", Color.GRAY);
        farbeHinzufuegen("gruen", Color.GREEN);
        farbeHinzufuegen("grün", Color.GREEN);
        farbeHinzufuegen("blau", Color.BLUE);
        farbeHinzufuegen("rot", Color.RED);
        farbeHinzufuegen("pink", Color.PINK);
        farbeHinzufuegen("magenta", Color.MAGENTA);
        farbeHinzufuegen("lila", COLOR_LILA);
        farbeHinzufuegen("cyan", Color.CYAN);
        farbeHinzufuegen("tuerkis", Color.CYAN);
        farbeHinzufuegen("türkis", Color.CYAN);
        farbeHinzufuegen("dunkelgrau", Color.DARK_GRAY);
        farbeHinzufuegen("hellgrau", Color.LIGHT_GRAY);
        farbeHinzufuegen("braun", COLOR_BRAUN);
        farbeHinzufuegen("schwarz", Color.BLACK);
        farbeHinzufuegen("hellblau", COLOR_HELLBLAU);
        farbeHinzufuegen("dunkelblau", COLOR_DUNKELBLAU);
        farbeHinzufuegen("hellgruen", COLOR_HELLGRUEN);
        farbeHinzufuegen("hellgrün", COLOR_HELLGRUEN);
        farbeHinzufuegen("dunkelgruen", COLOR_DUNKELGRUEN);
        farbeHinzufuegen("dunkelgrün", COLOR_DUNKELGRUEN);
    }

    /**
     * Setzt die Größe des Engine-Fensters.
     *
     * @param breite Fenster-Breite
     * @param hoehe  Fenster-Hoehe
     */
    @API
    public static void setzeFensterGroesse(int breite, int hoehe) {
        if (activeScene != null) {
            throw new RuntimeException("setzeFensterGroesse() kann nur aufgerufen werden, bevor das erste grafische Objekt erzeugt wurde");
        }

        if (breite <= 0 || hoehe <= 0) {
            throw new RuntimeException("Die Fenstermaße (Breite sowie Höhe) müssen jeweils größer als 0 sein. Eingabe war: " + breite + " Breite und " + hoehe + " Höhe");
        }

        fensterBreite = breite;
        fensterHoehe = hoehe;
    }

    /**
     * Setzt, ob das Hilfs-Raster, das die Koordinatenachsen visualisiert, dargestellt werden soll.
     *
     * @param sichtbar ist dieser Wert <code>true</code>, wird das Raster dargestellt. Ist er <code>false</code>,
     *                 wird das Raster deaktiviert.
     */
    @API
    public void setzeRasterSichtbar(boolean sichtbar) {
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

    @SuppressWarnings ( "StaticVariableOfConcreteClass" )
    private static EduScene activeScene;

    @Internal
    static EduScene getActiveScene() {
        if (activeScene == null) {
            activeScene = new EduScene();
            parallel(() -> Game.start(fensterBreite, fensterHoehe, activeScene));
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
     * @see #setzeAktiveSzene(String)
     */
    @API
    public void benenneAktiveSzene(String name) {
        EduScene activeScene = getActiveScene();
        if (activeScene.getSceneName() != null) {
            throw new RuntimeException("Die Szene hat bereits einen Namen: " + activeScene.getSceneName());
        }

        activeScene.setSceneName(name);
        szenen.put(name, activeScene);
    }

    /**
     * Erstellt eine neue (leere) Szene und setzt diese aktiv.
     * Damit
     * <ul>
     * <li>Wird der Bildschirm "geleert"</li>
     * <li>Geht die vorige Szene "verloren", wenn sie nicht mit benannt wurde.</li>
     * <li>Werden alle grafischen Objekte, die ab sofort erstellt werden, in der neuen Szene eingesetzt.</li>
     * </ul>
     *
     * @see #benenneAktiveSzene(String)
     */
    @API
    public void erstelleNeueSzene() {
        setActiveScene(new EduScene());
    }

    /**
     * Setzt die aktive Szene. Wurde eine Szene benannt, so bleibt sie gespeichert und kann wieder aktiv gesetzt
     * werden.
     *
     * @param name der Name der aktiv zu setzenden Szene.
     *
     * @see #benenneAktiveSzene(String)
     */
    @API
    public void setzeAktiveSzene(String name) {
        EduScene scene = szenen.get(name);
        if (scene == null) {
            throw new RuntimeException("Konnte keine Szene mit dem Namen '" + name + "' finden");
        }

        setActiveScene(scene);
    }

    /**
     * Gibt die Namen aller gespeicherten Szenen aus.
     *
     * @return Ein String Array. Jeder Eintrag entspricht dem Namen einer der gespeicherten Szenen des Spiels.
     * Szenen, die nicht benannt wurden, haben keinen Namen und werden daher nicht mit aufgelistet.
     *
     * @see #erstelleNeueSzene()
     * @see #benenneAktiveSzene(String)
     */
    @API
    public String[] nenneSzenenNamen() {
        return szenen.keySet().toArray(new String[0]);
    }

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
    public void erstelleNeueEbene(String ebenenName, int ebenenPosition) {
        getActiveScene().addLayer(ebenenName, ebenenPosition);
    }

    @API
    public void setzeEbenenParallaxe(String ebenenName, float x, float y, float zoom) {
        getActiveScene().setLayerParallax(ebenenName, x, y, zoom);
    }

    @API
    public void setzeEbenenZeitverzerrung(String ebenenName, float zeitverzerrung) {
        getActiveScene().setLayerTimeDistort(ebenenName, zeitverzerrung);
    }

    @API
    public void setzeAktiveEbene(String ebenenName) {
        getActiveScene().setActiveLayer(ebenenName);
    }

    @API
    public void setzeAktiveEbeneAufHauptebene() {
        getActiveScene().resetToMainLayer();
    }

    /**
     * Gibt die Namen aller Layer der <b>aktiven</b> Szene aus.
     *
     * @return Ein String Array. Jeder Eintrag entspricht dem Namen einer Ebene in der aktiven Szene des Spiels.
     *
     * @see #erstelleNeueEbene(String, int)
     * @see #setzeAktiveSzene(String)
     */
    @API
    public String[] nenneEbenennamenVonAktiverSzene() {
        return getActiveScene().getLayerNames();
    }

    @API
    public void verschiebeKamera(float x, float y) {
        getActiveScene().getCamera().moveBy(x, y);
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
    public void setzeKameraFokus(EduActor fokus) {
        getActiveScene().getCamera().setFocus(fokus.getActor());
    }

    @API
    public void rotiereKamera(float winkelInGrad) {
        getActiveScene().getCamera().rotateBy(winkelInGrad);
    }

    @API
    public void setzeKameraRotation(float winkelInGrad) {
        getActiveScene().getCamera().rotateTo(winkelInGrad);
    }

    @API
    public void setzeSchwerkraft(float schwerkraft) {
        getActiveScene().getActiveLayer().setGravity(new Vector(0, -schwerkraft));
    }

    @API
    public void zeigeNachricht(String nachricht) {
        Game.showMessage(nachricht, STANDARD_TITEL);
    }

    @API
    public boolean zeigeNachrichtMitBestaetigung(String frage) {
        return Game.requestOkCancel(frage, STANDARD_TITEL);
    }

    @API
    public boolean zeigeNachrichtMitJaNein(String frage) {
        return Game.requestYesNo(frage, STANDARD_TITEL);
    }

    @API
    public String zeigeNachrichtMitEingabe(String nachricht) {
        return Game.requestStringInput(nachricht, STANDARD_TITEL);
    }

    /**
     * Meldet ein Objekt an, das ab sofort auf Mausklicks reagieren wird.<br> Intern laesst sich
     * theoretisch ein Objekt <b>JEDER</b> Klasse anmelden!<br> Deshalb <i>sollten nur Objekte
     * angemeldet werden, die Instanzen eines interfaces EDU-<code>KLICKREAGIERBAR</code>-Interfaces
     * sind!!</i><br> <br> <br> Example:<br> <br> <code>KLICKREAGIERBAR { <br> //Eine
     * Methode diesen Namens MUSS existieren!!<br> public abstract void klickReagieren(int x, int
     * y);<br> }</code>
     *
     * @param mausKlickReagierbar Das anzumeldende Objekt. Dieses wird ab sofort above jeden Mausklick informiert.
     */
    @API
    public void registriereMausKlickReagierbar(MausKlickReagierbar mausKlickReagierbar) {
        getActiveScene().addEduClickListener(mausKlickReagierbar);
    }

    @API
    public void entferneMausKlickReagierbar(MausKlickReagierbar mausKlickReagierbar) {
        getActiveScene().removeEduClickListener(mausKlickReagierbar);
    }

    /**
     * Meldet ein Objekt an, das ab sofort auf Tastendruck reagieren wird.<br> Intern laesst sich
     * theoretisch ein Objekt <b>JEDER</b> Klasse anmelden!<br> Deshalb <i>sollten nur Objekte
     * angemeldet werden, die Instanzen des EDU-<code>TASTENREAGIERBARANMELDEN</code>-Interfaces
     * sind!!</i>
     *
     * @param tastenReagierbar Das anzumeldende Objekt. Dieses wird ab sofort above jeden Tastendruck informiert.
     */
    @API
    public void registriereTastenReagierbar(TastenReagierbar tastenReagierbar) {
        getActiveScene().addEduKeyListener(tastenReagierbar);
    }

    @API
    public void entferneTastenReagierbar(TastenReagierbar tastenReagierbar) {
        getActiveScene().removeEduKeyListener(tastenReagierbar);
    }

    /**
     * Meldet ein Objekt zum Ticken an. Intern laesst sich theoretisch ein Objekt <b>JEDER</b>
     * Klasse anmelden!<br> Deshalb <i>sollten nur Objekte angemeldet werden, die Instanzen des
     * EDU-<code>TICKER</code>-Interfaces sind!!</i>
     *
     * @param ticker              Das anzumeldende Objekt, dessen Tickermethode aufgerufen werden soll.<br> Es
     *                            <b>MUSS</b>
     *                            eine Methode <code>tick()</code> haben.
     * @param intervallInSekunden Das Intervall in Sekunden, in dem das anzumeldende Objekt aufgerufen.
     */
    @API
    public void registriereTicket(float intervallInSekunden, Ticker ticker) {
        getActiveScene().addEduTicker(intervallInSekunden, ticker);
    }

    /**
     * Meldet einen "Ticker" ab.
     *
     * @param ticket Das Angemeldete "Ticker"-Objekt, das nun nicht mehr aufgerufen werden soll.
     *
     * @see #registriereTicket(float, Ticker)
     */
    @API
    public void entferneTicker(Ticker ticket) {
        getActiveScene().removeEduTicker(ticket);
    }

    /**
     * Meldet ein Objekt an, dass zu jedem Frame-Update <b>in der aktuell aktiven Szene</b> durch Aufruf der Methode
     * <code>bildAktualisierungReagieren(int ms)</code> informiert wird (Parameter gibt die Anzahl an Millisekunden an,
     * die seit dem letzten Frame-Update vergangen sind.
     *
     * @param bildAktualisierungReagierbar Ein beliebiges Objekt. Hat das Objekt keine Methode mit der Signatur
     *                                     <code>bildAktualisierungReagieren(int)</code>, so passiert nichts.
     *                                     Andernfalls wird ab sofort zu jedem
     *                                     Frame-Update der <b>aktuellen</b> Szene die Methode ausgeführt.
     *
     * @see #entferneBildAktualisierungReagierbar(BildAktualisierungReagierbar)
     */
    @API
    public void registriereBildAktualisierungReagierbar(BildAktualisierungReagierbar bildAktualisierungReagierbar) {
        getActiveScene().addEduFrameUpdateListener(bildAktualisierungReagierbar);
    }

    /**
     * Entfernt einen pro-forma Frameupdate-Listener von der <b>aktiven</b> Szene.
     *
     * @param bildAktualisierungReagierbar Das zu entfernende Objekt. War es nie angemeldet, so passiert nichts.
     *
     * @see #registriereBildAktualisierungReagierbar(BildAktualisierungReagierbar)
     */
    @API
    public void entferneBildAktualisierungReagierbar(BildAktualisierungReagierbar bildAktualisierungReagierbar) {
        getActiveScene().removeEduFrameUpdateListener(bildAktualisierungReagierbar);
    }

    /**
     * Meldet einen MausRad-Listener an der <b>aktiven</b> Szene an.
     *
     * @param mausRadReagierbar Ein Objekt mit einer Methode mit Signatur <code>mausRadReagieren(float)</code>
     *
     * @see #entferneMausRadReagierbar(MausRadReagierbar)
     */
    @API
    public void registriereMausRadReagierbar(MausRadReagierbar mausRadReagierbar) {
        getActiveScene().addEduMouseWheelListener(mausRadReagierbar);
    }

    /**
     * Meldet einen MausRad-Listener an der <b>aktiven</b> Szene ab.
     *
     * @param mausRadReagierbar Der abzumeldende Mausrad-Listener
     *
     * @see #registriereMausRadReagierbar(MausRadReagierbar)
     */
    @API
    public void entferneMausRadReagierbar(MausRadReagierbar mausRadReagierbar) {
        getActiveScene().removeEduMouseWheelListener(mausRadReagierbar);
    }

    /**
     * Gibt die X-Koordinate der Maus auf der Spielebene an.
     *
     * @return Die X-Koordinate der Maus auf der Spielebene (in Meter)
     *
     * @see #nenneMausPositionY()
     */
    @API
    public float nenneMausPositionX() {
        return getActiveScene().getMousePosition().x;
    }

    /**
     * Gibt die Y-Koordinate der Maus auf der Spielebene an.
     *
     * @return Die Y-Koordinate der Maus auf der Spielebene (in Meter)
     *
     * @see #nenneMausPositionX()
     */
    @API
    public float nenneMausPositionY() {
        return getActiveScene().getMousePosition().y;
    }
}

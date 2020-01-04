package ea.edu;

import ea.Game;
import ea.Vector;
import ea.edu.event.*;
import ea.edu.internal.EduScene;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.util.Logger;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

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
@API
public class Spiel {

    public static final String STANDARD_TITEL = "Engine Alpha - EDU Version";
    public static final int STANDARD_BREITE = 800;
    public static final int STANDARD_HOEHE = 600;

    private static final HashMap<String, Color> farben = new HashMap<>();
    private static final HashMap<String, EduScene> szenen = new HashMap<>();

    private static final Color COLOR_LILA = new Color(145, 19, 255);
    private static final Color COLOR_ORANGE = new Color(255, 116, 0);
    private static final Color COLOR_BRAUN = new Color(119, 77, 50);
    private static final Color COLOR_HELLBLAU = new Color(0, 194, 255);
    private static final Color COLOR_DUNKELBLAU = new Color(21, 0, 137);
    private static final Color COLOR_HELLGRUEN = new Color(157, 255, 0);
    private static final Color COLOR_DUNKELGRUEN = new Color(11, 71, 0);
    /**
     * Der Threshold-Zeitintervalwert, ab dem eine Warnung ausgegeben wird für Tickeranmeldung
     */
    private static final double TICKER_THRESHOLD = 0.09;

    private static int fensterBreite = STANDARD_BREITE;
    private static int fensterHoehe = STANDARD_HOEHE;

    @SuppressWarnings ( "StaticVariableOfConcreteClass" )
    private static EduScene activeScene;

    /**
     * Führt das übergebene Runnable parallel aus.
     * <p>
     * Die einfachste Verwendung ist über eine Methodenreferenz: {@code Spiel.parallel(this::schalteAmpel)}
     */
    @API
    public static void parallel(Runnable runnable) {
        new Thread(runnable).start();
    }

    /**
     * Fügt eine String-awt/Color-Zuordnung zu.
     *
     * @param string Farbname (Groß-/Kleinschreibung egal)
     * @param color  Entsprechendes Java-AWT-Color-Objekt
     */
    @API
    public static void registriereFarbe(String string, Color color) {
        farben.put(string.toLowerCase(), color);
    }

    /**
     * @return Listet alle verfügbaren Farbnamen.
     */
    @API
    public static String[] nenneFarben() {
        return farben.keySet().toArray(new String[0]);
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
        registriereFarbe("gelb", Color.YELLOW);
        registriereFarbe("weiss", Color.WHITE);
        registriereFarbe("weiß", Color.WHITE);
        registriereFarbe("orange", COLOR_ORANGE);
        registriereFarbe("grau", Color.GRAY);
        registriereFarbe("gruen", Color.GREEN);
        registriereFarbe("grün", Color.GREEN);
        registriereFarbe("blau", Color.BLUE);
        registriereFarbe("rot", Color.RED);
        registriereFarbe("pink", Color.PINK);
        registriereFarbe("magenta", Color.MAGENTA);
        registriereFarbe("lila", COLOR_LILA);
        registriereFarbe("cyan", Color.CYAN);
        registriereFarbe("tuerkis", Color.CYAN);
        registriereFarbe("türkis", Color.CYAN);
        registriereFarbe("dunkelgrau", Color.DARK_GRAY);
        registriereFarbe("hellgrau", Color.LIGHT_GRAY);
        registriereFarbe("braun", COLOR_BRAUN);
        registriereFarbe("schwarz", Color.BLACK);
        registriereFarbe("hellblau", COLOR_HELLBLAU);
        registriereFarbe("dunkelblau", COLOR_DUNKELBLAU);
        registriereFarbe("hellgruen", COLOR_HELLGRUEN);
        registriereFarbe("hellgrün", COLOR_HELLGRUEN);
        registriereFarbe("dunkelgruen", COLOR_DUNKELGRUEN);
        registriereFarbe("dunkelgrün", COLOR_DUNKELGRUEN);
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

    @Internal
    public static EduScene getActiveScene() {
        if (activeScene == null) {
            activeScene = new EduScene();
            parallel(() -> Game.start(fensterBreite, fensterHoehe, activeScene));
        }

        return activeScene;
    }

    @Internal
    private static void setActiveScene(EduScene eduScene) {
        activeScene = eduScene;
        Game.transitionToScene(activeScene);
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
        if (activeScene.getName() != null) {
            throw new RuntimeException("Die Szene hat bereits einen Namen: " + activeScene.getName());
        }

        activeScene.setName(name);
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
    public void erzeugeNeueSzene() {
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
     * @see #erzeugeNeueSzene()
     * @see #benenneAktiveSzene(String)
     */
    @API
    public String[] nenneSzenennamen() {
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
    public void erzeugeNeueEbene(String ebenenName, int ebenenPosition) {
        getActiveScene().addLayer(ebenenName, ebenenPosition);
    }

    @API
    public void setzeEbenenparallaxe(String ebenenName, double x, double y, double zoom) {
        getActiveScene().setLayerParallax(ebenenName, (float) x, (float) y, (float) zoom);
    }

    @API
    public void setzeEbenenzeitverzerrung(String ebenenName, double zeitverzerrung) {
        getActiveScene().setLayerTimeDistort(ebenenName, (float) zeitverzerrung);
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
     * @see #erzeugeNeueEbene(String, int)
     * @see #setzeAktiveSzene(String)
     */
    @API
    public String[] nenneEbenennamenVonAktiverSzene() {
        return getActiveScene().getLayerNames();
    }

    @API
    public void verschiebeKamera(double x, double y) {
        getActiveScene().getCamera().moveBy(new Vector(x, y));
    }

    @API
    public void setzeKamerazoom(double zoom) {
        getActiveScene().getCamera().setZoom((float) zoom);
    }

    @API
    public double nenneKamerazoom() {
        return getActiveScene().getCamera().getZoom();
    }

    @API
    public void setzeKamerafokus(EduActor fokus) {
        getActiveScene().getCamera().setFocus(fokus.getActor());
    }

    @API
    public void rotiereKamera(double grad) {
        getActiveScene().getCamera().rotateBy((float) grad);
    }

    @API
    public void setzeKamerarotation(double grad) {
        getActiveScene().getCamera().rotateTo((float) grad);
    }

    @API
    public void setzeSchwerkraft(double schwerkraft) {
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
     * Meldet ein Objekt an, das ab sofort auf Mausklicks reagieren wird.
     *
     * @param mausKlickReagierbar Das anzumeldende Objekt.
     */
    @API
    public void registriereMausKlickReagierbar(MausKlickReagierbar mausKlickReagierbar) {
        getActiveScene().addEduClickListener(mausKlickReagierbar);
    }

    /**
     * Entfernt ein Objekt wieder, sodass es nicht mehr auf Mausklicks reagiert.
     *
     * @param mausKlickReagierbar Das abzumeldende Objekt.
     */
    @API
    public void entferneMausKlickReagierbar(MausKlickReagierbar mausKlickReagierbar) {
        getActiveScene().removeEduClickListener(mausKlickReagierbar);
    }

    /**
     * Meldet ein Objekt an, das ab sofort auf Tasten reagieren wird.
     *
     * @param tastenReagierbar Das anzumeldende Objekt.
     */
    @API
    public void registriereTastenReagierbar(TastenReagierbar tastenReagierbar) {
        getActiveScene().addEduKeyListener(tastenReagierbar);
    }

    /**
     * Entfernt ein Objekt wieder, sodass es nicht mehr auf Tasten reagiert.
     *
     * @param tastenReagierbar Das abzumeldende Objekt.
     */
    @API
    public void entferneTastenReagierbar(TastenReagierbar tastenReagierbar) {
        getActiveScene().removeEduKeyListener(tastenReagierbar);
    }

    /**
     * Meldet ein Objekt zum Ticken an.
     *
     * @param ticker              Das anzumeldende Objekt, dessen Tickermethode aufgerufen werden soll.<br> Es
     *                            <b>MUSS</b>
     *                            eine Methode <code>tick()</code> haben.
     * @param intervallInSekunden Das Intervall in Sekunden, in dem das anzumeldende Objekt aufgerufen.
     */
    @API
    public void registriereTicker(double intervallInSekunden, Ticker ticker) {
        if (intervallInSekunden < TICKER_THRESHOLD) {
            Logger.warning("Du hast einen Ticker mit geringem Intervall angemeldet (" + intervallInSekunden + " s). Bei so Intervalwert nahe der Framerate können unerwünschte Effekte eintreten. Nutze stattdessen BildAktualisierungsReagierbar !", "EDU");
        }
        getActiveScene().addEduTicker((float) intervallInSekunden, ticker);
    }

    /**
     * Meldet einen "Ticker" ab.
     *
     * @param ticker Das Angemeldete "Ticker"-Objekt, das nun nicht mehr aufgerufen werden soll.
     *
     * @see #registriereTicker(double, Ticker)
     */
    @API
    public void entferneTicker(Ticker ticker) {
        getActiveScene().removeEduTicker(ticker);
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
    public double nenneMausPositionX() {
        return getActiveScene().getMousePosition().getX();
    }

    /**
     * Gibt die Y-Koordinate der Maus auf der Spielebene an.
     *
     * @return Die Y-Koordinate der Maus auf der Spielebene (in Meter)
     *
     * @see #nenneMausPositionX()
     */
    @API
    public double nenneMausPositionY() {
        return getActiveScene().getMousePosition().getY();
    }
}

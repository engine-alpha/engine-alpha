package ea.example.showcase;

import ea.Scene;
import ea.Vector;
import ea.actor.Actor;
import ea.actor.Circle;
import ea.actor.Rectangle;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.handle.Physics;
import ea.input.KeyListener;

import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Eine einfache Demonstration der Engine-Physik durch eine Ball-Wurf-Simulation. Es wird ein Ball (durch Wirkung
 * eines Impulses) geworfen.
 *
 * <h3>Nutzung der Simulation</h3>
 *
 * <p>Die Simulation kann gesteuert werden durch:</p>
 * <ul>
 *     <li>S-Key: Startet Simulation</li>
 *     <li>R-Key: Setzt Simulation zurück</li>
 *     <li>Die Keyn Z und U ändern den Zoom auf die Umgebung (rudimentär implementiert)</li>
 *     <li>D-Key: Toggelt den Debug-Modus (zeigt das Pixel-Raster)</li>
 * </ul>
 *
 * <h3>Anpassung der Parameter</h3>
 *
 * <p>Die Simulation arbeitet mit einigen physikalischen Parametern, die sich ändern lassen. Folgende Parameter sind
 * als Konstanten im Code definiert und können im angepasst werden:</p>
 * <ul>
 *     <li><code>DURCHMESSER</code>: Der Durchmesser des Circlees (hat keinen Einfluss auf die Masse.</li>
 *     <li><code>HOEHE_UEBER_BODEN</code>: Abstand zwischen dem untersten Punkt des Balls und dem Boden</li>
 *     <li><code>MASSE</code>: Masse des Balls</li>
 *     <li><code>IMPULS: Impuls, der auf den Ball angewandt wird.</code></li>
 *     <li><code>WINKEL</code>: Winkel, in dem der Impuls auf den Ball angewandt wird.
 *          0° = parallel zum Boden, 90° = gerade nach oben</li>
 * </ul>
 * Created by Michael on 11.04.2017.
 */
public class BallThrow extends ShowcaseDemo implements CollisionListener<Actor>, KeyListener {

    /**
     * Der Circle. Auf ihn wird ein Impuls gewirkt.
     */
    private Circle ball;

    /**
     * Der Boden.
     */
    private Rectangle boden;

    /**
     * Der Startzeitpunkt der Simulation. Für Zeitmessung
     */
    private long startzeit;

    /**
     * Die Konstanten für die Umsetzung der Simulation
     * Einheiten sind:
     * Distanz : Meter
     * Masse : KG
     * Impuls : Ns
     * Winkel : Grad (nicht Bogenmaß)
     */
    private static final float DURCHMESSER=0.2f, HOEHE_UEBER_BODEN=1f, MASSE=1f, IMPULS=10, WINKEL=60;

    /**
     * Die PPM-Berechnungskonstante
     */
    private static final int PIXELPROMETER=100;

    private static final float BODEN_TIEFE = 700, ABSTAND_LINKS=50;

    public BallThrow(Scene parent) {
        super(parent);
        initialisieren();
        addKeyListener(this);
    }

    public void initialisieren() {
        //Zuallererst die Größenordnung klar machen (muss vor Erstellung der Objekte geschehen)
        getWorldHandler().setPixelPerMeter(PIXELPROMETER);

        //Den Ball erstellen
        ball = new Circle(this, DURCHMESSER*PIXELPROMETER);
        add(ball);
        ball.setColor(Color.RED);
        ball.setBodyType(Physics.Type.DYNAMIC);
        ball.physics.setMass(MASSE);
        ball.position.setCenter(ABSTAND_LINKS,
                BODEN_TIEFE-(HOEHE_UEBER_BODEN*PIXELPROMETER + 0.5f*DURCHMESSER*PIXELPROMETER));

        //kamera.fokusSetzen(ball);

        //Den Boden erstellen
        boden=new Rectangle(this, 20000,20);
        boden.position.set(0,BODEN_TIEFE);
        add(boden);
        boden.setColor(Color.WHITE);
        boden.setBodyType(Physics.Type.STATIC);

        //Kollision zwischen Ball und Boden beobachten (Code ist uns egal, wir kennen nur einen Kollisionsfall)
        ball.addCollisionListener(this, boden);
    }

    /**
     * Wird bei jedem Keyndruck ausgeführt.
     *
     * @param e KeyEvent
     */
    @Override
    public void onKeyDown(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_S: // Starte die Simulation
                simulationStarten();
                break;
            case KeyEvent.VK_R: // Reset
                simulationZuruecksetzen();
                break;
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        // Ignore
    }

    /**
     * Startet die Simulation, indem ein Impuls auf den Ball gewirkt wird. Ab diesem Moment beginnt die Zeitmessung
     */
    private void simulationStarten() {
        //Zeitmessung beginnen = Startzeit erheben
        startzeit=System.currentTimeMillis();

        //Schwerkraft auf den Ball wirken lassen
        setGravity(new Vector(0, 9.81f));

        //Impuls berechnen und auf den Ball wirken lassen
        Vector impuls = new Vector((float)Math.cos(Math.toRadians(WINKEL))*IMPULS,
                (float)-Math.sin(Math.toRadians(WINKEL))*IMPULS);
        ball.physics.applyImpulse(impuls);
    }

    /**
     * Setzt die Simulation zurück. Die Schwerkraft auf den Ball wird deaktiviert, die Position des Balls wird
     * zurückgesetzt und der Ball wird in Ruhe versetzt.
     */
    private void simulationZuruecksetzen() {
        setGravity(new Vector(0,0)); //Schwerkraft deaktivieren
        ball.position.setCenter(ABSTAND_LINKS, //Ballposition zurücksetzen
                BODEN_TIEFE-(HOEHE_UEBER_BODEN*PIXELPROMETER + 0.5f*DURCHMESSER*PIXELPROMETER));
        ball.physics.cancelAll(); //Ball in Ruhe versetzen
    }

    /**
     * Wird bei jeder Kollision zwischen <b>mit diesem Interface angemeldeten</b> <code>Actor</code>-Objekten
     * aufgerufen.
     */
    @Override
    public void onCollision(CollisionEvent e) {
        //Kollision bedeutet, dass der Ball auf den Boden gefallen ist => Zeitmessung abschließen
        long endzeit = System.currentTimeMillis();
        long zeitdifferenz = endzeit-startzeit;

        //Zurückgelegte Distanz seit Simulationsstart ausmessen (Pixel-Differenz ausrechnen und auf Meter umrechnen)
        float distanz = (ball.position.getCenter().x-ABSTAND_LINKS) / PIXELPROMETER;

        //Messungen angeben
        System.out.println("Der Ball ist auf dem Boden aufgeschlagen. Seit Simulationsstart sind " +
         + (zeitdifferenz/1000) + " Sekunden und " + (zeitdifferenz%1000) + " Millisekunden vergangen.\n" +
                "Der Ball diese Distanz zurückgelegt: " + distanz + " m");
    }
}

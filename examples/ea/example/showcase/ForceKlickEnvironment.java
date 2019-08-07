package ea.example.showcase;

import ea.FrameUpdateListener;
import ea.Scene;
import ea.Vector;
import ea.actor.Actor;
import ea.actor.Circle;
import ea.actor.Geometry;
import ea.actor.Rectangle;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.handle.BodyType;
import ea.input.MouseButton;
import ea.input.MouseClickListener;

import java.awt.Color;
import java.awt.event.KeyEvent;

/**
 * Eine kleine Sandbox, in der man ein paar Grundfunktionen der EA-Physik (4.0) ausprobieren kann.
 *
 * <h3>Nutzung der Simulation</h3>
 * <p>Die Simulation wird mit der Maus beeinflusst. Klicken setzt einen Angriffspunkt. Ein weiteres Klicken wirkt
 * an dem Angriffspunkt einen Impuls. Stärke und Richtung hängen von der Position der Maus relativ zum ersten Point
 * ab. Der entsprechende Vector ist sichtbar.</p>
 * <h3>Funktionen</h3>
 * <ul>
 * <li>R Setzt die gesamte Simulation zurück. Alle Objekte verharren wieder in Ruhe an ihrer Ausgangsposition.</li>
 * <li>S Aktiviert/Deaktiviert Schwerkraft in der Simulation.</li>
 * <li>E Aktiviert/Deaktiviert Wände</li>
 * <li>D Aktiviert/Deaktiviert den Debug-Modus (und stellt damit ein Raster, FPS etc. dar)</li>
 * <li>I Aktiviert/Deaktiviert die Info-Box mit Infos zu den physikalischen Eigenschaften des zuletzt
 * angeklickten Objekts.</li>
 * <li>U und J erhöhen/reduzieren die Masse des zuöetzt angeklickten Objekts.</li>
 * <li>W und Q erhöhen/reduzieren die Elastizität der Wände.</li>
 * <li>1 und 2 zoomen rein/raus</li>
 * </ul>
 * <p>
 * Created by andonie on 05.09.15.
 */
public class ForceKlickEnvironment extends ShowcaseDemo implements CollisionListener<Actor>, MouseClickListener, FrameUpdateListener {

    /**
     * Wird für die Schwerkraft-Berechnung genutzt
     */
    private static final Vector ERDBESCHLEUNIGUNG = new Vector(0, 9.81f);

    /**
     * Beschreiben die Maße des "Spielfelds"
     */
    private final int FIELD_WIDTH, FIELD_DEPTH;

    @Override
    public void onCollision(CollisionEvent event) {
        attackedLast = event.getColliding();
    }

    @Override
    public void onCollisionEnd(CollisionEvent colliding) {
        if (attackedLast == colliding.getColliding()) {
            attackedLast = null;
        }
    }

    /**
     * Beschreibt die Zustände, in denen sich die Sandbox im Bezug auf Mausklick-Funktion befinden kann.
     */
    private enum KlickMode {
        ATTACK_POINT, DIRECTION_INTENSITY;
    }

    private Actor ground;
    private Actor attack;
    private Geometry[] walls = new Geometry[4];

    private Actor attackedLast = null;

    private Rectangle stange;
    private KlickMode klickMode = KlickMode.ATTACK_POINT;
    private Vector lastAttack;
    private boolean hatSchwerkraft = false;

    public static int PPM = 100;

    /**
     * Startet ein Sandbox-Fenster.
     */
    public ForceKlickEnvironment(Scene parent, int width, int height) {
        super(parent);
        FIELD_WIDTH = width;
        FIELD_DEPTH = height;
        getCamera().move(width / 2, height / 2);

        initialisieren();
    }

    /**
     *
     */
    public void initialisieren() {
        // Info-Message
        // fenster.nachrichtSchicken("Elastizität +[W]/-[Q] | Masse +[U] / -[J] | [R]eset | [S]chwerkraft | [E]insperren");

        // Boden
        Rectangle boden = new Rectangle(FIELD_WIDTH, 10);
        boden.position.set(0, FIELD_DEPTH);
        add(boden);
        boden.setColor(Color.WHITE);
        boden.setBodyType(BodyType.STATIC);
        ground = walls[0] = boden;

        //Der Rest der Wände
        Rectangle links = new Rectangle(10, FIELD_DEPTH);
        Rectangle rechts = new Rectangle(10, FIELD_DEPTH);
        rechts.position.set(FIELD_WIDTH - 10, 0);
        Rectangle oben = new Rectangle(FIELD_WIDTH, 10);
        add(links, rechts, oben);
        walls[1] = links;
        walls[2] = rechts;
        walls[3] = oben;

        for (int i = 1; i <= 3; i++) {
            walls[i].setColor(Color.WHITE);
            walls[i].setVisible(false);
            walls[i].setBodyType(BodyType.PASSIVE);
        }

        //Vector-Visualisierung
        Rectangle stab = new Rectangle(100, 5);
        add(stab);
        stab.setColor(new Color(200, 50, 50));
        stange = stab;
        stange.setLayerPosition(-10);

        //Attack-Visualisierung
        Circle atv = new Circle(10);
        add(atv);
        atv.setColor(Color.RED);
        attack = atv;
        attack.setLayerPosition(-10);

        //Maus erstellen, Listener Anmelden.
        getMouseClickListeners().add(this);
        attack.addCollisionListener(this);

        getFrameUpdateListeners().add(this);
        getKeyListeners().add(e -> {
            if (e.getKeyCode() == KeyEvent.VK_E) {
                boolean wasActive = walls[1].isVisible();
                BodyType newType = wasActive ? BodyType.PASSIVE : BodyType.STATIC;
                for (int i = 0; i <= 3; i++) {
                    walls[i].setVisible(!wasActive);
                    walls[i].setBodyType(newType);
                }
            }
        });
    }

    /**
     * Wird bei jedem Mausklick aufgerufen.
     *
     * @param p Point des Mausklicks auf der Zeichenebene.
     */
    @Override
    public void onMouseDown(Vector p, MouseButton mouseButton) {
        switch (klickMode) {
            case ATTACK_POINT:
                lastAttack = p;

                //Visualize Attack Point
                attack.position.set(p.add(new Vector(-5, -5)));
                attack.setVisible(true);

                //Prepare Vector Stick
                stange.setVisible(true);
                stange.position.set(p);

                klickMode = KlickMode.DIRECTION_INTENSITY;
                break;
            case DIRECTION_INTENSITY:

                if (lastAttack == null) {
                    klickMode = KlickMode.ATTACK_POINT;
                    return;
                }

                attack.setVisible(false);
                stange.setVisible(false);
                Vector distance = lastAttack.negate().add(p);

                if (attackedLast != null && attackedLast.getBodyType() == BodyType.DYNAMIC) {
                    attackedLast.physics.applyImpulse(distance.multiply(1), lastAttack);
                    attackedLast = null;
                }

                klickMode = KlickMode.ATTACK_POINT;

                break;
        }
    }

    @Override
    public void onMouseUp(Vector point, MouseButton mouseButton) {
        // Ignore
    }

    @Override
    public void onFrameUpdate(float deltaSeconds) {
        // Visualisiere ggf. die Vectorstange
        if (klickMode == KlickMode.DIRECTION_INTENSITY) {
            Vector pointer = getMousePosition();
            if (pointer == null || lastAttack == null) {
                return;
            }
            stange.resetDimensions(new Vector(lastAttack, pointer).getLength(), stange.getHeight());
            float rot = Vector.RIGHT.getAngle(lastAttack.negate().add(pointer));
            if (Float.isNaN(rot)) {
                return;
            }
            if (pointer.y < lastAttack.y) {
                rot = (float) (Math.PI * 2 - rot);
            }
            stange.position.setRotation(rot);
        }
    }
}

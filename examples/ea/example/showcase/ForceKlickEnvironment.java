package ea.example.showcase;

import ea.FrameUpdateListener;
import ea.Scene;
import ea.Vector;
import ea.actor.*;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.event.MouseButton;
import ea.event.MouseClickListener;

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
 * <li>R Setzt die gesamte Simulation zurück. Alle Objekte verharren wieder in Ruhe an ihrer AusgangssetPosition(</li>
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
    public static final float FIELD_WIDTH = 85;
    public static final float FIELD_DEPTH = 50;

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

    /**
     * Startet ein Sandbox-Fenster.
     */
    public ForceKlickEnvironment(Scene parent) {
        super(parent);

        // Info-Message
        // fenster.nachrichtSchicken("Elastizität +[W]/-[Q] | Masse +[U] / -[J] | [R]eset | [S]chwerkraft | [E]insperren");

        // Boden
        Rectangle boden = new Rectangle(FIELD_WIDTH, 1);
        boden.setPosition(0, FIELD_DEPTH);
        boden.setColor(Color.WHITE);
        boden.setBodyType(BodyType.STATIC);
        ground = walls[0] = boden;

        //Der Rest der Wände
        Rectangle links = new Rectangle(1, FIELD_DEPTH);
        Rectangle rechts = new Rectangle(1, FIELD_DEPTH);
        rechts.setPosition(FIELD_WIDTH - 1, 0);
        Rectangle oben = new Rectangle(FIELD_WIDTH, 1);

        walls[1] = links;
        walls[2] = rechts;
        walls[3] = oben;

        for (int i = 1; i <= 3; i++) {
            walls[i].setColor(Color.WHITE);
            walls[i].setVisible(false);
            walls[i].setBodyType(BodyType.SENSOR);
        }

        //Vector-Visualisierung
        Rectangle stab = new Rectangle(1, 0.5f);
        stab.setColor(new Color(200, 50, 50));
        stange = stab;
        stange.setLayerPosition(-10);

        //Attack-Visualisierung
        Circle atv = new Circle(0.5f);
        atv.setColor(Color.RED);
        attack = atv;
        attack.setLayerPosition(-10);

        //Maus erstellen, Listener Anmelden.
        attack.addCollisionListener(this);

        getKeyListeners().add(e -> {
            if (e.getKeyCode() == KeyEvent.VK_E) {
                boolean wasActive = walls[1].isVisible();
                BodyType newType = wasActive ? BodyType.SENSOR : BodyType.STATIC;
                for (int i = 0; i <= 3; i++) {
                    walls[i].setVisible(!wasActive);
                    walls[i].setBodyType(newType);
                }
            }
        });

        add(boden);
        add(links, rechts, oben);
        add(stab);
        add(atv);
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
                attack.setCenter(p);
                attack.setVisible(true);

                //Prepare Vector Stick
                stange.setVisible(true);
                stange.setPosition(p);

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
                    attackedLast.applyImpulse(distance.multiply(1), lastAttack);
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
            Vector currentMousePos = getMousePosition();
            if (currentMousePos == null || lastAttack == null) {
                return;
            }
            float vectorLength = new Vector(lastAttack, currentMousePos).getLength();
            if (vectorLength <= 0) {
                return;
            }
            stange.resetDimensions(vectorLength, stange.getHeight());

            //float rot = Vector.RIGHT.getAngle(lastAttack.fromThisTo(pointer).negate());
            float rot = Vector.RIGHT.getAngle(lastAttack.fromThisTo(currentMousePos));
            rot = Vector.RIGHT.getAngle(lastAttack.negate().add(currentMousePos));
            if (Float.isNaN(rot)) {
                return;
            }
            if (currentMousePos.y < lastAttack.y) {
                rot = 360 - rot;
            }
            stange.setRotation(rot);
        }
    }
}

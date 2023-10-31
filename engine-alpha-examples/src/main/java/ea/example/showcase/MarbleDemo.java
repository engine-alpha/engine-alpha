package ea.example.showcase;

import ea.FrameUpdateListener;
import ea.Random;
import ea.Scene;
import ea.Vector;
import ea.actor.BodyType;
import ea.actor.Circle;
import ea.actor.Rectangle;
import ea.event.KeyListener;

import java.awt.Color;
import java.awt.event.KeyEvent;

/**
 * Eine kleine Demo zum Verhalten vieler partikelähnlicher Physik-Objekte in der Engine.
 * <p>
 * Created by Michael on 11.04.2017.
 */
public class MarbleDemo extends ShowcaseDemo implements KeyListener {

    /**
     * Konstanten zur Beschreibung der Position des Trichters.
     */
    private static final int ABSTAND_OBEN = 300, ABSTAND_LINKS = 40, ABSTAND_RECHTS = 470;

    /**
     * Der Boden des Trichters. Kann durchlässig gemacht werden.
     */
    private final Rectangle boden;

    public MarbleDemo(Scene parent) {
        super(parent);

        //Trichter
        Rectangle lo = new Rectangle(50, 150);
        lo.setPosition(ABSTAND_LINKS, ABSTAND_OBEN);
        Rectangle lm = new Rectangle(50, 200);
        lm.setPosition(ABSTAND_LINKS, ABSTAND_OBEN + 150);
        Rectangle ro = new Rectangle(50, 150);
        ro.setPosition(ABSTAND_RECHTS, ABSTAND_OBEN);
        Rectangle rm = new Rectangle(50, 200);
        rm.setPosition(ABSTAND_RECHTS + 14, ABSTAND_OBEN + 120);
        Rectangle lu = new Rectangle(50, 120);
        lu.setPosition(ABSTAND_LINKS + 125, ABSTAND_OBEN + 255);
        Rectangle ru = new Rectangle(50, 120);
        ru.setPosition(ABSTAND_LINKS + 304, ABSTAND_OBEN + 260);

        boden = new Rectangle(230, 40);
        boden.setPosition(ABSTAND_LINKS + 125, ABSTAND_OBEN + 375);

        Rectangle[] allRectangles = new Rectangle[] {lo, lm, ro, rm, lu, ru, boden};

        for (Rectangle r : allRectangles) {
            r.setColor(Color.WHITE);
            add(r);
            r.setBodyType(BodyType.STATIC);
        }

        setGravity(new Vector(0, 15));

        lm.setRotation(-45);
        rm.setRotation(45);

        repeat(.1f, () -> {
            Circle marble = makeAMarble();
            add(marble);
            marble.setBodyType(BodyType.DYNAMIC);
            marble.setPosition(ABSTAND_LINKS + 200, ABSTAND_OBEN - 150);
            marble.applyImpulse(new Vector(Random.range() * 200 - 100, Random.range() * -300 - 100));
        });
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_X) { // Boden togglen
            if (boden.getBodyType() == BodyType.STATIC) {
                boden.setBodyType(BodyType.SENSOR);
                boden.setColor(new Color(255, 255, 255, 100));
            } else {
                boden.setBodyType(BodyType.STATIC);
                boden.setColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        // Ignore.
    }

    /**
     * Erstellt eine neue Murmel.
     *
     * @return eine Murmel. Farbe und Größe variieren.
     */
    public Circle makeAMarble() {

        class Marble extends Circle implements FrameUpdateListener {

            public Marble(float diameter) {
                super(diameter);
            }

            @Override
            public void onFrameUpdate(float deltaSeconds) {
                if (this.getCenter().getLength() > 1000) {
                    MarbleDemo.this.remove(this);
                }
            }
        }

        Circle murmel = new Marble(Random.range(50) + 10);
        murmel.setBodyType(BodyType.DYNAMIC);
        //murmel.setMass(4);
        murmel.setColor(new Color(Random.range(255), Random.range(255), Random.range(255)));

        return murmel;
    }
}

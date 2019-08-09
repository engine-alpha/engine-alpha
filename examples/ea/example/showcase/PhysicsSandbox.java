package ea.example.showcase;

import ea.FrameUpdateListener;
import ea.Scene;
import ea.Vector;
import ea.actor.*;
import ea.actor.Polygon;
import ea.actor.Rectangle;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.actor.BodyType;
import ea.event.KeyListener;
import ea.event.MouseButton;
import ea.event.MouseClickListener;

import java.awt.*;
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
 *     <li>R Setzt die gesamte Simulation zurück. Alle Objekte verharren wieder in Ruhe an ihrer Ausgangsposition.</li>
 *     <li>S Aktiviert/Deaktiviert Schwerkraft in der Simulation.</li>
 *     <li>E Aktiviert/Deaktiviert Wände</li>
 *     <li>D Aktiviert/Deaktiviert den Debug-Modus (und stellt damit ein Raster, FPS etc. dar)</li>
 *     <li>I Aktiviert/Deaktiviert die Info-Box mit Infos zu den physikalischen Eigenschaften des zuletzt
 *     angeklickten Objekts.</li>
 *     <li>U und J erhöhen/reduzieren die Masse des zuöetzt angeklickten Objekts.</li>
 *     <li>W und Q erhöhen/reduzieren die Elastizität der Wände.</li>
 *     <li>1 und 2 zoomen rein/raus</li>
 * </ul>
 *
 * Created by andonie on 05.09.15.
 */
public class PhysicsSandbox extends ShowcaseDemo implements MouseClickListener, FrameUpdateListener, KeyListener {

    /**
     * Textbox für Infos
     */
    /* private static class InfoBox
    extends ActorGroup {
        private Rectangle box; //Hintergrund

        private Text[] texte; //Die Texte

        public InfoBox(Scene scene) {
            super(scene);
            box = new Rectangle(scene, 150, 100);
            add(box);
            box.setColor(new Color(200, 200, 200, 100));

            texte = new Text[5];
            for(int i = 0; i < texte.length; i++) {
                texte[i] = new Text(scene, "Text1");
                texte[i].setPosition(i==0? 30 : 5, 5 + i*20);
                texte[i].setSize(14);
                add(texte[i]);
                texte[i].setColor(Color.RED);
            }
            texte[4].setSize(10);
        }

        public void setTexts(String... texts) {
            for(int i = 0; i < texts.length && i < this.texte.length; i++) {
                texte[i].setContent(texts[i]);
            }
        }
    } */

    /**
     * Wird für die Schwerkraft-Berechnung genutzt
     */
    private static final Vector ERDBESCHLEUNIGUNG = new Vector(0, 9.81f);

    /**
     * Beschreiben die Maße des "Spielfelds"
     */
    public static final int FIELD_WIDTH = 612, FIELD_DEPTH = 400;

    /**
     * Beschreibt die Anzahl an Test-Objekten im Spiel
     */
    private static final int NUMER_OF_TESTOBJECTS = 4;

    /**
     * Die Startpunkte für die Test-Objekte
     */
    private static final Vector[] STARTINGPOINTS = new Vector[] {
            new Vector(260, 250),
            new Vector(50, 60),
            new Vector(400, 100),
            new Vector(50, 200)
    };

    /**
     * Beschreibt die Zustände, in denen sich die Sandbox im Bezug auf Mausklick-Funktion befinden kann.
     */
    private enum KlickMode {
        ATTACK_POINT, DIRECTION_INTENSITY;
    }

    /**
     * Beschreibt, ob das Test-Objekt mit dem jeweiligen Index gerade im Angriffspunkt liegt.
     */
    private boolean[] isInAttackRange = new boolean[NUMER_OF_TESTOBJECTS];

    /**
     * Der Index des zuletzt angeklickten Test-Objekts
     */
    private int lastAttackTarget = 0;

    private Actor[] testObjects = new Actor[NUMER_OF_TESTOBJECTS];
    private Actor ground;
    private Actor attack;
    private Geometry[] walls = new Geometry[NUMER_OF_TESTOBJECTS];

    // private ActorGroup fixierungsGruppe;

    private Rectangle stange;
    // private InfoBox  box;

    private KlickMode klickMode = KlickMode.ATTACK_POINT;
    private Vector lastAttack;
    private boolean hatSchwerkraft = false;

    private final int FRAME_WIDHT, FRAME_HEIGHT;

    /**
     * Startet ein Sandbox-Fenster.
     */
    public PhysicsSandbox(Scene parent, int frame_width, int frame_height) {
        super(parent);
        this.FRAME_WIDHT = frame_width;
        this.FRAME_HEIGHT = frame_height;
        getCamera().moveBy(
                FRAME_WIDHT/4,
                FRAME_HEIGHT/4
        );
        //ppmSetzen(30);
        initialisieren();
    }

    /**
     * In dieser Methode wird die gesamte Sandbox initialisiert.
     */
    public void initialisieren() {

        //Info-Message
        //fenster.nachrichtSchicken("Elastizität +[W]/-[Q] | Masse +[U] / -[J] | [R]eset | [S]chwerkraft | [E]insperren");

        //Test-Objekte
        Rectangle rechteck = new Rectangle(100, 60);
        rechteck.setPosition(10, 10);
        add(rechteck);
        rechteck.setColor(Color.YELLOW);
        rechteck.setBodyType(BodyType.DYNAMIC);
        testObjects[0] = rechteck;

        //fixierungsGruppe = new ActorGroup(this);
        //add(fixierungsGruppe);


        Circle kreis = new Circle(50);
        kreis.setPosition(10,10);
        //wurzel.add(kreis);
        //fixierungsGruppe.add(kreis);
        kreis.setColor(Color.MAGENTA);
        kreis.setBodyType(BodyType.DYNAMIC);
        testObjects[1] = kreis;

        Circle kreis2 = new Circle(20);
        //wurzel.add(kreis2);
        //fixierungsGruppe.add(kreis2);
        kreis2.setColor(Color.GREEN);
        kreis2.setBodyType(BodyType.DYNAMIC);
        //kreis2.physics.masse(50);
        testObjects[2] = kreis2;

        Polygon polygon = new Polygon(new Vector(0,0), new Vector(20, 30), new Vector(10, 50),
                new Vector(80, 10), new Vector(120, 0));
        //fixierungsGruppe.add(polygon);
        polygon.setColor(Color.BLUE);
        polygon.setBodyType(BodyType.DYNAMIC);
        testObjects[3] = polygon;

        //Boden
        Rectangle boden = new Rectangle(FIELD_WIDTH, 10);
        boden.setPosition(0, FIELD_DEPTH);
        add(boden);
        boden.setColor(Color.WHITE);
        boden.setBodyType(BodyType.STATIC);
        ground = walls[0] = boden;

        //Der Rest der Wände
        Rectangle links = new Rectangle(10, FIELD_DEPTH);
        Rectangle rechts = new Rectangle(10, FIELD_DEPTH);
        rechts.setPosition(FIELD_WIDTH-10, 0);
        Rectangle oben = new Rectangle(FIELD_WIDTH, 10);
        add(links, rechts, oben);
        walls[1] = links;
        walls[2] = rechts;
        walls[3] = oben;

        for(int i = 1; i <= 3; i++) {
            walls[i].setColor(Color.WHITE);
            walls[i].setVisible(false);
            walls[i].setBodyType(BodyType.SENSOR);
        }


        //Vector-Visualisierung
        Rectangle stab = new Rectangle(100, 5);
        add(stab);
        stab.setColor(new Color(200, 50, 50));
        stange = stab;
        stange.setLayerPosition(3);

        //Attack-Visualisierung
        Circle atv = new Circle(10);
        add(atv);
        atv.setColor(Color.RED);
        attack = atv;
        attack.setLayerPosition(4);

        /* box = new InfoBox(this);
        add(box);
        box.setPosition(200, 30); */


        //Test-Objekte zur Kollision Anmelden
        for(int i = 0; i < testObjects.length; i++) {
            final int key = i;
            CollisionListener<Actor> kr = new CollisionListener<Actor>() {
                @Override
                public void onCollision(CollisionEvent e) {
                    isInAttackRange[lastAttackTarget = key] = true;
                }
                @Override
                public void onCollisionEnd(CollisionEvent e) {
                    //Code = index d. Test-Objekts, das attack-range verlassen hat.
                    isInAttackRange[key] = false;
                }
            };
            attack.addCollisionListener(testObjects[i], kr);
        }

        getCamera().setZoom(2);
        resetSituation();
    }

    /**
     * Setzt den Zustand der Sandbox zurück zur Ausgangsaufstellung.
     */
    private void resetSituation() {
        //Testobjekt zurücksetzen und in Ruhezustand bringen.
        for(int i = 0; i < testObjects.length; i++) {
            testObjects[i].resetMovement();
            testObjects[i].setPosition(STARTINGPOINTS[i]);
            testObjects[i].setRotation(0);
        }


        //Attack zurücksetzen (falls gesetzt)
        attack.setVisible(false);
        lastAttack = null;

        //Vectorstange zurücksetzen (falls aktiv)
        stange.setVisible(false);
    }


    /**
     * Wird bei jedem Keyndruck aufgerufen.
     * @param e  Der Code der gedrückten Key.
     */
    @Override
    public void onKeyDown(KeyEvent e) {
        switch(e.getKeyCode()) {
            case KeyEvent.VK_R: // RESET
                resetSituation();
                break;
            case KeyEvent.VK_S: // SCHWERKRAFT-TOGGLE
                hatSchwerkraft = !hatSchwerkraft;
                for(Actor testObject : testObjects) {
                    testObject.getLayer().getParent().setGravity(hatSchwerkraft ?
                            ERDBESCHLEUNIGUNG : Vector.NULL);
                }
                //System.out.println("Schwerkraft: " + hatSchwerkraft + " - ");
                break;
            case KeyEvent.VK_E: // Toggle Environment
                boolean wasActive = walls[1].isVisible();
                BodyType newType = wasActive ? BodyType.SENSOR : BodyType.STATIC;
                //System.out.println("Type = " + newType);
                for(int i = 1; i <= 3; i++) {
                    walls[i].setVisible(!wasActive);
                    walls[i].setBodyType(newType);
                }
                break;
            case KeyEvent.VK_I: //Toggle Info Box
                // box.setVisible(!box.isVisible());
                break;
            case KeyEvent.VK_U: //Increase Mass
                changeMass(10);
                break;
            case KeyEvent.VK_J: //Decrease Mass
                changeMass(-10);
                break;
            case KeyEvent.VK_W: //Elastizitaet der Wände erhöhen
                ground.setRestitution(ground.getRestitution() + 0.1f);
                System.out.println("Ela der Wand " + ground.getRestitution());
                break;
            case KeyEvent.VK_Q: //Elastizitaet der Wände erhöhen
                ground.setRestitution(ground.getRestitution() - 0.1f);
                System.out.println("Ela der Wand " + ground.getRestitution());
                break;
            case KeyEvent.VK_1: //Zoom Out
                getCamera().setZoom(getCamera().getZoom()-0.1f);
                break;
            case KeyEvent.VK_2: //Zoom In
                getCamera().setZoom(getCamera().getZoom()+0.1f);
                break;
            case KeyEvent.VK_B: //Toggle die Circlefixierung
                /* if(fixierungsGruppe.isFixated()) {
                    fixierungsGruppe.freeFixation();
                } else {
                    fixierungsGruppe.fixate();
                } */
                break;
        }
    }

    /**
     * Ändert die Masse vom letzten Objekt, was im Attack-Point war/ist.
     * @param deltaM Die Masseänderung (positiv=mehr Masse, negativ=weniger Masse).
     */
    private void changeMass(int deltaM) {
        testObjects[lastAttackTarget].setMass(
                testObjects[lastAttackTarget].getMass()+deltaM);
    }

    /**
     * Wird bei jedem Mausklick aufgerufen.
     * @param p Point des Mausklicks auf der Zeichenebene.
     */
    @Override
    public void onMouseDown(Vector p, MouseButton button) {
        switch(klickMode) {
            case ATTACK_POINT:
                lastAttack = p;

                //Visualize Attack Point
                attack.setPosition(p.add(new Vector(-5, -5)));
                attack.setVisible(true);

                //Prepare Vector Stick
                stange.setVisible(true);
                stange.setPosition(p);


                klickMode = KlickMode.DIRECTION_INTENSITY;
                break;
            case DIRECTION_INTENSITY:

                if(lastAttack==null) {
                    klickMode = KlickMode.ATTACK_POINT;
                    return;
                }

                attack.setVisible(false);
                stange.setVisible(false);
                Vector distance = lastAttack.negate().add(p);

                for(int i = 0; i < testObjects.length; i++) {
                    if(isInAttackRange[i])
                        testObjects[i].applyImpulse(distance.multiply(10), lastAttack);
                }


                klickMode = KlickMode.ATTACK_POINT;

                break;
        }
    }

    /**
     * Wird jeden Frame des Spiels exakt einmal aufgerufen.
     * @param deltaSeconds    Die Zeit in Sekunden, die seit dem letzten Frame-Update vergangen sind.
     */
    @Override
    public void onFrameUpdate(float deltaSeconds) {
        //Visualisiere ggf. die Vectorstange
        if(klickMode == KlickMode.DIRECTION_INTENSITY) {
            Vector pointer = getMousePosition();
            if(pointer==null || lastAttack == null)
                return;
            Vector pos = stange.getPosition();
            remove(stange);
            stange = new Rectangle(new Vector(lastAttack, pointer).getLength(), 5);
            System.out.println("new Rectangle: " + stange);
            stange.setColor(new Color(200, 50, 50));
            stange.setLayerPosition(-10);
            stange.setPosition(pos);
            add(stange);
            float rot = Vector.RIGHT.getAngle(lastAttack.negate().add(pointer));
            if(Float.isNaN(rot))
                return;
            if(pointer.getY() < lastAttack.getY())
                rot = (float)( Math.PI*2 - rot);
            stange.setRotation(rot);
        }




        //Update für die Textbox
        Vector vel = testObjects[lastAttackTarget].getVelocity();
        String vx = Float.toString(vel.getX()), vy = Float.toString(vel.getY());
        vx = vx.substring(0, Math.min(vx.length(), 4));
        vy = vy.substring(0, Math.min(vy.length(), 4));
        /* box.setTexts(
                "Objekt: " + (lastAttackTarget+1),
                "Masse: " + testObjects[lastAttackTarget].physics.getMass(),
                "v: (" + vx + " | " + vy + ")",
                "Elastizität: " + testObjects[lastAttackTarget].physics.getElasticity(),
                "Toggles: [D]ebug | [I]nfo Box");  */
    }
}

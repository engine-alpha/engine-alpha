package ea.internal.phy;

import ea.Game;
import ea.Vector;
import ea.actor.Actor;
import ea.collision.CollisionListener;
import ea.internal.ano.NoExternalUse;
import ea.internal.frame.Dispatchable;
import ea.internal.util.Logger;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Die WorldHandler-Klasse ist die (nicht objektgebundene) Middleware zwischen der JBox2D Engine und
 * der EA. Sie ist verantwortlich für: <ul> <li>Den globalen "World"-Parameter aus der JBox2D
 * Engine.</li> <li>Translation zwischen JB2D-Vektoren (SI-Basiseinheiten) und denen der Engine
 * (Zeichengrößen)</li> </ul>
 */
public class WorldHandler implements ContactListener {
    /**
     * Die World dieses Handlers. Hierin laufen globale Einstellungen (z.B. Schwerkraft) ein.
     */
    private final World world;

    /**
     * Hashmap, die alle spezifisch angegebenen Actor-Actor Kollisionsüberwachungen innehat.
     */
    private final HashMap<Body, List<Checkup>> specificCollisionListeners = new HashMap<>();

    /**
     * Hashmap, die sämtliche allgemeinen CollisionListener-Listener innehat.
     */
    private final HashMap<Body, List<CollisionListener<Actor>>> generalCollisonListeners = new HashMap<>();

    /**
     * Diese Hashmap enthält sämtliche Bodies, die in der World existieren und mapt diese auf die
     * zugehörigen Actor-Objekte.
     */
    private final HashMap<Body, Actor> worldMap = new HashMap<>();

    /**
     * Umrechnungsgröße zwischen Größen der Physics-Engine und der Zeichenebene der EA.
     * Gibt an, wie viele Pixel genau einen Meter ausmachen.<br/>
     * <p>
     * <b>Einheit: [px/m]</b>
     */
    private float pixelProMeter = 30f;

    /**
     * Flag, das angibt, ob die Pixel Pro Meter bereits angefragt wurden.
     */
    private boolean ppmRequested = false;

    /**
     * Gibt die Umrechnungsgröße zwischen Größen der Physics-Engine und der Zeichenebene der EA an.
     *
     * @return Gibt an, wie viele Pixel genau einen Meter ausmachen.<br /> <b>Einheit: [px/m]</b>
     */
    public float getPixelProMeter() {
        return pixelProMeter;
    }

    /**
     * Blockiert die Möglichkeit weitere PPM-Changes zu machen.
     * Wird intern aufgerufen, sobald innerhalb der Engine die erste Shape kreiert wurde.
     */
    public void blockPPMChanges() {
        ppmRequested = true;
    }

    /**
     * Setzt die Umrechnungsgröße zwischen Größen der Physics-Engine und der Zeichenebene der EA.
     *
     * @param pixelProMeter Die Anzahl an Pixeln, die genau einen Meter ausmachen.<br /> <i>Die
     *                      Größe ist unabhängig vom Kamerazoom.</i>< br/> <b>Einheit: [px/m]</b>
     */
    public void setPixelProMeter(float pixelProMeter) {
        if (ppmRequested) {
            throw new IllegalStateException("Die Pixel-Pro-Meter Umrechnungszahl darf vectorFromThisTo Arbeit mit den Actor-Objekten" +
                    " der entsprechenden WorldHandler-Umgebung nicht geändert werden. Das Setzen der Konstante vor" +
                    " die Arbeit mit den Actor-Objekten move.");
        }
        this.pixelProMeter = pixelProMeter;
    }

    /**
     * Erstellt eine neue standardisierte Physics (Schwerkraft senkrecht vectorFromThisTo unten, 9,81 m/s^2)
     */
    @NoExternalUse
    public WorldHandler() {
        this.world = new World(new Vec2(0f, 0f)); //Erstelle standard-World mit Standard-Gravitation.
        this.world.setContactListener(this);
    }

    /**
     * Gibt den World-Parameter der Physics aus.
     *
     * @return Der JB2D-World-Parameter der Welt.
     */
    @NoExternalUse
    public World getWorld() {
        return world;
    }

    public void step(float frameDuration) {
        synchronized (this.world) {
            this.world.step(frameDuration / 1000, 6, 3);
        }
    }

    /**
     * Übersetzt einen EA-Vector in einen JB2D-Vector auf Basis des gesetzten
     * Pixel/Meter-Verhältnisses.
     *
     * @param eaV Ein EA-Vector.
     *
     * @return Der analoge Vector in der JB2D-Engine.
     */
    @NoExternalUse
    public Vec2 fromVektor(Vector eaV) {
        float x = eaV.x / pixelProMeter;
        float y = eaV.y / pixelProMeter;
        return new Vec2(x, y);
    }

    /**
     * Übersetzt einen JB2D-Vector in einen EA-Vector auf Basis des gesetzten
     * Pixel/Meter-Verhältnisses.
     *
     * @param jb2dV Ein JB2D-Vector.
     *
     * @return Der analoge Vector im EA-Format auf der Zeichenebene.
     */
    @NoExternalUse
    public Vector fromVec2(Vec2 jb2dV) {
        float x = jb2dV.x * pixelProMeter;
        float y = jb2dV.y * pixelProMeter;
        return new Vector(x, y);
    }

    /**
     * Erstellt einen Body und mappt ihn intern zum analogen Actor-Objekt.
     *
     * @param bd   Exakte Beschreibung des Bodies.
     * @param actor Actor-Objekt, das ab sofort zu dem Body gehört.
     *
     * @return Der Body, der aus der BodyDef generiert wurde. Er liegt in der Game-World dieses
     * Handlers.
     */
    public Body createBody(BodyDef bd, Actor actor) {
        Body body;

        synchronized (world) {
            body = world.createBody(bd);
            worldMap.put(body, actor);
        }

        return body;
    }

    /**
     * Entfernt alle internen Referenzen auf einen Body und das zugehörige Actor-Objekt.
     *
     * @param body der zu entfernende Body
     */
    @NoExternalUse
    public void removeAllInternalReferences(Body body) {
        specificCollisionListeners.remove(body);
        generalCollisonListeners.remove(body);
        worldMap.remove(body);
    }

    /**
     * Übersetzt einen Winkel in Radians in Grad.
     *
     * @param rad Ein Winkel in Radians.
     *
     * @return Der analoge Winkel in Grad.
     */
    public static float radToDeg(float rad) {
        return rad * degProRad;
    }

    /**
     * Umrechnungskonstante für Grad/Radians
     */
    private static final float degProRad = (float) ((double) 180 / Math.PI);


    /* ____________ CONTACT LISTENER INTERFACE ____________ */

    @Override
    public void beginContact(Contact contact) {
        processContact(contact, true);
    }

    @Override
    public void endContact(Contact contact) {
        processContact(contact, false);
    }

    /**
     * Verarbeitet einen Kontakt in der Physics-Engine.
     *
     * @param contact JBox2D Contact Objekt, das den Contact beschreibt.
     * @param isBegin true = Begin-Kontakt | false = End-Kontakt
     */
    @NoExternalUse
    private void processContact(Contact contact, boolean isBegin) {
        final Body b1 = contact.getFixtureA().getBody();
        final Body b2 = contact.getFixtureB().getBody();
        if (b1 == b2) {
            //Gleicher Body, don't care
            Logger.error("Collision", "Inter-Body Collision!");
            return;
        }

        /*
         * ~~~~~~~~~~~~~~~~~~~~~~~ TEIL I : Spezifische Checkups ~~~~~~~~~~~~~~~~~~~~~~~
         */

        //Sortieren der Bodies.
        Body lower = null, higher = null;
        if (b1.hashCode() == b2.hashCode()) {
            //Hashes sind gleich (blöde Sache!) -> beide Varianten probieren.
            List<Checkup> result1 = specificCollisionListeners.get(b1);
            if (result1 != null) {
                for (Checkup c : result1) {
                    c.checkCollision(b2, isBegin);
                }
            }
            List<Checkup> result2 = specificCollisionListeners.get(b2);
            if (result2 != null) {
                for (Checkup c : result2) {
                    c.checkCollision(b1, isBegin);
                }
            }
        } else {
            if (b1.hashCode() < b2.hashCode()) {
                //b1 < b2
                lower = b1;
                higher = b2;
            } else {
                //b1 > b2
                lower = b2;
                higher = b1;
            }
            List<Checkup> result = specificCollisionListeners.get(lower);
            if (result != null) {
                for (Checkup c : result) {
                    c.checkCollision(higher, isBegin);
                }
            }
        }


        /*
         * ~~~~~~~~~~~~~~~~~~~~~~~ TEIL II : Allgemeine Checkups ~~~~~~~~~~~~~~~~~~~~~~~
         */
        generalCheckup(b1, b2, isBegin);
        generalCheckup(b2, b1, isBegin);
    }

    @NoExternalUse
    private void generalCheckup(Body act, Body col, final boolean isBegin) {
        List<CollisionListener<Actor>> list = generalCollisonListeners.get(act);
        if (list != null) {
            Actor other = worldMap.get(col); // Darf (eigentlich) niemals null sein
            for (CollisionListener<Actor> kr : list) {
                Game.enqueueDispatchable(() -> {
                    if (isBegin) {
                        kr.onCollision(other);
                    } else {
                        kr.onCollisionEnd(other);
                    }
                });
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        //Ignore that shit.
        //System.out.println("PRE");
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        //Ignore that shit.
        //System.out.println("POST");
    }


    /* ____________ APPENDIX - Inner Classes ____________ */

    /**
     * Speichert ein Korrespondierendes Body-Objekt sowie
     */
    private static class Checkup<E extends Actor> {
        private final CollisionListener<E> reagierbar;  //Aufzurufen
        private final Body body2;                       //Der zweite Body (erster Body ist Hashmap-Schlüssel)
        private final E collider;                       //Das Actor-Objekt, das neben dem Actor angemeldet wurde

        private final Dispatchable begin = new Dispatchable() {
            @Override
            public void dispatch() {
                reagierbar.onCollision(collider);
            }
        };
        private final Dispatchable end = new Dispatchable() {
            @Override
            public void dispatch() {
                reagierbar.onCollisionEnd(collider);
            }
        };

        /**
         * Erstellt das Checkup-Objekt
         *
         * @param reagierbar Das aufzurufende KR
         * @param body2      Der zweite Body für den Checkup
         * @param collider   Der zugehörige Collider für diesen Checkup
         */
        private Checkup(CollisionListener<E> reagierbar, Body body2, E collider) {
            this.reagierbar = reagierbar;
            this.body2 = body2;
            this.collider = collider;
        }

        public void checkCollision(Body secondBodyOfActualCollision, boolean isBegin) {
            if (body2 == secondBodyOfActualCollision) {
                Game.enqueueDispatchable(isBegin ? begin : end);
            }
        }
    }

    /**
     * Meldet ein allgemeines KR-Interface in dieser World an.
     *
     * @param kr    Das anzumeldende KR Interface
     * @param actor Der Actor (KR Interface wird bei jeder Kollision des Actors informiert)
     */
    @NoExternalUse
    public static void allgemeinesKollisionsReagierbarEingliedern(CollisionListener<Actor> kr, Actor actor) {
        final WorldHandler worldHandler = actor.getPhysicsHandler().worldHandler();
        if (worldHandler == null) {
            Logger.error("Kollision", "Das anzumeldende Actor-Objekt war noch nicht an der Wurzel angemeldet. "
                    + "Erst an der Wurzel anmelden, bevor Kollisionsanmeldungen durchgeführt werden.");
            return;
        }

        Body body = actor.getPhysicsHandler().getBody();
        if (body == null) {
            Logger.error("Kollision", "Ein Actor-Objekt ohne physikalischen Body wurde zur Kollisionsüberwachung" +
                    " angemeldet.");
            return;
        }

        List<CollisionListener<Actor>> bodyList = worldHandler.generalCollisonListeners.get(body);
        if (bodyList == null) {
            bodyList = new CopyOnWriteArrayList<>();
            worldHandler.generalCollisonListeners.put(body, bodyList);
        }

        bodyList.add(kr);
    }

    /**
     * Meldet ein spezifisches KR-Interface in dieser World an.
     *
     * @param kr       Das anzumeldende KR Interface
     * @param actor    Der Actor (Haupt-Actor-Objekt)
     * @param collider Der Collider (zweites Actor-Objekt)
     * @param <E>      Der Type des Colliders.
     */
    @NoExternalUse
    public static <E extends Actor> void spezifischesKollisionsReagierbarEingliedern(
            CollisionListener<E> kr, Actor actor, E collider) {
        final WorldHandler wh1 = actor.getPhysicsHandler().worldHandler();
        final WorldHandler wh2 = collider.getPhysicsHandler().worldHandler();
        if (wh1 == null || wh2 == null || wh1 != wh2) {
            Logger.error("Kollision", "Zwei Objekte sollten zur Kollision angemeldet werden. " +
                    "Dafür müssen beide an der selben Wurzel (direkt oder indirekt) angemeldet sein.");
            return;
        }

        final WorldHandler worldHandler = wh1;

        Body b1 = actor.getPhysicsHandler().getBody(), b2 = collider.getPhysicsHandler().getBody();
        if (b1 == null || b2 == null) {
            Logger.error("Kollision", "Ein Actor-Objekt ohne physikalischen Body wurde zur Kollisionsüberwachung" +
                    " angemeldet.");
            return;
        }

        Body lower, higher;
        if (b1.hashCode() < b2.hashCode()) {
            //b1 < b2
            lower = b1;
            higher = b2;
        } else {
            //b1 > b2
            lower = b2;
            higher = b1;
        }

        Checkup<E> toAdd = new Checkup<E>(kr, higher, collider);

        List<Checkup> atKey = wh1.specificCollisionListeners.get(lower);
        if (atKey == null) {
            //NO LIST THERE YET: Create new Entry in Hashmap
            atKey = new CopyOnWriteArrayList<>();
            atKey.add(toAdd);
            wh1.specificCollisionListeners.put(lower, atKey);
        } else {
            atKey.add(toAdd);
        }
    }
}

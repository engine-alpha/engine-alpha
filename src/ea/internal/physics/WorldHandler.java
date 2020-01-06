package ea.internal.physics;

import ea.Layer;
import ea.actor.Actor;
import ea.actor.Joint;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.internal.annotations.Internal;
import ea.internal.util.Logger;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;
import org.jbox2d.dynamics.contacts.ContactEdge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Die WorldHandler-Klasse ist die (nicht objektgebundene) Middleware zwischen der JBox2D Engine und
 * der EA. Sie ist verantwortlich für: <ul> <li>Den globalen "World"-Parameter aus der JBox2D
 * Engine.</li> <li>Translation zwischen JB2D-Vektoren (SI-Basiseinheiten) und denen der Engine
 * (Zeichengrößen)</li> </ul>
 */
public class WorldHandler implements ContactListener {
    public static final int CATEGORY_PASSIVE = 1;
    public static final int CATEGORY_STATIC = 2;
    public static final int CATEGORY_KINEMATIC = 4;
    public static final int CATEGORY_DYNAMIC = 8;
    public static final int CATEGORY_PARTICLE = 16;

    public static final float STEP_TIME = 8f / 1000;

    /**
     * Das Layer, zu dem der WorldHandler gehört.
     */
    private final Layer layer;

    /**
     * Gibt an, ob die World/Physics gerade pausiert sind.
     */
    private boolean worldPaused = false;

    /**
     * Die World dieses Handlers. Hierin laufen globale Einstellungen (z.B. Schwerkraft) ein.
     */
    private final World world;

    /**
     * Hashmap, die alle spezifisch angegebenen Actor-Actor Kollisionsüberwachungen innehat.
     */
    private final Map<Body, List<Checkup>> specificCollisionListeners = new ConcurrentHashMap<>();

    /**
     * Hashmap, die sämtliche allgemeinen CollisionListener-Listener innehat.
     */
    private final Map<Body, List<CollisionListener<Actor>>> generalCollisonListeners = new HashMap<>();

    /**
     * Diese Liste enthält die (noch nicht beendeten) Kontakte, die nicht aufgelöst werden sollen.
     */
    private final Collection<FixturePair> contactsToIgnore = new ArrayList<>();

    private float simulationAccumulator = 0;

    /**
     * Erstellt eine neue standardisierte Physik ohne Schwerkraft.
     */
    @Internal
    public WorldHandler(Layer layer) {
        this.layer = layer;
        this.world = new World(new Vec2());
        this.world.setContactListener(this);
    }

    /**
     * Gibt den World-Parameter der Physics aus.
     *
     * @return Der JB2D-World-Parameter der Welt.
     */
    @Internal
    public World getWorld() {
        return world;
    }

    public void setWorldPaused(boolean worldPaused) {
        this.worldPaused = worldPaused;
    }

    public boolean isWorldPaused() {
        return this.worldPaused;
    }

    /**
     * Assertion-Methode, die sicherstellt, dass die (JBox2D-)World der gerade nicht im World-Step ist.
     * Dies ist wichtig für die Manipulation von Actors (Manipulation vieler physikalischen Eigenschaften während
     * des World-Steps führt zu Inkonsistenzen).
     *
     * @throws RuntimeException Wenn die World sich gerade im World-Step befindet. Ist dies nicht der Fall, passiert
     *                          nichts (und es wird keine Exception geworfen).
     */
    @Internal
    public void assertNoWorldStep() {
        if (getWorld().isLocked()) {
            throw new RuntimeException("Die Operation kann nicht während des World-Step ausgeführt werden. " + "Ggf. mit Game.afterWorldStep wrappen.");
        }
    }

    public void step(float deltaSeconds) {
        if (worldPaused) {
            return;
        }

        synchronized (this) {
            synchronized (this.world) {
                // We use constant time frames for consistency
                // https://gamedev.stackexchange.com/q/86609/38865
                simulationAccumulator += deltaSeconds;

                while (simulationAccumulator >= STEP_TIME) {
                    simulationAccumulator -= STEP_TIME;

                    this.world.step(STEP_TIME, 6, 3);
                }
            }
        }
    }

    /**
     * Erstellt einen Body und mappt ihn intern zum analogen Actor-Objekt.
     *
     * @param bd    Exakte Beschreibung des Bodies.
     * @param actor Actor-Objekt, das ab sofort zu dem Body gehört.
     *
     * @return Der Body, der aus der BodyDef generiert wurde. Er liegt in der Game-World dieses
     * Handlers.
     */
    public Body createBody(BodyDef bd, Actor actor) {
        Body body;

        synchronized (world) {
            body = world.createBody(bd);
            body.setUserData(actor);
        }

        return body;
    }

    /**
     * Entfernt alle internen Referenzen auf einen Body und das zugehörige Actor-Objekt.
     *
     * @param body der zu entfernende Body
     */
    @Internal
    public void removeAllInternalReferences(Body body) {
        specificCollisionListeners.remove(body);
        generalCollisonListeners.remove(body);
    }

    /**
     * Fügt einen Contact der Blacklist hinzu. Kontakte in der Blacklist werden bis zur Trennung nicht aufgelöst.
     * Der Kontakt wird nach endContact wieder entfernt.
     */
    @Internal
    public void addContactToBlacklist(Contact contact) {
        contactsToIgnore.add(new FixturePair(contact.m_fixtureA, contact.m_fixtureB));
    }

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
    @Internal
    private void processContact(final Contact contact, boolean isBegin) {
        final Body b1 = contact.getFixtureA().getBody();
        final Body b2 = contact.getFixtureB().getBody();

        if (b1 == b2) {
            // Gleicher Body, don't care
            Logger.error("Collision", "Inter-Body Collision!");
            return;
        }

        /*
         * ~~~~~~~~~~~~~~~~~~~~~~~ TEIL I : Spezifische Checkups ~~~~~~~~~~~~~~~~~~~~~~~
         */

        //Sortieren der Bodies.
        Body lower, higher;
        if (b1.hashCode() == b2.hashCode()) {
            //Hashes sind gleich (blöde Sache!) -> beide Varianten probieren.
            List<Checkup> result1 = specificCollisionListeners.get(b1);
            if (result1 != null) {
                for (Checkup c : result1) {
                    c.checkCollision(b2, contact, isBegin);
                }
            }
            List<Checkup> result2 = specificCollisionListeners.get(b2);
            if (result2 != null) {
                for (Checkup c : result2) {
                    c.checkCollision(b1, contact, isBegin);
                }
            }
        } else {
            if (b1.hashCode() < b2.hashCode()) {
                //f1 < f2
                lower = b1;
                higher = b2;
            } else {
                //f1 > f2
                lower = b2;
                higher = b1;
            }
            List<Checkup> result = specificCollisionListeners.get(lower);
            if (result != null) {
                for (Checkup c : result) {
                    c.checkCollision(higher, contact, isBegin);
                }
            }
        }

        /*
         * ~~~~~~~~~~~~~~~~~~~~~~~ TEIL II : Allgemeine Checkups ~~~~~~~~~~~~~~~~~~~~~~~
         */
        generalCheckup(b1, b2, contact, isBegin);
        generalCheckup(b2, b1, contact, isBegin);

        if (!isBegin) {
            //Contact ist beendet -> Set Enabled and remove from blacklist
            contact.setEnabled(true);
            //System.out.println("REMOVE");
            removeFromBlacklist(contact);
        }
    }

    private void removeFromBlacklist(Contact contact) {
        FixturePair fixturePair = null;
        for (FixturePair ignoredPair : contactsToIgnore) {
            if (ignoredPair.matches(contact.m_fixtureA, contact.m_fixtureB)) {
                fixturePair = ignoredPair;
                break;
            }
        }

        if (fixturePair != null) {
            contactsToIgnore.remove(fixturePair);
        }
    }

    @Internal
    private void generalCheckup(Body act, Body col, Contact contact, final boolean isBegin) {
        List<CollisionListener<Actor>> list = generalCollisonListeners.get(act);
        if (list != null) {
            Actor other = (Actor)col.getUserData();
            if (other == null) {
                return; // Is null on async removals
            }

            CollisionEvent<Actor> collisionEvent = new CollisionEvent<>(contact, other);
            for (CollisionListener<Actor> listener : list) {
                if (isBegin) {
                    listener.onCollision(collisionEvent);
                } else {
                    listener.onCollisionEnd(collisionEvent);
                }
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {
        for (FixturePair ignoredPair : contactsToIgnore) {
            if (ignoredPair.matches(contact.m_fixtureA, contact.m_fixtureB)) {
                contact.setEnabled(false);
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {
        // Ignore that shit.
    }

    public Layer getLayer() {
        return layer;
    }

    /* ____________ On-Request Collision Checkups ____________ */

    @Internal
    public Fixture[] queryAABB(AABB aabb) {
        ArrayList<Fixture> fixtures = new ArrayList<>();
        world.queryAABB((QueryCallback) fixtures::add, aabb);
        return fixtures.toArray(new Fixture[0]);
    }

    @Internal
    public static boolean isBodyCollision(Body a, Body b) {
        if (a == null || b == null) {
            return false;
        }

        for (ContactEdge contact = a.getContactList(); contact != null; contact = contact.next) {
            if (contact.other == b) {
                // Contact exists with other Body. Next, check if they are actually touching
                if (contact.contact.isTouching()) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Speichert ein Korrespondierendes Body-Objekt sowie
     */
    private static class Checkup<E extends Actor> {
        private final CollisionListener<E> listener;  // Aufzurufen
        private final Body body;                      // Der zweite Body (erster Body ist Hashmap-Schlüssel)
        private final E collidingActor;               // Das Actor-Objekt, das neben dem Actor angemeldet wurde

        /**
         * Erstellt das Checkup-Objekt
         *
         * @param listener       Das aufzurufende KR
         * @param body           Der zweite Body für den Checkup
         * @param collidingActor Der zugehörige Collider für diesen Checkup
         */
        private Checkup(CollisionListener<E> listener, Body body, E collidingActor) {
            this.listener = listener;
            this.body = body;
            this.collidingActor = collidingActor;
        }

        public void checkCollision(Body body, Contact contact, boolean isBegin) {
            if (this.body == body) {
                CollisionEvent<E> collisionEvent = new CollisionEvent<>(contact, collidingActor);
                if (isBegin) {
                    listener.onCollision(collisionEvent);
                } else {
                    listener.onCollisionEnd(collisionEvent);
                }
            }
        }
    }

    /**
     * Meldet ein allgemeines KR-Interface in dieser World an.
     *
     * @param listener Das anzumeldende KR Interface
     * @param actor    Der Actor (KR Interface wird bei jeder Kollision des Actors informiert)
     */
    @Internal
    public static void addGenericCollisionListener(CollisionListener<Actor> listener, Actor actor) {
        actor.addMountListener(() -> {
            Body body = actor.getPhysicsHandler().getBody();

            if (body == null) {
                throw new IllegalStateException("Body is missing on an Actor with an existing WorldHandler");
            }

            actor.getPhysicsHandler().getWorldHandler().generalCollisonListeners.computeIfAbsent(body, key -> new CopyOnWriteArrayList<>()).add(listener);
        });
    }

    /**
     * Meldet ein spezifisches CollisionListener-Interface in dieser World an.
     *
     * @param listener Das anzumeldende KR Interface
     * @param actor    Der Actor (Haupt-Actor-Objekt)
     * @param collider Der Collider (zweites Actor-Objekt)
     * @param <E>      Der Type des Colliders.
     */
    @Internal
    public static <E extends Actor> void addSpecificCollisionListener(Actor actor, E collider, CollisionListener<E> listener) {
        addMountListener(actor, collider, (worldHandler) -> {
            Body b1 = actor.getPhysicsHandler().getBody();
            Body b2 = collider.getPhysicsHandler().getBody();

            if (b1 == null || b2 == null) {
                Logger.error("Kollision", "Ein Actor-Objekt ohne physikalischen Body wurde zur Kollisionsüberwachung angemeldet.");
                return;
            }

            Body lower, higher;
            if (b1.hashCode() < b2.hashCode()) {
                lower = b1;
                higher = b2;
            } else {
                lower = b2;
                higher = b1;
            }

            Checkup<E> checkup = new Checkup<>(listener, higher, collider);
            worldHandler.specificCollisionListeners.computeIfAbsent(lower, key -> new CopyOnWriteArrayList<>()).add(checkup);
        });
    }

    @Internal
    public static <JointType extends org.jbox2d.dynamics.joints.Joint, Wrapper extends Joint<JointType>> Wrapper createJoint(Actor a, Actor b, JointBuilder<JointType> jointBuilder, Wrapper wrapper) {
        List<Runnable> releaseCallbacks = addMountListener(a, b, worldHandler -> wrapper.setJoint(jointBuilder.createJoint(worldHandler.getWorld(), a.getPhysicsHandler().getBody(), b.getPhysicsHandler().getBody()), worldHandler));
        releaseCallbacks.forEach(wrapper::addReleaseListener);

        return wrapper;
    }

    @Internal
    public static List<Runnable> addMountListener(Actor a, Actor b, Consumer<WorldHandler> runnable) {
        List<Runnable> releases = new ArrayList<>();

        AtomicBoolean skipListener = new AtomicBoolean(true);

        Runnable listenerA = () -> {
            WorldHandler worldHandler = a.getPhysicsHandler().getWorldHandler();
            if (!skipListener.get() && b.isMounted() && b.getPhysicsHandler().getWorldHandler() == worldHandler) {
                runnable.accept(worldHandler);
            }
        };

        Runnable listenerB = () -> {
            WorldHandler worldHandler = b.getPhysicsHandler().getWorldHandler();
            if (!skipListener.get() && a.isMounted() && a.getPhysicsHandler().getWorldHandler() == worldHandler) {
                runnable.accept(worldHandler);
            }
        };

        a.addMountListener(listenerA);
        b.addMountListener(listenerB);

        skipListener.set(false);

        releases.add(() -> a.removeMountListener(listenerA));
        releases.add(() -> b.removeMountListener(listenerB));

        if (a.isMounted() && b.isMounted()) {
            runnable.accept(a.getPhysicsHandler().getWorldHandler());
        }

        return releases;
    }

    private static class FixturePair {
        private final Fixture f1;
        private final Fixture f2;

        public FixturePair(Fixture b1, Fixture b2) {
            this.f1 = b1;
            this.f2 = b2;
        }

        /**
         * Prüft dieses Body-Tupel auf Referenzgleichheit mit einem weiteren.
         *
         * @param a Body A
         * @param b Body B
         *
         * @return this == (A|B)
         */
        public boolean matches(Fixture a, Fixture b) {
            return (f1 == a && f2 == b) || (f1 == b && f2 == a);
        }
    }
}

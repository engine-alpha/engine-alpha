package ea.internal.phy;

import ea.*;

import ea.internal.ano.NoExternalUse;
import ea.internal.frame.FrameThread;
import ea.internal.frame.WorldThread;
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
 * Die WorldHandler-Klasse ist die (nicht objektgebundene) Middleware zwischen der JBox2D Engine und der EA.
 * Sie ist verantwortlich für:
 * <ul>
 *     <li>Den globalen "World"-Parameter aus der JBox2D Engine.</li>
 *     <li>Translation zwischen JB2D-Vektoren (SI-Basiseinheiten) und denen der Engine (Zeichengrößen)</li>
 * </ul>
 * Created by andonie on 14.02.15.
 */
public class WorldHandler
implements ContactListener {


    /**
     * Die World dieses Handlers. Hierin laufen globale Einstellungen (z.B. Schwerkraft) ein.
     */
    private final World world;

    /**
     * Der WorldThread, der sich um die Rechen-Steps innerhalb der Engine kümmert.
     * Die Referenz wird genutzt, um sicherzustellen, dass keine Bodies erstellt werden,
     * während die world sich im step befindet.
     */
    private WorldThread worldThread;

    private final HashMap<Body, List<Checkup>> collisionTracker = new HashMap<>();

    @NoExternalUse
    public void setWorldThread(WorldThread worldThread) {
        this.worldThread = worldThread;
    }

    /**
     * Umrechnungsgröße zwischen Größen der Physik-Engine und der Zeichenebene der EA.
     * Gibt an, wie viele Pixel genau einen Meter ausmachen.<br/>
     *
     * <b>Einheit: [px/m]</b>
     */
    private float pixelProMeter = 30f;

    /**
     * Flag, das angibt, ob die Pixel Pro Meter bereits angefragt wurden.
     */
    private boolean ppmRequested = false;

    /**
     * Gibt die Umrechnungsgröße zwischen Größen der Physik-Engine und der Zeichenebene der EA an.
     * @return  Gibt an, wie viele Pixel genau einen Meter ausmachen.<br />
     *          <b>Einheit: [px/m]</b>
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
     * Setzt die Umrechnungsgröße zwischen Größen der Physik-Engine und der Zeichenebene der EA.
     * @param pixelProMeter Die Anzahl an Pixeln, die genau einen Meter ausmachen.<br />
     *                      <i>Die Größe ist unabhängig vom Kamerazoom.</i>< br/>
     *                      <b>Einheit: [px/m]</b>
     */
    public void setPixelProMeter(float pixelProMeter) {
        if(ppmRequested)
            throw new IllegalStateException("Die Pixel-Pro-Meter Umrechnungszahl darf nach Arbeit mit den Raum-Objekten" +
                    " der entsprechenden WorldHandler-Umgebung nicht geändert werden. Das Setzen der Konstante vor" +
                    " die Arbeit mit den Raum-Objekten verschieben.");
        this.pixelProMeter = pixelProMeter;
    }

    /**
     * Erstellt eine neue standardisierte Physik (Schwerkraft senkrecht nach unten, 9,81 m/s^2)
     */
    @NoExternalUse
    public WorldHandler() {
        this.world = new World(new Vec2(0f, 0f)); //Erstelle standard-World mit Standard-Gravitation.
        this.world.setContactListener(this);
    }

    /**
     * Gibt den World-Parameter der Physik aus.
     * @return  Der JB2D-World-Parameter der Welt.
     */
    @NoExternalUse
    public World getWorld() {
        return world;
    }

    /**
     * Gibt die interne Referenz auf den World Thread aus.
     * @return die World-Thread Referenz.
     */
    @NoExternalUse
    public WorldThread getWorldThread() {
        return worldThread;
    }

    /**
     * Übersetzt einen EA-Vektor in einen JB2D-Vektor auf Basis des gesetzten Pixel/Meter-Verhältnisses.
     * @param eaV   Ein EA-Vektor.
     * @return      Der analoge Vektor in der JB2D-Engine.
     */
    @NoExternalUse
    public Vec2 fromVektor(Vektor eaV) {
        float x = eaV.x / pixelProMeter;
        float y = eaV.y / pixelProMeter;
        return new Vec2(x,y);
    }

    /**
     * Übersetzt einen JB2D-Vektor in einen EA-Vektor auf Basis des gesetzten Pixel/Meter-Verhältnisses.
     * @param jb2dV Ein JB2D-Vektor.
     * @return      Der analoge Vektor im EA-Format auf der Zeichenebene.
     */
    @NoExternalUse
    public Vektor fromVec2(Vec2 jb2dV) {
        float x = jb2dV.x * pixelProMeter;
        float y = jb2dV.y * pixelProMeter;
        return new Vektor(x,y);
    }

    public Body createBody(BodyDef bd) {
        return worldThread.createBody(bd);
    }

    /**
     * Übersetzt einen Winkel in Radians in Grad.
     * @param rad Ein Winkel in Radians.
     * @return    Der analoge Winkel in Grad.
     */
    public static float radToDeg(float rad) {
        return rad * degProRad;
    }

    /**
     * Umrechnungskonstante für Grad/Radians
     */
    private static final float degProRad = (float)((double)180/Math.PI);


    public final void addCollisionListening(KollisionsReagierbar kr, Raum r1, Raum r2, int code) {

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

    private void processContact(Contact contact, boolean isBegin) {
        Body b1 = contact.getFixtureA().getBody();
        Body b2 = contact.getFixtureB().getBody();
        if(b1==b2) {
            //Gleicher Body, don't care
            Logger.error("Collision", "Inter-Body Collision!");
            return;
        }
        Body lower=null, higher=null;
        if (b1.hashCode() == b2.hashCode()) {
            //Hashes sind gleich (blöde Sache!) -> beide Varianten probieren.
            List<Checkup> result1 = collisionTracker.get(b1);
            if(result1 != null) {
                for(Checkup c : result1) {
                    c.checkCollision(b2, isBegin);
                }
            }
            List<Checkup> result2 = collisionTracker.get(b2);
            if(result2 != null) {
                for(Checkup c : result2) {
                    c.checkCollision(b1, isBegin);
                }
            }
            return;
        } else if(b1.hashCode() < b2.hashCode()) {
            //b1 < b2
            lower = b1;
            higher = b2;
        } else {
            //b1 > b2
            lower = b2;
            higher = b1;
        }

        List<Checkup> result = collisionTracker.get(lower);
        if(result != null) {
            for(Checkup c : result) {
                c.checkCollision(higher, isBegin);
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
    private static class Checkup {
        private final KollisionsReagierbar reagierbar;  //Aufzurufen
        private final Body body2;                       //Der zweite Body (erster Body ist Hashmap-Schlüssel)
        private final int code;                         //Der Code für den Aufruf
        private final FrameThread frameThread;          //Zum Anmelden des Dispatches

        private final Dispatchable begin = new Dispatchable() {
            @Override
            public void dispatch() {
                reagierbar.kollision(code);
            }
        };
        private final Dispatchable end = new Dispatchable() {
            @Override
            public void dispatch() {
                reagierbar.kollisionBeendet(code);
            }
        };

        /**
         * Erstellt das Checkup-Objekt
         * @param reagierbar    Das aufzurufende KR
         * @param body2         Der zweite Body für den Checkup
         * @param code          Der Code für den Aufruf
         * @param frameThread   Der Frame-Thread, an dem der Dispatch (aktivierung des KR) angemeldet wird.
         */
        private Checkup(KollisionsReagierbar reagierbar, Body body2, int code, FrameThread frameThread) {
            this.reagierbar = reagierbar;
            this.body2 = body2;
            this.code = code;
            this.frameThread = frameThread;
        }

        public void checkCollision(Body secondBodyOfActualCollision, boolean isBegin) {
            if(body2 == secondBodyOfActualCollision) {
                frameThread.addInternalEvent(isBegin ? begin : end);
            }
        }
    }

    public static void kollisionsReagierbarEingliedern(KollisionsReagierbar kr, int code, Raum r1, Raum r2) {
        final WorldHandler wh1 = r1.getPhysikHandler().worldHandler();
        final WorldHandler wh2 = r2.getPhysikHandler().worldHandler();
        if(wh1 == null || wh2 == null || wh1 != wh2) {
            Logger.error("Kollision", "Zwei Objekte sollten zur Kollision angemeldet werden. " +
                    "Dafür müssen beide an der selben Wurzel (direkt oder indirekt) angemeldet sein.");
            return;
        }

        final WorldHandler worldHandler = wh1;

        Body b1 = r1.getPhysikHandler().getBody(), b2 = r2.getPhysikHandler().getBody();
        if(b1 == null || b2 == null) {
            Logger.error("Kollision", "Ein Raum-Objekt ohne physikalischen Body wurde zur Kollisionsüberwachung" +
                    " angemeldet.");
            return;
        }

        Body lower, higher;
        if(b1.hashCode() < b2.hashCode()) {
            //b1 < b2
            lower = b1;
            higher = b2;
        } else {
            //b1 > b2
            lower = b2;
            higher = b1;
        }

        Checkup toAdd = new Checkup(kr, higher, code, worldHandler.worldThread.getMaster());

        List<Checkup> atKey = wh1.collisionTracker.get(lower);
        if(atKey == null) {
            //NO LIST THERE YET: Create new Entry in Hashmap
            atKey = new CopyOnWriteArrayList<>();
            atKey.add(toAdd);
            wh1.collisionTracker.put(lower, atKey);
        } else {
            atKey.add(toAdd);
        }
    }
}

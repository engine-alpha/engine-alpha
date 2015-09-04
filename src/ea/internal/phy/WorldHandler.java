package ea.internal.phy;

import ea.*;

import ea.internal.ano.NoExternalUse;
import ea.internal.frame.WorldThread;
import ea.internal.util.Logger;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.Contact;

import java.util.LinkedList;
import java.util.Queue;
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


    public static void kollisionsReagierbarEingliedern(KollisionsReagierbar kr, Raum r1, Raum r2) {
        final WorldHandler wh1 = r1.getPhysikHandler().worldHandler();
        final WorldHandler wh2 = r2.getPhysikHandler().worldHandler();
        if(wh1 == null || wh2 == null || wh1 != wh2) {
            Logger.error("Kollision", "Zwei Objekte sollten zur Kollision angemeldet werden. " +
                    "Dafür müssen beide an der selben Wurzel (direkt oder indirekt) angemeldet sein.");
        }

        final WorldHandler worldHandler = wh1;


    }

    private final CopyOnWriteArrayList<BodyHandler> waitingForFixture = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<BodyHandler> wantToDo = new CopyOnWriteArrayList<>();

    private final CopyOnWriteArrayList<BodyHandler> killMe = new CopyOnWriteArrayList<>();
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
        //contact.getFixtureA();
        System.out.println("BEGIN");
        Body b1 = contact.getFixtureA().getBody();
        Body b2 = contact.getFixtureB().getBody();
        if(b1==b2) {
            //Gleicher Body, don't care
            Logger.debug("Collision", "Inter-Body Collision!");
            return;
        }

    }

    @Override
    public void endContact(Contact contact) {

        System.out.println("____________________END____________________");
    }

    @Override
    public void preSolve(Contact contact, Manifold manifold) {

        System.out.println("PRE");
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse contactImpulse) {

        System.out.println("POST");
    }

    /**
     * Wird vom World-Thread aufgerufen, <b>nachdem</b> ein Sorld-Step ausgeführt
     * wurde. Innerhalb dieses Aufrufes ist die World garantiert NICHT im locked
     * state. Hierin können sicher Fixtures u.Ä. erstellt werden.
     */
    @NoExternalUse
    public void afterWorldStep() {
        for(BodyHandler bh : waitingForFixture) {
            bh.createBodyAndFixture();
        }
        waitingForFixture.clear();

        for(BodyHandler bh : wantToDo) {

        }
    }

    /**
     * Wird von einem BodyHandler am Ende des Konstruktors aufgerufen.
     * Reiht eben den ein, dass die Body / Fixture Creation direkt nach
     * dem nächsten World Step stattfinden kann.
     * @param bh
     */
    @NoExternalUse
    public void enqueueNewBodyHandler(BodyHandler bh) {
        this.waitingForFixture.add(bh);
    }


}

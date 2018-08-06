package ea.internal.phy;

import ea.Vector;
import ea.actor.ActorGroup;
import ea.handle.Physics;
import ea.internal.util.Logger;
import org.jbox2d.dynamics.Body;

/**
 * Spezieller Physics-Handler für ActorGroup.
 *
 * @author Michael Andonie
 */
public class KnotenHandler extends PhysikHandler {
    private final ActorGroup actorGroup;

    /**
     * Symbolische Position des Knotens.
     */
    private Vector position = Vector.NULL;

    /**
     * Symbolische Rotation des Knotens
     */
    private float rotation = 0f;

    /**
     * Initialisiert den Physics-Handler.
     *
     * @param raum Der ActorGroup, um den sich dieser Handler kümmert.
     */
    public KnotenHandler(ActorGroup raum) {
        super(raum, Physics.Type.PASSIVE, false);
        actorGroup = raum;
    }

    @Override
    public void setSensor(boolean isSensor) {
        //
    }

    @Override
    public void verschieben(Vector v) {
        position = position.add(v);
        actorGroup.forEach(r -> r.position.move(v));
    }

    @Override
    public Vector mittelpunkt() {
        return null;
    }

    @Override
    public boolean beinhaltet(Vector p) {
        return false;
    }

    @Override
    public Vector position() {
        return position;
    }

    @Override
    public float rotation() {
        return rotation;
    }

    @Override
    public void rotieren(float radians) {
        rotation += radians;
        actorGroup.forEach(r -> r.position.setRotation(radians));
    }

    @Override
    public void dichteSetzen(float dichte) {
        Logger.error("Physics", "Ein ActorGroup hat keine Dichte");
    }

    @Override
    public float dichte() {
        Logger.error("Physics", "Ein ActorGroup hat keine Dichte");
        return 0;
    }

    @Override
    public void reibungSetzen(float reibung) {
        Logger.error("Physics", "Ein ActorGroup hat keine Reibung");
    }

    @Override
    public float reibung() {
        Logger.error("Physics", "Ein ActorGroup hat keine Reibung");
        return 0;
    }

    @Override
    public void elastizitaetSetzen(float ela) {
        Logger.error("Physics", "Ein ActorGroup hat keine Elastizität");
    }

    @Override
    public float elastizitaet() {
        Logger.error("Physics", "Ein ActorGroup hat keine Elastizität");
        return 0;
    }

    @Override
    public void masseSetzen(float masse) {
        Logger.error("Physics", "Ein ActorGroup hat keine Masse");
    }

    @Override
    public float masse() {
        Logger.error("Physics", "Ein ActorGroup hat keine Masse");
        return 0;
    }

    @Override
    public void kraftWirken(Vector kraft) {

    }

    @Override
    public void drehMomentWirken(float drehmoment) {

    }

    @Override
    public void drehImpulsWirken(float drehimpuls) {

    }

    @Override
    public void schwerkraftSetzen(Vector schwerkraftInN) {

    }

    @Override
    public PhysikHandler typ(Physics.Type type) {
        return null;
    }

    @Override
    public void kraftWirken(Vector kraftInN, Vector globalerOrt) {

    }

    @Override
    public void impulsWirken(Vector impulsInNS, Vector globalerOrt) {

    }

    @Override
    public void killBody() {

    }

    @Override
    public WorldHandler worldHandler() {
        return actorGroup.getScene().getWorldHandler();
    }

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public void physicalReset() {
        actorGroup.forEach(r -> r.physics.cancelAll());
    }

    @Override
    public void geschwindigkeitSetzen(Vector geschwindigkeitInMProS) {

    }

    @Override
    public Vector geschwindigkeit() {
        return null;
    }

    @Override
    public void rotationBlockiertSetzen(boolean block) {

    }

    @Override
    public boolean rotationBlockiert() {
        return false;
    }

    @Override
    public boolean testIfGrounded() {
        return false;
    }
}

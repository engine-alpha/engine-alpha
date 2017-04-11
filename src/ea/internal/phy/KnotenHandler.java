package ea.internal.phy;

import ea.*;
import ea.internal.util.Logger;
import org.jbox2d.dynamics.Body;

/**
 * Spezieller Physik-Handler für Knoten.
 * Created by andonie on 05.09.15.
 */
public class KnotenHandler
extends PhysikHandler {

    private final Knoten knoten;

    /**
     * Symbolische Position des Knotens.
     */
    private Punkt position = Punkt.ZENTRUM;

    /**
     * Symbolische Rotation des Knotens
     */
    private float rotation = 0f;

    /**
     * Initialisiert den Physik-Handler.
     *
     * @param raum      Der Knoten, um den sich dieser Handler kümmert.
     */
    public KnotenHandler(Knoten raum) {
        super(raum, Physik.Typ.PASSIV, false);
        knoten = raum;
    }

    @Override
    public void setSensor(boolean isSensor) {
        //
    }

    @Override
    public void update(WorldHandler worldHandler) throws IllegalStateException {
        //NICHTS ZU TUN
        // (die Weitergabe wurde bereits im Knoten implementiert)
    }

    @Override
    public void verschieben(Vektor v) {
        position = position.verschobeneInstanz(v);
        for(Raum m : knoten.getList()) {
            m.position.verschieben(v);
        }
    }

    @Override
    public Punkt mittelpunkt() {
        return null;
    }

    @Override
    public boolean beinhaltet(Punkt p) {
        return false;
    }

    @Override
    public Punkt position() {
        return position;
    }

    @Override
    public float rotation() {
        return rotation;
    }

    @Override
    public void rotieren(float radians) {
        rotation += radians;
        for(Raum m : knoten.getList()) {
            m.position.rotieren(radians);
        }
    }

    @Override
    public void dichteSetzen(float dichte) {
        Logger.error("Physik", "Ein Knoten hat keine Dichte");
    }

    @Override
    public float dichte() {
        Logger.error("Physik", "Ein Knoten hat keine Dichte");
        return 0;
    }

    @Override
    public void reibungSetzen(float reibung) {
        Logger.error("Physik", "Ein Knoten hat keine Reibung");
    }

    @Override
    public float reibung() {
        Logger.error("Physik", "Ein Knoten hat keine Reibung");
        return 0;
    }

    @Override
    public void elastizitaetSetzen(float ela) {
        Logger.error("Physik", "Ein Knoten hat keine Elastizität");
    }

    @Override
    public float elastizitaet() {
        Logger.error("Physik", "Ein Knoten hat keine Elastizität");
        return 0;
    }

    @Override
    public void masseSetzen(float masse) {
        Logger.error("Physik", "Ein Knoten hat keine Masse");
    }

    @Override
    public float masse() {
        Logger.error("Physik", "Ein Knoten hat keine Masse");
        return 0;
    }

    @Override
    public void kraftWirken(Vektor kraft) {

    }

    @Override
    public void drehMomentWirken(float drehmoment) {

    }

    @Override
    public void drehImpulsWirken(float drehimpuls) {

    }

    @Override
    public void schwerkraftSetzen(Vektor schwerkraftInN) {

    }

    @Override
    public PhysikHandler typ(Physik.Typ typ) {
        return null;
    }

    @Override
    public void kraftWirken(Vektor kraftInN, Punkt globalerOrt) {

    }

    @Override
    public void impulsWirken(Vektor impulsInNS, Punkt globalerOrt) {

    }

    @Override
    public void killBody() {

    }

    @Override
    public WorldHandler worldHandler() {
        return null;
    }

    @Override
    public Body getBody() {
        return null;
    }

    @Override
    public void physicalReset() {
        for(Raum m : knoten.getList()) {
            m.physik.inRuheVersetzen();
        }
    }

    @Override
    public void geschwindigkeitSetzen(Vektor geschwindigkeitInMProS) {

    }

    @Override
    public Vektor geschwindigkeit() {
        return null;
    }

    @Override
    public void rotationBlockiertSetzen(boolean block) {

    }

    @Override
    public boolean rotationBlockiert() {
        return false;
    }
}

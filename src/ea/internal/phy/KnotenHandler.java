package ea.internal.phy;

import ea.*;
import org.jbox2d.dynamics.Body;

/**
 * Spezieller Physik-Handler für Knoten.
 * Created by andonie on 05.09.15.
 */
public class KnotenHandler
extends PhysikHandler {

    private final Knoten knoten;

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

    }

    @Override
    public void verschieben(Vektor v) {

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
        return null;
    }

    @Override
    public float rotation() {
        return 0;
    }

    @Override
    public void rotieren(float radians) {

    }

    @Override
    public void dichteSetzen(float dichte) {

    }

    @Override
    public float dichte() {
        return 0;
    }

    @Override
    public void reibungSetzen(float reibung) {

    }

    @Override
    public float reibung() {
        return 0;
    }

    @Override
    public void elastizitaetSetzen(float ela) {

    }

    @Override
    public float elastizitaet() {
        return 0;
    }

    @Override
    public void masseSetzen(float masse) {

    }

    @Override
    public float masse() {
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

    }

    @Override
    public void geschwindigkeitSetzen(Vektor geschwindigkeitInMProS) {

    }

    @Override
    public Vektor geschwindigkeit() {
        return null;
    }
}

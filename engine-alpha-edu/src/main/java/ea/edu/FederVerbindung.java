package ea.edu;

import ea.actor.PrismaticJoint;
import ea.internal.annotations.API;

public class FederVerbindung extends Verbindung<PrismaticJoint> {
    /**
     * Erzeugt eine neue Federverbindung.
     *
     * @param joint Internes Box2D-Objekt
     * @hidden
     */
    FederVerbindung(PrismaticJoint joint) {
        super(joint);
    }

    @API
    public void setzeMotorgeschwindigkeit(double geschwindigkeit) {
        this.joint.setMotorSpeed((float) geschwindigkeit);
    }

    @API
    public double nenneMotorgeschwindigkeit() {
        return this.joint.getMotorSpeed();
    }

    @API
    public void setzeMaximaleMotorKraft(double maximalerWert) {
        this.joint.setMaximumMotorForce((float) maximalerWert);
    }

    @API
    public double nenneMaximaleMotorKraft() {
        return this.joint.getMaximumMotorForce();
    }

    @API
    public void setzeMotorAktiv(boolean aktiv) {
        this.joint.setMotorEnabled(aktiv);
    }

    @API
    public boolean nenneMotorAktiv() {
        return this.joint.isMotorEnabled();
    }

    @API
    public void setzeGrenzwerte(double untereGrenze, double obereGrenze) {
        this.joint.setLimits((float) untereGrenze, (float) obereGrenze);
    }

    @API
    public void setzeObereGrenze(double obereGrenze) {
        this.joint.setUpperLimit((float) obereGrenze);
    }

    @API
    public void setzeUntereGrenze(double untereGrenze) {
        this.joint.setLowerLimit((float) untereGrenze);
    }

    @API
    public double nenneObereGrenze() {
        return this.joint.getUpperLimit();
    }

    @API
    public double nenneUntereGrenze() {
        return this.joint.getLowerLimit();
    }

    @API
    public void setzeGrenzwerteAktiv(boolean aktiv) {
        this.joint.setLimitEnabled(aktiv);
    }

    @API
    public boolean nenneGrenzwerteAktiv() {
        return this.joint.isLimitEnabled();
    }
}

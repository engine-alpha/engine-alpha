package ea.edu;

import ea.actor.PrismaticJoint;

public class FederVerbindung extends Verbindung<PrismaticJoint> {
    public FederVerbindung(PrismaticJoint joint) {
        super(joint);
    }

    public void setzeMotorgeschwindigkeit(double geschwindigkeit) {
        this.joint.setMotorSpeed((float)geschwindigkeit);
    }

    public double nenneMotorgeschwindigkeit() {
        return this.joint.getMotorSpeed();
    }

    public void setzeMaximaleMotorKraft(double maximalerWert) {
        this.joint.setMaximumMotorForce((float)maximalerWert);
    }

    public double nenneMaximaleMotorKraft() {
        return this.joint.getMaximumMotorForce();
    }

    public void setzeMotorAktiv(boolean aktiv) {
        this.joint.setMotorEnabled(aktiv);
    }

    public boolean nenneMotorAktiv() {
        return this.joint.isMotorEnabled();
    }

    public void setzeGrenzwerte(double untereGrenze, double obereGrenze) {
        this.joint.setLimits((float)untereGrenze, (float)obereGrenze);
    }

    public void setzeObereGrenze(double obereGrenze) {
        this.joint.setUpperLimit((float) obereGrenze);
    }

    public void setzeUntereGrenze(double untereGrenze) {
        this.joint.setLowerLimit((float) untereGrenze);
    }

    public double nenneObereGrenze() {
        return this.joint.getUpperLimit();
    }

    public double nenneUntereGrenze() {
        return this.joint.getLowerLimit();
    }

    public void setzeGrenzwerteAktiv(boolean aktiv) {
        this.joint.setLimitEnabled(aktiv);
    }

    public boolean nenneGrenzwerteAktiv() {
        return this.joint.isLimitEnabled();
    }

}

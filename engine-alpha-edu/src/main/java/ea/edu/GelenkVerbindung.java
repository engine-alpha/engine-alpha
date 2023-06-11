package ea.edu;

import ea.actor.RevoluteJoint;
import ea.internal.annotations.API;

public class GelenkVerbindung extends Verbindung<RevoluteJoint> {

    /**
     * Erzeugt eine neue Gelenkverbindung.
     *
     * @param joint Internes Box2D-Objekt
     * @hidden
     */
    GelenkVerbindung(RevoluteJoint joint) {
        super(joint);
    }

    /**
     * Setzt eine Motorgeschwindigkeit für diesen joint.
     *
     * @param geschwindigkeit Geschwindigkeit in Grad pro Sekunde. <code>360</code> erzeugt eine volle Rotation
     *                        pro Sekunde.
     *                        Wird dieser Wert auf 0 gesetzt, so versucht diese Gelenkverbindung möglichst jeder
     *                        Rotation von außen entgegenzuwirken (je höher das maximale Drehmoment, desto effektiver
     *                        ist dieser Mechanismus)
     * @see #setzeMaximalesMotorDrehmoment(double)
     */
    @API
    public void setzeMotorgeschwindigkeit(double geschwindigkeit) {
        this.joint.setMotorSpeed((float) geschwindigkeit);
    }

    /**
     * Gibt die Motorgeschwindigkeit dieser Gelenkverbindung aus.
     *
     * @return Die Motorgeschwindigkeit dieser Gelenkverbindung in Grad pro Sekunde.
     */
    @API
    public double nenneMotorgeschwindigkeit() {
        return this.joint.getMotorSpeed();
    }

    /**
     * Setzt das maximale Drehmoment dieses Gelenkes, wenn es als Motor agiert.
     *
     * @param maximalerWert Das maximale Drehmoment, mit dem dieses Gelenk als Motor agiert.
     */
    @API
    public void setzeMaximalesMotorDrehmoment(double maximalerWert) {
        this.joint.setMaximumMotorTorque((float) maximalerWert);
    }

    @API
    public double nenneMaximalesMotorDrehmoment() {
        return this.joint.getMaximumMotorTorque();
    }

    @API
    public void setzeMotorAktiv(boolean aktiv) {
        this.joint.setMotorEnabled(aktiv);
    }

    @API
    public boolean nenneMotorAktiv() {
        return this.joint.isMotorEnabled();
    }

    /**
     * Begrenzt die Rotationsfreiheit dieses Gelenks.
     *
     * @param untereGrenze Die maximale Rotation nach oben
     * @param obereGrenze  Die maximale Rotation nach unten
     */
    @API
    public void setzeWinkelGrenzwerte(double untereGrenze, double obereGrenze) {
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

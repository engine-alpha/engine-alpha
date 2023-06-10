package ea.edu;


import ea.actor.Joint;

/**
 * Wrapt die Joint Handler
 * @author Michael Andonie
 */
public class Verbindung<T extends Joint<?>> {

    /**
     * Referenz auf das Engine Joint Objekt
     */
    protected T joint;

    public Verbindung(T joint) {
        this.joint = joint;
    }

    /**
     * Entfernt diese Verbindung.
     * Nachdem diese Methode ausgeführt wurde, ist diese Verbindung nicht mehr aktiv
     */
    public void entferneVerbindung() {
        this.joint.release();
    }

}

package ea.internal.phy;

import ea.Raum;
import ea.internal.ano.NoExternalUse;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Ein <code>Body-Handler</code> kümmert sich um die <i>physikalische Darstellung</i> eines <code>Raum</code>-Objekts.<br />
 * Er übernimmt zwei wesentliche Aufgaben:
 * <ul>
 *     <li>Die Kontrolle und Steuerung innerhalb der <b>Physik-Engine</b> aus Sicht des respektiven Raum Objekts.</li>
 *     <li>Die Speicherung der <i>räumlichen Eigenschaften</i> (Position und Rotation) des respektiven Raum-Objekts.</li>
 * </ul>
 * Created by andonie on 15.02.15.
 */
public class BodyHandler {

    /**
     * Das Raum-Objekt, um das sich dieses <code>BodyHandler</code>-Objekt kümmert.
     */
    private final Raum raum;

    /**
     * Die Physik des Handlers. Ist zu Beginn undefiniert. Kann nur einmal gesetzt werden.
     */
    private Physik physik;

    /**
     * Die Liste mit Physik-Ausführungen, die vor der Anmeldung einer Physik geschehen sind. Werden nachgeholt, sobald
     * eine Physik angemeldet wurde.
     */
    private List<PrePhysicsInvocation> prePhysicInvocations = new LinkedList<PrePhysicsInvocation>();

    /**
     * Erstellt einen neuen Body-Handler
     * @param raum
     */
    @NoExternalUse
    public BodyHandler(Raum raum) {
        this.raum = raum;
    }

    /**
     * Gibt an, ob dieser Handler bereits eine Physik-Umgebung hat.
     * @return  Die
     */
    @NoExternalUse
    public boolean hatPhysik() {
        return physik!=null;
    }

    /**
     * Setzt die Physik für diesen Handler.
     * Diese Methode kann nur einmal aufgerufen werden. Wird diese Methode ein zweites mal mit einem
     * anderen Physik-Objekt aufgerufen, so wird eine Exception geworfen.
     * @param physik    die zu setzende Physik.
     */
    @NoExternalUse
    public void physikSetzen(Physik physik) {
        if(hatPhysik()) {
            if (physik == this.physik) return;
            throw new IllegalStateException("Ein Raum-Objekt, das bereits auf einer Zeichenebene war, wurde an einer" +
                    " neuen Zeichenebene eingefügt. Das ist nicht möglich.");
        }
        this.physik = physik;
        for(PrePhysicsInvocation ppi : prePhysicInvocations) {
            ppi.physicsCatchup();
        }
        prePhysicInvocations = null; //Speicher wieder freigeben (nach Garbage Collection)
    }

    /**
     * Dieses Interface beschreibt eine Physik-relevante Aktion, die noch nicht durchführbar ist, da
     * die aktive Physik noch nicht bekannt ist. Die Aktion wird gespeichert und beim setzen der Physik geändert.
     */
    @NoExternalUse
    private interface PrePhysicsInvocation {
        public abstract void physicsCatchup();
    }
}

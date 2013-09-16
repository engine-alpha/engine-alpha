/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ea.graphic;
import ea.Fenster;

import java.awt.*;
/**
 * Simple grafische Klassen bestehen nicht aus Flaechen. Sie haben nicht die vielfaeltigen
 * Eigenschaften von Objekten der Klasse <code>Raum</code>.<br />
 * Ihre Verwendung ist nur im Mangel von Alternativen aus der <code>Raum</code>-Hierarchie
 * zu empfehlen.
 * @author Andonie
 */
public abstract class SimpleGraphic {
    /**
     * Methode zum zeichnen. Wird individuell ueberschrieben.
     * @param g Das Graphics-Objekt.
     * @param dx Die X-Verschiebung der Kamera
     * @param dy Die Y-Verschiebung der Kamera
     */
    public abstract void paint(Graphics g, int dx, int dy);

    /**
     * Konstruktor initialisiert bereits die Darstellung.
     */
    public SimpleGraphic() {
        Fenster.instanz().fillSimple(this);
    }

    public void loeschen() {
        Fenster.instanz().removeSimple(this);
    }
}

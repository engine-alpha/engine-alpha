/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ea;
import java.awt.*;
/**
 * Ein Strich ist ein einfaches zeichenbares Objekt.<br />
 * @author Andonie
 */
public class Strich
extends SimpleGraphic{

    /**
     * Der erste Punkt
     */
    private Punkt a;
    /**
     * Der zweite Punkt
     */
    private Punkt b;
    /**
     * Die Farbe der Darstellung
     */
    private Color c = Color.BLACK;

    /**
     * Konstruktor.
     * @param ax    X-Koordinate des Punktes A.
     * @param ay    Y-Koordinate des Punktes A.
     * @param bx    X-Koordinate des Punktes B.
     * @param by    Y-Koordinate des Punktes B.
     * @see #Strich(ea.Punkt, ea.Punkt)
     */
    public Strich(int ax, int ay, int bx, int by) {
        a = new Punkt(ax, ay);
        b = new Punkt(bx, by);
    }

    /**
     * Konstruktor.
     * @param a Der erste Punkt A.
     * @param b Der zweite Punkt B.
     * @see #Strich(int, int, int, int)
     */
    public Strich(Punkt a, Punkt b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Setzt die Punkte neu.
     * @param a Der neue Punkt a
     * @param b Der neue Punkt b
     * @see #Strich(int, int, int, int) 
     */
    public void punkteSetzen(Punkt a, Punkt b) {
        this.a = a;
        this.b = b;
    }

    /**
     * Setzt die Punkte neu
     * @param ax    X-Koordinate des Punktes A.
     * @param ay    Y-Koordinate des Punktes A.
     * @param bx    X-Koordinate des Punktes B.
     * @param by    Y-Koordinate des Punktes B.
     * @see #punkteSetzen(ea.Punkt, ea.Punkt)
     */
    public void punkteSetzen(int ax, int ay, int bx, int by) {
        a = new Punkt(ax, ay);
        b = new Punkt(bx, by);
    }

    /**
     * Setzt die Farbe des Striches neu
     * @param   s   Der Name der Farbe als String
     */
    public void farbeSetzen(String s) {
        c = Raum.zuFarbeKonvertieren(s);
    }

    /**
     * Setzt die Farbe des Striches neu
     * @param   f   Die Farbe als Objekt der Klasse <code>Farbe</code>.
     */
    public void farbeSetzen(Farbe f) {
        c = f.wert();
    }

    /**
     * Methode zum zeichnen. Wird individuell ueberschrieben.
     * @param g Das Graphics-Objekt.
     * @param dx Die X-Verschiebung der Kamera
     * @param dy Die Y-Verschiebung der Kamera
     */
    @Override
    public void paint(Graphics g, int dx, int dy) {
        g.setColor(c);
        g.drawLine(b.x-dx, b.y-dy, a.x-dx, a.y-dy);
    }
}

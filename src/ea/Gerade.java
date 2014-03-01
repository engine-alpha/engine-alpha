/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

/**
 * Eine Gerade ist die Verbindungslinie zwischen 2 Punkten.<br />
 * Sie ist definiert ueber ihre Steigung und einen Y-Achsenabschnitt.
 * 
 * @author Michael Andonie
 */
public class Gerade
{
    /**
     * Die Steigung der Geraden
     */
    public double steigung;
    
    /**
     * Die Absolute. Auch als Y-Achsenabschnitt bekannt.<br />
     * Diese Variable uebernimmt eine zweite Aufgabe, sollte die Gerade ein sonderfallSenkrecht sein. In diesem Fall beschreibt sie den X-Achsenabschnitt.
     */
    public float absolute;
    
    /**
     * Gibt an, ob die Gerade exakt senkrecht steht.<br />
     * In diesem Fall waere sie nicht ueber eine Geradengleichung beschreibbar, und auch Tests mit Hoeher/Tiefer ergaeben keinen Sinn.
     */
    public boolean sonderfallSenkrecht = false;
    
    /**
     * Gibt an, ob die Gerade exakt waagrecht steht.<br />
     * In diesem Fall wuerde ein Links/Rechts-Test, keinen Sinn ergeben.
     */
    public boolean sonderfallWaagrecht = false;
    
    /**
     * Konstruktor fuer Objekte der Klasse Gerade
     * @param   p1  Der erste bestimmende Punkt
     * @param   p2  Der zweite bestimmende Punkt
     */
    public Gerade(Punkt p1, Punkt p2)
    {
        if(p1.y - p2.y == 0) {
            sonderfallWaagrecht = true;
        }
        if(p1.x - p2.x == 0) {
            sonderfallSenkrecht = true;
            absolute = p1.x;
        } else {
            //STeigung = Y-Unterschied : X-Unterschied
            steigung = (p1.y-p2.y) / (p1.x - p2.x);
            //Absolute: Nach Y "aufloesen" :
            /*y = m*x + t
             *t = y - m*x
             *x = (y-t)/m */
             absolute = (int)(p1.y - (steigung*p1.x));
        }
    }
    
    /**
     * Berechnet den Winkel, den die Gerade mit der Vertikalen einschliesst.
     * @return  Der einschliessende Winkel, im <b>Bogenmass</b>!
     */
    public double winkel() {
        if(sonderfallSenkrecht) {
            return 0;
        }
        else {
            return ((Math.PI/2) + Math.atan(steigung));
        }
    }
    
    /**
     * Funktion unter einberechnung der Sonderfaelle.
     * @return
     */
    public boolean punktLiegtUeber(Punkt p) {
        return false;
    }
    
    /**
     * Prueft, ob die Gerade ueber oder unter einem bestimmten Punkt liegt.<br />
     * <b>ACHTUNG!</b><br />
     * Diese Methode testet nach den Hoehenwerten, die ja, bekanntlich, bei der Fensterprogrammierung umgekehrt sind.<br />
     * Liegt der Punkt also - im Bild sichtbar - tiefer als die Gerade, ist das Ergebnis TRUE!
     * @param  p Der zu testende Punkt
     * @return  true, wenn die Gerade Ueber dem Punkt liegt. Ist die Gerade senkrecht, so ist das Ergebnis automatisch FALSE
     */
    public boolean istHoeherAls(Punkt p) {
        if(sonderfallSenkrecht) {
            return false;
        }
        //Die Hoehe der Geraden an dem X-Wert des Punktes
        float y = (float) ((steigung*p.x)+absolute);
        return (y > p.y);
    }
    
    public boolean istRechtsVon(Punkt p) {
        if(sonderfallWaagrecht) {
            return false;
        }
        int x = (int)((p.y - absolute)/steigung);
        return (x > p.x);
    }
}

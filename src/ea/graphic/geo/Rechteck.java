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

package ea.graphic.geo;

/**
 * Beschreiben Sie hier die Klasse Rechteck.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Rechteck
extends Geometrie
{
    /**
     * Die Laenge
     */
    private int laenge;
    
    /**
     * Die Breite
     */
    private int breite;
    
    /**
     * Konstruktor fuer Objekte der Klasse Rechteck
     * @param   x   Die X Position (Koordinate der linken oberen Ecke) des Rechtecks
     * @param   y   Die X Position (Koordinate der linken oberen Ecke) des Rechtecks
     * @param   breite  Die Breite des Rechtecks
     * @param   hoehe   Die hoehe des Rechtecks
     */
    public Rechteck(int x, int y, int breite, int hoehe)
    {
        super(x, y);
        this.breite = breite;
        this.laenge = hoehe;
        aktualisierenFirst();
    }
    
    /**
     * Setzt beide Masse feur dieses Rechteck neu.
     * @param   breite  Die neue Breite des Rechtecks
     * @param   hoehe   Die neue Hoehe des Rechtecks
     */
    public void masseSetzen(int breite, int hoehe) {
        this.breite = breite;
        this.laenge = hoehe;
        aktualisieren();
    }
    
    /**
     * Setzt die Breite fuer dieses Rechteck neu.
     * @param   breite  Die neue Breite des Rechtecks
     * @see hoeheSetzen(int)
     */
    public void breiteSetzen(int breite) {
        this.breite = breite;
        aktualisieren();
    }
    
    /**
     * Setzt die Hoehe fuer dieses Rechteck neu.
     * @param   hoehe   Die neue Hoehe des Rechtecks
     * @see breiteSetzen(int)
     */
    public void hoeheSetzen(int hoehe) {
        this.laenge = hoehe;
        aktualisieren();
    }
    
    /**
     * In dieser Methode werden saemtliche Dreiecke neu berechnet und die Referenz bei Aufruf in der Superklasse hierauf gesetzt
     * @return  Ein Dreieck-Array mit allen, die Figur beschreibenden Dreiecken als Inhalt.
     */
    @Override
    public Dreieck[] neuBerechnen() {
        Dreieck[] i = {
            new Dreieck(new Punkt(x, y), new Punkt(x + breite, y), new Punkt(x, y + laenge)),
            new Dreieck(new Punkt(x, y+laenge), new Punkt(x+breite, y+laenge), new Punkt(x+breite, y))
        };
        return i;
    }

    /**
     * Zeichnet das Objekt.
     * @param   g   Das zeichnende Graphics-Objekt
     * @param   r    Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br />
     *                         Hierbei soll zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann gezeichnet werden.
     */
    @Override
    public void zeichnen(java.awt.Graphics g, BoundingRechteck r) {
        if(!r.schneidetBasic(this.dimension())) {
            return;
        }
        g.setColor(super.formen()[0].getColor());
        g.fillRect(x-r.x, y-r.y, breite, laenge);
    }
}

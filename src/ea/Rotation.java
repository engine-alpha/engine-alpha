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
 * Die Klasse <code>Rotation</code> beschreibt eine Rotation, also eine Drehung, diese ist durch zwei Eigenschaften 
 * definiert:<br />
 * 1. Der Winkel, um den gedreht wird<br />
 * 2. Der Punkt, um den gedreht<br />
 * <br />
 * <b>Ein Anwendungsbeispiel:</b><br /><br />
 * <code>
 * //Baustelle: wird vervollstaendigt, wenn Rotationen vollstaendig in Raum implementiert sind
 * </code>
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Rotation
{
    /**
     * Der Winkel, um den gedreht werden soll im Gradmass
     */
    private int winkel;

    /**
     * Das Zentrum der Drehung.
     */
    private Punkt zentrum;
    
    /**
     * Konstruktor fuer Objekte der Klasse Rotation
     * @param   winkelInGrad    Der Winkel, um den gedreht werden soll <b>im Gradmass</b>.
     * @param   zentrum Der Punkt, um den gedreht werden soll
     */
    public Rotation(int winkelInGrad, Punkt zentrum) {
        winkel = winkelInGrad;
        this.zentrum = zentrum;
    }
    
    /**
     * Methode zur Rueckgabe des Zentrums der Rotation.
     * @return   Der Punkt, um den gedreht werden soll als <code>Punkt</code>-Objekt
     */
    public Punkt zentrum() {
        return zentrum;
    }
    
    /**
     * Gibt den Drehwinkel im Gradmass aus.
     * @return  Der Winkel im Gradmass
     */
    public int winkelGrad() {
        return winkel;
    }
    
    /**
     * Gibt den Drehwinkel im Bogenmass aus.
     * @return  Der Winkel im Bogenmass
     */
    public double winkelBogen() {
        return Math.toRadians(winkel);
    }
}

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
 * Ein KollisionsReagierbar-Objekt kann auf das aufeinandertreffen zweier Raum-Objekte reagieren.<br />
 * Bei einer komplizierteren Aufgabe sieht das Anmelden bei einem <code>Physik</code>-Objekt
 * des Listeners ungefaehr so aus:<br />
 * <br /><br /><br /><br />
 * <code>
 * //Bereits Instanziiertes Physik-Objekt.
 * Physik physik;
 * 
 * 
 * </code><br />
 * 
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */

public interface KollisionsReagierbar
{
    /**
     * Diese Methode wird dann aufgerufen, wenn die mit diesem Interface zusammen angemeldeten Raum-Objekte 
     * kollidieren.
     * @param   code    Der bei der Anmeldung mitgegebene Code zur eventuellen Weiterverarbeitung bei Mehrfachanmeldung 
     * Dieses Interfaces
     */
    public abstract void kollision(int code);
}

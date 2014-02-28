/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 * 
 * Copyright (C) 2011 Michael Andonie
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

/**
 * Diese Klasse ist ein groesseres Spiel.<br />
 * Es gibt hier Reaktionsmoeglichkeiten auf Tastendruck/-Loslassen und Gedrueckthalten.
 * 
 * @author Michael Andonie
 */
public abstract class BigGame extends Game implements TastenGedruecktReagierbar, TastenLosgelassenReagierbar {
	
	/**
	 * Konstruktor fuer ein <code>BigGame</code>
	 * 
	 * @param x
	 *            Die Breite des Fensters
	 * @param y
	 *            Die Hoehe des Fenstsers
	 * @param titel
	 *            Der Titel des Spielfensters
	 * @param vollbild
	 *            Ob das Fenster ein echtes Vollbild sein soll.<br />
	 *            Manche Computer unterstuetzen kein Vollbild, in diesem Fall wird ein moeglichst grosses Fenster erzeugt.
	 * @param exitOnEsc
	 *            Ist dieser Wert <code>true</code>, so wird das Spiel automatisch beendet, wenn die "Escape"-Taste gedrueckt
	 *            wurde. Dies bietet sich vor allem an, wenn das Spiel ein Vollbild ist oder die Maus aufgrund der Verwendung einer Maus im Spiel nicht auf das "X"-Symbol
	 *            des Fensters geklickt werden kann, wodurch der Benutzer im Spiel "gefangen" waere und <b>dies ist etwas unbeschreiblich aergerliches fuer den Spielbenutzer!!!!!!!!!!!</b>
	 */
	public BigGame(int x, int y, String titel, boolean vollbild, boolean exitOnEsc) {
		super(x, y, titel, vollbild, exitOnEsc);
		super.tastenGedruecktReagierbarAnmelden(this);
		super.tastenLosgelassenReagierbarAnmelden(this);
	}
}

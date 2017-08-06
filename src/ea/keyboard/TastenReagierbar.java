/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
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

package ea.keyboard;

import ea.internal.ano.API;

/**
 * Dieses Interface wird implementiert, um auf die verschiedenen Tastaturbefehle reagieren zu
 * können.
 *
 * @author Michael Andonie
 */
@API
public interface TastenReagierbar {
	/**
	 * Wird bei einem angemeldeten TastenReagierbar aufgerufen, sobald eine Taste, die in der
	 * Kennungstabelle liegt, bei entsprechend aktiviertem Fenster gedrückt wird.<br /> Die Tabelle
	 * liegt der Anleitung der Engine bei.
	 *
	 * @param tastenkuerzel
	 * 		Die Repräsentation der Taste als Zahl. Hierdurch wird ein sehr einfaches Handeln in
	 * 		benötigter Größe über eine <b>switch()-Anweisung</b> möglich.<br /> Welche Zahl welchem
	 * 		Tastenkuerzel entspricht, ist dem <b>Handbuch</b> zu entnehmen. Außerdem gibt es die Klasse
	 * 		{@link Taste}, die für jede Taste eine Konstante enthält.
	 */
	@API
	public abstract void reagieren (int tastenkuerzel);
}

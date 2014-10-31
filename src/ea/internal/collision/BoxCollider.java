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

package ea.internal.collision;

import ea.BoundingRechteck;
import ea.Punkt;
import ea.Vektor;

public class BoxCollider extends Collider {

	/**
	 * Der Vektor, der die Diagonale (von links oben nach rechts unten) beschreibt. Beide
	 * Skalarwerte dürfen nicht negativ sein.
	 */
	Vektor diagonale;

	/**
	 * Default-Konstruktor. Erstellt einen Box-Collider mit Offset (0|0) und Länge = 0, Breite = 0.
	 */
	public BoxCollider () {
		super();
	}

	/**
	 * Erstellt einen Box-Collider mit <i>Offset (0|0)</i>.
	 *
	 * @param diagonale
	 * 		Die Diagonale der Box <i>von der linken, oberen Ecke hin zur rechten, unteren Ecke</i>.
	 */
	public BoxCollider (Vektor diagonale) {
		this(diagonale, Vektor.NULLVEKTOR);
	}

	/**
	 * Vollständiger Konstruktor. Erstellt einen neuen Box-Collider mit allen relevanten
	 * Parametern.
	 *
	 * @param offset
	 * 		Der Offset für diesen Collider.
	 * @param diagonale
	 * 		Die Diagonale der Box <i>von der linken, oberen Ecke hin zur rechten, unteren Ecke</i>.
	 */
	public BoxCollider (Vektor diagonale, Vektor offset) {
		this.offset = offset;
		this.diagonale = diagonale;
	}

	/**
	 * Generiert aus einem <code>BoundingRechteck</code> und einem gegebenen Offset einen
	 * Box-Collider.
	 *
	 * @param offset
	 * 		Der Offset, den dieser Collider haben soll.
	 * @param br
	 * 		Das <code>BoundingRechteck</code>, das Grundlage für die Masse des Box-Collider sein soll.
	 *
	 * @return Der Box-Collider, der diesem <code>BoundingRechteck</code> entspricht:<br> <ul>
	 * <li>Die Position auf der Zeichenebene entspricht ab sofort dem Offset</li> <li>Breite und
	 * Höhe werden übernommen</li> </ul>
	 */
	public static BoxCollider fromBoundingRechteck (Vektor offset, BoundingRechteck br) {
		BoxCollider bc = new BoxCollider();
		bc.offsetSetzen(offset);
		bc.diagonale = new Vektor(br.breite, br.hoehe);

		return bc;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verursachtCollision (Punkt positionThis, Punkt positionOther, Collider collider) {
		if (collider instanceof BoxCollider) {
			return Collider.boxboxCollision(this, (BoxCollider) collider, positionThis, positionOther);
		} else if (collider instanceof SphereCollider) {
			return Collider.sphereboxCollision((SphereCollider) collider, this, positionOther, positionThis);
		} else if (collider instanceof ColliderGroup) {
			return collider.verursachtCollision(positionOther, positionThis, this);
		}
		//Default:
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean istNullCollider () {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collider clone () {
		return new BoxCollider(offset.clone(), diagonale.clone());
	}

	/**
	 * Gibt ein Äquivalent des Box Colliders als <code>BoundingRechteck</code> aus.
	 *
	 * @param start	Die aktuelle linke obere Ecke, an der der Box Collider für das
	 * 				Äquivalent stehen soll.
	 * @return Ein <code>BoundingRechteck</code>, dass die selbe Breite und Höhe wie
	 * 			dieser Box Collider hat, und dessen linke obere Ecke <code>start</code> ist.
	 */
	public BoundingRechteck alsBR (Punkt start) {
		return new BoundingRechteck(start.x + offset.x, start.y + offset.y, diagonale.x, diagonale.y);
	}
}

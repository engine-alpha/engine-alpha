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

import ea.Farbe;
import ea.Knoten;
import ea.Punkt;
import ea.Raum;

/**
 * Dieser Collider kann sich nicht mit irgendetwas schneiden. Es kann keine Collision mit diesem
 * Collider geben.
 *
 * @author andonie
 */
public class NullCollider extends Collider {

	/**
	 * Singeleton-Instanz des Colliders
	 */
	private static NullCollider instance;

	/**
	 * Nur über diese Methode kriegt man Zugriff auf die Singleton-Instanz dieser Klasse.
	 *
	 * @return Die eine Instanz dieser Klasse.
	 */
	public static NullCollider getInstance () {
		return instance == null ? instance = new NullCollider() : instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verursachtCollision (Punkt positionThis, Punkt positionOther, Collider collider) {
		// immer false
		return false;
	}

	/**
	 * {@inheritDoc} Gibt <code>true</code> zur�ck.
	 */
	@Override
	public boolean istNullCollider () {
		return true;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Raum visualize(Punkt p, Farbe color) {
        return new Knoten();
    }

    /**
	 * {@inheritDoc} Nachdem es nur eine unterscheidbare Instanz von <code>NullCollider</code> gibt,
	 * wird hier aus Performance-Gründen eine Referenz auf das Objekt selbst zurückgegeben, um nicht
	 * unnötig Speicher zu allozieren.
	 */
	@Override
	public Collider clone () {
		return this;
	}
}

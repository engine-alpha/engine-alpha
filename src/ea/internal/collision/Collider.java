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

import ea.Dreieck;
import ea.Punkt;
import ea.Vektor;

/**
 * Ein Collider ist die abstrakte Form einer <i>Umgebung in der Zeichenebene</i>. Durch das
 * Vergleichen von verschiedenen Collidern l�sst sich eine effektive <i>Collision Detection</i>
 * erm�glichen, also das Pr�fen auf Kollisionen zweier <code>#ea.Raum</code>-Objekte.
 *
 * @author Andonie
 */
public abstract class Collider
		implements Cloneable {

	/**
	 * Der Offset dieses Colliders. Die Verwendung des Offsets hängt von den implementierenden
	 * Subklassen ab.
	 *
	 * @see #offsetSetzen(Punkt)
	 */
	protected Vektor offset = Vektor.NULLVEKTOR;

	/**
	 * Logische Abfrage für die Kollision zweier Boxen.
	 *
	 * @param b1
	 * 		Box 1
	 * @param b2
	 * 		Box 2
	 * @param p1
	 * 		Position von Box 1 auf der Zeichenebene
	 * @param p2
	 * 		Position von Box 2 auf der Zeichenebene
	 *
	 * @return        <code>true</code>, wenn sich beide Boxen bei aktueller Belegung schneiden, sonst
	 * <code>false</code>.
	 */
	public static boolean boxboxCollision (BoxCollider b1, BoxCollider b2, Punkt p1, Punkt p2) {
		return ((b2.offset.y + p2.y) < (p1.y + b1.offset.y + b1.diagonale.y) && (p2.y + b2.offset.y + b2.diagonale.y) > p1.y + b1.offset.y) &&
				(b2.offset.x + p2.x + b2.diagonale.x) > p1.x + b2.offset.x && b2.offset.x + p2.x < (p1.x + b1.offset.x + b1.diagonale.x);
	}

	/**
	 * Logische Abfrage für die Kollision zweier Kreise.
	 *
	 * @param s1
	 * 		Kreis 1
	 * @param s2
	 * 		Kreis 2
	 * @param p1
	 * 		Position von Kreis 1 auf der Zeichenebene
	 * @param p2
	 * 		Position von Kreis 2 auf der Zeichenebene
	 *
	 * @return        <code>true</code>, wenn sich beide Kreise bei aktueller Belegung schneiden, sonst
	 * <code>false</code>.
	 */
	public static boolean spheresphereCollision (SphereCollider s1, SphereCollider s2, Punkt p1, Punkt p2) {
		float r1 = s1.durchmesser / 2, r2 = s2.durchmesser / 2;
		float dx = (s1.offset.x + p1.x + r1) - (s2.offset.x + p2.x + r2), dy = (s1.offset.y + p1.y + r1) - (s2.offset.y + p2.y + r2);
		float summeRadien = (s1.durchmesser + s2.durchmesser) / 2;

		//System.out.println("Sphere-Sphere Collision: dx=" + dx + "  -  dy=" + dy + "  -  sumRad=" + summeRadien);

		return (dx * dx) + (dy * dy) <= summeRadien * summeRadien;
	}

	/**
	 * Logische Abfrage für die Kollision eines Kreises mit einer Box.
	 *
	 * @param sphere
	 * 		Der Kreis
	 * @param box
	 * 		Die Box
	 * @param ps
	 * 		Position von Sphere
	 * @param pb
	 * 		Position von Box
	 *
	 * @return            <code>true</code>, wenn sich Kreis und Box schneiden, sonst <code>false</code>.
	 */
	public static boolean sphereboxCollision (SphereCollider sphere, BoxCollider box, Punkt ps, Punkt pb) {
		sphere.modelsphere.positionSetzen(ps.verschobenerPunkt(sphere.offset));
		for (Dreieck d : sphere.modelsphere.formen()) {
			Punkt[] punkte = d.punkte();
			for (int i = 0; i < punkte.length; i++) {
				if (punkte[i].realX() >= box.offset.x + pb.x
						&& punkte[i].realY() >= box.offset.y + pb.y
						&& punkte[i].realX() <= (box.offset.x + pb.x + box.diagonale.x)
						&& punkte[i].realY() <= (box.offset.y + pb.y + box.diagonale.y)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Setzt den Offset dieses Colliders neu. <br /><i>Erläuterung</i><br /> Zunächst liegen
	 * Position vom <code>Raum</code>-Objekt und dem Collider direkt aufeinander. Der Offset wird
	 * also relativ zur aktuellen Position des <code>Raum</code>-Objektes in der Zeichenebene
	 * hinzugerechnet, um die endgültige Position des Colliders für die Kollisionsabfragen
	 * festzulegen.
	 *
	 * @param os
	 * 		Der neue Offset für diesen Collider.
	 *
	 * @see #offset()
	 */
	public final void offsetSetzen (Vektor os) {
		this.offset = os;
	}

	/**
	 * Gibt den Offset dieses Vektors relativ zum zugehörigen <code>Raum</code> an.
	 *
	 * @return Der Offset dieses Vektors relativ zum zugehörigen <code>Raum</code> an.
	 */
	public final Vektor offset () {
		return offset;
	}

	/**
	 * Prüft, ob dieser Collider sich mit einem weiteren Collider schneidet.
	 *
	 * @param other
	 * 		Ein zweiter Collider.
	 *
	 * @return    <code>true</code>, falls sich dieser Collider mit dem zweiten Collider schneidet.
	 * Schneiden sich dieser Collider und der zweite Collider nicht, so gibt diese Funktion
	 * <code>false</code> zurück.
	 */
	public abstract boolean verursachtCollision (Punkt positionThis, Punkt positionOther, Collider other);

	/**
	 * Gibt zur�ck, ob dieser Collider ein <code>NullCollider</code> ist, also nur
	 * <code>false</code> zur�ckgeben kann.
	 *
	 * @return    <code>true</code>, falls dieser Collider ein <i>Null-Collider</i> ist, sonst
	 * <code>false</code>.
	 */
	public abstract boolean istNullCollider ();

	/**
	 * Überschriebene Clone-Methode für effizientes, <b>tiefgehendes</b> klonen von Collidern. Das
	 * bedeutet, dass auch tiefer liegende Referenzen <b>frisch geklont</b> werden, bis in die
	 * tiefste Referenzebene.
	 */
	@Override
	public abstract Collider clone ();
}

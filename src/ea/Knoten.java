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

package ea;

import ea.internal.collision.Collider;
import ea.internal.collision.ColliderGroup;
import ea.internal.gra.Listung;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

/**
 * Ein Knoten ist eine Sammlung vielen Raum-Objekten, die hierdurch einheitlich bewegt, und
 * einheitlich behandelt werden koennen.
 *
 * @author Michael Andonie, Niklas Keller <me@kelunik.com>
 */
public class Knoten extends Raum implements Listung {
	/**
	 * Die Liste aller Raum-Objekte, die dieser Knoten fasst.
	 */
	private Vector<Raum> list;

	/**
	 * Konstruktor für Objekte der Klasse Knoten
	 */
	public Knoten () {
		list = new Vector<>();
	}

	/**
	 * Entfernt alle Raum-Objekte von diesem Knoten, die an diesem Knoten gelagert sind.<br /> <br
	 * /> <b>ACHTUNG</b><br /> Sollte <i>Physik</i> benutzt werden:<br /> Diese Methode macht alle
	 * abgemeldeten <code>Raum</code>-Objekt fuer die Physik neutral!!!<br /> Sollte dies NICHT
	 * gewuenscht sein, gibt es hierfuer die Methode <code>leerenOhnePhysikAbmelden()</code>.
	 *
	 * @see #leerenOhnePhysikAbmelden()
	 */
	public void leeren () {
		for (int i = list.size() - 1; i >= 0; i--) {
			list.get(i).neutralMachen();
			list.get(i).loeschen();
		}

		list.clear();
	}

	/**
	 * Loescht alle Raum-Objekte, die an diesem Knoten gelagert sind, ohne sie jedoch von ihrer
	 * Physik her zu beeinflussen.
	 */
	public void leerenOhnePhysikAbmelden () {
		list.clear();
	}

	/**
	 * Entfernt ein Raum-Objekt von diesem Knoten.<br /> War es mehrfach angesteckt, so werden alle
	 * Verbindungen geloescht, war es niemals angemeldet, so passiert <b>gar nichts</b>.<br /> <br
	 * /> <b>Achtung!!</b><br /> Sollte <i>Physik</i> benutzt werden:<br /> Diese Methode macht alle
	 * abgemeldeten <code>Raum</code>-Objekt fuer die Physik neutral!!!<br /> Sollte dies NICHT
	 * gewuenscht sein, gibt es hierfuer die Methode <code>entfernenOhnePhysikAbmelden()</code>.
	 *
	 * @param m
	 * 		Das von diesem Knoten zu entfernende Raum-Objekt
	 *
	 * @see #entfernenOhnePhysikAbmelden(Raum)
	 */
	public void entfernen (Raum m) {
		if (list.contains(m)) {
			m.neutralMachen();
			m.loeschen();
		}

		// noinspection StatementWithEmptyBody
		while (list.remove(m)) ;
	}

	/**
	 * Entfernt ein Raum-Objekt von diesem Knoten, <b>ohne seine Physik zu beeinflussen</b>.<br />
	 * War es mehrfach angesteckt, so werden alle Verbindungen geloescht, war es niemals angemeldet,
	 * so passiert <b>gar nichts</b>.
	 *
	 * @param m
	 * 		Das von diesem Knoten zu entfernende Raum-Objekt
	 */
	public void entfernenOhnePhysikAbmelden (Raum m) {
		list.remove(m);
	}

	/**
	 * Prueft, ob ein bestimmtes Raum-Objekt in diesem Knoten gelagert ist.<br /> <br />
	 * <b>ACHTUNG</b><br /> Diese Methode prueft nicht eventuelle Unterknoten, ob diese vielleiht
	 * das Raum-Objekt beinhalten, sondern nur den eigenen Inhalt!
	 *
	 * @param m
	 * 		Das Raum-Objekt, das auf Vorkommen in diesem Knoten ueberprueft werden soll
	 *
	 * @return <code>true</code>, wenn das Raum-Objekt <b>ein- oder auch mehrmals</b> an diesem
	 * Knoten liegt
	 */
	public boolean besitzt (Raum m) {
		return list.contains(m);
	}

	/**
	 * Kombinationsmethode. Hiermit kann man so viele Raum-Objekte gleichzeitig an den Knoten
	 * anmelden, wie man will.<br /> <b>Beispiel:</b><br /> <br /> <code> //Der Knoten, um alle
	 * Objekte zu sammeln<br /> Knoten knoten = new Knoten();<br /> <br /> //Lauter gebastelte
	 * Raum-Objekte<br /> Raum r1<br /> Raum r2;<br /> Raum r3;<br /> Raum r4;<br /> Raum r5<br />
	 * Raum r6;<br /> Raum r7;<br /> Raum r8;<br /> Raum r9<br /> Raum r10;<br /> Raum r11;<br />
	 * Raum r12;<br /> <br /> //Eine Methode, um alle anzumelden:<br /> knoten.add(r1, r2, r3, r4,
	 * r5, r6, r7, r8, r9, r10, r11, r12);<br /> </code><br /> Das Ergebnis: 11 Zeilen Programmcode
	 * gespart.
	 */
	public void add (Raum... m) {
		for (Raum n : m) {
			add(n);
		}
	}

	/**
	 * Fuegt ein Raum-Objekt diesem Knoten hinzu.<br /> Das zugefuegte Objekt wird ab dann in alle
	 * Methoden des Knotens (<code>verschieben(), dimension()</code> etc.) mit eingebunden.
	 *
	 * @param m
	 * 		Das hinzuzufuegende Raum-Objekt
	 */
	public void add (Raum m) {
		// reverse to keep backwardscompability
		Collections.reverse(list);

		list.add(m);

		Collections.reverse(list);
		Collections.sort(list);
	}

	/**
	 * Gibt alle Elemente des Knotens in Form eines <code>Raum</code>-Objekt-Arays aus.
	 *
	 * @return Alle Elemente als vollstaendig gefuelltes <code>Raum</code>-Objekt-Aray.
	 */
	public Raum[] alleElemente () {
		return list.toArray(new Raum[list.size()]);
	}

	/**
	 * Bewegt jedes angelegte Objekt für sich allein (Physik-Modus)!<br /> Das bedeutet, dass das
	 * Blockiertwerden eines einzelnen <code>Raum</code>-Objektes an diesem Knoten <b>nicht</b>
	 * automatisch alle anderen Objekte blockiert.
	 *
	 * @param v
	 * 		Der die Verschiebung beschreibende Vektor.
	 *
	 * @return Nur dann <code>true</code>, wenn bei allen anderen Objekten die Rueckgabe auch
	 * <code>true</code> ist. Sonst ist die Rueckgabe <code>false</code>.
	 */
	@Override
	public boolean bewegen (Vektor v) {
		boolean ret = true;

		for (int i = list.size() - 1; i >= 0; i--) {
			if (!list.get(i).bewegen(v)) {
				ret = false;
			}
		}

		return ret;
	}

	/**
	 * Verschiebt diesen Knoten.<br /> Das heisst, dass saemtliche anliegenden Raum-Objekte
	 * gleichermassen Verschoben werden.
	 *
	 * @param v
	 * 		Der Vektor, der die Verschiebung angibt.
	 */
	@Override
	public void verschieben (Vektor v) {
		for (int i = list.size() - 1; i >= 0; i--) {
			list.get(i).verschieben(v);
		}
	}

	/**
	 * Zeichnet den Knoten.<br /> Das heisst, der Zeichnen-Befehl wird an die Unterobjekte
	 * weitergetragen.<br /> Diese Methode ist nur intern von Bedeutung
	 *
	 * @param g
	 * 		Das Grafik-Objekt
	 * @param r
	 * 		Das Rechteck, dass die Kameraposition definiert
	 */
	@Override
	public void zeichnen (Graphics2D g, BoundingRechteck r) {
		try {
			for (int i = list.size() - 1; i >= 0; i--) {
				list.get(i).zeichnenBasic(g, r);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// Wahrscheinlich wurde die Liste geleert.
		}
	}

	/**
	 * Die dimension()-Methode.<br /> Gibt ein <code>BoundingRechteck</code> aus, das alle
	 * Komponente dieses Knotens bedeckt.
	 *
	 * @return Das BoundingRechteck, das alle Komponente dieses Knotens bedeckt.<br /> Ist ein
	 * BoundingRechteck mit den Werten (0|0|0|0), wenn dieses Knoten keine Konkreten Raum-Objekte
	 * gesammelt hat.
	 */
	@Override
	public BoundingRechteck dimension () {
		BoundingRechteck ret = null;

		try {
			for (int i = list.size() - 1; i >= 0; i--) {
				if (ret == null) {
					ret = list.get(i).dimension();
				} else {
					ret = ret.summe(list.get(i).dimension());
				}
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// Wahrscheinlich wurde die Liste geleert.
		}

		if (ret == null) {
			return new BoundingRechteck(0, 0, 0, 0);
		} else {
			return ret;
		}
	}

	/**
	 * {@inheritDoc} Collider ist eine Gruppierung der Collider aller <code>Raum</code>-Objekte, die
	 * an diesem Knoten angehängt sind.
	 */
	@Override
	public Collider erzeugeCollider () {
		ColliderGroup group = new ColliderGroup();
		for (Raum r : list) {
			group.addCollider(r.erzeugeCollider());
		}
		return group;
	}

	/**
	 * Berechnet exakter alle Rechteckigen Flaechen, auf denen dieses Objekt liegt.<br /> Diese
	 * Methode wird von komplexeren Gebilden, wie geometrischen oder Listen ueberschrieben.
	 *
	 * @return Alle Rechtecksflaechen, auf denen dieses Objekt liegt. Ist standartisiert ein Array
	 * der Groesse 1 mit der <code>dimension()</code> als Inhalt.
	 */
	@Override
	public BoundingRechteck[] flaechen () {
		ArrayList<BoundingRechteck> data = new ArrayList<BoundingRechteck>();

		for (int i = list.size() - 1; i >= 0; i--) {
			BoundingRechteck[] arr = list.get(i).flaechen();

			for (BoundingRechteck br : arr) {
				data.add(br);
			}
		}

		return data.toArray(new BoundingRechteck[data.size()]);
	}
}

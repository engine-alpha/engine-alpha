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

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

import ea.internal.gra.Listung;
import ea.internal.util.Logger;

/**
 * Ein Knoten ist eine Sammlung vielen Raum-Objekten, die hierdurch einheitlich bewegt, und einheitlich behandelt werden koennen.
 * 
 * @author Michael Andonie
 */
@SuppressWarnings("serial")
public class Knoten extends Raum implements Listung {
	/**
	 * Die Liste aller Raum-Objekte, die dieser Knoten fasst.
	 */
	private ArrayList<Raum> list;
	
	/**
	 * Konstruktor fuer Objekte der Klasse Knoten
	 */
	public Knoten() {
		list = new ArrayList<Raum>();
	}
	
	/**
	 * Entfernt alle Raum-Objekte von diesem Knoten, die an diesem Knoten gelagert sind.<br />
	 * <br />
	 * <b>ACHTUNG</b><br />
	 * Sollte <i>Physik</i> benutzt werden:<br />
	 * Diese Methode macht alle abgemeldeten <code>Raum</code>-Objekt fuer die Physik neutral!!!<br />
	 * Sollte dies NICHT gewuenscht sein, gibt es hierfuer die Methode <code>leerenOhnePhysikAbmelden()</code>.
	 * 
	 * @see leerenOhnePhysikAbmelden()
	 */
	public synchronized void leeren() {
		for (int i = list.size() - 1; i > -1; i--) {
			list.get(i).neutralMachen();
			list.get(i).loeschen();
			list.remove(i);
		}
	}
	
	/**
	 * Loescht alle Raum-Objekte, die an diesem Knoten gelagert sind, ohne sie jedoch von ihrer
	 * Physik her zu beeinflussen.
	 */
	public synchronized void leerenOhnePhysikAbmelden() {
		list.clear();
	}
	
	/**
	 * Entfernt ein Raum-Objekt von diesem Knoten.<br />
	 * War es mehrfach angesteckt, so werden alle Verbindungen geloescht, war es niemals angemeldet, so
	 * passiert <b>gar nichts</b>.<br />
	 * <br />
	 * <b>Achtung!!</b><br />
	 * Sollte <i>Physik</i> benutzt werden:<br />
	 * Diese Methode macht alle abgemeldeten <code>Raum</code>-Objekt fuer die Physik neutral!!!<br />
	 * Sollte dies NICHT gewuenscht sein, gibt es hierfuer die Methode <code>entfernenOhnePhysikAbmelden()</code>.
	 * 
	 * @param m
	 *            Das von diesem Knoten zu entfernende Raum-Objekt
	 * @see entfernenOhnePhysikAbmelden(Raum)
	 */
	public synchronized void entfernen(Raum m) {
		if (list.contains(m)) {
			m.neutralMachen();
			m.loeschen();
		} else {
			Logger.warning("Achtung! Das am Knoten zu entfernende Raum-Objekt war gar nicht am Knoten angemeldet!");
		}
		

		while(list.remove(m));
	}
	
	/**
	 * Entfernt ein Raum-Objekt von diesem Knoten, <b>ohne seine Physik zu beeinflussen</b>.<br />
	 * War es mehrfach angesteckt, so werden alle Verbindungen geloescht, war es niemals angemeldet, so
	 * passiert <b>gar nichts</b>.
	 * 
	 * @param m
	 *            Das von diesem Knoten zu entfernende Raum-Objekt
	 */
	public synchronized void entfernenOhnePhysikAbmelden(Raum m) {
		list.remove(m);
	}
	
	/**
	 * Prueft, ob ein bestimmtes Raum-Objekt in diesem Knoten gelagert ist.<br />
	 * <br />
	 * <b>ACHTUNG</b><br />
	 * Diese Methode prueft nicht eventuelle Unterknoten, ob diese vielleiht das Raum-Objekt beinhalten, sondern nur den eigenen Inhalt!
	 * 
	 * @param m
	 *            Das Raum-Objekt, das auf Vorkommen in diesem Knoten ueberprueft werden soll
	 * @return <code>true</code>, wenn das Raum-Objekt <b>ein- oder auch mehrmals</b> an diesem Knoten liegt
	 */
	public synchronized boolean besitzt(Raum m) {
		return list.contains(m);
	}
	
	/**
	 * Kombinationsmethode. Hiermit kann man so viele Raum-Objekte gleichzeitig an den Knoten anmelden, wie man will.<br />
	 * <b>Beispiel:</b><br />
	 * <br />
	 * <code>
	 * //Der Knoten, um alle Objekte zu sammeln<br />
	 * Knoten knoten = new Knoten();<br />
	 * <br />
	 * //Lauter gebastelte Raum-Objekte<br />
	 * Raum r1<br />
	 * Raum r2;<br />
	 * Raum r3;<br />
	 * Raum r4;<br />
	 * Raum r5<br />
	 * Raum r6;<br />
	 * Raum r7;<br />
	 * Raum r8;<br />
	 * Raum r9<br />
	 * Raum r10;<br />
	 * Raum r11;<br />
	 * Raum r12;<br />
	 * <br />
	 * //Eine Methode, um alle anzumelden:<br />
	 * knoten.add(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12);<br />
	 * </code><br />
	 * Das Ergebnis: 11 Zeilen Programmcode gespart.
	 */
	public synchronized void add(Raum... m) {
		for (Raum n : m)
			list.add(n);
	}
	
	/**
	 * Fuegt ein Raum-Objekt diesem Knoten hinzu.<br />
	 * Das zugefuegte Objekt wird ab dann in alle Methoden des Knotens (<code>verschieben(), dimension()</code> etc.)
	 * mit eingebunden.
	 * 
	 * @param m
	 *            Das hinzuzufuegende Raum-Objekt
	 */
	public synchronized void add(Raum m) { // reverse to keep backwardscompability
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
	public Raum[] alleElemente() {
		return list.toArray(new Raum[0]);
	}
	
	/**
	 * Bewegt jedes angelegte Objekt fuer sich allein (Physik-Modus)!<br />
	 * Das bedeutet, dass das Blockiertwerden eines einzelnen <code>Raum</code>-Objektes an diesem
	 * Knoten <b>nicht</b> automatisch alle anderen Objekte blockiert.
	 * 
	 * @param v
	 *            Der die Verschiebung beschreibende Vektor.
	 * @return Nur dann <code>true</code>, wenn bei allen anderen Objekten die Rueckgabe auch <code>true</code> ist. Sonst ist die Rueckgabe <code>false</code>.
	 */
	@Override
	public boolean bewegen(Vektor v) {
		boolean ret = true;
		for (Raum m : list) {
			if (!m.bewegen(v)) {
				ret = false;
			}
		}
		return ret;
	}
	
	/**
	 * Verschiebt diesen Knoten.<br />
	 * Das heisst, dass saemtliche anliegenden Raum-Objekte gleichermassen Verschoben werden.
	 * 
	 * @param v
	 *            Der Vektor, der die Verschiebung angibt.
	 */
	@Override
	public void verschieben(Vektor v) {
		for (Raum m : list) {
			m.verschieben(v);
		}
	}
	
	/**
	 * Ob dieser Knoten ein beliebiges anderes <code>Raum</code>-Objekt schneidet.
	 * 
	 * @param m
	 *            Das auf Kollision mit diesem Knoten zu testende <code>Raum</code>-Objekt
	 * @return <code>true</code>, wenn mindestens eines der an diesem Knoten anliegenden <code>Raum</code>-Objekte
	 *         das zu testenden Objekt schneidet.
	 */
	@Override
	public boolean schneidet(Raum m) {
		for (Raum r : list) {
			if (r.schneidet(m)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Zeichnet den Knoten.<br />
	 * Das heisst, der Zeichnen-Befehl wird an die Unterobjekte weitergetragen.<br />
	 * Diese Methode ist nur intern von Bedeutung
	 * 
	 * @param g
	 *            Das Grafik-Objekt
	 * @param r
	 *            Das Rechteck, dass die Kameraposition definiert
	 */
	@Override
	public void zeichnen(Graphics g, BoundingRechteck r) {
		try {
			for (int i = list.size() - 1; i > -1; i--)
				list.get(i).zeichnenBasic(g, r);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}
	
	/**
	 * Die dimension()-Methode.<br />
	 * Gibt ein <code>BoundingRechteck</code> aus, das alle Komponente dieses Knotens bedeckt.
	 * 
	 * @return Das BoundingRechteck, das alle Komponente dieses Knotens bedeckt.<br />
	 *         Ist ein BoundingRechteck mit den Werten (0|0|0|0), wenn dieses Knoten keine Konkreten Raum-Objekte gesammelt hat.
	 */
	@Override
	public BoundingRechteck dimension() {
		BoundingRechteck ret = null;
		for (Raum raum : list) {
			if (ret == null) {
				ret = raum.dimension();
			} else {
				ret = ret.summe(raum.dimension());
			}
		}
		if (ret == null) {
			return new BoundingRechteck(0, 0, 0, 0);
		} else {
			return ret;
		}
	}
	
	/**
	 * Berechnet exakter alle Rechteckigen Flaechen, auf denen dieses Objekt liegt.<br />
	 * Diese Methode wird von komplexeren Gebilden, wie geometrischen oder Listen ueberschrieben.
	 * 
	 * @return Alle Rechtecksflaechen, auf denen dieses Objekt liegt.
	 *         Ist standartisiert ein Array der Groesse 1 mit der <code>dimension()</code> als Inhalt.
	 */
	@Override
	public BoundingRechteck[] flaechen() {
		ArrayList<BoundingRechteck> data = new ArrayList<BoundingRechteck>();
		
		for (Raum r : list) {
			BoundingRechteck[] arr = r.flaechen();
			
			for (BoundingRechteck br : arr)
				data.add(br);
		}
		
		return data.toArray(new BoundingRechteck[0]);
	}
}

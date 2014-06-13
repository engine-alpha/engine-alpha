/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
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

import java.io.Serializable;

/**
 * Ein nicht grafisches Rechteck auf der Zeichenebene, das eine allgemeine Fläche beschreibt.
 * 
 * @author Michael Andonie
 */
public final class BoundingRechteck implements Serializable {
	private static final long serialVersionUID = 99L;
	
	/**
	 * <b>Reelle</b> <code>x</code>-Position des Rechtecks
	 */
	public final float x;
	
	/**
	 * <b>Reelle</b> <code>y</code>-Position des Rechtecks
	 */
	public final float y;
	
	/**
	 * <b>Reelle</b> Breite des Rechtecks
	 */
	public final float breite;
	
	/**
	 * <b>Reelle</b> Höhe des Rechtecks
	 */
	public final float hoehe;
	
	/**
	 * Konstruktor für Objekte der Klasse <code>BoundingRechteck</code> mit <b>reellen</b> Werten.
	 * 
	 * @param x
	 *            Die <code>x</code>-Koordinate der <i>oberen linken Ecke</i> des Rechtecks
	 * @param y
	 *            Die <code>y</code>-Koordinate der <i>oberen linken Ecke</i> des Rechtecks
	 * @param dX
	 *            Die Breite des Bounding-Rechtecks
	 * @param dY
	 *            Die Höhe des Bounding-Rechtecks
	 */
	public BoundingRechteck(float x, float y, float dX, float dY) {
		this.x = x;
		this.y = y;
		this.breite = dX;
		this.hoehe = dY;
	}
	
	/**
	 * Berechnet ein neues BoundingRechteck mit denselben Maßen wie dieses, jedoch um einen bestimmten Vektor verschoben.
	 * 
	 * @param v
	 *            Der Vektor, der die Verschiebung des neuen Objektes von diesem beschreibt.
	 * @return Ein neues <code>BoundingRechteck</code>-Objekt, das die selbe Maße wie dieses hat, jedoch um die entsprechende Verschiebung verschoben ist.
	 */
	public BoundingRechteck verschobeneInstanz(Vektor v) {
		return new BoundingRechteck(x + v.x, y + v.y, breite, hoehe);
	}
	
	/**
	 * Berechnet aus diesem rein aus Zahlen bestehenden Rahmen ein Rechteck, das in der Zeichenebene darstellbar ist.
	 * 
	 * @return Ein neues Rechteck-Objekt, das genau dieses BoundingRechteck abdeckt
	 */
	public Rechteck ausDiesem() {
		return new Rechteck(x, y, breite, hoehe);
	}
	
	/**
	 * Gibt ein neues BoundingRechteck zurück, das seinen Punkt genau im angegebenen Zentrum hat.
	 * 
	 * @param p
	 *            Das Zentrum des zurückzugebenden BoundingRechtecks.
	 * @return Ein BoundingRechteck mit der gleichen Höhe und Breite wie dieses, jedoch so verschoben,
	 *         dass es mit seiner Mitte im angegebenen Zentrum liegt.
	 */
	public BoundingRechteck mittenAngleichInstanz(Punkt p) {
		Punkt z = this.zentrum();
		return this.verschobeneInstanz(new Vektor(p.realX() - z.realX(), p.realY() - z.realY()));
	}
	
	/**
	 * Ein Mittenangleich mit einem anderen BoundingRechteck
	 * 
	 * @param r
	 *            Das BoundingRechteck, an dessen Mitte auch die dieses Rechtecks sein soll.
	 */
	public BoundingRechteck mittenAngleichInstanz(BoundingRechteck r) {
		return this.mittenAngleichInstanz(r.zentrum());
	}
	
	/**
	 * Berechnet aus diesem und einem weiteren BoundingRechteck ein neues, dass die beiden genau fasst.
	 * 
	 * @param r
	 *            Das zweite Rechteck fuer die Berechnung
	 * @return Ein neues BoundingRechteck, dass die beiden Rechtecke genau umfasst.
	 */
	public BoundingRechteck summe(BoundingRechteck r) {
		float x, y, dX, dY;

		if (r.x < this.x) {
			x = r.x;
		} else {
			x = this.x;
		}

		if (r.y < this.y) {
			y = r.y;
		} else {
			y = this.y;
		}

		if (r.x + r.breite > this.x + this.breite) {
			dX = (r.x + r.breite) - x;
		} else {
			dX = (this.x + this.breite) - x;
		}

		if (r.y + r.hoehe > this.y + this.hoehe) {
			dY = (r.y + r.hoehe) - y;
		} else {
			dY = (this.y + this.hoehe) - y;
		}

		return new BoundingRechteck(x, y, dX, dY);
	}
	
	/**
	 * Berechnet, ob dieses Rechteck über einer Grenze liegt und wenn <b>nicht</b>, dann berechnet es eines,
	 * das gerade so an der Untergrenze liegt.
	 * 
	 * @param untergrenze
	 *            Die Grenze, auf der das Ergebnis maximal liegen darf.
	 * @return Ein BoundingRechteck derselben Höhe und Breite wie dieses, das in jedem Fall über, oder auf der Grenze liegt, wenn es passt, ist es <code>this</code>.
	 */
	public BoundingRechteck ueber(int untergrenze) {
		if (y + hoehe < untergrenze) {
			return this;
		} else {
			return new BoundingRechteck(x, untergrenze - hoehe, breite, hoehe);
		}
	}
	
	/**
	 * Berechnet, ob dieses Rechteck unter einer Grenze liegt, und wenn <b>nicht</b>, dann berechnet es eines,
	 * das gerade so an der Obergrenze liegt.
	 * 
	 * @param obergrenze
	 *            Die Grenze, auf der das Ergebnis maximal liegen darf.
	 * @return Ein BoundingRechteck derselben Hoehe und Breite wie dieses, das in jedem Fall unter, oder auf der Grenze liegt, wenn es passt, ist es <code>this</code>.
	 */
	public BoundingRechteck unter(int obergrenze) {
		if (y > obergrenze) {
			return this;
		} else {
			return new BoundingRechteck(x, obergrenze, breite, hoehe);
		}
	}
	
	/**
	 * Berechnet, ob dieses Rechteck rechts von einer bestimmten Grenze liegt, und wenn <b>nicht</b>, dann berechnet es eines,
	 * das gerade so an der linken Extremgrenze liegt.
	 * 
	 * @param grenzeLinks
	 *            Der Wert, den das Ergebnisrechteck maximal links sein darf
	 * @return Ein BoundingRechteck derselben Höhe und Breite, das in jedem rechts jenseits oder auf der Grenze liegt.<br />
	 *         Wenn diese Eigenschaften bereits von diesem Objekt erfüllt werden, so wird <code>this</code> zurückgegeben.
	 */
	public BoundingRechteck rechtsVon(int grenzeLinks) {
		if (x > grenzeLinks) {
			return this;
		} else {
			return new BoundingRechteck(grenzeLinks, y, breite, hoehe);
		}
	}
	
	/**
	 * Berechnet, ob dieses Rechteck links von einer bestimmten Grenze liegt, und wenn <b>nicht</b>, dann berechnet es eines,
	 * das gerade so an der rechten Extremgrenze liegt.
	 * 
	 * @param grenzeRechts
	 *            Der Wert, den das Ergebnisrechteck maximal rechts sein darf
	 * @return Ein BoundingRechteck derselben Höhe und Breite, das in jedem Fall links jenseits oder auf der Grenze liegt.<br />
	 *         Wenn diese Eigenschaften bereits von diesem Objekt erfüllt werden, so wird <code>this</code> zurückgegeben.
	 */
	public BoundingRechteck linksVon(int grenzeRechts) {
		if (x + breite < grenzeRechts) {
			return this;
		} else {
			return new BoundingRechteck(grenzeRechts - breite, y, breite, hoehe);
		}
	}
	
	/**
	 * Gibt ein neues BoundingRechteck mit selber Höhe und Breite, jedoch einer bestimmten, zu definierenden Position.<br />
	 * Diese Position ist die der <i>linken oberen Ecke</i> des BoundingRechtecks.
	 * 
	 * @param realX
	 *            Die <i>X-Koordinate der linken oberen Ecke</i> des BoundingRechtecks
	 * @param realY
	 *            Die <i>Y-Koordinate der linken oberen Ecke</i> des BoundingRechtecks
	 * @return Ein neues BoundingRechteck mit der eingegebenen Position und derselben Breite und Höhe.
	 */
	public BoundingRechteck anPosition(float realX, float realY) {
		return new BoundingRechteck(realX, realY, breite, hoehe);
	}
	
	/**
	 * Gibt einen <code>BoundingKreis</code> aus, der das Rechteck minimal, aber voll umschließt.
	 * @return	der <code>BoundingKreis</code> aus, der das Rechteck minimal, aber voll umschließt.
	 * 			Die Ecken des Rechtecks liegen alle auf dem Kreis.
	 */
	public KreisCollider umschliessenderKreis() {
		Punkt z = this.zentrum();
		return new KreisCollider(z, z.abstand(new Punkt(x,y)));
	}
	
	/**
	 * Testet, ob sich ein Dreieck in dem BoundingRechteck befindet.<br />
	 * Hierbei wird zuerst getestet, ob ein Punkt des Dreiecks im Rechteck ist, dann,
	 * falls nötig ob ein Punkt des Rechtecks im Dreieck ist.
	 */
	public boolean schneidet(Dreieck d) {
		if(d == null) {
			return false;
		}

		Punkt[] punkte = d.punkte();

		for (int i = 0; i < punkte.length; i++) {
			if (istIn(punkte[i])) {
				return true;
			}
		}

		punkte = this.punkte();

		for (int i = 0; i < punkte.length; i++) {
			if (d.beinhaltet(punkte[i])) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Testet, ob ein Punkt sich in dem BoundingRechteck befindet.
	 * 
	 * @param p
	 *            Der Punkt, der getestet werden soll
	 * @return true, wenn der Punkt in dem BoundingRechteck ist
	 */
	public boolean istIn(Punkt p) {
		return (p.realX() >= this.x && p.realY() >= this.y && p.realX() <= (x + breite) && p.realY() <= (y + hoehe));
	}
	
	/**
	 * Berechnet den Mittelpunkt dieses BoundingRechtecks in der Zeichenebene.
	 * 
	 * @return Der Punkt mit den Koordinaten, der im Zentrum des Rechtecks liegt (bei ungeraden Koordinaten mit Abrundung)
	 */
	public Punkt zentrum() {
		return new Punkt(x + ((breite) / 2), y + ((hoehe) / 2));
	}
	
	/**
	 * // TODO Dokumentation
	 *
	 * @return Ein Punkt-ArrarealY der Laenge 4, dessen Inhalt die 4 beschreibenden Punkte des BoundingRechteck 's darstellt.
	 */
	public Punkt[] punkte() {
		Punkt[] p = {
			new Punkt(x, y),
			new Punkt(x + breite, y),
			new Punkt(x, y + hoehe),
			new Punkt(x + breite, y + hoehe)
		};
		return p;
	}
	
	/**
	 * Berechnet, ob dieses BoundingRechteck links von einem zweiten ist
	 * 
	 * @param r Das Rechteck, bei dem dies getestet werden soll
	 * @return <code>true</code>, wenn dieses Rechteck rechts von dem anderen ist, sonst <code>false</code>.
	 */
	public boolean linksVon(BoundingRechteck r) {
		return ((this.x) < (r.x));
	}
	
	/**
	 * Berechnet, ob dieses BoundingRechteck ueber einem zweiten ist
	 * 
	 * @param r Das Rechteck, bei dem dies getestet werden soll
	 * @return <code>true</code>, wenn dieses Rechteck rechts von dem anderen ist, sonst <code>false</code>.
	 */
	public boolean ueber(BoundingRechteck r) {
		return ((this.y) < (r.y));
	}
	
	/**
	 * Testet, ob ein anderes BoundingRechteck dieses schneidet.<br />
	 * Schneiden bedeutet folgendes im Sinne der Engine Alpha:<br />
	 * <i>Beide Rechtecke teilen sich mindestens einen (aber meistens mehrere) Punkte auf der Zeichenebene</i>.
	 * 
	 * @param fig
	 *            Das zweite zu testende BoundingRechteck
	 * @return <code>true</code>, wenn sich die beiden schneiden, sonst <code>false</code>.
	 */
	public boolean schneidetBasic(BoundingRechteck fig) {
		if (fig.y < (this.y + this.hoehe) && (fig.y + fig.hoehe) > this.y) {
			if ((fig.x + fig.breite) > this.x && fig.x < (this.x + this.breite)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Diese Methoden prüft, ob dieses Bounding-Rechteck ein zweites vollkommen umschliesst.<br />
	 * <i>Gemeinsame Ränder zählen <b>AUCH</b> als umschliessen!</i>
	 * 
	 * @param innen
	 *            Das Innere Bounding-Rechteck. Es soll geprüft werden, ob dieses Vollkommen von dem die Methode
	 *            ausführenden Rechteck umschlossen wird.
	 * @return <code>true</code>, wenn das <b>ausfuehrende Bounding-Rechteck das als Argument übergebene BoundingRechteck voll
	 *         umschliesst</b>, sonst <code>false</code>.
	 */
	public boolean umschliesst(BoundingRechteck innen) {
		return (this.x <= innen.x && this.y <= innen.y && (this.x + this.breite) >= (innen.x + innen.breite) && (this.y + this.hoehe) >= (innen.y + innen.hoehe));
	}
	
	/**
	 * Berechnet, ob dieses BoundingRechteck auf einem zweiten "steht".
	 * 
	 * @param r
	 *            Das BoundingRechteck, auf dem dieses stehen koennte
	 * @return <code>true</code>, wenn dies so ist, sonst <code>false</code>.
	 */
	public boolean stehtAuf(BoundingRechteck r) {
		if ((r.x + r.breite) > this.x && r.x < (this.x + this.breite)) {
			return (r.y == this.y + this.hoehe);
		}
		return false;
	}
	
	/**
	 * Berechnet, wie weit man waagrecht ein BoundingRechteck verschieben müsste, damit es dieses nicht mehr berührt.
	 * 
	 * @param r
	 *            Das BoundingRechteck, das eventuell verschoben werden müsste.
	 * @return Die Zahl, die angibt, wie weit man es verschieben muesste, oder 0 wenn sich die beiden nicht berühren.
	 */
	public float verschiebenX(BoundingRechteck r) {
		if (!this.schneidetBasic(r)) {
			return 0;
		}
		if (r.linksVon(this)) {
			return this.x - (r.x + r.breite);
		} else {
			return (this.x + this.breite) - r.x;
		}
	}
	
	/**
	 * Berechnet, wie weit man senkrecht ein BoundingRechteck verschieben müsste, damit es dieses nicht mehr berührt.
	 * 
	 * @param r
	 *            Das BoundingRechteck, das eventuell verschoben werden müsste.
	 * @return Die Zahl, die angibt, wie weit man es verschieben müsste, oder 0 wenn sich die beiden nicht berühren.
	 */
	public float verschiebenY(BoundingRechteck r) {
		if (!this.schneidetBasic(r)) {
			return 0;
		}
		if (r.ueber(this)) {
			return this.y - (r.y + r.hoehe);
		} else {
			return (this.y + this.hoehe) - r.y;
		}
	}
	
	/**
	 * Berechnet den Höhenunterschied zwischen dem Fuß des höheren und dem Kopf des tieferen BoundingRechtecks.
	 * 
	 * @param r
	 *            Das BoundingRechteck, dessen Höhenunterschied zu diesem gefunden werden soll
	 * @return Der <b>absolute (also niemals negative)</b> Unterschied in der Höhe zwischen den beiden Objekten.
	 * <b>Überlagern sie sich, so ist der Rückgabewert 0</b>!
	 */
	public float hoehenUnterschied(BoundingRechteck r) {
		if (this.schneidetBasic(r)) {
			return 0;
		}
		if (this.y < r.y) { // Dieses Rechteck ist das Hoehere!!
			return r.y - (this.y + this.hoehe);
		} else { // Das andere Rechteck ist realHoeher!!
			return this.y - (r.y + r.hoehe);
		}
	}
	
	/**
	 * Transformiert dieses Boudning-Rechteck auf 2 Weisen: Einmal in der Postion und zusätzlich in
	 * seiner Höhe.
	 * 
	 * @param v
	 *            Der Vektor, der die Verschiebung beschreibt.
	 * @param dHoehe
	 *            Die Höhen<b>änderung</b>.
	 * @return Ein neues BoundingRechteck, das verschoben und in seiner Höhe geändert ist.
	 */
	public BoundingRechteck verschErhoeht(Vektor v, int dHoehe) {
		return new BoundingRechteck(x + v.x, y + v.y, breite, hoehe + dHoehe);
	}
	
	/**
	 * Sollte dieses Bounding-Rechteck nicht voll innerhalb eines bestimmten anderen,
	 * äußeren Rechtecks liegen, so wird versucht, dieses Bounding-Rechteck <i>in das
	 * andere mit möglichst wenig Verschiebung</i> zu bringen. Diese Methode wird intern
	 * für die Beschränkung des Kamera-Bereiches genutzt.
	 *
	 * <div class='hinweisProbleme'><b>Achtung</b>: Voraussetzung dafuer, dass dieser Algorithmus
	 * Sinn macht ist, dass das äußere Rechteck ausreichend größer als dieses ist!</div>
	 * 
	 * @param aussen
	 *            Das äußere Rechteck, innerhalb dessen sich das Ergebnis-Rechteck
	 *            befinden wird (sollte das äußere ausreichend groß sein).
	 * @return Das Ergebnis-Rechteck, das sich im äußeren Rechteck befinden wird.
	 */
	public BoundingRechteck in(BoundingRechteck aussen) {
		float realX = this.x, realY = this.y;

		if (this.x < aussen.x) {
			realX = aussen.x;
		}

		if (this.x + this.breite > aussen.x + aussen.breite) {
			realX = aussen.x + aussen.breite - this.breite;
		}

		if (this.y < aussen.y) {
			realY = aussen.y;
		}

		if (this.y + this.hoehe > aussen.y + aussen.hoehe) {
			realY = aussen.y + aussen.hoehe - this.hoehe;
		}

		return new BoundingRechteck(realX, realY, this.breite, this.hoehe);
	}
	
	/**
	 * Erstellt einen Klon von diesem BoundingRechteck.
	 * 
	 * @return Ein neues BoundingRechteck mit genau demselben Zustand wie dieses.
	 */
	public BoundingRechteck klon() {
		return new BoundingRechteck(x, y, breite, hoehe);
	}
	
	/**
	 * Gibt eine String-Repräsentation dieses Objektes aus.
	 * 
	 * @return Die String-Repräsentation dieses Objektes. Hierin wird Auskunft über alle 4
	 *         ausschlaggebenden Zahlen (<code>x</code>, <code>y</code>, <code>dX</code> und <code>dY</code> gemacht)
	 */
	@Override
	public String toString() {
		return "Bounding-Rechteck: x:" + x + " y: " + y + " dX: " + breite + " dY: " + hoehe;
	}

	/**
	 * Gibt die <b>reelle</b> X-Koordinate der oberen linken Ecke aus.
	 * 
	 * @return Die <b>reelle</b> X-Koordinate der oberen linken Ecke dieses BoundingRechtecks.
	 * @see #getRealY()
	 * @see #getRealBreite()
	 * @see #getRealHoehe()
	 */
	public float getRealX() {
		return x;
	}

	/**
	 * Gibt die <b>reelle</b> Y-Koordinate der oberen linken Ecke aus.
	 * 
	 * @return Die <b>reelle</b> Y-Koordinate der oberen linken Ecke dieses BoundingRechtecks.
	 * @see #getRealX()
	 * @see #getRealBreite()
	 * @see #getRealHoehe()
	 */
	public float getRealY() {
		return y;
	}

	/**
	 * Gibt die <b>reelle</b> Breite aus.
	 * 
	 * @return Die <b>reelle</b> Breite dieses BoundingRechtecks.
	 * @see #getRealX()
	 * @see #getRealY()
	 * @see #getRealHoehe()
	 */
	public float getRealBreite() {
		return breite;
	}

	/**
	 * Gibt die <b>reelle</b> Hoehe aus.
	 * 
	 * @return Die <b>reelle</b> Hoehe dieses BoundingRechtecks.
	 * @see #getRealX()
	 * @see #getRealY()
	 * @see #getRealBreite()
	 */
	public float getRealHoehe() {
		return hoehe;
	}
}
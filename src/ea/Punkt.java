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
 * Ein Punkt beschreibt einen exakt bestimmten eindimensionalen Punkt auf der Zeichenebene.<br />
 * Er ist durch 2 Koordinaten exakt bestimmt.
 * 
 * @author Michael Andonie
 */
public final class Punkt
{
	/**
	 * Konstante für den Punkt mit den reellen Koordinaten (0,0)
	 */
	public static final Punkt ZENTRUM = new Punkt(0f,0f);
	
	/**
	 * Die X-Koordinate
	 */
	public final int x;
	
	/**
	 * Die Y-Koordinate
	 */
	public final int y;
	
	/**
	 * Der kontinuierliche(re) X-Wert des Punktes. Die anderen Koordinaten sind ggf.
	 * nur gerundet.
	 */
	public final float realX;
	
	/**
	 * Der kontinuierliche(re) Y-Wert des Punktes. Die anderen Koordinaten sind ggf.
	 * nur gerundet.
	 */
	public final float realY;
	
	/**
	 * Standard-Konstruktor fuer Objekte der Klasse Punkt.
	 */
	public Punkt(int x, int y)
	{
		this.x = x;
		this.y = y;
		this.realX = (float) x;
		this.realY = (float) y;
	}
	
	/**
	 * Fortgeschrittener-Konstruktor fuer Objekte der Klasse Punkt.
	 * Hier können Koordinaten <b>wesentlich genauer</b> eingegeben werden.
	 */
	public Punkt(float x, float y)
	{
		this.x = (int) x;
		this.y = (int) y;
		this.realX = x;
		this.realY = y;
	}
	
	/**
	 * Beschreibt den Abstand zwischen diesem und einem anderen Punkt in der Luftlinie.<br />
	 * Hierbei wird lediglich der Satz des Pythagoras angewendet (a^2 + b^2 = c^2).
	 * 
	 * @param p
	 *            Der Punkt, zu dem die direkte Laenge hin berechnet werden soll.
	 * @return Die Laenge der Luftlinie zwischen diesem und dem anderen Punkt.<br />
	 *         Dieser Wert ist nie negativ.
	 */
	public double abstand(Punkt p) {
		double x, y;
		x = Math.abs(this.realX - p.realX);
		y = Math.abs(this.realY - p.realY);
		return Math.sqrt((x * x) + (y * y));
	}
	
	/**
	 * Gibt einen Punkt aus, der um eine bestimmte Verschiebung verschobenen Koordinaten dieses
	 * Punktes hat.
	 * 
	 * @param v
	 *            Die Verschiebung, die dieser Punkt erhalten wuerde, um mit der Ausgabe uebereinzustimmen.
	 * @return Ein Punkt, mit der X-Koordinate <code>p.x + v.x</code> und der Y-Koordinate <code>p.y + v.y</code>. (tatsächlich
	 *         werden die <b>reellen</b> Werte addiert.
	 * @see verschobeneInstanz(Vektor)
	 */
	public Punkt verschobenerPunkt(Vektor v) {
		return new Punkt(this.realX + v.realX, this.realY + v.realY);
	}
	
	/**
	 * Gibt einen Punkt aus, der die um eine Verschiebung veraenderten Koordinaten dieses Punktes hat.<br />
	 * Also quasi diesen Punkt, waere er um eine Verschiebeung veraendert.<br />
	 * <br />
	 * Diese Methode ist identisch mit <code>verschobenerPunkt(Vektor)</code>. Sie existiert der einheitlichen Methodennomenklatur
	 * der Zeichenebenen-Klassen halber.
	 * 
	 * @param v
	 *            Der Vektor, der diese Verschiebung beschreibt.
	 * @return Der Punkt, der die Koordinaten dieses Punktes - verschoben um den Vektor - hat.
	 * @see verschobenerPunkt(Vektor)
	 */
	public Punkt verschobeneInstanz(Vektor v) {
		return verschobenerPunkt(v);
	}
	
	/**
	 * {@inheritDoc} Überschriebene Equals-Methode. Zwei Punkte sind gleich, wenn sie <b>Exakt</b> aufeinanderliegen.
	 * Daher müssen die <b>reellen</b> Koordinaten übereinstimmen.
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Punkt) {
			Punkt p = (Punkt) o;
			return this.realX == p.realX && this.realY == p.realY;
		}
		return false;
	}
	
	/**
	 * Gibt zurück, ob dieser Punkt <i>echt ganzzahlig</i> ist, also ob seine <b>tatsächlichen Koordinaten</b>
	 * beide Ganzzahlen sind.
	 * 
	 * @return <code>true</code>, wenn <b>beide</b> Koordinaten dieses Punktes ganzzahlig sind, sonst <code>false</code>.
	 */
	public boolean istEchtGanzzahlig() {
		return this.realX == (float) x && this.realY == (float) y;
	}
	
	/**
	 * Gibt die X-Koordinate dieses Punktes zurueck.
	 * 
	 * @return Die X-Koordinate dieses Punktes.
	 * @see #y()
	 */
	public int x() {
		return x;
	}
	
	/**
	 * Gibt die Y-Koordinate dieses Punktes zurueck.
	 * 
	 * @return Die Y-Koordinate dieses Punktes.
	 * @see #x()
	 */
	public int y() {
		return y;
	}
	
	/**
	 * Gibt die (annähernd) reelle X-Koordinate dieses Punktes zurueck.
	 * 
	 * @return Die X-Koordinate dieses Punktes.
	 * @see #getRealY()
	 */
	public float getRealX() {
		return realX;
	}
	
	/**
	 * Gibt die (annähernd) reelle Y-Koordinate dieses Punktes zurueck.
	 * 
	 * @return Die Y-Koordinate dieses Punktes.
	 * @see #getRealX()
	 */
	public float getRealY() {
		return realY;
	}
	
}

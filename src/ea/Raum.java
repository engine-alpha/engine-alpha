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
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;

import java.awt.*;

/**
 * Raum bezeichnet alles, was sich auf der Zeichenebene befindet.<br /> Dies ist die absolute
 * Superklasse aller grafischen Objekte. Umgekehrt kann somit jedes grafische Objekt die folgenden
 * Methoden nutzen.
 *
 * @author Michael Andonie, Niklas Keller
 */
public abstract class Raum implements Comparable<Raum> {

	/**
	 * Gibt an, ob das Objekt zur Zeit ueberhaupt sichtbar sein soll.<br /> Ist dies nicht der Fall,
	 * so wird die Zeichenroutine direkt uebergangen.
	 */
	private boolean sichtbar = true;

	/**
	 * Z-Index des Raumes, je höher, desto weiter oben wird der Raum gezeichnet
	 */
	private int zIndex = 1;

	/**
	 * Opacity = Durchsichtigkeit des Raumes
	 * <p/>
	 * <ul><li><code>0.0f</code> entspricht einem komplett durchsichtigen Bild.</li>
	 * <li><code>1.0f</code> entspricht einem undurchsichtigem Bild.</li></ul>
	 */
	private float opacity = 1;

	/**
	 * Composite des Grafik-Objekts. Zwischenspeicherung des letzten Zustands
	 */
	private Composite composite;

	/**
	 * Setzt den Z-Index dieses Raumes. Je größer, desto weiter vorne wird ein Raum gezeichnet.
	 * <b>Diese Methode muss ausgeführt werden, bevor der Raum zu einem Knoten hinzugefügt
	 * wird.</b>
	 *
	 * @param z
	 * 		zu setzender Index
	 */
    @API
	public void zIndex (int z) {
		zIndex = z;
	}

	/**
	 * Setzt die Sichtbarkeit des Objektes.
	 *
	 * @param sichtbar
	 * 		Ob das Objekt sichtbar sein soll oder nicht.<br /> Ist dieser Wert <code>false</code>, so
	 * 		wird es nicht im Fenster gezeichnet.<br />
	 *
	 * @see #sichtbar()
	 */
    @API
	public final void sichtbarSetzen (boolean sichtbar) {
		this.sichtbar = sichtbar;
	}

	/**
	 * Gibt an, ob das Raum-Objekt sichtbar ist.
	 *
	 * @return Ist <code>true</code>, wenn das Raum-Objekt zur Zeit sichtbar ist.
	 *
	 * @see #sichtbarSetzen(boolean)
	 */
    @API
	public final boolean sichtbar () {
		return this.sichtbar;
	}

	/**
	 * Die Basiszeichenmethode.<br /> Sie schließt eine Fallabfrage zur Sichtbarkeit ein. Diese
	 * Methode wird bei den einzelnen Gliedern eines Knotens aufgerufen.
	 *
	 * @param g
	 * 		Das zeichnende Graphics-Objekt
	 * @param r
	 * 		Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br /> Hierbei soll
	 * 		zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann
	 * 		gezeichnet werden.
	 *
	 * @see #zeichnen(Graphics2D, BoundingRechteck)
	 */
    @NoExternalUse
	public final void zeichnenBasic (Graphics2D g, BoundingRechteck r) {
		if (sichtbar && this.camcheck(r)) {
            beforeRender(g, r);
            zeichnen(g, r);
            afterRender(g, r);
		}
	}

    /**
     * Interne Methode. Prüft, ob das anliegende Objekt (teilweise) innerhalb des sichtbaren Bereichs liegt.
     * @param r Die Bounds der Kamera.
     * @return  <code>true</code>, wenn das Objekt (teilweise) innerhalb des derzeit sichtbaren Breichs liegt, sonst
     *          <code>false</code>.
     */
    @NoExternalUse
    private boolean camcheck(BoundingRechteck r) {
        //FIXME : Parameter ändern (?) - Funktionalität implementieren.
        //throw new UnsupportedOperationException("4.0 Implementierung steht aus.");
        return true;
    }

    /**
	 * Zeichnet das Objekt.
	 *
	 * @param g
	 * 		Das zeichnende Graphics-Objekt
	 * @param r
	 * 		Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br /> Hierbei soll
	 * 		zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann
	 * 		gezeichnet werden.
	 */
    @NoExternalUse
	public abstract void zeichnen (Graphics2D g, BoundingRechteck r);



	/**
	 * Setzt die Position des Objektes gänzlich neu auf der Zeichenebene. Das Setzen ist technisch
	 * gesehen eine Verschiebung von der aktuellen Position an die neue. <br /><br />
	 * <b>Achtung!</b><br /> Bei <b>allen</b> Objekten ist die eingegebene Position die linke, obere
	 * Ecke des Rechtecks, das die Figur optimal umfasst. Das heißt, dass dies bei Kreisen z.B.
	 * <b>nicht</b> der Mittelpunkt ist! Hierfür gibt es die Sondermethode
	 * <code>mittelpunktSetzen(int x, int y)</code>.
	 *
	 * @param x
	 * 		neue <code>x</code>-Koordinate
	 * @param y
	 * 		neue <code>y</code>-Koordinate
	 *
	 * @see #positionSetzen(Punkt)
	 * @see #mittelpunktSetzen(int, int)
	 * @see #setX(float)
	 * @see #setY(float)
	 */
    @API
	public void positionSetzen (float x, float y) {
		this.positionSetzen(new Punkt(x, y));
	}

	/**
	 * Setzt die Position des Objektes gänzlich neu auf der Zeichenebene. Das Setzen ist technisch
	 * gesehen eine Verschiebung von der aktuellen Position an die neue. <br /><br />
	 * <b>Achtung!</b><br /> Bei <b>allen</b> Objekten ist die eingegebene Position die linke, obere
	 * Ecke des Rechtecks, das die Figur optimal umfasst. Das heißt, dass dies bei Kreisen z.B.
	 * <b>nicht</b> der Mittelpunkt ist! Hierfür gibt es die Sondermethode
	 * <code>mittelpunktSetzen(int x, int y)</code>.
	 *
	 * @param p
	 * 		Der neue Zielpunkt
	 *
	 * @see #positionSetzen(float, float)
	 * @see #mittelpunktSetzen(int, int)
	 * @see #setX(float)
	 * @see #setY(float)
	 */
    @API
	public void positionSetzen (Punkt p) {
		//FIXME Implementation
	}

	/**
	 * Verschiebt das Objekt ohne Bedingungen auf der Zeichenebene. Dies ist die <b>zentrale</b>
	 * Methode zum
	 *
	 * @param v
	 * 		Der Vektor, der die Verschiebung des Objekts angibt.
	 *
	 * @see Vektor
	 * @see #verschieben(float, float)
	 * @see #bewegen(Vektor)
	 * @see #bewegen(float, float)
	 */
    @API
	public void verschieben (Vektor v) {

        //TODO Implementierung jb2d

	}

    @API
    public boolean bewegen (Vektor v) {
        //TODO Implementierung jb2d
        return false;
    }

	/**
	 * Verschiebt die Raum-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.
	 * <p/>
	 * Diese Methode arbeitet nach dem Mittelpunkt des das Objekt abdeckenden BoundingRechtecks
	 * durch den Aufruf der Methode <code>zentrum()</code>. Daher ist diese Methode in der Anwendung
	 * auf ein Knoten-Objekt nicht unbedingt sinnvoll.
	 *
	 * @param x
	 * 		Die <code>x</code>-Koordinate des neuen Mittelpunktes des Objektes
	 * @param y
	 * 		Die <code>y</code>-Koordinate des neuen Mittelpunktes des Objektes
	 *
	 * @see #mittelpunktSetzen(Punkt)
	 * @see #verschieben(Vektor)
	 * @see #positionSetzen(float, float)
	 * @see #zentrum()
	 */
    @API
	public void mittelpunktSetzen (float x, float y) {
		this.mittelpunktSetzen(new Punkt(x, y));
	}

	/**
	 * Verschiebt die Raum-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.<br />
	 * Diese Methode Arbeitet nach dem Mittelpunkt des das Objekt abdeckenden BoundingRechtecks
	 * durch den Aufruf der Methode <code>zentrum()</code>. Daher ist diese Methode im Anwand auf
	 * ein Knoten-Objekt nicht unbedingt sinnvoll.<br /> Macht dasselbe wie
	 * <code>mittelPunktSetzen(p.x, p.y)</code>.
	 *
	 * @param p
	 * 		Der neue Mittelpunkt des Raum-Objekts
	 *
	 * @see #mittelpunktSetzen(float, float)
	 * @see #verschieben(Vektor)
	 * @see #positionSetzen(float, float)
	 * @see #zentrum()
	 */
    @API
	public void mittelpunktSetzen (Punkt p) {
		this.verschieben(this.zentrum().nach(p));
	}	/**
	 * Gibt die x-Koordinate der linken oberen Ecke zurück. Sollte das Raumobjekt nicht rechteckig
	 * sein, so wird die Position der linken oberen Ecke des umschließenden Rechtecks genommen.
	 * <p/>
	 * TODO: Deprecate positionX() in favor of this new method?
	 *
	 * @return <code>x</code>-Koordinate
	 *
	 * @see #getY()
	 * @see #position()
	 */
    @API
	public float getX () {
		//FIXME
        return 0;
	}

	/**
	 * Berechnet das Zentrum des Raum-Objekts als Punkt auf der Zeichenebene.
	 * <p/>
	 * Das Zentrum wird über die Methode <code>dimension()</code> berechnet, und zwar über die
	 * Methode des resultierenden BoundingRechtecks:<br /> <code>dimension().zentrum()</code>
	 *
	 * @return Zentrum dieses Raumobjekts
	 */
    @API
	public Punkt zentrum () {
        //FIXME Implementation
		throw new UnsupportedOperationException("4.0 Implementierung steht aus.");
	}

    /**
	 * Setzt die x-Koordinate der Position des Objektes gänzlich neu auf der Zeichenebene. Das
	 * Setzen ist technisch gesehen eine Verschiebung von der aktuellen Position an die neue. <br
	 * /><br /> <b>Achtung!</b><br /> Bei <b>allen</b> Objekten ist die eingegebene Position die
	 * linke, obere Ecke des Rechtecks, das die Figur optimal umfasst. Das heißt, dass dies bei
	 * Kreisen z.B. <b>nicht</b> der Mittelpunkt ist! Hierfür gibt es die Sondermethode
	 * <code>mittelpunktSetzen(int x, int y)</code>.
	 *
	 * @param x
	 * 		neue <code>x</code>-Koordinate
	 *
	 * @see #positionSetzen(float, float)
	 * @see #mittelpunktSetzen(int, int)
	 * @see #setY(float)
	 */
    @API
	public void setX (float x) {
		this.verschieben(x - getX(), 0);
	}

	/**
	 * Gibt die y-Koordinate der linken oberen Ecke zurück. Sollte das Raumobjekt nicht rechteckig
	 * sein, so wird die Position der linken oberen Ecke des umschließenden Rechtecks genommen.
	 * <p/>
	 * TODO: Deprecate positionX() in favor of this new method?
	 *
	 * @return <code>y</code>-Koordinate
	 *
	 * @see #getX()
	 * @see #position()
	 */
    @API
	public float getY () {
        //FIXME
		return 0;
	}

	/**
	 * Test, ob ein anderes Raum-Objekt von diesem geschnitten wird.
	 *
	 * @param r
	 * 		Das Objekt, das auf Kollision mit diesem getestet werden soll.
	 *
	 * @return TRUE, wenn sich beide Objekte schneiden.
	 */
    @API
	public final boolean schneidet (Raum r) {
        //FIXME Implementierung
		return false;
	}

    /**
	 * Setzt die y-Koordinate der Position des Objektes gänzlich neu auf der Zeichenebene. Das
	 * Setzen ist technisch gesehen eine Verschiebung von der aktuellen Position an die neue. <br
	 * /><br /> <b>Achtung!</b><br /> Bei <b>allen</b> Objekten ist die eingegebene Position die
	 * linke, obere Ecke des Rechtecks, das die Figur optimal umfasst. Das heißt, dass dies bei
	 * Kreisen z.B. <b>nicht</b> der Mittelpunkt ist! Hierfür gibt es die Sondermethode
	 * <code>mittelpunktSetzen(int x, int y)</code>.
	 *
	 * @param y
	 * 		neue <code>y</code>-Koordinate
	 *
	 * @see #positionSetzen(float, float)
	 * @see #mittelpunktSetzen(int, int)
	 * @see #setX(float)
	 */
    @API
	public void setY (float y) {
		this.verschieben(0, y - getY());
	}

	/**
	 * Dreht die Zeichenfläche um den Mittelpunkt des Raumes um die gegebenen Grad, bevor mit dem
	 * Zeichenn begonnen wird.<br /> <b><i>Diese Methode sollte nicht außerhalb der Engine verwendet
	 * werden.</i></b>
	 *
	 * @see #zeichnen(Graphics2D, BoundingRechteck)
	 * @see #afterRender(Graphics2D, BoundingRechteck)
	 */
	@NoExternalUse
	public final void beforeRender(Graphics2D g, BoundingRechteck r) {
		/*lastMiddle = mittelPunkt().verschobeneInstanz(new Vektor(-r.x, -r.y));

		lastDrehung = Math.toRadians(drehung);

		if (lastDrehung != 0) {
			g.rotate(lastDrehung, lastMiddle.x, lastMiddle.y);
		}

		if (opacity != 1) {
			composite = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
		} else {
			composite = null;
		}*/
        //throw new UnsupportedOperationException("4.0 Implementierung steht aus.");
        //FIXME Implementation
	}

	/**
	 * Methode zum schnellen Herausfinden des Mittelpunktes des Raum-Objektes.
	 *
	 * @return Die Koordinaten des Mittelpunktes des Objektes
	 *
	 * @see #position()
	 */
    @API
	public Punkt mittelPunkt () {
        //FIXME Implementation
        throw new UnsupportedOperationException("4.0 Implementierung steht aus.");
	}

	/**
	 * Dreht die Zeichenfläche wieder zurück in den Ausgangszustand. <b><i>Diese Methode sollte
	 * nicht außerhalb der Engine verwendet werden.</i></b>
	 *
	 * @see #zeichnen(Graphics2D, BoundingRechteck)
	 * @see #beforeRender(Graphics2D, BoundingRechteck)
	 */
	@NoExternalUse
	public final void afterRender(Graphics2D g, BoundingRechteck r) {
		/*if (composite != null) {
			g.setComposite(composite);
		}

		if (lastDrehung != 0) {
			g.rotate(-lastDrehung, lastMiddle.x, lastMiddle.y);
		}*/
        //FIXME Implementation
        //throw new UnsupportedOperationException("4.0 Implementierung steht aus.");
	}

	/**
	 * Prueft, ob ein bestimmter Punkt innerhalb des Raum-Objekts liegt.
	 *
	 * @param p
	 * 		Der Punkt, der auf Inhalt im Objekt getestet werden soll.
	 *
	 * @return TRUE, wenn der Punkt innerhalb des Objekts liegt.
	 */
    @API
	public final boolean beinhaltet (Punkt p) {
		//FIXME Implementation
        return false;
	}

    /**
	 * Verschiebt das Objekt.<br /> Hierbei wird nichts anderes gemacht, als <code>verschieben(new
	 * Vektor(dX, dY))</code> auszufuehren. Insofern ist diese Methode dafuer gut, sich nicht mit
	 * der Klasse Vektor auseinandersetzen zu muessen.
	 *
	 * @param dX
	 * 		Die Verschiebung in Richtung X
	 * @param dY
	 * 		Die Verschiebung in Richtung Y
	 *
	 * @see #verschieben(Vektor)
	 * @see #bewegen(Vektor)
	 * @see #bewegen(float, float)
	 */
    @API
	public void verschieben (float dX, float dY) {
		this.verschieben(new Vektor(dX, dY));
	}

    /**
     * TODO Doku
     * @param dX
     * @param dY
     * @return
     */
    @API
    public boolean bewegen (float dX, float dY) {
        return this.bewegen(new Vektor(dX, dY));
    }

	/**
	 * TODO Doku
	 *
	 */
    @API
	public Punkt position () {
        //FIXME Implementation
        throw new UnsupportedOperationException("4.0 Implementierung steht aus.");
	}

	/**
	 * Diese Methode loescht alle eventuell vorhandenen Referenzen innerhalb der Engine auf dieses
	 * Objekt, damit es problemlos geloescht werden kann.<br /> <b>Achtung:</b> zwar werden
	 * hierdurch alle Referenzen geloescht, die <b>nur innerhalb</b> der Engine liegen (dies
	 * betrifft vor allem Animationen etc), jedoch nicht die innerhalb eines
	 * <code>Knoten</code>-Objektes!!!!!!!!!<br /> Das heisst, wenn das Objekt an einem Knoten liegt
	 * (was <b>immer der Fall ist, wenn es auch gezeichnet wird (siehe die Wurzel des
	 * Fensters)</b>), muss es trotzdem selbst geloescht werden, <b>dies erledigt diese Methode
	 * nicht!!</b>.
	 */
    @NoExternalUse
	public void loeschen () {
		//Leer - kann von childs überschrieben werden.
	}

	/**
	 * Gibt die aktuelle Opacity des Raumes zurück.
	 *
	 * @return Gibt die aktuelle Opacity des Raumes zurück.
	 */
	@API
	@SuppressWarnings ( "unused" )
	public float getOpacity () {
		return opacity;
	}

	/**
	 * Setzt die Opacity des Raumes.
	 * <p/>
	 * <ul><li><code>0.0f</code> entspricht einem komplett durchsichtigen Raum.</li>
	 * <li><code>1.0f</code> entspricht einem undurchsichtigem Raum.</li></ul>
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void setOpacity (float opacity) {
		this.opacity = opacity;
	}

	/**
	 * Hilfsmethode für die Sortierung der Räume nach dem Z-Index. <b><i>Diese Methode sollte nicht
	 * außerhalb der Engine verwendet werden.</i></b>
	 *
	 * @see #zIndex
	 * @see #zIndex(int)
	 */
	@Override
	@NoExternalUse
	public int compareTo (Raum r) {
		if (zIndex < r.zIndex) {
			return 1;
		}

		if (zIndex > r.zIndex) {
			return -1;
		}

		return 0;
	}










}
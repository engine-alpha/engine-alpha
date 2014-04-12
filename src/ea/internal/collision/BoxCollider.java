package ea.internal.collision;

import org.omg.CORBA.PUBLIC_MEMBER;

import ea.BoundingRechteck;
import ea.Punkt;
import ea.Vektor;

public class BoxCollider 
extends Collider {
	
	/**
	 * Der Vektor, der die Diagonale (von links oben nach rechts unten) beschreibt.
	 * Beide Skalarwerte dürfen nicht negativ sein.
	 */
	Vektor diagonale;
	
	/**
	 * Generiert aus einem <code>BoundingRechteck</code> und einem gegebenen Offset einen Box-Collider.
	 * @param offset Der Offset, den dieser Collider haben soll.
	 * @param br	Das <code>BoundingRechteck</code>, das Grundlage für die Masse des Box-Collider sein soll.
	 * @return		Der Box-Collider, der diesem <code>BoundingRechteck</code> entspricht:<br />
	 * <ls>
	 * <li>Die Position auf der Zeichenebene entspricht ab sofort dem Offset</li>
	 * <li>Breite und Höhe werden übernommen</li>
	 * </ls>
	 */
	public static BoxCollider fromBoundingRechteck(Vektor offset, BoundingRechteck br) {
		BoxCollider bc = new BoxCollider();
		bc.offsetSetzen(offset);
		bc.diagonale = new Vektor(br.breite, br.hoehe);
		return bc;
	}
	
	/**
	 * Default-Konstruktor. Erstellt einen Box-Collider mit Offset (0|0) und Länge = 0, Breite = 0.
	 */
	public BoxCollider() {
		super();
	}
	
	/**
	 * Vollständiger Konstruktor. Erstellt einen neuen Box-Collider mit allen relevanten Parametern.
	 * @param offset		Der Offset für diesen Collider.
	 * @param diagonale		Die Diagonale der Box <i>von der linken, oberen Ecke hin zur rechten, unteren Ecke</i>.
	 */
	public BoxCollider(Vektor offset, Vektor diagonale) {
		this.offset = offset;
		this.diagonale = diagonale;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verursachtCollision(Punkt positionThis, Punkt positionOther, Collider collider) {
		if(collider instanceof BoxCollider) {
			return Collider.boxboxCollision(this, (BoxCollider)collider, positionThis, positionOther);
		} else if(collider instanceof SphereCollider) {
			return Collider.sphereboxCollision((SphereCollider)collider, this, positionOther, positionThis);
		} else if(collider instanceof ColliderGroup) {
			return collider.verursachtCollision(positionOther, positionThis, this);
		}
		//Default:
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean istNullCollider() {
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collider clone() {
		return new BoxCollider(offset.clone(), diagonale.clone());
	}
	
	/**
	 * s
	 * @param start
	 * @return
	 */
	public BoundingRechteck alsBR(Punkt start) {
		return new BoundingRechteck(start.x + offset.x, start.y + offset.y, diagonale.x, diagonale.y);
	}

}

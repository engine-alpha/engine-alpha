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
	 * s
	 * @param start
	 * @return
	 */
	public BoundingRechteck alsBR(Punkt start) {
		return new BoundingRechteck(start.x + offset.x, start.y + offset.y, diagonale.x, diagonale.y);
	}

}

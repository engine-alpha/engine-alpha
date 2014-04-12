package ea.internal.collision;

import ea.Kreis;
import ea.Punkt;
import ea.Vektor;

public class SphereCollider 
extends Collider {
	
	/**
	 * Der Durchmesser des Kreises, das diesen Collider ausmacht.
	 */
	final float durchmesser;
	
	/**
	 * Kreis als Approximation mehrerer Dreiecke für eine effektive CollisionDetection mit
	 * Boxes.
	 */
	Kreis modelsphere;
	
	/**
	 * Erstellt einen neuen sphärischen Collider <b>ohne Offset</b>.
	 * @param durchmesser	Der gewünschte Durchmesser des Colliders.
	 */
	public SphereCollider(float durchmesser) {
		this(durchmesser, Vektor.NULLVEKTOR);
	}

	/**
	 * Erstellt einen neuen sphärischen Collider 
	 * @param durchmesser
	 * @param offset
	 */
	public SphereCollider(float durchmesser, Vektor offset) {
		this(durchmesser, offset, 8);
	}
	
	/**
	 * Erstellt einen neuen sohärischen Collider.
	 * @param durchmesser	Der gewünschte Durchmesser.
	 * @param offset		Der gewünschte Offset.
	 * @param genauigkeit	Die gewünschte Genauigkeit (2^genauigkeit Ecken werden erzeugt für Kollisionstests)
	 */
	public SphereCollider(float durchmesser, Vektor offset, int genauigkeit) { 
		this.offset = offset;
		this.durchmesser = durchmesser;
		modelsphere = new Kreis(0,0, durchmesser, genauigkeit);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verursachtCollision(Punkt positionThis, Punkt positionOther, Collider collider) {
		if(collider instanceof SphereCollider) {
			return Collider.spheresphereCollision(this, (SphereCollider)collider, positionThis, positionOther);
		} else if(collider instanceof BoxCollider) {
			return Collider.sphereboxCollision(this, (BoxCollider)collider, positionThis, positionOther);
		} else if(collider instanceof ColliderGroup) {
			return collider.verursachtCollision(positionOther, positionThis, this);
		}
		//Default:
		return false;
	}
	
	public Kreis ausDiesem(Punkt position) {
		return new Kreis(position.x + offset.x, position.y + offset.y, modelsphere.radius()*2);
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
		Collider newSC = modelsphere.erzeugeCollider();
		newSC.offsetSetzen(offset);
		return new SphereCollider(durchmesser, offset);
	}

}

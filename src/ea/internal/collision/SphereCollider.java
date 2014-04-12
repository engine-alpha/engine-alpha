package ea.internal.collision;

import ea.Kreis;
import ea.Punkt;

public class SphereCollider 
extends Collider {
	
	/**
	 * Der Durchmesser des Kreises, das diesen Collider ausmacht.
	 */
	float durchmesser;
	
	/**
	 * Kreis als Approximation mehrerer Dreiecke für eine effektive CollisionDetection mit
	 * Boxes.
	 */
	Kreis modelsphere;
	
	/**
	 * Erstellt einen neuen sphärischen Collider.
	 * @param durchmesser	Der gewünschte Durchmesser des Colliders.
	 */
	public SphereCollider(float durchmesser) { 
		this.durchmesser = durchmesser;
		modelsphere = new Kreis(0,0, durchmesser);
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
			collider.verursachtCollision(positionOther, positionThis, this);
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
		return new SphereCollider(durchmesser);
	}

}

package ea.internal.collision;

import java.util.ArrayList;

import ea.Punkt;

/**
 * Eine Aggregation von Collidern.
 * @author andonie
 *
 */
public class ColliderGroup extends Collider {

	/**
	 * Die Liste der Collider, die zu dieser Collider-Group geh�ren.
	 */
	private ArrayList<Collider> colliders = new ArrayList<Collider>();
	
	/**
	 * Fügt einen neuen Collider zu dieser Group hinzu.
	 * @param c	Der hinzuzuf�gende Collider.
	 */
	public void addCollider(Collider c) {
		colliders.add(c);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verursachtCollision(Punkt positionThis, Punkt positionOther, Collider collider) {
		Punkt positionMitOffset = positionThis.verschobenerPunkt(offset);
		for(Collider c : colliders) {
			if(c.verursachtCollision(positionMitOffset, positionOther, collider))
				return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * Gibt <code>true</code> zur�ck.
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
		ColliderGroup group = new ColliderGroup();
		for(Collider c : colliders) {
			group.addCollider(c.clone());
		}
		return group;
	}
	
}

package ea.internal.collision;

import java.util.ArrayList;

/**
 * Eine Aggregation von Collidern
 * @author andonie
 *
 */
public class ColliderGroup extends Collider {

	
	private ArrayList<Collider> colliders = new ArrayList<Collider>();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verursachtCollision(Collider collider) {
		// TODO Auto-generated method stub
		return false;
	}
	
}

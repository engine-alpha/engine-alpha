package ea.internal.collision;

import ea.Punkt;

/**
 * Dieser Collider kann sich nicht mit irgendetwas schneiden.
 * Es kann keine Collision mit diesem Collider geben.
 * @author andonie
 *
 */
public class NullCollider
extends Collider {

	/**
	 * Singeleton-Instanz des Colliders
	 */
	private static NullCollider instance;

	/**
	 * Nur über diese Methode kriegt man Zugriff auf die Singleton-Instanz 
	 * dieser Klasse.
	 * @return	Die eine Instanz dieser Klasse.
	 */
	public static NullCollider getInstance() {
		return instance==null ? instance=new NullCollider() : instance;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean verursachtCollision(Punkt positionThis, Punkt positionOther, Collider collider) {
		// immer false
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 * Gibt <code>true</code> zur�ck.
	 */
	@Override
	public boolean istNullCollider() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * Nachdem es nur eine unterscheidbare Instanz von <code>NullCollider</code> gibt,
	 * wird hier aus Performance-Gründen eine Referenz auf das Objekt selbst
	 * zurückgegeben, um nicht unnötig Speicher zu allozieren.
	 */
	@Override
	public Collider clone() {
		return this;
	}
}

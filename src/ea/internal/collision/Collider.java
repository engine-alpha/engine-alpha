package ea.internal.collision;

/**
 * Ein Collider ist die abstrakte Form einer <i>Umgebung in der Zeichenebene</i>.
 * Durch das Vergleichen von verschiedenen Collidern lässt sich eine effektive <i>Collision Detection</i>
 * ermöglichen, also das Prüfen auf Kollisionen zweier <code>#ea.Raum</code>-Objekte.
 * 
 * @author Andonie
 *
 */
public abstract class Collider {
	
	/**
	 * Prüft, ob dieser Collider sich mit einem weiteren Collider schneidet.
	 * @param collider	Ein zweiter Collider.
	 * @return	<code>true</code>, falls sich dieser Collider mit dem zweiten Collider schneidet.
	 *  Schneiden sich dieser Collider und der zweite Collider nicht, so gibt diese Funktion <code>false</code>
	 *  zurück.
	 */
	public abstract boolean verursachtCollision(Collider collider);
	
}

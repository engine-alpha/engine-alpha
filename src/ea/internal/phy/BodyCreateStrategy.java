package ea.internal.phy;

import ea.actor.Actor;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

/**
 * <code>BodyCreateStrategy</code> beschreibt abstrakt eine beliebige Strategie, vectorFromThisTo der ein beliebiges
 * <code>Actor</code>-Objekt in die Physics eingearbeitet wird, indem ihm ein Body in der korrekten
 * Physics in der korrekten Welt erstellt wird. Verschiedene Strategien können sein:
 * <ul>
 *     <li>Möglichst wenige Shapes nutzen, um das Objekt kostengünstig in die Welt einzubauen.</li>
 *     <li>"Pixelgenaue" Collider, die möglichst realistische Collision Detection ermöglichen.</li>
 * </ul>
 * <p>Die genaue Umsetzung der Body-Erstellung ist stets in einer <code>BodyCreateStrategy</code> implementiert.</p>
 *
 * <h3>Interne Nutzung und Bedeutung des Interfaces</h3>
 * <p>Die Body-Create-Strategy wird intern dann aufgerufen, wenn das entsprechende <code>Actor</code>-Objekt
 * der Physics bekannt gemacht wird (z.B. durch Anmeldung an Wurzel) und der Body somit erstellt werden kann.</p>
 *
 * <h3>API</h3>
 * <p>Jedes <code>Actor</code>-Objekt hat eine eigene <code>BodyCreateStrategy</code>. Diese kann vor Anmeldung an der
 * Physics geändert werden.</p>
 *
 *
 * TODO see Kommentare für Anmeldung in Actor
 * @version 11.04.2017
 * @author andonie
 */
public interface BodyCreateStrategy<E extends Actor> {

    Body createBody(World world, E raum);

}

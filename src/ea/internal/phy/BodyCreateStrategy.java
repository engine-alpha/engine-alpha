package ea.internal.phy;

import ea.Raum;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

/**
 * <code>BodyCreateStrategy</code> beschreibt abstrakt eine beliebige Strategie, nach der ein beliebiges
 * <code>Raum</code>-Objekt in die Physik eingearbeitet wird, indem ihm ein Body in der korrekten
 * Physik in der korrekten Welt erstellt wird. Verschiedene Strategien können sein:
 * <ul>
 *     <li>Möglichst wenige Shapes nutzen, um das Objekt kostengünstig in die Welt einzubauen.</li>
 *     <li>"Pixelgenaue" Collider, die möglichst realistische Collision Detection ermöglichen.</li>
 * </ul>
 * <p>Die genaue Umsetzung der Body-Erstellung ist stets in einer <code>BodyCreateStrategy</code> implementiert.</p>
 *
 * <h3>Interne Nutzung und Bedeutung des Interfaces</h3>
 * <p>Die Body-Create-Strategy wird intern dann aufgerufen, wenn das entsprechende <code>Raum</code>-Objekt
 * der Physik bekannt gemacht wird (z.B. durch Anmeldung an Wurzel) und der Body somit erstellt werden kann.</p>
 *
 * <h3>API</h3>
 * <p>Jedes <code>Raum</code>-Objekt hat eine eigene <code>BodyCreateStrategy</code>. Diese kann vor Anmeldung an der
 * Physik geändert werden.</p>
 *
 *
 * TODO see Kommentare für Anmeldung in Raum
 * @version 11.04.2017
 * @author andonie
 */
public interface BodyCreateStrategy<E extends Raum> {

    Body createBody(World world, E raum);

}

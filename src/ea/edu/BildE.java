/**
 * 
 */
package ea.edu;

import ea.Bild;

/**
 * Diese Klasse wrapt die Funktionen der Klasse <code>Bild</code> und 
 * stellt sie für die lokale BlueJ-API mieglichst klar bereit.
 * @author Andonie
 *
 */
public class BildE extends Bild {
	
	public BildE(int x, int y, String pfad) {
		super(x,y,pfad);
		FensterE.getFenster().wurzel.add(this);
	}
}

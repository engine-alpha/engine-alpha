package ea.edu;

import ea.*;

public class TextE 
extends Text {
	
	/**
	 * Konstruktor erstellt einen fertig sichtbaren Text.
	 * Seine Position lässt sich leicht über die geerbten Methoden ändern.
	 * @param content	Der Inhalt des Texts.
	 */
	public TextE(String content) {
		super(content, 100, 140);
		farbeSetzen("Gruen");
        FensterE.getFenster().wurzel.add(this);
	}
}

/* Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

import java.awt.Dimension;

import javax.swing.*;

/**
 * Diese Klasse ist ausschliesslich dazu da, um der EA eine 
 * runnable-Funktion zu geben. Hierbei wird ein kleines Fenster
 * geoeffnet, dass eine Information zur Engine angibt.
 * @author Andonie
 *
 */
public class EngineAlpha {

	private class PromoFrame
	extends JFrame {
		
		public PromoFrame() {
			super("Engine Alpha");
			
			int width = 300;
			int height = 200;
			super.setSize(width, height);
			
			Dimension screenSize = getToolkit().getScreenSize();
			super.setLocation((screenSize.width-width) / 2, (screenSize.height - height)/2);
		}
		
	}
	
	private static JEditorPane ladeContent() {
		
		return null;
	}
	
	public static void main(String[] args) {
		
	}
}

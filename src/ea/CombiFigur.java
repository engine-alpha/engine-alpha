/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ea;

import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;

/**
 * Eine Combifigur ist eine Figur, die aus verschiedenen "Unterfiguren" besteht. Im Gegensatz zu
 * einer <code>ActionFigur</code> bedeutet dies, dass <b>alle</b> "Unterfiguren" gleichzeitig zu
 * sehen sind, und <b>nicht nur eine</b> aktuelle.
 *
 * @author Michael Andonie
 */
@SuppressWarnings ( "serial" )
public class CombiFigur extends Raum {

	/**
	 * Alle angemeldeten Figuren.
	 */
	private volatile ActionFigur[] figuren;

	/**
	 * Die fuer die jeweiligen Figuren angemeldeten Namen.
	 */
	private String[] namen;

	/**
	 * Konstruktor. Nach Aufruf dieser Methode entsteht eine weiter benutzbare
	 * <code>CombiFigur</code> mit <b>einem</b> Element.
	 *
	 * @param figur1
	 * 		Die erste Figur.
	 * @param name1
	 * 		Der Name der ersten Figur.
	 */
	public CombiFigur (ActionFigur figur1, String name1) {
		figuren = new ActionFigur[] {figur1};
		namen = new String[] {name1};
	}

	/**
	 * Fuegt der CombiFigur eine neue Figur zu.
	 *
	 * @param figur
	 * 		Die hinzuzufuegende Figur selbst.
	 * @param name
	 * 		Der Name, unter dem diese Figur ab dann zu erreichen ist.
	 */
	public void add (ActionFigur figur, String name) {
		String[] namenNeu = new String[namen.length + 1];
		ActionFigur[] figurenNeu = new ActionFigur[figuren.length + 1];
		for (int i = 0; i < namen.length; i++) {
			namenNeu[i] = namen[i];
			figurenNeu[i] = figuren[i];
		}
		namenNeu[namenNeu.length - 1] = name;
		figurenNeu[figurenNeu.length - 1] = figur;
	}

	/**
	 * Sucht eine bestimmte "Unterfigur" und gibt diese aus.
	 *
	 * @param name
	 * 		Der Name der zu suchenden Figur.
	 *
	 * @return Die gesuchte Figur mit dem entsprechenden Namen. Existiert keine Figur mit dem Namen,
	 * ist die Rueckgabe <code>null</code>.
	 */
	public ActionFigur get (String name) {
		for (int i = 0; i < namen.length; i++) {
			if (namen[i].equals(name)) {
				return figuren[i];
			}
		}

		Logger.error("Figur", "Der Eingegebene Name eines Gliedes dieser CombiFigur existiert nicht: " + name);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(Graphics2D g) {
		for (int i = 0; i < figuren.length; i++) {
            figuren[i].render(g);
        }
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Shape createShape(float pixelProMeter) {
        return figuren[0].createShape(pixelProMeter);
    }

}

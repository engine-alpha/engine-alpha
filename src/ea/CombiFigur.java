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

import ea.internal.collision.Collider;
import ea.internal.collision.ColliderGroup;
import ea.internal.util.Logger;

import java.awt.*;
import java.util.ArrayList;

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
		figuren = new ActionFigur[]{figur1};
		namen = new String[]{name1};
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

		Logger.error("Achtung! Der Eingegebene Name eines Gliedes dieser CombiFigur existiert nicht: " + name);
		return null;
	}

	/**
	 * Verschiebt das Objekt.
	 *
	 * @param v
	 * 		Der Vektor, der die Verschiebung des Objekts angibt.
	 *
	 * @see Vektor
	 * @see #verschieben(float, float)
	 */
	@Override
	public void verschieben (Vektor v) {
		for (int i = 0; i < figuren.length; i++) {
			figuren[i].verschieben(v);
		}
	}

	/**
	 * Zeichnet das Objekt.
	 *
	 * @param g
	 * 		Das zeichnende Graphics-Objekt
	 * @param r
	 * 		Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br /> Hierbei soll
	 * 		zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann
	 * 		gezeichnet werden.
	 */
	@Override
	public void zeichnen (Graphics2D g, BoundingRechteck r) {
		super.beforeRender(g, r);

		for (int i = 0; i < figuren.length; i++) {
			figuren[i].zeichnen(g, r);
		}

		super.afterRender(g, r);
	}

	/**
	 * Methode zum Beschreiben der rechteckigen Flaeche, die dieses Objekt einnimmt.<br /> Diese
	 * Methode wird zentral fuer die Trefferkollisionen innerhalb der Engine benutzt und gehoert zu
	 * den wichtigsten Methoden der Klasse und der Engine.
	 *
	 * @return Ein BoundingRechteck mit dem minimal noetigen Umfang, um das Objekt <b>voll
	 * einzuschliessen</b>.
	 */
	@Override
	public BoundingRechteck dimension () {
		BoundingRechteck dim = figuren[0].dimension();

		for (ActionFigur f : figuren)
			dim = dim.summe(f.dimension());

		return dim;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collider erzeugeCollider () {
		ColliderGroup cg = new ColliderGroup();
		for (ActionFigur f : figuren) {
			cg.addCollider(f.erzeugeCollider());
		}
		return erzeugeLazyCollider();
	}

	/**
	 * Berechnet exakter alle Rechteckigen Flaechen, auf denen dieses Objekt liegt.<br /> Diese
	 * Methode wird von komplexeren Gebilden, wie geometrischen oder Listen ueberschrieben.
	 *
	 * @return Alle Rechtecksflaechen, auf denen dieses Objekt liegt. Ist standartisiert ein Array
	 * der Groesse 1 mit der <code>dimension()</code> als Inhalt.
	 *
	 * @see Knoten
	 */
	@Override
	public BoundingRechteck[] flaechen () {
		ArrayList<BoundingRechteck> list = new ArrayList<BoundingRechteck>();

		for (ActionFigur f : figuren)
			list.add(f.dimension());

		return list.toArray(new BoundingRechteck[list.size()]);
	}
}

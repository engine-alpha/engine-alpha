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

package ea;

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.phy.KnotenHandler;
import ea.internal.phy.WorldHandler;
import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.joints.*;

import java.awt.*;
import java.util.Collections;
import java.util.Vector;

/**
 * Ein Knoten ist eine Sammlung vielen Raum-Objekten, die hierdurch einheitlich bewegungSimulieren, und
 * einheitlich behandelt werden koennen.
 *
 * @author Michael Andonie, Niklas Keller <me@kelunik.com>
 */
public class Knoten extends Raum {
	/**
	 * Die Liste aller Raum-Objekte, die dieser Knoten fasst.
	 */
	private final Vector<Raum> list;

	/**
	 * Die Joints, die dieser Knoten gerade innehat.
	 */
	private final Vector<Joint> joints;

	/**
	 * Ob der Knoten die Anmeldung neuer Raum-Objekte blockiert.
	 */
	private boolean lock = false;

    /**
     * Gibt die interne Darstellung der Child-Liste als Vector
     * aus.
     * @return  die interne Darstellung der Child-Liste als Vector
     */
    @NoExternalUse
    public Vector<Raum> getList() {
        return list;
    }

	/**
	 * Konstruktor für Objekte der Klasse Knoten
	 */
	public Knoten () {
		list = new Vector<>();
		joints = new Vector<>();
        super.physikHandler = new KnotenHandler(this);
	}

    /**
     * Der World Handler dieses Knotens. Beschreibt die physikalische Welt, in der der Knoten sich befindet.
     */
    private WorldHandler worldHandler = null;


	/**
	 * Loescht alle Raum-Objekte, die an diesem Knoten gelagert sind.
	 */
	public void leeren () {
        for(Raum m : list) {
            m.kill();
        }
		list.clear();
	}

	/**
	 * Entfernt ein Raum-Objekt von diesem Knoten.<br /> War es mehrfach angesteckt, so werden alle
	 * Verbindungen geloescht, war es niemals angemeldet, so passiert <b>gar nichts</b>.<br /> <br
	 * /> <b>Achtung!!</b><br /> Sollte <i>Physik</i> benutzt werden:<br /> Diese Methode macht alle
	 * abgemeldeten <code>Raum</code>-Objekt fuer die Physik neutral!!!
	 *
	 * @param m
	 * 		Das von diesem Knoten zu entfernende Raum-Objekt
	 *
	 */
    @API
	public void entfernen (Raum m) {
		if (list.contains(m)) {
			m.kill();
		}

		// noinspection StatementWithEmptyBody
		while (list.remove(m)) ;
	}

	/**
	 * Prueft, ob ein bestimmtes Raum-Objekt in diesem Knoten gelagert ist.<br /> <br />
	 * <b>ACHTUNG</b><br /> Diese Methode prueft nicht eventuelle Unterknoten, ob diese vielleiht
	 * das Raum-Objekt beinhalten, sondern nur den eigenen Inhalt!
	 *
	 * @param m
	 * 		Das Raum-Objekt, das auf Vorkommen in diesem Knoten ueberprueft werden soll
	 *
	 * @return <code>true</code>, wenn das Raum-Objekt <b>ein- oder auch mehrmals</b> an diesem
	 * Knoten liegt
	 */
	public boolean besitzt (Raum m) {
		return list.contains(m);
	}

	/**
	 * Kombinationsmethode. Hiermit kann man so viele Raum-Objekte gleichzeitig an den Knoten
	 * tastenReagierbarAnmelden, wie man will.<br /> <b>Beispiel:</b><br /> <br /> <code> //Der Knoten, um alle
	 * Objekte zu sammeln<br /> Knoten knoten = new Knoten();<br /> <br /> //Lauter gebastelte
	 * Raum-Objekte<br /> Raum r1<br /> Raum r2;<br /> Raum r3;<br /> Raum r4;<br /> Raum r5<br />
	 * Raum r6;<br /> Raum r7;<br /> Raum r8;<br /> Raum r9<br /> Raum r10;<br /> Raum r11;<br />
	 * Raum r12;<br /> <br /> //Eine Methode, um alle anzumelden:<br /> knoten.add(r1, r2, r3, r4,
	 * r5, r6, r7, r8, r9, r10, r11, r12);<br /> </code><br /> Das Ergebnis: 11 Zeilen Programmcode
	 * gespart.
	 */
	public void add (Raum... m) {
		for (Raum n : m) {
			add(n);
		}
	}

	/**
	 * Fuegt ein Raum-Objekt diesem Knoten hinzu.<br /> Das zugefuegte Objekt wird ab dann in alle
	 * Methoden des Knotens (<code>verschieben(), dimension()</code> etc.) mit eingebunden.
	 *
	 * @param m
	 * 		Das hinzuzufuegende Raum-Objekt
	 */
	public void add (Raum m) {
		if(lock) {
			Logger.error("Knoten", "Fehler: Der Knoten, an dem ein neues Objekt anzumelden war, " +
					"ist im Lock-Zustand.");
			return;
		}
		list.add(m);

		Collections.sort(list);

        if(worldHandler != null)
            m.updateWorld(worldHandler);
	}


	/**
	 * <p>Fixiert alle Elemente physikalisch aneinander. Nach dem Aufruf bleiben die Positionen aller Objekte im Knoten
	 * relativ zueinander gleich. Kräfte, die auf ein einzelnes Element wirken, haben damit auch Einfluss auf den Rest
	 * der Elemente.</p>
	 * <p>Nach Aufruf dieser Funktion können <b>keine Elemente mehr an diesem Knoten eingefügt werden</b>.</p>
	 * @see #freeAllElements()
	 * @see #isFixated()
	 */
	@API
	public void fixateAllElements() {
		if(lock) {
			Logger.error("Knoten", "Die Elemente dieses Knoten sind bereits fixiert.");
			return;
		}

		lock = true; //<- Lock setzen. Der Knoten ist jetzt voll

		Raum last = null;
		for(Raum r : list) {
			if(last == null) {
				last = r;
				continue;
			}

			//Joint Definieren
			WeldJointDef weldJointDef = new WeldJointDef();
			weldJointDef.initialize(last.getPhysikHandler().getBody(), r.getPhysikHandler().getBody(),
					worldHandler.fromVektor(last.position.get().alsVektor()));

			//Joint in die Welt setzen
			Joint knotenJoint = worldHandler.getWorld().createJoint(weldJointDef);

			//Referenz zum Joint halten
			joints.add(knotenJoint);
		}
	}

	/**
	 * Löst die Fixierung der Elemente des Knotens wieder. Nach Aufruf dieser Methode bewegen sich die Elemente in
	 * diesem Knoten wieder unabhängig voneinander.
	 * @see #fixateAllElements()
	 * @see #isFixated()
	 */
	@API
	public void freeAllElements() {
		if(!lock) {
			Logger.error("Knoten", "Die Elemente dieses Knoten sind gerade nicht fixiert.");
			return;
		}

		lock = false;

		//Alle Joints aus der Welt nehmen
		for(Joint joint : joints) {
			worldHandler.getWorld().destroyJoint(joint);
		}

		//Liste leeren
		joints.clear();
	}

	/**
	 * Gibt an, ob die Elemente in diesem Knoten gerade aneinander fixiert sind oder nicht.
	 * @return	<code>true</code>, wenn die Elemente dieses Knotens gerade alle aneinander fixiert sind. Sonst
	 * <code>false</code>.
	 * @see #fixateAllElements()
	 * @see #freeAllElements()
	 */
	@API
	public boolean isFixated() {
		return lock;
	}

	/**
	 * Gibt alle Elemente des Knotens in Form eines <code>Raum</code>-Objekt-Arays aus.
	 *
	 * @return Alle Elemente als vollstaendig gefuelltes <code>Raum</code>-Objekt-Aray.
	 */
	public Raum[] alleElemente () {
		return list.toArray(new Raum[list.size()]);
	}

    /**
     * {@inheritDoc}
     *
     * Reicht das Update-Signal an die childs weiter.
     */
    @NoExternalUse
    @Override
    public void updateWorld(WorldHandler worldHandler) {
        if(worldHandler == null) {
            return;
        }
        this.worldHandler = worldHandler;
        super.updateWorld(worldHandler);
        for (int i = list.size() - 1; i >= 0; i--) {
            list.get(i).updateWorld(worldHandler);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Überspringt das Pre-Rendering und gibt nur den
     * Befehl zu zeichnen weiter, um mehrfache Einrechnung der Rotation zu
     * verhindern.
     */
    @Override
    public void renderBasic(Graphics2D g, BoundingRechteck r) {
        if(sichtbar())
            for (int i = list.size() - 1; i >= 0; i--) {
                list.get(i).renderBasic(g,r);
            }
    }

	/**
	 * {@inheritDoc}
     * Der Zeichnen-Befehl wird an die Unterobjekte weitergetragen.<br />
	 *
	 * @param g
	 * 		Das Grafik-Objekt
	 */
	@Override
    @NoExternalUse
	public void render(Graphics2D g) {
		throw new IllegalStateException("Die Render-Routine eines Knotens wurde aufgerufen. " +
                "Dies sollte nicht passieren.");
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public Shape createShape(float pixelProMeter) {
        return null; // Knoten hat keine Shape => Null.
    }

    /**
     * Setzt die Durchsichtigkeit für jedes angemeldete Objekt.
     *
     * @param opacity {@inheritDoc}
     */
    @Override
    @API
    public void setOpacity (float opacity) {
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                list.get(i).setOpacity(opacity);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Wahrscheinlich wurde die Liste geleert.
        }
    }

    @NoExternalUse
	public WorldHandler getWorldHandler() {
		return worldHandler;
	}
}

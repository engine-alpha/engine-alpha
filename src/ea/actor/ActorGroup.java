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

package ea.actor;

import ea.BoundingRechteck;
import ea.Scene;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.phy.KnotenHandler;
import ea.internal.util.Logger;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.WeldJointDef;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Ein ActorGroup ist eine Sammlung vielen Actor-Objekten, die hierdurch einheitlich bewegungSimulieren,
 * und einheitlich behandelt werden koennen.
 *
 * @author Michael Andonie
 * @author Niklas Keller
 */
public class ActorGroup extends Actor {
    /**
     * Die Liste aller Actor-Objekte, die diese ActorGroup fasst.
     */
    private final List<Actor> list;

    /**
     * Die Joints, die diese ActorGroup gerade innehat.
     */
    private final List<Joint> joints;

    /**
     * Ob der ActorGroup die Anmeldung neuer Actor-Objekte blockiert.
     */
    private boolean lock = false;

    /**
     * Führt die angegebene Funktion für jedes Element am ActorGroup aus.
     */
    @NoExternalUse
    public void forEach(Consumer<Actor> functor) {
        synchronized (this.list) {
            for (Actor room : this.list) {
                functor.accept(room);
            }
        }
    }

    /**
     * Konstruktor für Objekte der Klasse ActorGroup
     */
    public ActorGroup() {
        list = new ArrayList<>();
        joints = new ArrayList<>();
        super.physicsHandler = new KnotenHandler(this);
    }

    @Override
    public void onAttach(Scene scene) {
        super.onAttach(scene);

        for (Actor room : this.list) {
            room.onAttach(scene);
        }
    }

    @Override
    public void onDetach() {
        for (Actor room : this.list) {
            room.onDetach();
        }

        super.onDetach();
    }

    /**
     * Löscht alle Actor-Objekte, die an diesem ActorGroup gelagert sind.
     */
    public void removeAll() {
        for (Actor room : this.list) {
            remove(room);
        }
    }

    /**
     * Entfernt ein Actor-Objekt von diesem ActorGroup.<br /> War es mehrfach angesteckt, so werden alle
     * Verbindungen geloescht, war es niemals angemeldet, so passiert <b>gar nichts</b>.<br /> <br
     * /> <b>Achtung!!</b><br /> Sollte <i>Physics</i> benutzt werden:<br /> Diese Methode macht alle
     * abgemeldeten <code>Actor</code>-Objekt fuer die Physics neutral!!!
     *
     * @param m Das von diesem ActorGroup zu entfernende Actor-Objekt
     */
    @API
    public void remove(Actor m) {
        synchronized (this.list) {
            if (!list.contains(m)) {
                return;
            }

            // noinspection StatementWithEmptyBody
            while (list.remove(m)) ;
        }

        // Always detach _after_ removing from the list, otherwise Rendering might result in a NPE.
        if (this.getScene() != null) {
            m.onDetach();
        }
    }

    /**
     * Prueft, ob ein bestimmtes Actor-Objekt in diesem ActorGroup gelagert ist.<br /> <br />
     * <b>ACHTUNG</b><br /> Diese Methode prueft nicht eventuelle Unterknoten, ob diese vielleiht
     * das Actor-Objekt beinhalten, sondern nur den eigenen Inhalt!
     *
     * @param m Das Actor-Objekt, das auf Vorkommen in diesem ActorGroup ueberprueft werden soll
     *
     * @return <code>true</code>, wenn das Actor-Objekt <b>ein- oder auch mehrmals</b> an diesem
     * ActorGroup liegt
     */
    public boolean contains(Actor m) {
        return list.contains(m);
    }

    /**
     * Kombinationsmethode. Hiermit kann man so viele Actor-Objekte gleichzeitig an den ActorGroup
     * tastenReagierbarAnmelden, wie man will.<br /> <b>Beispiel:</b><br /> <br /> <code> //Der
     * ActorGroup, um alle Objekte zu sammeln<br /> ActorGroup knoten = new ActorGroup();<br /> <br /> //Lauter
     * gebastelte Actor-Objekte<br /> Actor r1<br /> Actor r2;<br /> Actor r3;<br /> Actor r4;<br /> Actor
     * r5<br /> Actor r6;<br /> Actor r7;<br /> Actor r8;<br /> Actor r9<br /> Actor r10;<br /> Actor
     * r11;<br /> Actor r12;<br /> <br /> //Eine Methode, um alle anzumelden:<br /> knoten.add(r1,
     * r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12);<br /> </code><br /> Das Ergebnis: 11 Zeilen
     * Programmcode gespart.
     */
    public void add(Actor... m) {
        for (Actor n : m) {
            add(n);
        }
    }

    /**
     * Fuegt ein Actor-Objekt diesem ActorGroup hinzu.<br /> Das zugefuegte Objekt wird ab dann in alle
     * Methoden des Knotens (<code>move(), dimension()</code> etc.) mit eingebunden.
     *
     * @param m Das hinzuzufuegende Actor-Objekt
     */
    public void add(Actor m) {
        if (lock) {
            Logger.error("ActorGroup", "Fehler: Der ActorGroup, an dem ein neues Objekt anzumelden war, " +
                    "ist im Lock-Zustand.");
            return;
        }

        if (this.getScene() != null) {
            m.onAttach(this.getScene());
        }

        synchronized (this.list) {
            // Add to list _after_ calling onAttach, otherwise rendering might ask for position with
            // a NullHandler being set for physics.
            list.add(m);
            Collections.sort(list);
        }
    }

    /**
     * <p>Fixiert alle Elemente physikalisch aneinander. Nach dem Aufruf bleiben die Positionen
     * aller Objekte im ActorGroup relativ zueinander gleich. Kräfte, die auf ein einzelnes Element
     * wirken, haben damit auch Einfluss auf den Rest der Elemente.</p> <p>Nach Aufruf dieser
     * Funktion können <b>keine Elemente mehr an diesem ActorGroup eingefügt werden</b>.</p>
     *
     * @see #freeAllElements()
     * @see #isFixated()
     */
    @API
    public void fixateAllElements() {
        if (lock) {
            Logger.error("ActorGroup", "Die Elemente dieses ActorGroup sind bereits fixiert.");
            return;
        }

        lock = true; //<- Lock setzen. Der ActorGroup ist jetzt voll

        Actor last = null;
        for (Actor r : list) {
            if (last == null) {
                last = r;
                continue;
            }

            //Joint Definieren
            WeldJointDef weldJointDef = new WeldJointDef();
            weldJointDef.initialize(last.getPhysicsHandler().getBody(), r.getPhysicsHandler().getBody(),
                    getScene().getWorldHandler().fromVektor(last.position.get().asVector()));

            //Joint in die Welt setzen
            Joint knotenJoint = getScene().getWorldHandler().getWorld().createJoint(weldJointDef);

            //Referenz zum Joint halten
            joints.add(knotenJoint);
        }
    }

    /**
     * Löst die Fixierung der Elemente des Knotens wieder. Nach Aufruf dieser Methode bewegen sich
     * die Elemente in diesem ActorGroup wieder unabhängig voneinander.
     *
     * @see #fixateAllElements()
     * @see #isFixated()
     */
    @API
    public void freeAllElements() {
        if (!lock) {
            Logger.error("ActorGroup", "Die Elemente dieses ActorGroup sind gerade nicht fixiert.");
            return;
        }

        lock = false;

        //Alle Joints aus der Welt nehmen
        for (Joint joint : joints) {
            getScene().getWorldHandler().getWorld().destroyJoint(joint);
        }

        //Liste removeAll
        joints.clear();
    }

    /**
     * Gibt an, ob die Elemente in diesem ActorGroup gerade aneinander fixiert sind oder nicht.
     *
     * @return <code>true</code>, wenn die Elemente dieses Knotens gerade alle aneinander fixiert
     * sind. Sonst <code>false</code>.
     *
     * @see #fixateAllElements()
     * @see #freeAllElements()
     */
    @API
    public boolean isFixated() {
        return lock;
    }

    /**
     * Gibt alle Elemente des Knotens in Form eines <code>Actor</code>-Objekt-Arays aus.
     *
     * @return Alle Elemente als vollstaendig gefuelltes <code>Actor</code>-Objekt-Aray.
     */
    public Actor[] getAllMembers() {
        return list.toArray(new Actor[list.size()]);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Überspringt das Pre-Rendering und gibt nur den Befehl zu zeichnen weiter, um mehrfache
     * Einrechnung der Rotation zu verhindern.
     */
    @Override
    public void renderBasic(Graphics2D g, BoundingRechteck r) {
        if (isVisible()) {
            synchronized (this.list) {
                for (Actor room : this.list) {
                    room.renderBasic(g, r);
                }
            }
        }
    }

    /**
     * {@inheritDoc} Der Zeichnen-Befehl wird an die Unterobjekte weitergetragen.<br />
     *
     * @param g Das Grafik-Objekt
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
        return null; // ActorGroup hat keine Shape => Null.
    }

    /**
     * Setzt die Durchsichtigkeit für jedes angemeldete Objekt.
     *
     * @param opacity {@inheritDoc}
     */
    @Override
    @API
    public void setOpacity(float opacity) {
        try {
            for (int i = list.size() - 1; i >= 0; i--) {
                list.get(i).setOpacity(opacity);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            // Wahrscheinlich wurde die Liste geleert.
        }
    }
}

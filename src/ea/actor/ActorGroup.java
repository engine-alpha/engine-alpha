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
import ea.FrameUpdateListener;
import ea.Game;
import ea.Scene;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.input.KeyListener;
import ea.input.MouseClickListener;
import ea.input.MouseWheelListener;
import ea.internal.phy.KnotenHandler;
import ea.internal.phy.PhysikHandler;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.WeldJointDef;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
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
     * Alle Actor-Objekte der Gruppe.
     */
    private final List<Actor> actors;

    /**
     * Die Joints, die diese ActorGroup gerade innehat.
     */
    private final List<Joint> joints;

    /**
     * Ob der ActorGroup die Anmeldung neuer Actor-Objekte blockiert.
     */
    private boolean lock = false;

    /**
     * Konstruktor für Objekte der Klasse ActorGroup
     */
    public ActorGroup(Scene scene) {
        super(scene, null);

        actors =  new ArrayList<>();
        joints = new ArrayList<>();
    }

    /**
     * Führt die angegebene Funktion für jedes Element am ActorGroup aus.
     */
    @NoExternalUse
    public void forEach(Consumer<Actor> functor) {
        synchronized (this.actors) {
            for (Actor room : this.actors) {
                functor.accept(room);
            }
        }
    }

    @Override
    public void destroy() {
        if (this.getScene() != null) {
            synchronized (this.actors) {
                for (Actor actor : this.actors) {
                    this.detachListeners(actor);
                    actor.destroy();
                }
            }
        }

        super.destroy();
    }

    /**
     * Löscht alle Actor-Objekte, die an diesem ActorGroup gelagert sind.
     */
    @API
    public void clear() {
        synchronized (this.actors) {
            Actor[] actors = this.actors.toArray(new Actor[0]);
            this.actors.clear();

            // Always detach _after_ removing from the actors,
            // otherwise rendering might result in a NPE.
            for (Actor actor : actors) {
                if (this.getScene() != null) {
                    this.detachListeners(actor);
                    actor.destroy();
                }
            }
        }
    }

    /**
     * Entfernt ein Actor-Objekt von diesem ActorGroup.<br /> War es mehrfach angesteckt, so werden alle
     * Verbindungen geloescht, war es niemals angemeldet, so passiert <b>gar nichts</b>.<br /> <br
     * /> <b>Achtung!!</b><br /> Sollte <i>Physics</i> benutzt werden:<br /> Diese Methode macht alle
     * abgemeldeten <code>Actor</code>-Objekt fuer die Physics neutral!!!
     *
     * @param actor Das von diesem ActorGroup zu entfernende Actor-Objekt
     */
    @API
    public void remove(Actor actor) {
        synchronized (this.actors) {
            if (Game.isLocked()) {
                Game.enqueue(() -> remove(actor));
                return;
            }

            if (!this.actors.remove(actor)) {
                return;
            }
        }

        // Always detach _after_ removing from the actors,
        // otherwise rendering might result in a NPE.
        this.detachListeners(actor);
        actor.destroy();
    }

    /**
     * Prüft, ob ein bestimmtes Actor-Objekt in dieser ActorGroup gelagert ist.<br /> <br />
     * <b>ACHTUNG</b><br /> Diese Methode prüft nicht für eventuelle Unterknoten, ob diese
     * vielleicht das Actor-Objekt beinhalten, sondern nur den eigenen Inhalt!
     *
     * @param m Das Actor-Objekt, das auf Vorkommen in diesem ActorGroup überprueft werden soll
     *
     * @return <code>true</code>, wenn das Actor-Objekt <b>ein- oder auch mehrmals</b> in dieser
     * ActorGroup vorkommt
     */
    public boolean contains(Actor m) {
        return this.actors.contains(m);
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
            this.add(n);
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
            throw new IllegalStateException("Die ActorGroup ist bereits fixiert und kann nicht mehr geändert werden.");
        }

        if (this.getScene() != null) {
            this.attachListeners(m);
        }

        synchronized (this.actors) {
            // Add to actors _after_ calling onAttach,
            // otherwise rendering might ask for position with a NullHandler being set for physics.
            actors.add(m);
            actors.sort(Comparator.comparingInt(Actor::getLayer));
        }
    }

    private void attachListeners(Actor actor) {
        if (actor instanceof MouseClickListener) {
            this.getScene().addMouseClickListener((MouseClickListener) actor);
        }

        if (actor instanceof KeyListener) {
            this.getScene().addKeyListener((KeyListener) actor);
        }

        if (actor instanceof FrameUpdateListener) {
            this.getScene().addFrameUpdateListener((FrameUpdateListener) actor);
        }

        if (actor instanceof MouseWheelListener) {
            this.getScene().addMouseWheelListener((MouseWheelListener) actor);
        }
    }

    private void detachListeners(Actor actor) {
        if (actor instanceof MouseClickListener) {
            this.getScene().removeMouseClickListener((MouseClickListener) actor);
        }

        if (actor instanceof KeyListener) {
            this.getScene().removeKeyListener((KeyListener) actor);
        }

        if (actor instanceof FrameUpdateListener) {
            this.getScene().removeFrameUpdateListener((FrameUpdateListener) actor);
        }

        if (actor instanceof MouseWheelListener) {
            this.getScene().removeMouseWheelListener((MouseWheelListener) actor);
        }
    }

    /**
     * <p>Fixiert alle Elemente physikalisch aneinander. Nach dem Aufruf bleiben die Positionen
     * aller Objekte im ActorGroup relativ zueinander gleich. Kräfte, die auf ein einzelnes Element
     * wirken, haben damit auch Einfluss auf den Rest der Elemente.</p> <p>Nach Aufruf dieser
     * Funktion können <b>keine Elemente mehr an diesem ActorGroup eingefügt werden</b>.</p>
     *
     * @see #freeFixation()
     * @see #isFixated()
     */
    @API
    public void fixate() {
        if (lock) {
            throw new IllegalStateException("Die ActorGroup ist bereits fixiert und kann nicht mehr geändert werden.");
        }

        lock = true; //<- Lock setzen. Der ActorGroup ist jetzt voll

        Actor first = null;
        for (Actor actor : actors) {
            if (first == null) {
                first = actor;
                continue;
            }

            // Joint Definieren
            WeldJointDef weldJointDef = new WeldJointDef();
            weldJointDef.initialize(
                    first.getPhysicsHandler().getBody(),
                    actor.getPhysicsHandler().getBody(),
                    getScene().getWorldHandler().fromVektor(first.position.get())
            );

            // Joint erstellen und Referenz zum Joint halten
            this.joints.add(getScene().getWorldHandler().getWorld().createJoint(weldJointDef));
        }
    }

    /**
     * Löst die Fixierung der Elemente des Knotens wieder. Nach Aufruf dieser Methode bewegen sich
     * die Elemente in diesem ActorGroup wieder unabhängig voneinander.
     *
     * @see #fixate()
     * @see #isFixated()
     */
    @API
    public void freeFixation() {
        if (!lock) {
            throw new IllegalStateException("Die ActorGroup ist nicht fixiert.");
        }

        lock = false;

        // Alle Joints aus der Welt nehmen
        for (Joint joint : joints) {
            getScene().getWorldHandler().getWorld().destroyJoint(joint);
        }

        // Liste clear
        joints.clear();
    }

    /**
     * Gibt an, ob die Elemente in diesem ActorGroup gerade aneinander fixiert sind oder nicht.
     *
     * @return <code>true</code>, wenn die Elemente dieses Knotens gerade alle aneinander fixiert
     * sind. Sonst <code>false</code>.
     *
     * @see #fixate()
     * @see #freeFixation()
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
    public Actor[] getMembers() {
        return actors.toArray(new Actor[0]);
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
            synchronized (this.actors) {
                for (Actor actor : this.actors) {
                    actor.renderBasic(g, r);
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
        throw new RuntimeException("Bug! Eine ActorGroup kann nicht gerendert werden.");
    }

    /**
     * Setzt die Durchsichtigkeit für jedes angemeldete Objekt.
     *
     * @param opacity {@inheritDoc}
     */
    @Override
    @API
    public void setOpacity(float opacity) {
        forEach((actor) -> actor.setOpacity(opacity));
    }


    @Override
    protected PhysikHandler createDefaultPhysicsHandler(Shape shape) {
        return new KnotenHandler(this);
    }
}

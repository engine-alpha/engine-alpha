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

import ea.internal.collision.Collider;
import ea.internal.util.Logger;

import java.awt.*;
import java.util.ArrayList;

/**
 * Eine Actionfigur ist eine besondere Figur. Diese hat verschiedene <b>Zustände</b> und kann
 * verschiedene <b>Aktionen</b> durchführen. Das bedeutet: Jeder <b>Zustand</b> und jede
 * <b>Aktion</b> werden von einem eigenen <code>Figur</code>-Objekt dargestellt.
 * <p/>
 * Die Figur des aktuellen <b>Zustandes</b> wird normalerweise dargestellt.
 * <p/>
 * Wird eine <b>Aktion</b> ausgeführt, so wird die dazugehoerige Figur einmal durchanimiert.
 * Anschliessend kehrt die Figur in ihren <b>Zustand</b> zurück.
 * <p/>
 * <b>WICHTIG</b>:<br /> Damit eine Actionfigur <i>immer</i> ordnungsgemäß funktioniert
 * (Spiegelungen), sollten alle Figuren die selben Masße ("Pixel"-Hoehe/-Breite) haben.
 * <p/>
 * Es müssen nicht alle Felder ausgefüllt sein, damit Kollisionstests etc. immer funktionieren. Es
 * sei denn, die rechenintensive Arbeit wurde durch die Klasse <code>Game</code> ausgeschaltet
 * ({@link ea.Game#rechenintensiveArbeitSetzen(boolean)}).
 *
 * @author Michael Andonie
 */
public class ActionFigur extends Raum {
	/**
	 * Eine Liste mit allen Figuren (um die Animationsschritte zu machen).
	 */
	private static final ArrayList<ActionFigur> FIGUREN;

	static {
		FIGUREN = new ArrayList<>();

		// FIXME: 1 ms ist ein zu kleines Intervall und sorgt für die Performance-Schwierigkeiten bei vielen Figuren
		// Richtige Time nutzen? Wie schnell sind Time-Aufrufe in Java?

		Manager.standard.anmelden(new Ticker() {
			int runde = 0;

			public void tick () {
				runde++;

				try {
					for (ActionFigur f : FIGUREN) {
						f.animationsSchritt(runde);
					}
				} catch (Exception e) {
					// don't care (ConcurrentModification and NullPointer)
				}
			}
		}, 1);
	}

	/**
	 * Die Sammlung aller Zustände
	 */
	private Figur[] states;

	/**
	 * Die Namen der Zustände
	 */
	private String[] stateNames;

	/**
	 * Die Sammlung aller Aktionen
	 */
	private Figur[] actions;

	/**
	 * Die Namen der Aktionen
	 */
	private String[] actionNames;

	/**
	 * Der Index des aktuellen Zustandes
	 */
	private int indexState = 0;

	/**
	 * Der Index der zuletzt auszuführenden Aktion
	 */
	private int indexAction = 0;

	/**
	 * <code>true</code>, solange eine Aktion auszuführen ist
	 */
	private boolean performsAction = false;

	/**
	 * Konstruktor.
	 *
	 * @param zustand
	 * 		Der erste Zustand der Figur. Weitere Zustände können über die Methode {@link
	 * 		#neuerZustand(Figur, String)} angemeldet werden.
	 * @param name
	 * 		Der Name des ersten Zustandes. Weitere Zustände können über die Methode {@link
	 * 		#neuerZustand(Figur, String)} angemeldet werden.
	 * 		<p/>
	 * 		<b>Beim Namen wird die Groß- / Kleinschreibung ignoriert.</b>
	 */
	public ActionFigur (Figur zustand, String name) {
		this.states = new Figur[0];
		this.stateNames = new String[0];
		this.actions = new Figur[0];
		this.actionNames = new String[0];

		neuerZustand(zustand, name);
		FIGUREN.add(this);
	}

	/**
	 * Meldet einen neuen Zustand für diese Figur an.
	 *
	 * @param zustand
	 * 		Die Figur, die diesen Zustand beschreibt.
	 * @param name
	 * 		Der Name, unter dem dieser Zustand aufgerufen wird.
	 * 		<p/>
	 * 		<b>Beim Namen wird die Groß- / Kleinschreibung ignoriert.</b>
	 *
	 * @see #neueAktion(Figur, String)
	 */
	@API
	public void neuerZustand (Figur zustand, String name) {
		zustand.entfernen();

		if (this.states.length > 0) {
			zustand.positionSetzen(aktuelleFigur().position());
		}

		Figur[] statesBefore = this.states;
		String[] stateNamesBefore = this.stateNames;

		this.states = new Figur[statesBefore.length + 1];
		this.stateNames = new String[stateNamesBefore.length + 1];

		for (int i = 0; i < statesBefore.length; i++) {
			this.states[i] = statesBefore[i];
			this.stateNames[i] = stateNamesBefore[i];
		}

		this.states[this.states.length - 1] = zustand;
		this.stateNames[this.stateNames.length - 1] = name.toLowerCase();
	}

	/**
	 * Gibt die aktuelle Figur zurück.
	 *
	 * @return Die Figur, die gerade von dieser ActionFigur zu sehen ist.
	 */
	public Figur aktuelleFigur () {
		return performsAction ? actions[indexAction] : states[indexState];
	}

	/**
	 * Meldet eine neue Aktion für diese Figur an.
	 *
	 * @param action
	 * 		Die Figur, die diese Aktion beschreibt.
	 * @param name
	 * 		Der Name, unter dem diese Aktion aufgerufen wird.
	 * 		<p/>
	 * 		<b>Beim Namen wird die Groß- / Kleinschreibung ignoriert.</b>
	 *
	 * @see #neuerZustand(Figur, String)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void neueAktion (Figur action, String name) {
		action.entfernen();
		action.positionSetzen(aktuelleFigur().position());

		Figur[] actionsBefore = actions;
		String[] actionNamesBefore = actionNames;

		actions = new Figur[actionsBefore.length + 1];
		actionNames = new String[actionNamesBefore.length + 1];

		for (int i = 0; i < actionsBefore.length; i++) {
			actions[i] = actionsBefore[i];
			actionNames[i] = actionNamesBefore[i];
		}

		actions[actions.length - 1] = action;
		actionNames[actionNames.length - 1] = name.toLowerCase();
	}

	/**
	 * Versetzt diese Actionfigur in einen bestimmten Zustand.
	 * <p/>
	 * <i>Vollführt die Figur jedoch gerade eine <b>Aktion</b>, so ist der neue Zustand erst danach
	 * sichtbar.</i>
	 *
	 * @param name
	 * 		Der Name des Zustandes, in den die Figur versetzt werden soll. Dies ist der Name, der beim
	 * 		Anmelden des Zustandes mitgegeben wurde.
	 * 		<p/>
	 * 		<b>Beim Namen wird die Groß- / Kleinschreibung ignoriert.</b>
	 *
	 * @see #aktionSetzen(String)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void zustandSetzen (String name) {
		name = name.toLowerCase();

		for (int i = 0; i < stateNames.length; i++) {
			if (stateNames[i].equals(name)) {
				indexState = i;
				return;
			}
		}

		Logger.error("Achtung! Der Name des auszufuehrenden Zustandes wurde nie bei einer Anmeldung mitgegeben! " +
				"Der Name, der nicht unter den Zustaenden gefunden wurde war: " + name);
	}

	/**
	 * Versetzt diese Actionfigur in eine bestimmte Aktion.
	 *
	 * @param name
	 * 		Der Name der Aktion, die die Figur ausführen soll. Dies ist der Name, der beim Anmelden der
	 * 		Aktion mitgegeben wurde.
	 * 		<p/>
	 * 		<b>Beim Namen wird die Groß- / Kleinschreibung ignoriert.</b>
	 *
	 * @see #zustandSetzen(String)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void aktionSetzen (String name) {
		name = name.toLowerCase();

		for (int i = 0; i < actionNames.length; i++) {
			if (actionNames[i].equals(name)) {
				hatAktionSetzen(true);
				indexAction = i;
				return;
			}
		}

		Logger.error("Achtung! Der Name der auszufuehrenden Aktion wurde nie bei einer Anmeldung mitgegeben! " +
				"Der Name, der nicht unter den angemeldeten Aktionen gefunden wurde war: " + name);
	}

	/**
	 * Setzt, ob diese Figur zur Zeit eine Aktion hat.
	 * <p/>
	 * Diese Methode sollte <b>nicht</b> von außen aktiviert werden. Hierfuer gibt es:<br /> {@link
	 * #zustandSetzen(String)} und {@link #aktionSetzen(String)}.
	 *
	 * @param action
	 * 		Ob diese Figur gerade eine Aktion ausführt.
	 */
	@NoExternalUse
	public void hatAktionSetzen (boolean action) { // TODO auf private setzen?
		if (performsAction) {
			actions[indexAction].animationsBildSetzen(0);
		}

		performsAction = action;
	}

	/**
	 * Gibt den aktuellen Zustand dieser Action-Figur als <code>String</code> aus.
	 *
	 * @return Der Name des aktuellen Zustandes. Ist die Figur zur Zeit in einem <b>Zustand</b>, so
	 * ist dies der Name des aktuellen Zustandes, vollführt die Figur zur Zeit eine <b>Aktion</b>,
	 * so ist dies der Name der aktuellen Aktion.
	 */
	@API
	@SuppressWarnings ( "unused" )
	public String aktuellesVerhalten () {
		return performsAction ? actionNames[indexAction] : stateNames[indexState];
	}

	/**
	 * Spiegelt <b><u>alle</u> Figuren der <i>Zustände</i> und <i>Aktionen</i></b> dieser Figur an
	 * der X-Achse.
	 * <p/>
	 * <b>ACHTUNG!</b>
	 * <p/>
	 * Damit diese Methode <b>nicht zu ungewollten Missverständnissen</b> führt, sollte folgendes
	 * beachtet werden:<br /> <i>Sämtliche Figuren, die an dieser Action-Figur angemeldet sind,
	 * sollten <b>dieselben Maße haben</b>. Sie sollten also alle aus derselben Anzahl an
	 * "Unterquadraten" (gleiche Höhe und Breite) bestehen!</i>
	 * <p/>
	 * Ansonsten würde ein ungewolltes "Verschieben" der verschiedenen Action-Figur-Verhalten
	 * passieren.
	 *
	 * @param spiegel
	 * 		Ob alle angelegten Figuren (der <i>Zustände</i> und <i>Aktionen</i>) an der X-Achse
	 * 		gespiegelt werden sollen.
	 *
	 * @see Figur#spiegelXSetzen(boolean)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void spiegelXSetzen (boolean spiegel) {
		for (int i = 0; i < actions.length; i++) {
			actions[i].spiegelXSetzen(spiegel);
		}

		for (int i = 0; i < states.length; i++) {
			states[i].spiegelXSetzen(spiegel);
		}
	}

	/**
	 * Spiegelt <b><u>alle</u> Figuren der <i>Zustände</i> und <i>Aktionen</i></b> dieser Figur an
	 * der Y-Achse.
	 * <p/>
	 * <b>ACHTUNG!</b>
	 * <p/>
	 * Damit diese Methode <b>nicht zu ungewollten Missverständnissen</b> führt, sollte folgendes
	 * beachtet werden:<br /> <i>Sämtliche einzelnen Figuren, die an dieser Action-Figur angemeldet
	 * sind, sollten <b>dieselben Maße haben</b>. Sie sollten also alle aus derselben Anzahl an
	 * "Unterquadraten" (gleiche Höhe und Breite) bestehen!</i>
	 * <p/>
	 * Ansonsten würde ein ungewolltes "Verschieben" der verschiedenen Action-Figur-Verhalten
	 * passieren.
	 *
	 * @param spiegel
	 * 		Ob alle angelegten Figuren (der <i>Zustände</i> und <i>Aktionen</i>) an der Y-Achse
	 * 		gespiegelt werden sollen.
	 *
	 * @see Figur#spiegelYSetzen(boolean)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void spiegelYSetzen (boolean spiegel) {
		for (int i = 0; i < actions.length; i++) {
			actions[i].spiegelYSetzen(spiegel);
		}

		for (int i = 0; i < states.length; i++) {
			states[i].spiegelYSetzen(spiegel);
		}
	}

	/**
	 * Färbt <b>alle</b> Figuren dieser Action-Figur in eine Farbe ein.
	 *
	 * @param farbe
	 * 		Die Farbe, die alle Felder aller Figuren annehmen werden. Als Standardfarben-String.
	 *
	 * @see Figur#einfaerben(Farbe)
	 * @see #einfaerben(Farbe)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void einfaerben (String farbe) {
		einfaerben(Farbe.vonString(farbe));
	}

	/**
	 * Färbt <b>alle</b> Figuren dieser Action-Figur in eine Farbe ein.
	 *
	 * @param farbe
	 * 		Die Farbe, die alle Felder aller Figuren annehmen werden.
	 *
	 * @see Figur#einfaerben(Farbe)
	 * @see #einfaerben(String)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void einfaerben (Farbe farbe) {
		for (int i = 0; i < actions.length; i++) {
			actions[i].einfaerben(farbe);
		}

		for (int i = 0; i < states.length; i++) {
			states[i].einfaerben(farbe);
		}
	}

	/**
	 * Setzt den Größenfaktor <b>aller</b> anliegender Einzelfiguren dieser Action-Figur neu. Sowohl
	 * die einzelnen <i>Zustände</i> als auch die einzelnen <i>Aktionen</i>.
	 *
	 * @param faktor
	 * 		Der neue Größenfaktor
	 *
	 * @see Figur#faktorSetzen(int)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void faktorSetzen (int faktor) {
		for (int i = 0; i < actions.length; i++) {
			actions[i].faktorSetzen(faktor);
		}
		for (int i = 0; i < states.length; i++) {
			states[i].faktorSetzen(faktor);
		}
	}

	/**
	 * Vollführt einen Animationsschritt, sofern dies Sinn macht.
	 *
	 * @param runde
	 * 		Die aktuelle Runde
	 */
	private void animationsSchritt (int runde) {
		if (performsAction) {
			animationsActionSchritt(runde);
		} else {
			states[indexState].animationsSchritt(runde);
		}

		BoundingRechteck r = this.dimension();

		for (int i = 0; i < actions.length; i++) {
			actions[i].positionSetzen(r.x, r.y);
		}

		for (int i = 0; i < states.length; i++) {
			states[i].positionSetzen(r.x, r.y);
		}
	}

	/**
	 * Vollführt einen Animationsschritt für die aktuelle Aktion.
	 *
	 * @param runde
	 * 		Die aktuelle Runde
	 */
	private void animationsActionSchritt (int runde) {
		int curr = actions[indexAction].aktuellesBild();
		int last = actions[indexAction].animation().length - 1;

		if (curr == last) {
			if (runde % actions[indexAction].intervall() == 0) {
				hatAktionSetzen(false);
			}
		} else {
			actions[indexAction].animationsSchritt(runde);
		}
	}

	/**
	 * Gibt zurück, ob diese Action-Figur gerade eine Aktion ausführt.
	 * <p/>
	 * Eine Aktion ausführen ist ein sehr kurzlebiger Zustand, daher ist dies nie dauerhaft gegeben,
	 * es sei denn, der Befehl hierzu wird dauerhaft aufgerufen.
	 *
	 * @return <code>true</code>, wenn diese Action-Figur gerade eine Aktion ausführt, sonst
	 * <code>false</code>.
	 */
	@API
	@SuppressWarnings ( "unused" )
	public boolean vollfuehrtAktion () {
		return performsAction;
	}

	/**
	 * Zeichnet das Objekt.
	 *
	 * @param g
	 * 		Das zeichnende Graphics-Objekt
	 * @param r
	 * 		Das BoundingRechteck, das die Kameraperspektive repräsentiert.
	 * 		<p/>
	 * 		Hierbei soll zunächst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst
	 * 		dann gezeichnet werden.
	 */
	@Override
	public void zeichnen (Graphics2D g, BoundingRechteck r) {
		super.beforeRender(g, r);

		if (performsAction) {
			actions[indexAction].zeichnen(g, r);
		} else {
			states[indexState].zeichnen(g, r);
		}

		super.afterRender(g, r);
	}

	/**
	 * Berechnet ein minimales BoundingRechteck, das das Objekt <b>voll einschließt</b>.
	 *
	 * @return Ein BoundingRechteck mit dem minimal nötigen Umfang, um das Objekt <b>voll
	 * einzuschließen</b>.
	 *
	 * @see Raum#dimension()
	 */
	@API
	@Override
	public BoundingRechteck dimension () {
		return performsAction ? actions[indexAction].dimension() : states[indexState].dimension();
	}

	/**
	 * Verschiebt die Actionfigur.
	 *
	 * @param v
	 * 		Die Verschiebung als Objekt der Klasse <code>Vektor</code>
	 */
	@API
	@Override
	@SuppressWarnings ( "unused" )
	public void verschieben (Vektor v) {
		for (int i = 0; i < states.length; i++) {
			states[i].verschieben(v);
		}

		for (int i = 0; i < actions.length; i++) {
			actions[i].verschieben(v);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collider erzeugeCollider () {
		return states[0].erzeugeCollider();
	}

	/**
	 * Berechnet exakt die derzeitig von dieser Figur okkupierten Flächen auf der Zeichenebene.
	 *
	 * @return Ein Array aus allen Flächen, die von dieser Figur <b>exakt</b> ausgefüllt werden.
	 */
	@Override
	public BoundingRechteck[] flaechen () {
		if (performsAction) {
			return actions[indexAction].flaechen();
		}
		return states[indexState].flaechen();
	}
}
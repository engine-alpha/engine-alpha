/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 * 
 * Copyright (C) 2011 Michael Andonie
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

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

import ea.internal.util.Logger;

/**
 * Eine Actionfigur ist eine besondere Figur. Diese hat verschiedene <b>Zustaende</b> und kann verschiedene
 * <b>Aktionen</b> durchführen.
 * Das bedeutet: Jeder <b>Zustand</b> und jede <b>Aktion</b> werden von einem <code>Figur</code>-Objekt
 * dargestellt. <br />
 * Die Figur des aktuellen <b>Zustandes</b> wird normalerweise dargestellt.<br />
 * Wird ein <b>Aktion<b/> ausgefuehrt, so wird die dazugehoerige Figur einmal durchanimiert. Anschliessend
 * kehrt die Figur in ihren <b>Zustand</b> zurueck.<br />
 * <br />
 * <b>WICHTIG</b>:<br />
 * Damit eine Actionfigur <i>immer</i> ordnungsgemaess funktioniert (Spiegelungen), sollten alle Figuren die selben Masse ("Pixel"-Hoehe/-Breite) haben.<br />
 * Es muessen nicht alle Felder ausgefuellt sein, damit Kollisionstests etc. immer funktionieren. Es sei denn, die rechenintensive Arbeit
 * wurde durch die Klasse <code>Game</code> ausgeschaltet (Methode <code>rechenintensiveArbeitSetzen(...)</code> ).
 * 
 * @author Michael Andonie
 */
@SuppressWarnings("serial")
public class ActionFigur extends Raum {
	
	/**
	 * Die Sammlung aller Zustaende
	 */
	private Figur[] zustand;
	
	/**
	 * Die Namen der Zustaende
	 */
	private String[] zustandName;
	
	/**
	 * Die Sammlung aller Aktionen
	 */
	private Figur[] aktion;
	
	/**
	 * Der Name der Aktionen
	 */
	private String[] aktionName;
	
	/**
	 * Der Index des aktuellen ZUSTANDES
	 */
	private int index = 0;
	
	/**
	 * Der Index der zuletzt auszufuehrenden Aktion
	 */
	private int indexAction = 0;
	
	/**
	 * TRUE, solange eine Aktion auszufuehren ist.
	 */
	private boolean performsAction = false;
	
	/**
	 * Eine Liste mit allen Figuren (um die Animationsschritte zu machen).
	 */
	private static final ArrayList<ActionFigur> liste;
	
	static {
		liste = new ArrayList<ActionFigur>();
		Manager.standard.anmelden(new Ticker() {
			int runde = 0;
			
			public void tick() {
				runde++;
				try {
					for (ActionFigur f : liste) {
						f.animationsSchritt(runde);
					}
				} catch (ConcurrentModificationException e) {
					
				} catch (NullPointerException e) {
					
				}
			}
		}, 1);
	}
	
	/**
	 * Konstruktor. Erstellt eine Actionfigur
	 * 
	 * @param zustand1
	 *            Der erste Zustand der Figur. Weitere Zustaende koennen ueber die Methode <code>neuerZustand</code> angemeldet werden.
	 * @param zustandsName1
	 *            Der name des ersten Zustandes. Weitere Zustaende koennen ueber die Methode <code>neuerZustand</code> angemeldet werden.
	 */
	public ActionFigur(Figur zustand1, String zustandsName1) {
		zustand = new Figur[0];
		zustandName = new String[0];
		aktion = new Figur[0];
		aktionName = new String[0];
		neuerZustand(zustand1, zustandsName1);
		liste.add(this);
	}
	
	/**
	 * Meldet einen neuen Zustand fuer diese Figur an.
	 * 
	 * @param zustandFigur
	 *            Die Figur, die diesen Zustand beschreibt.
	 * @param name
	 *            Der Name, unter dem dieser Zustand aufgerufen wird.
	 * @see neueAktion(Figur, String)
	 */
	public void neuerZustand(Figur zustandFigur, String name) {
		zustandFigur.entfernen();
		if (zustand.length != 0) {
			zustandFigur.positionSetzen(aktuelleFigur().position());
		}
		Figur[] f = zustand;
		zustand = new Figur[f.length + 1];
		String[] s = zustandName;
		zustandName = new String[s.length + 1];
		for (int i = 0; i < f.length; i++) {
			zustand[i] = f[i];
			zustandName[i] = s[i];
		}
		zustand[zustand.length - 1] = zustandFigur;
		zustandName[zustandName.length - 1] = name;
	}
	
	/**
	 * Meldet eine neue Aktion fuer diese Figur an.
	 * 
	 * @param aktionsFigur
	 *            Die Figur, die diese Aktion beschreibt.
	 * @param name
	 *            Der Name, unter dem diese Aktion aufgerufen wird.
	 * @see neuerZustand(Figur, String)
	 */
	public void neueAktion(Figur aktionsFigur, String name) {
		aktionsFigur.entfernen();
		aktionsFigur.positionSetzen(aktuelleFigur().position());
		Figur[] f = aktion;
		aktion = new Figur[f.length + 1];
		String[] s = aktionName;
		aktionName = new String[s.length + 1];
		for (int i = 0; i < f.length; i++) {
			aktion[i] = f[i];
			aktionName[i] = s[i];
		}
		aktion[aktion.length - 1] = aktionsFigur;
		aktionName[aktionName.length - 1] = name;
	}
	
	/**
	 * Versetzt diese <i>Actionfigur</i> in einen bestimmten Zustand.<br />
	 * Vollfuehrt die Figur jedoch gerade eine <b>Aktion</b>, so ist der neue Zustand erst
	 * danach sichtbar.
	 * 
	 * @param name
	 *            Der Name des Zustandes, in den die Figur versetzt werden soll.
	 *            Dies ist der Name, der beim Anmelden des Zustandes mitgegeben wurde.
	 * @see aktionSetzen(String)
	 */
	public void zustandSetzen(String name) {
		String s = name.toLowerCase();
		for (int i = 0; i < zustandName.length; i++) {
			if (zustandName[i].toLowerCase().equals(s)) {
				index = i;
				return;
			}
		}
		
		Logger.error("Achtung! Der Name des auszufuehrenden Zustandes wurde nie bei einer Anmeldung mitgegeben! " +
				"Der Name, der nicht unter den Zustaenden gefunden wurde war: " + name);
	}
	
	/**
	 * Versetzt diese <i>Actionfigur</i> in eine bestimmte Aktion.
	 * 
	 * @param name
	 *            Der Name der Aktion, die die Figur ausfuehren soll.
	 *            Dies ist der Name, der beim Anmelden der Aktion mitgegeben wurde.
	 * @see zustandSetzen(String)
	 */
	public void aktionSetzen(String name) {
		String s = name.toLowerCase();
		for (int i = 0; i < aktionName.length; i++) {
			if (aktionName[i].toLowerCase().equals(s)) {
				hatAktionSetzen(true);
				indexAction = i;
				return;
			}
		}
		
		Logger.error("Achtung! Der Name der auszufuehrenden Aktion wurde nie bei einer Anmeldung mitgegeben! " +
				"Der Name, der nicht unter den angemeldeten Aktionen gefunden wurde war: " + name);
	}
	
	/**
	 * Setzt, ob diese Figur zur Zeit eine Aktion hat.<br />
	 * Diese Methode sollte <b>nicht</b> von aussen aktiviert werden. Hierfuer gibt es:<br />
	 * <code>zustandSetzen(String)</code> und <code>aktionSetzen(String)</code>.
	 * 
	 * @param action
	 *            Ob diese Figur gerade eine Aktion ausfuehrt.
	 */
	public void hatAktionSetzen(boolean action) {
		if (performsAction) {
			aktion[indexAction].animationsBildSetzen(0);
		}
		performsAction = action;
	}
	
	/**
	 * Gibt die aktuelle Figur zurueck.
	 * 
	 * @return Die Figur, die gerade von dieser ActionFigur zu sehen ist.
	 */
	public Figur aktuelleFigur() {
		if (performsAction) {
			return aktion[indexAction];
		} else {
			return zustand[index];
		}
	}
	
	/**
	 * Gibt den aktuellen Zustand dieser <i>Action-Figur</i> als <code>String</code> aus.
	 * 
	 * @return Der Name des aktuellen Zustandes. Ist die Figur zur Zeit in einem <b>Zustand</b>, so
	 *         ist dies der Name des aktuellen Zustandes, vollfuehrt die Figur zur Zeit eine <b>Aktion</b>, so
	 *         ist dies der Name der aktuellen Aktion.
	 */
	public String aktuellesVerhalten() {
		if (performsAction) {
			return aktionName[indexAction];
		} else {
			return zustandName[index];
		}
	}
	
	/**
	 * Spiegelt <b>ALLE Figuren der <i>Zustaende</i> und <i>Aktionen</i></b> dieser Figur an der X-Achse. <br />
	 * <br />
	 * <b>ACHTUNG!!!!</b><br />
	 * Damit diese Methode <b>nicht zu ungewollten Missverstaendnissen</b> fuehrt, sollte folgendes beachtet werden:<br />
	 * <i>Saemtliche einzelnen Figuren, die an dieser Action-Figur angemeldet sind, sollten <b>dieselben Masse haben</b>. Sie
	 * sollten also alle aus derselben Anzahl an "Unterquadraten" (gleiche hoehe * breite) bestehen!</i><br />
	 * <br />
	 * Ansonsten wuerde ein ungewolltes "Verschieben" der verschiedenen Action-Figur-Verhalten passieren.
	 * 
	 * @param spiegel
	 *            Ob alle angelegten Figuren (der <i>Zustaende</i> und <i>Aktionen</i>) an der X-Achse gespiegelt
	 *            werden sollen.
	 * @see Figur.spiegelXSetzen(boolean)
	 */
	public void spiegelXSetzen(boolean spiegel) {
		for (int i = 0; i < aktion.length; i++) {
			aktion[i].spiegelXSetzen(spiegel);
		}
		for (int i = 0; i < zustand.length; i++) {
			zustand[i].spiegelXSetzen(spiegel);
		}
	}
	
	/**
	 * Spiegelt <b>ALLE Figuren der <i>Zustaende</i> und <i>Aktionen</i></b> dieser Figur an der Y-Achse. <br />
	 * <br />
	 * <b>ACHTUNG!!!!</b><br />
	 * Damit diese Methode <b>nicht zu ungewollten Missverstaendnissen</b> fuehrt, sollte folgendes beachtet werden:<br />
	 * <i>Saemtliche einzelnen Figuren, die an dieser Action-Figur angemeldet sind, sollten <b>dieselben Masse haben</b>. Sie
	 * sollten also alle aus derselben Anzahl an "Unterquadraten" (gleiche hoehe * breite) bestehen!</i><br />
	 * <br />
	 * Ansonsten wuerde ein ungewolltes "Verschieben" der verschiedenen Action-Figur-Verhalten passieren.
	 * 
	 * @param spiegel
	 *            Ob alle angelegten Figuren (der <i>Zustaende</i> und <i>Aktionen</i>) an der Y-Achse gespiegelt
	 *            werden sollen.
	 * @see Figur.spiegelYSetzen(boolean)
	 */
	public void spiegelYSetzen(boolean spiegel) {
		for (int i = 0; i < aktion.length; i++) {
			aktion[i].spiegelYSetzen(spiegel);
		}
		for (int i = 0; i < zustand.length; i++) {
			zustand[i].spiegelYSetzen(spiegel);
		}
	}
	
	/**
	 * Faerbt <b>alle</b> Figuren dieser Action-Figur in eine Farbe ein.
	 * 
	 * @param f
	 *            Die Farbe, die alle Felder aller Figuren annehmen werden.
	 * @see Figur.einfaerben(Farbe)
	 * @see einfaerben(String)
	 */
	public void einfaerben(Farbe f) {
		for (int i = 0; i < aktion.length; i++) {
			aktion[i].einfaerben(f);
		}
		for (int i = 0; i < zustand.length; i++) {
			zustand[i].einfaerben(f);
		}
	}
	
	/**
	 * Faerbt <b>alle</b> Figuren dieser Action-Figur in eine Farbe ein.
	 * 
	 * @param farbe
	 *            Die Farbe, die alle Felder aller Figuren annehmen werden. Als Standardfarben-String.
	 * @see Figur.einfaerben(Farbe)
	 * @see einfaerben(Farbe)
	 */
	public void einfaerben(String farbe) {
		einfaerben(Farbe.vonString(farbe));
	}
	
	/**
	 * Setzt den Groessenfaktor <b>ALLLER</b> anliegender Einzel-Figuren dieser ActionFigur neu.<br />
	 * Sowohl die einzelnen <i>Zustaende</i> als auch die einzelnen <i>Aktionen</i>.
	 * 
	 * @param faktor
	 *            Der neue Groessenfaktor
	 * @see ea.Figur.faktorSetzen(int)
	 */
	public void faktorSetzen(int faktor) {
		for (int i = 0; i < aktion.length; i++) {
			aktion[i].faktorSetzen(faktor);
		}
		for (int i = 0; i < zustand.length; i++) {
			zustand[i].faktorSetzen(faktor);
		}
	}
	
	/**
	 * Vollfuehrt einen Animationsschritt, sofern dies Sinn macht.
	 * 
	 * @param runde
	 *            Die aktuelle Runde
	 */
	private void animationsSchritt(int runde) {
		if (performsAction) {
			if (aktion[indexAction].aktuellesBild() == aktion[indexAction].animation().length - 1) {
				if (runde % aktion[indexAction].intervall() == 0) {
					hatAktionSetzen(false);
				}
			} else {
				aktion[indexAction].animationsSchritt(runde);
			}
		} else {
			zustand[index].animationsSchritt(runde);
		}
		BoundingRechteck r = this.dimension();
		for (int i = 0; i < aktion.length; i++) {
			aktion[i].positionSetzen(r.x, r.y);
		}
		for (int i = 0; i < zustand.length; i++) {
			zustand[i].positionSetzen(r.x, r.y);
		}
	}
	
	/**
	 * Gibt zurueck, ob diese Action-Figur gerade eine Aktion ausfuehrt.<br />
	 * Eine Aktion ausfuehren ist ein sehr kurzlebiger Zustand, daher ist dies nie
	 * dauerhaft gegeben, es sei denn, der Befehl hierzu wird dauerhaft durchgegeben.
	 * 
	 * @return <code>true</code>, wenn diese Action-Figur gerade eine Aktion ausfuehrt,
	 *         andernfalls <code>false</code>.
	 */
	public boolean vollfuehrtAktion() {
		return performsAction;
	}
	
	/**
	 * Verschiebt die Actionfigur um eine bestimmte Verschiebung.
	 * 
	 * @param v
	 *            Die Verschiebung als Objekt der Klasse <code>Vektor</code>
	 */
	@Override
	public void verschieben(Vektor v) {
		for (int i = 0; i < zustand.length; i++) {
			zustand[i].verschieben(v);
		}
		for (int i = 0; i < aktion.length; i++) {
			aktion[i].verschieben(v);
		}
	}
	
	/**
	 * Zeichnet das Objekt.
	 * 
	 * @param g
	 *            Das zeichnende Graphics-Objekt
	 * @param r
	 *            Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br />
	 *            Hierbei soll zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann gezeichnet werden.
	 */
	@Override
	public void zeichnen(Graphics2D g, BoundingRechteck r) {
		super.beforeRender(g);
		
		if (performsAction) {
			aktion[indexAction].zeichnen(g, r);
		} else {
			zustand[index].zeichnen(g, r);
		}
		
		super.afterRender(g);
	}
	
	/**
	 * Test, ob ein anderes Raum-Objekt von diesem geschnitten wird.
	 * 
	 * @param r
	 *            Das Objekt, das auf Kollision mit diesem getestet werden soll.
	 * @return TRUE, wenn sich beide Objekte schneiden.
	 */
	@Override
	public boolean schneidet(Raum r) {
		BoundingRechteck[] mini = r.flaechen();
		BoundingRechteck[] eig = this.flaechen();
		for (int i = 0; i < mini.length; i++) {
			for (int j = 0; j < eig.length; j++) {
				if (mini[i].schneidetBasic(eig[j])) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @return Ein BoundingRechteck mit dem minimal noetigen Umfang, um das Objekt <b>voll einzuschliessen</b>.
	 * @see ea.Raum.dimension()
	 */
	@Override
	public BoundingRechteck dimension() {
		if (performsAction) {
			return aktion[indexAction].dimension();
		}
		return zustand[index].dimension();
	}
	
	/**
	 * Berechnet exakt die derzeitig von dieser Figur okkupierten Flaechen auf der Zeichenebene.
	 * 
	 * @return Ein Array aus allen Flaechen, die von dieser Figur EXAKT ausgefuellt werden.
	 */
	@Override
	public BoundingRechteck[] flaechen() {
		if (performsAction) {
			return aktion[indexAction].flaechen();
		}
		return zustand[index].flaechen();
	}
}
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

import ea.internal.ani.Animierer;
import ea.internal.ani.GeradenAnimierer;
import ea.internal.ani.KreisAnimierer;
import ea.internal.ani.StreckenAnimierer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Der Animationsmanager handelt benutzerfreundlich einfache Animationen.
 * <p/>
 * Er arbeitet intern mit unterschiedlichen Objekten der Klasse Animierer.<br /> Es können
 * theoretisch problemlos auch mehrere Animationen gekoppelt werden. Denn diese arbeitet nicht mit
 * {@link ea.Raum#positionSetzen(Punkt)} sondern mit relativen Verschiebungen über die Methode
 * {@link ea.Raum#verschieben(Vektor)}.
 *
 * @author Michael Andonie
 * @see Animierer
 */
public class AnimationsManager extends Manager implements AnimationsEndeReagierbar {
	/**
	 * Animationsmanager-Singleton
	 */
	private static AnimationsManager instanz;

	/**
	 * Eine Liste mit allen aktive Animierern.
	 */
	private List<Animierer> animierer = new CopyOnWriteArrayList<>();

	/**
	 * Konstruktor. Nur Intern benutzt, da Singleton.
	 */
	private AnimationsManager () {
		super();
	}

	/**
	 * Beendet sämtliche laufenden Animationen
	 */
	@API
	public static void neutralize () {
		getAnimationsManager().alleAbmelden();
	}

	/**
	 * Diese Methode gibt den <b>einen und einzigen existierenden</b> Animationsmanager aus.<br />
	 * Dies ist ein realisiserter <i>Singleton</i>.
	 *
	 * @return Der eine aktive Animationsmanager.
	 */
	@API
	public static AnimationsManager getAnimationsManager () {
		if (instanz == null) {
			instanz = new AnimationsManager();
		}

		return instanz;
	}

	/**
	 * Bis ins Lächerliche vereinfachte Methode zum zum Kreisanimieren.<br /> Hierbei wird nicht nur
	 * die gesamte Bewegung automatisch wiederholt und die Umlaufzeit auf 1,5 Sekunden
	 * voreingestellt, sondern auch noch der Mittelpunkt der Kreisbewegung automatisch ermittelt. Er
	 * wird sich 150 Koordinatenpunkte unterhalb des Mittelpunktes des zu animierenden Raum-Objektes
	 * befinden.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt. Hierbei ist dessen Zentrum auf der Kreisbahn.
	 *
	 * @see #kreisAnimation(Raum, Punkt, boolean, int)
	 * @see #kreisAnimation(Raum, Punkt, int)
	 * @see #kreisAnimation(Raum, Punkt)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void kreisAnimation (Raum ziel) {
		kreisAnimation(ziel, unterhalb(ziel));
	}

	/**
	 * Extrem vereinfachte Methode zum zum Kreisanimieren.<br /> Hierbei wird nicht nur die gesamte
	 * Bewegung automatisch wiederholt, sondern auch die Umlaufzeit auf 1,5 Sekunden
	 * voreingestellt.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt. Hierbei ist dessen Zentrum auf der Kreisbahn.
	 * @param zentrum
	 * 		Das Zentrum des Animationskreises
	 *
	 * @see #kreisAnimation(Raum, Punkt, boolean, int)
	 * @see #kreisAnimation(Raum, Punkt, int)
	 */
	@API
	public void kreisAnimation (Raum ziel, Punkt zentrum) {
		kreisAnimation(ziel, zentrum, 1500);
	}

	/**
	 * Interne Berechnungsmethode für die absolut standiesierte Methode von {@link
	 * ea.AnimationsManager#kreisAnimation(Raum)}.
	 *
	 * @param m
	 * 		Das Raum-Objekt, nach dem gerechnet wird.
	 *
	 * @return ein Punkt 150 Pixel unter dem Zentrum der Figur
	 */
	private static Punkt unterhalb (Raum m) {
		return m.zentrum().verschobenerPunkt(new Vektor(0, -150));
	}

	/**
	 * Vereinfachte Methode zum Kreisanimieren.<br /> Hierbei wird die gesamte Bewegung automatisch
	 * wiederholt.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt. Hierbei ist dessen Zentrum auf der Kreisbahn.
	 * @param zentrum
	 * 		Das Zentrum des Animationskreises
	 * @param umlaufzeit
	 * 		Gibt in <b>Millisekunden</b> an, wie lange eine Umdrehung um das Zentrum dauern soll.<br />
	 * 		<b>ACHTUNG:</b><br /> Dieser Wert muss groesser sein als <b>200</b>, da intern eine
	 * 		Umdrehung 200 Einzelschritte hat.
	 *
	 * @see #kreisAnimation(Raum, Punkt, boolean, int)
	 */
	@API
	public void kreisAnimation (Raum ziel, Punkt zentrum, int umlaufzeit) {
		kreisAnimation(ziel, zentrum, true, umlaufzeit);
	}

	/**
	 * Animiert ein Raum-Objekt auf einer Kreisbahn.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt. Hierbei ist dessen Zentrum auf der Kreisbahn.
	 * @param zentrum
	 * 		Das Zentrum des Animationskreises
	 * @param loop
	 * 		Gibt an, ob die Animation einfach ist oder nicht.
	 * @param umlaufzeit
	 * 		Gibt in <b>Millisekunden</b> an, wie lange eine Umdrehung um das Zentrum dauern soll.<br />
	 * 		<b>ACHTUNG:</b><br /> Dieser Wert muss groesser sein als <b>200</b>, da intern eine
	 * 		Umdrehung 200 Einzelschritte hat.
	 */
	@API
	public void kreisAnimation (Raum ziel, Punkt zentrum, boolean loop, int umlaufzeit) {
		this.kreisAnimation(ziel, zentrum, loop, umlaufzeit, true);
	}

    /**
     * Animiert ein Raum-Objekt auf einer Kreisbahn.
     *
     * @param ziel
     * 		Das zu animierende Raum-Objekt. Hierbei ist dessen Zentrum auf der Kreisbahn.
     * @param zentrum
     * 		Das Zentrum des Animationskreises
     * @param loop
     * 		Gibt an, ob die Animation einfach ist oder nicht.
     * @param umlaufzeit
     * 		Gibt in <b>Millisekunden</b> an, wie lange eine Umdrehung um das Zentrum dauern soll.<br />
     * 		<b>ACHTUNG:</b><br /> Dieser Wert muss groesser sein als <b>200</b>, da intern eine
     * 		Umdrehung 200 Einzelschritte hat.
     */
    @API
    public void kreisAnimation (Raum ziel, Punkt zentrum, boolean loop, int umlaufzeit, boolean uhrzeigersinn) {
        final KreisAnimierer k = new KreisAnimierer(ziel, zentrum, umlaufzeit, loop, this, this, uhrzeigersinn);

        animierer.add(k);
        k.starten();
    }

	/**
	 * Berechnet eine Zahl, die entweder die Eingabe selbst oder 1 ist, sofern die Eingabe kleiner
	 * als 1 ist.
	 *
	 * @param z
	 * 		Die einzugebende Zahl
	 *
	 * @return 1, wenn <code>z < 1</code>, sonst <code>z</code>.
	 */
	@NoExternalUse
	public static float intervall (float z) {
		return Math.max(z, 1);
	}

	/**
	 * Leicht vereinfachte Form der Streckanimationsmethode.<br /> Hierbei wird, sofern wiederholt
	 * wird, automatisch in einem Kreislauf animiert.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt. Sein Zentrum (ueber die Methode <code>zentrum()</code>)
	 * 		wird die Strecke abwandern (und natuerlich mit ihm die ganze Figur).
	 * @param laufDauer
	 * 		Die Zeit <b>in Millisekunden</b>, die vergeht, bis die Bewegung dieser Animation alle
	 * 		"Ettapen"-Punkte einmal abgegangen ist.
	 * @param wiederholen
	 * 		Gibt an, ob diese Animation in Dauerschleife wiederholt werden soll, oder ob sie nur einmal
	 * 		bis zum letzten Punkt sich abspielen soll, und anschliessend sich automatisch selbst
	 * 		beenden soll.
	 * @param strecke
	 * 		Eine beliebige Anzahl von "Ettappen"-Punkten. Das Zielobjekt wird sich zwischen diesen
	 * 		bewegen, wobei die Bewegung zwischen 2 Punkten immer gleich lang ist, unnabhaengig von
	 * 		ihrer Entfernung zueinander!<br /> Die Erste Ettappe ist das aktuelle Zentrum des
	 * 		Zielobjekts.
	 *
	 * @see #streckenAnimation(Raum, int, boolean, boolean, Punkt...)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void streckenAnimation (Raum ziel, int laufDauer, boolean wiederholen, Punkt... strecke) {
		streckenAnimation(ziel, laufDauer, wiederholen, wiederholen, strecke);
	}

	/**
	 * Animiert ein Raum-Objekt auf einer Strecke aus einer bestimmten Zahl linearer Teilstrecken.
	 * <p/>
	 * Ein <b>Anwendungsbeispiel:</b>
	 * <p/>
	 * <code> //Instanziiertes, nicht null-wertiges Raum-Objekt.<br /> Raum objekt;<br /> <br />
	 * //Erstellen eines Managers. In der Klasse "Game" wird bereits einer bereitgestellt (Muss also
	 * dort nicht extra instanziiert werden!!)<br /> AnimationsManager manager = new
	 * AnimationsManager();<br /> <br /> //Ausführen der Methode des Managers<br />
	 * manager.streckenAnimation(objekt, 4000, true, true, new Punkt(100, 100), new Punkt(200, 100),
	 * new Punkt(200, 200));<br /> </code><br /> Hierbei wird das Objekt auf der Strecke zwischen<br
	 * /> - Seinem Mittelpunkt<br /> - Dem Punkt (100|100)<br /> - Dem Punkt (200|100)<br /> - Und
	 * dem Punkt (200|200)<br /> bewegt und anschliessend wieder <b>zurueck zu seinem alten
	 * Mittelpunkt</b>, da sowohl <code>wiederholen</code> als auch <code>geschlossen true</code>
	 * ist. <br /> Diese Bewegung ("einmal den Kreislauf") dauert 4 Sekunden (= 4000
	 * Millisekunden).
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt. Sein Zentrum (ueber die Methode <code>zentrum()</code>)
	 * 		wird die Strecke abwandern (und natuerlich mit ihm die ganze Figur).
	 * @param laufDauer
	 * 		Die Zeit <b>in Millisekunden</b>, die vergeht, bis die Bewegung dieser Animation alle
	 * 		"Ettapen"-Punkte einmal abgegangen ist.
	 * @param wiederholen
	 * 		Gibt an, ob diese Animation in Dauerschleife wiederholt werden soll, oder ob sie nur einmal
	 * 		bis zum letzten Punkt sich abspielen soll, und anschliessend sich automatisch selbst
	 * 		beenden soll.
	 * @param geschlossen
	 * 		Gibt an, ob <b>bei Wiederholung</b> die Animation vom letzten wieder zum ersten Punkt
	 * 		laufen soll oder ob sie vom letzten zum vorletzten (usw) rueckwaerts weiterlaufen soll, bis
	 * 		zum Anfang und anschliessend wieder wechseln soll, also sozusagen immer wieder <b>hin und
	 * 		her</b> pendeln soll.<br /> Natuerlich ist dieser Parameter irrelevant, wenn <b>wiederholen
	 * 		<code>false</code> ist</b>.
	 * @param strecke
	 * 		Eine beliebige Anzahl von "Ettappen"-Punkten. Das Zielobjekt wird sich zwischen diesen
	 * 		bewegen, wobei die Bewegung zwischen 2 Punkten immer gleich lang ist, unnabhaengig von
	 * 		ihrer Entfernung zueinander!<br /> Die Erste Ettappe ist das aktuelle Zentrum des
	 * 		Zielobjekts.
	 */
	@API
	public void streckenAnimation (Raum ziel, int laufDauer, boolean wiederholen, boolean geschlossen, Punkt... strecke) {
		final StreckenAnimierer s = new StreckenAnimierer(ziel, wiederholen, geschlossen, this, laufDauer / strecke.length, this, strecke);

		animierer.add(s);
		s.starten();
	}

	/**
	 * Vereinfachte Form der Streckanimationsmethode.<br /> Hierbei wird automatisch in einem
	 * geschlossenen Kreislauf wiederholt.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt. Sein Zentrum (ueber die Methode <code>zentrum()</code>)
	 * 		wird die Strecke abwandern (und natuerlich mit ihm die ganze Figur).
	 * @param laufDauer
	 * 		Die Zeit <b>in Millisekunden</b>, die vergeht, bis die Bewegung dieser Animation alle
	 * 		"Ettapen"-Punkte einmal abgegangen ist.
	 * @param strecke
	 * 		Eine beliebige Anzahl von "Ettappen"-Punkten. Das Zielobjekt wird sich zwischen diesen
	 * 		bewegen, wobei die Bewegung zwischen 2 Punkten immer gleich lang ist, unnabhaengig von
	 * 		ihrer Entfernung zueinander!<br /> Die Erste Ettappe ist das aktuelle Zentrum des
	 * 		Zielobjekts.
	 *
	 * @see #streckenAnimation(Raum, int, boolean, boolean, Punkt...)
	 * @see #streckenAnimation(Raum, int, boolean, Punkt...)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void streckenAnimation (Raum ziel, int laufDauer, Punkt... strecke) {
		streckenAnimation(ziel, laufDauer, true, true, strecke);
	}

	/**
	 * Stark vereinfachte Form der Streckanimationsmethode.<br /> Hierbei wird die Animation
	 * automatisch in einem geschlossenen Kreislauf wiederholt und die Zeit fuer die Bewegung
	 * zwischen den einzelnen Punkten betraegt jeweils eine Sekunde.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt. Sein Zentrum (ueber die Methode <code>zentrum()</code>)
	 * 		wird die Strecke abwandern (und natuerlich mit ihm die ganze Figur).
	 * @param strecke
	 * 		Eine beliebige Anzahl von "Ettappen"-Punkten. Das Zielobjekt wird sich zwischen diesen
	 * 		bewegen, wobei die Bewegung zwischen 2 Punkten immer gleich lang ist, unnabhaengig von
	 * 		ihrer Entfernung zueinander!<br /> Die Erste Ettappe ist das aktuelle Zentrum des
	 * 		Zielobjekts.
	 *
	 * @see #streckenAnimation(Raum, int, boolean, boolean, Punkt...)
	 * @see #streckenAnimation(Raum, int, boolean, Punkt...)
	 * @see #streckenAnimation(Raum, int, Punkt...)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void streckenAnimation (Raum ziel, Punkt... strecke) {
		streckenAnimation(ziel, (strecke.length + 1) * 1000, true, true, strecke);
	}

	/**
	 * Noch staerker vereinfachte Variante der <code>geradenAnimation()</code>-Methode.<br /> Der
	 * Weg bis zum <code>orientierung</code>-Punkt wird in <b>einer Sekunde</b> abgegangen, und die
	 * Animation selbst dauert <b>2 Sekunden</b>.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt
	 * @param orientierung
	 * 		Der Punkt, durch den die Animationslinie verlaeuft.
	 *
	 * @see #geradenAnimation(Raum, Punkt, int, int)
	 * @see #geradenAnimation(Raum, Punkt, int)
	 */
	@API
	@SuppressWarnings ( "unused" )
	public void geradenAnimation (Raum ziel, Punkt orientierung) {
		geradenAnimation(ziel, orientierung, 1000);
	}

	/**
	 * Vereinfachte Variante der <code>geradenAnimation()</code>-Methode.<br /> HIer wird bereits
	 * voreingestellt, das die Animation andauert, bis das Objekt den Orientierungspunkt erreicht
	 * hat und noch einmal die selbe Strecke abgegangen ist (sprich, doppelt so lange laufzeit wie
	 * die als Argument mitgegebene <code>zielGeschwindigkeit</code>.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt
	 * @param orientierung
	 * 		Der Punkt, durch den die Animationslinie verlaeuft.
	 * @param zielGeschwindigkeit
	 * 		Die <b>Zeit, die vergeht</b> (in millisekunden), bis die Animation den Zielpunkt erreicht.
	 *
	 * @see #geradenAnimation(Raum, Punkt, int, int)
	 */
	@API
	public void geradenAnimation (Raum ziel, Punkt orientierung, int zielGeschwindigkeit) {
		geradenAnimation(ziel, orientierung, zielGeschwindigkeit, zielGeschwindigkeit * 2);
	}

	/**
	 * Animiert ein Objekt auf einer einfachen Halbgerade.<br /> Die Animation endet nach dem Ablauf
	 * ihrer ihr zugesprochenen Dauer (<b>in Millisekunden</b>).<br /> <br /> <b>Ein
	 * Beispiel:</b><br /> <br /> <code> //Das zu animierende, instanziierte Raum-Objekt<br /> Raum
	 * raum;<br /> <br /> //Der AnimationsManager. (Im Zweifelsfall bereits in der Klasse Game als
	 * Variable Vorhanden)<br /> AnimationsManager manager;<br /> <br /> //Die Animation
	 * einleiten<br /> manager.geradenAnimation(raum, new Punkt(300, 200), 1000, 3000);<br />
	 * </code><br /> Dies erstellt eine Geraden-Animation mit konstanter Geschwindigkeit, die:<br />
	 * - Vom Mittelpunkt des <code>raum</code>-Animationsobjekts anfaengt<br /> - Durch den Punkt
	 * (300|200) verlaeuft<br /> - Diesen Punkt nach 1000 Millisekunden (= 1 Sekunde) erreicht
	 * und<br /> - 3000 Millisekunden (= 3 Sekunden) andauert, und dann automatisch beendet wird.<br
	 * /> <br /> Diese Methode hat den Vorteil, das sie nie dauerhaft Speicherressourcen verbraucht,
	 * da keine dieser Animationen (im Gegensatz zu zB einer Kreis-Animation) unbegrenzt lange
	 * laufen kann.
	 *
	 * @param ziel
	 * 		Das zu animierende Raum-Objekt
	 * @param orientierung
	 * 		Der Punkt, durch den die Animationslinie verlaeuft.
	 * @param zielGeschwindigkeit
	 * 		Die <b>Zeit, die vergeht</b> (in millisekunden), bis die Animation den Zielpunkt erreicht.
	 * @param dauerInMS
	 * 		Die Dauer in Millisekunden, bis die Animation beendet wird. ISt diese geringer als die
	 * 		<code>zielGeschwindigkeit</code>, so erreicht die Animation nicht den Orientierungspunkt
	 * 		(der Eingabeparamter <code>orientierung</code>)
	 */
	@API
	public void geradenAnimation (Raum ziel, Punkt orientierung, int zielGeschwindigkeit, int dauerInMS) {
		GeradenAnimierer g = new GeradenAnimierer(ziel, orientierung, zielGeschwindigkeit, dauerInMS, this, this);
		animierer.add(g);
		g.starten();
	}

	/**
	 * Beendet <b>alle</b> Animationen von einem Raum-Objekt.<br /> Gibt es keine Animation von
	 * diesem, so passiert <b>gar nichts</b>.
	 *
	 * @param raum
	 * 		Das Raum-Objekt, dessen Animation(en) von diesem Manager beendet werden soll(en)
	 */
	@API
	public void animationBeendenVon (Raum raum) {
		for (Animierer a : animierer) {
			if (raum == a.ziel()) {
				a.beenden();
				animierer.remove(a);
			}
		}
	}

	/**
	 * Die implementierte <code>endeReagieren</code>-Methode.
	 * <p/>
	 * Hierin wird jede Referenz auf die beendete Animation gelöscht.
	 *
	 * @param an
	 * 		Die gerade geendetet Animation
	 */
	@Override
	public void endeReagieren (Animierer an) {
		animierer.remove(an);
	}
}

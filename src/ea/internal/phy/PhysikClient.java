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

package ea.internal.phy;

import ea.FallReagierbar;
import ea.Raum;
import ea.StehReagierbar;
import ea.Vektor;

/**
 * Ein Physik-Client ueberwacht ganz generell ein Raum-Objekt in der Physik.<br /> Es ist die
 * Ausgangsklasse fuer Gravitatoren und Passivatoren - aber auch fuer die NullClient-Klasse. Diese
 * ist die Standart-Physikklasse fuer nicht an der Physik beteiligte, neutrale Objekte.
 *
 * @author Michael Andonie
 */
public abstract class PhysikClient {
	/**
	 * Das Raum-Objekt, das ueberwacht wird
	 */
	protected final Raum ziel;

	/**
	 * Konstruktor.
	 *
	 * @param ziel
	 * 		Das zu ueberwachende Ziel
	 */
	protected PhysikClient (Raum ziel) {
		this.ziel = ziel;
	}

	/**
	 * Bewegt das ziel-Objekt innerhalb der kuenstlichen Physik.
	 *
	 * @param v
	 * 		Der die Bewegung beschreibende Vektor.
	 *
	 * @return <code>true</code>, wenn die Bewegung <i>in vollem Masse</i> moeglich war, sonst
	 * <code>false</code>.
	 */
	public abstract boolean bewegen (Vektor v);

	/**
	 * Diese Methode wird immer dann aufgerufen, wenn ein Client nicht weiter benoetigt wird, und er
	 * alle seine Funktionen beenden soll, um die von ihm belegten Ressourcen freizugeben.
	 */
	public abstract void aufloesen ();

	/**
	 * Laesst das anliegende Objekt einen Sprung vollfuehren, <b>wenn es ein Aktiv-Objekt ist</b>.
	 * Sonst ist dieser Methodenaufruf wirkungslos.
	 *
	 * @param kraft
	 * 		Die Sprungkraft.
	 *
	 * @return <code>true</code>, wenn erfolgreich gesprungen wurde. In allen anderen Faellen
	 * <code>false</code>.
	 */
	public abstract boolean sprung (int kraft);

	/**
	 * Setzt, ob das Ziel-Objekt von Schwerkraft beeinflusst wird.<br /> Der Aufruf dieser Methode
	 * ist nur bei Gravitatoren sinnvoll. Bei allen anderen <code>PhysikClient</code>-Klassen wird
	 * hierbei eine Fehlermeldung ausgegeben.
	 *
	 * @param aktiv
	 * 		Ist dieser Wert <code>true</code>, so wird dieses Objekt von Schwerkraft beeinflusst. Ist
	 * 		er <code>false</code>, dann nicht.
	 */
	public abstract void schwerkraftAktivSetzen (boolean aktiv);

	/**
	 * Diese Methode setzt die kritische Tiefe eines Aktiv-Objektes. Ab dieser wird das
	 * entsprechende <code>FallReagierbar</code>-Inteface, <b>das angemeldet wurde</b>, ueber den
	 * Fall informiert.
	 *
	 * @param tiefe
	 * 		Die Tiefe, ab der das anliegende <code>FallReagierbar</code>-Interface informiert werden
	 * 		soll.
	 *
	 * @see #fallReagierbarAnmelden(FallReagierbar, int)
	 */
	public abstract void kritischeTiefeSetzen (int tiefe);

	/**
	 * In dieser Methode wird der <code>FallReagierbar</code>-Listener angemeldet.<br /> Dieser wird
	 * ab sofort <i>immer wieder</i> informiert, solange das Ziel-<code>Raum</code>-Objekt unterhalb
	 * der Toleranzgrenze liegt.
	 *
	 * @param f
	 * 		Das <code>FallReagierbar</code>-Objekt, das ab sofort im Grenzfall informiert wird.
	 * @param tiefe
	 * 		Die kritische Tiefe, ab der das Interface informiert wird.
	 *
	 * @see kritischeTiefeSetzen
	 */
	public abstract void fallReagierbarAnmelden (FallReagierbar f, int tiefe);

	/**
	 * In dieser Methode wird der <code>StehReagierbar</code>-Listener angemeldet.<br /> Dieser wird
	 * ab sofort immer dann <i>einmalig</i> informiert, wenn das Ziel-<code>Raum</code>-Objekt nach
	 * einem Sprung/Fall wieder auf einem Passiv-Objekt steht.
	 *
	 * @param s
	 * 		Das <code>StehReagierbar</code>-Objekt, das ab sofort immer einmalig informiert wird, wenn
	 * 		das Ziel-Objekt zum Stehen kommt.
	 */
	public abstract void stehReagierbarAnmelden (StehReagierbar s);

	/**
	 * Gibt an, ob das Ziel-<code>Raum</code>-Objekt auf einem Passiv-Objekt steht oder nicht.
	 *
	 * @return <code>true</code>, wenn das Ziel-<code>Raum</code>-Objekt auf einem Passiv-Objekt
	 * steht, sonst <code>false</code>.
	 */
	public abstract boolean steht ();

	/**
	 * Gibt das Ziel-Objekt aus.
	 *
	 * @return Das Ziel dieses Physik-Clients.
	 */
	public Raum ziel () {
		return ziel;
	}

	/**
	 * Setzt die Schwerkraft fuer dieses Objekt.<br /> <b>Achtung:</b> Standardwert: 4<br />
	 * Groesserer Wert = langsamer Fallen<br /> Kleinerer Wert = schneller Fallen <br /> Negativer
	 * Wert : Moege Gott uns allen gnaedig sein...
	 *
	 * @param schwerkraft
	 * 		Der Wert fuer die Schwerkraft der Physik.<br /> <b>Wichtig:</b> Dies repraesentiert
	 * 		<i>keinen</i> Wert fuer die (Erd-) Beschleunigungszahl "g" aus der Physik. Schon allein
	 * 		deshalb, weil die Zahl umgekehrt wirkt (s. oben).
	 *
	 * @see ea.Raum#aktivMachen()
	 */
	public abstract void schwerkraftSetzen (int schwerkraft);

	/**
	 * Berechnet einen <b>neuen Impuls</b> auf das Client-Objekt.
	 *
	 * @param impuls
	 * 		der neue Impuls, der auf das Objekt wirken soll. <b>(in [kg* (m / s)])</b>
	 */
	public abstract void impulsHinzunehmen (Vektor impuls);

	/**
	 * Addiert eine Geschwindigkeit v' zur aktuellen Geschwindigkeit v. Die neue Geschwindigkeit des
	 * Client-Objekts ist damit:<br /> <code>v_neu = v + v'</code>
	 *
	 * @param geschwindigkeit
	 * 		Die neue Geschwindigkeit v', die zur aktuellen Geschwindigkeit v hinzuaddiert werden
	 * 		soll.<b>(in [m / s])</b>
	 */
	public abstract void geschwindigkeitHinzunehmen (Vektor geschwindigkeit);

	/**
	 * @return Der Luftwiderstandskoeffizient
	 */
	public abstract float getLuftwiderstandskoeffizient ();

	/**
	 * @return ob das Objekt beeinflussbar ist.
	 */
	public abstract boolean istBeeinflussbar ();

	/**
	 * @return Die Masse des Objekts.
	 */
	public abstract float getMasse ();

	/**
	 * @return Die aktuelle Kraft, die auf das Objekt wirkt.
	 */
	public abstract Vektor getForce ();

	/**
	 * Setzt den Luftwiderstandskoeffizienten für das Client-Objekt. Dieser bestimmt, <b>wie
	 * intensiv der Luftwiderstand das Objekt beeinträchtigt</b>. Je höher dieser Wert ist, desto
	 * <i>stärker</i> ist der Luftwiderstand. Ist er 0, gibt es <i>keinen</i> Luftwiderstand.
	 *
	 * @param luftwiderstandskoeffizient
	 * 		Der Luftwiderstandskoeffizient. Darf nicht kleiner als 0 sein!
	 */
	public abstract void luftwiderstandskoeffizientSetzen (float luftwiderstandskoeffizient);

	/**
	 * Setzt, ob das Objekt ab sofort beeinflussbar sein soll. <br/> Das bedeutet:<br /> <ul>
	 * <li>Beeinflussbare Objekte lassen sich verschieben.</li> <li>Unbeeinflussbare Objekte werden
	 * von Impulsen nicht beeindruckt und geben ihn so wie er ist zurück.</li> <li>Unbeeinflussbare
	 * Objekte sind, Wände, Decken, Ebenen, beeinflussbare sind meist Spielfiguren.</li> <li>Auch
	 * unbeeinflussbare Objekte sind <b>bewegbar und man kann Kräfte/Impulse auf sie
	 * Auswirken</b>.</li> <li>Kollidiert ein beeinflussbares Objekt mit einem nicht beeinflussbaren
	 * Objekt, so blockiert das unbeeinflussbare Objekt das beeinflussbare Objekt. Letzteres prallt
	 * evtl. leicht ab.</li> <li>Kollidieren 2 beeinflussbare Objekte, so prallen sie voneinander
	 * ab.</li> <li>Kollidieren 2 unbeeinflussbare Objekte, so passiert gar nichts. Ggf.
	 * durchschneiden sie sich gegenseitig.</li> </ul>
	 *
	 * @param beeinflussbar
	 * 		ist dieser Wert <code>true</code>, ist das Objekt ab sofort beeinflussbar. Sonst ist es
	 * 		nicht beeinflussbar.
	 */
	public abstract void beeinflussbarSetzen (boolean beeinflussbar);

	/**
	 * Setzt die Masse des Clien-Objekts neu. Das kann auch mitten im Spiel geändert werden. Die
	 * Masse bestimmt zum Beispiel, wie sich das Objekt bei Kollisionen oder einem neuen Impuls
	 * verhält.
	 *
	 * @param masse
	 * 		die neue Masse des Client-Objekts.<b>(in [kg])</b>
	 */
	public abstract void masseSetzen (float masse);

	/**
	 * Setzt <b>hart</b> die <b>konstante</b> Kraft, die auf das Client-Objekt wirkt. Das bedeutet,
	 * dass die vorher gegoltene Kraft gelöscht wird ohne Rücksicht auf mögliche
	 * Implikationen/Probleme.
	 *
	 * @param kraft
	 * 		Die neue Kraft, die auf das Client-Objekt wirken soll.<b>(in [m / s^2] = [N])</b>
	 */
	public abstract void kraftSetzen (Vektor kraft);

	/**
	 * Setzt <b>hart</b> die Geschwindigkeit des Client-Objekts. Das bedeutet, dass die vorher
	 * gegoltene Geschwindikeit gelöscht wird ohne Rücksicht auf mögliche Implikationen/Probleme.
	 *
	 * @param geschwindigkeit
	 * 		Die neue Geschwindigkeit für das Client-Objekt. <b>(in [m / s])</b>
	 */
	public abstract void geschwindigkeitSetzen (Vektor geschwindigkeit);

	/**
	 * Setzt alle Einfluesse auf das Client-Objekt zurück. Dies sind: <ul> <li>Die Kraft F, die
	 * gerade auf das Objekt wirkt.</li> <li>Die Geschwindigkeit v, die das Objekt gerade hat.</li>
	 * </ul>
	 */
	public abstract void einfluesseZuruecksetzen ();

	/**
	 * Wendet eine Kraft für einen bestimmten Zeitraum auf das Client-Objekt an. Hierdurch entsteht
	 * ein <b>neuer Impuls</b> auf das Objekt, der dessen Geschwindigkeit (und Richtung) ändern
	 * kann.<br /> Wichtig: Dies ist eine <i>Heuristik</i>: Die Dauer sein <i>genügend klein</i> und
	 * die Kraft <i>konstant</i>, solange sie wirkt. Die rein physikalische Rechnung wäre wesentlich
	 * rechenintensiver.
	 *
	 * @param kraft
	 * 		Die Kraft, die auf das Objekt anliegen soll. <b>(in [kg* (m / s^2)] = [N])</b>
	 * @param t_kraftuebertrag
	 * 		Die Dauer, für die die Kraft auf das Objekt wirkt. <b>(in [s)])</b>
	 */
	public abstract void kraftAnwenden (Vektor kraft, float t_kraftuebertrag);
}

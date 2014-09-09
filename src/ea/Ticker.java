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

import java.io.Serializable;

/**
 * Eine Ticker zeichnet sich durch eine ausführbare Methode aus, die in einem gewissen Zeitabstand
 * immer wieder ausgeführt wird, bis entsprechend dagegen vorgegangen wird.<br /> Ein Ticker kann in
 * der Klasse {@link ea.Manager} angemeldet werden und dort gestartet werden, unter Eingabe des
 * Warteintervalls.<br /><br />
 * <p/>
 * Ticker werden auch innerhalb der Engine benutzt, zur organisierten, threadsparenden Animation.<br
 * /> Die Benutzung eines Tickers wird in der Methodenbeschreibung bestens erklärt.
 *
 * @author Michael Andonie
 */
public interface Ticker extends Serializable {
	/**
	 * Die bei jedem "Tick" auszuführende Methode.<br /> Diese wird wiederholt in einem bestimmten
	 * Intervall aufgerufen, hierin können also Dinge wie statische Bewegungen oder regelmäßige
	 * Überprüfungen realisiert werden.<br /> <b>Anmeldung eines Tickers:</b> Bei einem
	 * Managerobjekt wird folgende Methode aufgerufen:<br/> <br/> <code>int meinInterall = 50; //Der
	 * Ticker wird alle 50 Millisekunden aufgerufen<br /> managerObjekt.anmelden(meinTicker);<br/>
	 * managerObjekt.starten(meinTicker, meinIntervall);</code><br /> <br/> Es geht auch
	 * einfacher:<br /> <code>managerObjekt.anmelden(meinTicker, meinIntervall); // Macht genau
	 * dasselbe, aber in nur einer Methode</code><br /> <br /> Aber: An einem Manager kann niemals
	 * derselbe Ticker mehrfach angemeldet werden! <br /><br /> <b>Achtung: In diese Methode sollten
	 * keine langwierigen Prozesse gelagert werden!</b><br /> Allerdings trifft dies nur auf die
	 * allerwenigsten Prozesse zu, wie zum Beispiel komplizierte Berechnungsalgorythmen wie
	 * <code>Math.sin()</code> oder ähnliches.<br /> Mehrere Bewegungen lassen sich schnell und
	 * problemlos organisieren, zudem eine Toleranz von einer Millisekunde (meist ausreichend lang)
	 * garantiert ist.
	 */
	public abstract void tick ();
}

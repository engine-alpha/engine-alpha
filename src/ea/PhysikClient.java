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

/**
 * Ein Physik-Client ueberwacht ganz generell ein Raum-Objekt in der Physik.<br />
 * Es ist die Ausgangsklasse fuer Gravitatoren und Passivatoren - aber auch fuer die NullClient-Klasse.
 * Diese ist die Standart-Physikklasse fuer nicht an der Physik beteiligte, neutrale Objekte
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
	 *            Das zu ueberwachende Ziel
	 */
	protected PhysikClient(Raum ziel) {
		this.ziel = ziel;
	}
	
	/**
	 * Bewegt das ziel-Objekt innerhalb der kuenstlichen Physik.
	 * 
	 * @param v
	 *            Der die Bewegung beschreibende Vektor.
	 * @return <code>true</code>, wenn die Bewegung <i>in vollem Masse</i> moeglich war, sonst <code>false</code>.
	 */
	public abstract boolean bewegen(Vektor v);
	
	/**
	 * Diese Methode wird immer dann aufgerufen, wenn ein Client nicht weiter benoetigt
	 * wird, und er alle seine Funktionen beenden soll, um die von ihm belegten Ressourcen
	 * freizugeben.
	 */
	public abstract void aufloesen();
	
	/**
	 * Laesst das anliegende Objekt einen Sprung vollfuehren, <b>wenn es ein Aktiv-Objekt ist</b>.
	 * Sonst ist dieser Methodenaufruf wirkungslos.
	 * 
	 * @param kraft
	 *            Die Sprungkraft.
	 * @return <code>true</code>, wenn erfolgreich gesprungen wurde. In allen anderen Faellen <code>false</code>.
	 */
	public abstract boolean sprung(int kraft);
	
	/**
	 * Setzt, ob das Ziel-Objekt von Schwerkraft beeinflusst wird.<br />
	 * Der Aufruf dieser Methode ist nur bei Gravitatoren sinnvoll. Bei allen anderen <code>PhysikClient</code>-Klassen
	 * wird hierbei eine Fehlermeldung ausgegeben.
	 * 
	 * @param aktiv
	 *            Ist dieser Wert <code>true</code>, so wird dieses Objekt von Schwerkraft
	 *            beeinflusst. Ist er <code>false</code>, dann nicht.
	 */
	public abstract void schwerkraftAktivSetzen(boolean aktiv);
	
	/**
	 * Diese Methode setzt die kritische Tiefe eines Aktiv-Objektes. Ab dieser wird das entsprechende <code>FallReagierbar</code>-Inteface,
	 * <b>das angemeldet wurde</b>, ueber den Fall informiert.
	 * 
	 * @param tiefe
	 *            Die Tiefe, ab der das anliegende <code>FallReagierbar</code>-Interface informiert werden soll.
	 * @see fallReagierbarAnmelden(FallReagierbar, int)
	 */
	public abstract void kritischeTiefeSetzen(int tiefe);
	
	/**
	 * In dieser Methode wird der <code>FallReagierbar</code>-Listener angemeldet.<br />
	 * Dieser wird ab sofort <i>immer wieder</i> informiert, solange das Ziel-<code>Raum</code>-Objekt unterhalb der
	 * Toleranzgrenze liegt.
	 * 
	 * @param f
	 *            Das <code>FallReagierbar</code>-Objekt, das ab sofort im Grenzfall informiert wird.
	 * @param tiefe
	 *            Die kritische Tiefe, ab der das Interface informiert wird.
	 * @see kritischeTiefeSetzen
	 */
	public abstract void fallReagierbarAnmelden(FallReagierbar f, int tiefe);
	
	/**
	 * In dieser Methode wird der <code>StehReagierbar</code>-Listener angemeldet.<br />
	 * Dieser wird ab sofort immer dann <i>einmalig</i> informiert, wenn das Ziel-<code>Raum</code>-Objekt nach einem
	 * Sprung/Fall wieder auf einem Passiv-Objekt steht.
	 * 
	 * @param s
	 *            Das <code>StehReagierbar</code>-Objekt, das ab sofort immer einmalig informiert wird, wenn das Ziel-Objekt
	 *            zum Stehen kommt.
	 */
	public abstract void stehReagierbarAnmelden(StehReagierbar s);
	
	/**
	 * Gibt an, ob das Ziel-<code>Raum</code>-Objekt auf einem Passiv-Objekt steht oder nicht.
	 * 
	 * @return <code>true</code>, wenn das Ziel-<code>Raum</code>-Objekt auf einem Passiv-Objekt steht, sonst <code>false</code>.
	 */
	public abstract boolean steht();
	
	/**
	 * Gibt das Ziel-Objekt aus.
	 * 
	 * @return Das Ziel dieses Physik-Clients.
	 */
	public Raum ziel() {
		return ziel;
	}
	
	/**
	 * Setzt die Schwerkraft fuer dieses Objekt.<br />
	 * <b>Achtung:</b>
	 * Standardwert: 4<br />
	 * Groesserer Wert = langsamer Fallen<br />
	 * Kleinerer Wert = schneller Fallen <br />
	 * Negativer Wert : Moege Gott uns allen gnaedig sein...
	 * 
	 * @param schwerkraft
	 *            Der Wert fuer die Schwerkraft der Physik.<br />
	 *            <b>Wichtig:</b> Dies repraesentiert <i>keinen</i> Wert fuer die (Erd-)
	 *            Beschleunigungszahl "g" aus der Physik. Schon allein deshalb, weil die
	 *            Zahl umgekehrt wirkt (s. oben).
	 * @see ea.Raum#aktivMachen()
	 */
	public abstract void schwerkraftSetzen(int schwerkraft);
}

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

package ea.internal.phy;

import ea.BoundingRechteck;
import ea.FallReagierbar;
import ea.Knoten;
import ea.Raum;
import ea.StehReagierbar;
import ea.Vektor;
import ea.internal.util.Logger;

/**
 * Ein Passivator ueberwacht und steuert ein passives Objekt in der Physik.
 * 
 * @author Michael Andonie
 */
public class Passivator
		extends PhysikClient {
	
	/**
	 * Das Inertialsystem dieses Passivators
	 */
	private volatile Knoten system;
	
	/**
	 * Die Aktive Physik
	 */
	private final Physik physik = Physik.getPhysik();
	
	/**
	 * Konstruktor.
	 * 
	 * @param ziel
	 *            Das zu ueberwachende Raum-Objekt
	 * @param p
	 *            Die Aktive Physik
	 */
	public Passivator(Raum ziel) {
		super(ziel);
		(system = new Knoten()).add(ziel);
		physik.passivAnmelden(this);
	}
	
	/**
	 * Realisiert das Bewegen
	 * 
	 * @param v
	 *            Die Bewegung als Vektor.
	 * @return <code>true</code>, da die Bewegung eines Passiv-Objektes immer in vollem Masse moeglich ist.
	 */
	@Override
	public boolean bewegen(Vektor v) {
		xVersch((int) v.x);
		yVersch((int) v.y);
		return true;
	}
	
	/**
	 * Vollfuehrt die einzelnen Schritte fuer die Y-Verschiebung.
	 * 
	 * @param dY
	 *            Die Y-Aenderung
	 */
	public void yVersch(int dY) {
		int z;
		if (dY > 0) {
			z = 1;
		} else if (dY < 0) {
			z = -1;
			dY = -dY;
		} else {
			return;
		}
		Vektor v = new Vektor(0, z);
		for (int i = 0; i < dY; i++) {
			system.leerenOhnePhysikAbmelden();
			system.add(ziel);
			BoundingRechteck test = ziel.dimension().verschErhoeht(Vektor.OBEN, 1);
			physik.alleAktivenTestenUndEinsetzenOhne(system, test, v, this);
			system.verschieben(v);
		}
	}
	
	/**
	 * Vollfuehrt die einzelen Schritte fuer die Y-Verschiebung.
	 * 
	 * @param dX
	 *            Die X-Aenderung
	 */
	public void xVersch(int dX) {
		int z;
		if (dX > 0) {
			z = 1;
		} else if (dX < 0) {
			z = -1;
			dX = -dX;
		} else {
			return;
		}
		Vektor v = new Vektor(z, 0);
		Vektor testV = new Vektor(z, -1);
		for (int i = 0; i < dX; i++) {
			system.leerenOhnePhysikAbmelden();
			system.add(ziel);
			BoundingRechteck test = ziel.dimension().verschErhoeht(testV, 1);
			physik.alleAktivenTestenUndEinsetzen(system, test, v);
			system.verschieben(v);
		}
	}
	
	/**
	 * Prueft, ob ein BoundingRechteck sich mit dem Zielobjekt schneidet
	 * 
	 * @param r
	 *            Das Pruef-Rechteck
	 * @return <code>true</code>, wenn sich das Ziel mit dem Argument schneidet,
	 *         sonst <code>false</code>.
	 */
	public boolean in(BoundingRechteck r) {
		return r.schneidetBasic(ziel.dimension());
	}
	
	/**
	 * Diese Methode wird immer dann aufgerufen, wenn ein Client nicht weiter benoetigt
	 * wird, und er alle seine Funktionen beenden soll, um die von ihm belegten Ressourcen
	 * freizugeben.
	 */
	@Override
	public void aufloesen() {
		physik.passivAbmelden(this);
	}
	
	/**
	 * Die ueberschriebene Sprung-Methode. Da sie nicht benoetigt wird, wird hierbei
	 * eine Fehlermeldung ausgegeben. Denn es wurde ein oassives Objekt zum Sprung
	 * gebracht.
	 * 
	 * @param kraft
	 *            Die (theoretische) Sprungkraft.
	 */
	@Override
	public boolean sprung(int kraft) {
		Logger.error("Achtung! Ein fuer die Physik passives Objekt wurde zum Sprung gezwungen. Es passiert nichts.");
		
		return false;
	}
	
	/**
	 * Diese Methode soll setzen, ob Schwerkraft aktiv ist. Dies macht jedoch bei einem Passiv-Objekt keinen Sinn.<br />
	 * Daher wird nur eine Fehlermeldung ausgegeben.
	 * 
	 * @param aktiv
	 *            Ob die Schwerkraft aktiv sein soll. Ist jedoch hier absolut irrelevant.
	 */
	@Override
	public void schwerkraftAktivSetzen(boolean aktiv) {
		Logger.error("Achtung! Das Objekt, bei dem der Einfluss der Schwerkraft gesetzt werden sollte, ist ein passives " +
				"Objekt. Folglich macht der Aufruf dieser Methode keinen Sinn. Dafuer muesste das entsprechende Objekt ein Aktiv-Objekt sein!");
	}
	
	/**
	 * Diese Methode setzt die kritische Tiefe eines Aktiv-Objektes. Aber hierin wird nur eine Fehlermeldung ausgegeben,
	 * da bei dieser Klasse ein passives Objekt vorliegt, das keine kritische Falltiefe haben kann
	 * 
	 * @param tiefe
	 *            Die Tiefe, ab der das anliegende <code>FallReagierbar</code>-Interface informiert werden soll.
	 * @see #fallReagierbarAnmelden(FallReagierbar, int)
	 */
	@Override
	public void kritischeTiefeSetzen(int tiefe) {
		Logger.error("Achtung! Das Raum-Objekt, dem eine kritische Tiefe gegeben werden sollte, ist kein Aktiv-Objekt, sondern passiv! " +
				"Bitte erst dieses Objekt aktiv machen, dann diese Methode aufrufen!");
	}
	
	/**
	 * In dieser Methode wird der <code>FallReagierbar</code>-Listener angemeldet.<br />
	 * Aber hierin wird nur eine Fehlermeldung ausgegeben,
	 * da bei dieser Klasse ein passives Objekt vorliegt, das keine kritische Falltiefe haben kann.
	 * 
	 * @param f
	 *            Das <code>FallReagierbar</code>-Objekt, das ab sofort im Grenzfall informiert wird.
	 * @param tiefe
	 *            Die kritische Tiefe, ab der das Interface informiert wird.
	 * @see kritischeTiefeSetzen
	 */
	@Override
	public void fallReagierbarAnmelden(FallReagierbar f, int tiefe) {
		Logger.error("Achtung! Das Raum-Objekt, dem ein FallReagierbar-Listener zugewiesen werden sollte, ist kein Aktiv-Objekt, sondern passiv! " +
				"Bitte erst dieses Objekt aktiv machen, dann diese Methode aufrufen!");
	}
	
	/**
	 * In diese Methode wird ein <code>StehReagierbar</code>-Listener angemeldet.<br />
	 * Aber in dieser Klasse wird nur eine Fehlermeldung ausgegeben, da das zu ueberwachende Objekt passiv und nicht aktiv ist.
	 * 
	 * @param s
	 *            Der theoretisch anzumeldende Listener.
	 */
	@Override
	public void stehReagierbarAnmelden(StehReagierbar s) {
		Logger.error("Achtung! Das Raum-Objekt, dem ein StehReagierbar-Listener zugewiesen werden sollte, ist kein Aktiv-Objekt, sonder ein " +
				"passives Objekt! Bitte erst dieses Objekt aktiv machen, dann diese Methode aufrufen!");
	}
	
	/**
	 * Soll testen, ob das Ziel-Objekt steht. <br />
	 * Gibt jedoch eine Fehlermeldung aus, da das Ziel-Objekt kein Aktiv-Objekt ist und damit nicht Stehen/Fallen kann.
	 * 
	 * @return Immer <code>false</code>, da die Eigenschaft stehen in diesem Fall nicht konsistent definierbar ist.
	 */
	@Override
	public boolean steht() {
		Logger.error("Achtung! Das Raum-Objekt, an dem das Stehen erfragt werden sollte, ist kein Aktiv-Objekt, sonder ein " +
				"passives Objekt! Bitte erst dieses Objekt aktiv machen, dann diese Methode aufrufen! Solange ist die Rueckgabe immer false.");
		return false;
	}
	
	/**
	 * Setzt die Schwerkraft fuer dieses Objekt.<br />
	 * Da dies jedoch bei einem passiven Objekt nicht moeglich ist,
	 * gibt es eine Fehlermeldung.
	 * 
	 * @param schwerkraft
	 *            Der Wert fuer die Schwerkraft der Physik.<br />
	 *            <b>Wichtig:</b> Dies repraesentiert <i>keinen</i> Wert fuer die (Erd-)
	 *            Beschleunigungszahl "g" aus der Physik. Schon allein deshalb, weil die
	 *            Zahl umgekehrt wirkt (s. oben).
	 * @see ea.Raum#aktivMachen()
	 */
	@Override
	public void schwerkraftSetzen(int schwerkraft) {
		Logger.error("Achtung! Ein passives Raum-Objekt sollte eine neue Schwerkraft verpasst bekommen. Das ist nicht moeglich. "
				+ "Nur Aktiv-Objekte koennen eine Schwerkraft gesetzt bekommen.");
	}

    /**
     * {@inheritDoc}
     */
	@Override
	public void impulsHinzunehmen(Vektor impuls) {
		Logger.error("Passivobjekte unterstützen leider keine Impulsrechnung. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void geschwindigkeitHinzunehmen(Vektor geschwindigkeit) {
		Logger.error("Passivobjekte unterstützen leider keine Geschwindigkeit. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public float getLuftwiderstandskoeffizient() {
		Logger.error("Passivobjekte unterstützen leider keinen Luftwiderstand. Dafür gibt es die Newton-Körper!");
		return 0;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public boolean istBeeinflussbar() {
		Logger.error("Passivobjekte unterstützen leider keinen Beeinflussbarkeit. Dafür gibt es die Newton-Körper!");
		return false;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public float getMasse() {
		Logger.error("Passivobjekte unterstützen leider keine Masse. Dafür gibt es die Newton-Körper!");
		return 0;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public Vektor getForce() {
		Logger.error("Passivobjekte unterstützen leider keine Kraftrechnung. Dafür gibt es die Newton-Körper!");
		return null;
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void luftwiderstandskoeffizientSetzen(float luftwiderstandskoeffizient) {
		Logger.error("Passivobjekte unterstützen leider keinen Luftwiderstand. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void beeinflussbarSetzen(boolean beeinflussbar) {
		Logger.error("Passivobjekte unterstützen leider keinen Beeinflussbarkeit. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void masseSetzen(float masse) {
		Logger.error("Passivobjekte unterstützen leider keine Masse. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void kraftSetzen(Vektor kraft) {
		Logger.error("Passivobjekte unterstützen leider keine Kraftrechnung. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void geschwindigkeitSetzen(Vektor geschwindigkeit) {
		Logger.error("Passivobjekte unterstützen leider keine Geschwindigkeit. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void einfluesseZuruecksetzen() {
		Logger.error("Passivobjekte unterstützen leider keine Einflüsse. Dafür gibt es die Newton-Körper!");
	}

	/**
     * {@inheritDoc}
     */
	@Override
	public void kraftAnwenden(Vektor kraft, float t_kraftuebertrag) {
		Logger.error("Passivobjekte unterstützen leider keine Kraftrechnung. Dafür gibt es die Newton-Körper!");
	}
}

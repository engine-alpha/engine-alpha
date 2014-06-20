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

import ea.*;

/**
 * Ein <code>Physik</code>-Client, der eine rudimentäre Implementierung
 * <b>Newton'scher Mechanik</b> implementieren soll.
 * Physik-Objekte, die von einem solchen Client betreut werden, zeichnen
 * sich durch folgende Eigenschaften aus:<br />
 * <ul>
 * <li>Sie haben eine aktuelle </b>Geschwindigkeit</b>.</li>
 * <li>Es wirkt eine <b>Kraft</b> (ggf. stellvertretend als Summe mehrerer Kräfte) auf sie.<li>
 * <li>Sie haben eine <b>Masse</b>.</li>
 * <li>Die Kraft wirkt eine <b>Beschleunigung</b> - also eine <i>Änderung der Geschwindigkeit</i> auf
 * sie aus. Die Beschleunigung hängt ausserdem von der <i>Masse</i> des Objekts ab.</li>
 * <li>Es wirkt konstant <b>Reibung</b>. In diesem Modell bedeutet dies eine <i>dynamische Verminderung der 
 * aktuellen Geschwindigkeit</i>. Die Verminderung ist stärker, je höher die Geschwindigkeit ist. Ihre
 * generelle Intensität lässt sich jedoch ändern.</li>
 * <li><b>Neue Kräfte</b> können atomar (also "von jetzt auf gleich", nicht über einen Zeitraum (z.B. 100 ms)) auf
 * Objekte angewendet werden. Das ist Vergleichbar mit einem schnellen Stoß in eine bestimmte Richtung. Dies lässt sich durch einen
 * <b>Impuls</b> realisieren, aber auch über direkte Eingabe einer Geschwindigkeitsänderung.</li>
 * <li>Es können Kräfte <b>dauerhaft</b> auf ein Objekt wirken, wie zum Beispiel die <i>Schwerkraft</i>.</li>
 * <li>Mehrere Objekte können <b>kollidieren</b>. Dann prallen sie <i>elastisch</i> voneinander ab. Dies funktioniert
 * intern über <i>Impulsrechnung</i>.</li>
 * </ul>
 * @author Michael Andonie
 *
 */
@SuppressWarnings("serial")
public class MechanikClient 
extends PhysikClient
implements Ticker {
	
	/**
	 * Diese Konstante gibt an, wie viele Meter ein Pixel hat. Das ist
	 * normalerweise ein sehr <b>kleiner</b> Wert (Standard: 0.01f).
	 */
	private static float METER_PRO_PIXEL = 0.001f;
	
	/**
	 * Setzt, wie viele Meter auf einen Pixel im Spielfenster gehen.
	 * @param meterpropixel	Die Anzahl an Metern, die auf einen Pixel fallen.<br/>
	 * Beispiele:<br />
	 * <ul>
	 * <li><code>10(.0f)</code> => Auf einen Pixel fallen <b>10</b> Meter. => Ein Meter = 0,1 Pixel</li>
	 * <li><code>0.1f</code> => Auf einen Pixel fallen <b>0,1</b> Meter. => Ein Meter = 10 Pixel</li>
	 * </ul>
	 */
	public static void setzeMeterProPixel(float meterpropixel) {
		if (meterpropixel <= 0.0f) {
			throw new IllegalArgumentException("Die Anzahl an Metern pro Pixel muss positiv sein!");
		} else if(MECH_TIMER.hatAktiveTicker()) {
			throw new RuntimeException("Die Anzahl von Metern pro Pixel kann nach der Nutzung der "
					+ "Physik nicht mehr geändert werden!");
		}
		METER_PRO_PIXEL = meterpropixel;
	}
	
	/**
	 * Der Timer, der sich aller Mechanik-Clients annimmt.
	 */
	public static Manager MECH_TIMER = new Manager();
	
	/**
	 * Das Intervall, in dem die Spielmechanik upgedated wird <b>in Sekunden</b>. Wird benutzt 
	 * für die Extrapolation.
	 * Orientiert sich an der <b>Update-Geschwindigkeit</b> der Zeichenebene
	 * @see ea.internal.gra.Zeichner.UPDATE_INTERVALL
	 */
	static final float DELTA_T = (float)ea.internal.gra.Zeichner.UPDATE_INTERVALL * 0.001f;
	
	/**
	 * Der Listener zum hoehren von Faellen.
	 */
	private FallReagierbar fallListener = FallDummy.getDummy();
	
	/**
     * Das StehReagierbar-Interface, das auf stehen reagieren soll.
     */
    private StehReagierbar sListener = StehDummy.getDummy();
	
	/**
	 * Die kritische Tiefe, bei der der Fall-Listener informiert wird.
	 */
	private int kritischeTiefe = 0;
	
	/**
	 * Die aktuelle Geschwindigkeit v des Client-Objekts.<br />
	 * <b>Einheit: m/s</b>
	 */
	private Vektor velocity;
	
	/**
	 * Die letzte Geschwindigkeit v des Client-Objekts.<br />
	 * Wird fuer das Absterben der Reibung benutzt
	 * <b>Einheit: m/s</b>
	 */
	private Vektor lastVelocity;
	
	/**
	 * Die aktuelle Kraft F, die auf das Client-Objekt wirkt. <br />
	 * <b>Einheit: N = m/s^2</b>
	 */
	private Vektor force;
	
	/**
	 * Die aktuelle Masse m des Objekts. <br/>
	 * <b>Einheit: Kilogramm</b>
	 */
	private float masse = 30.0f;
	
	/**
	 * Gibt an, ob das Objekt <b>beeinflussbar</b> ist. 
	 * @see #beeinflussbarSetzen(boolean)
	 */
	private boolean beeinflussbar=true;
	
	/**
	 * @return the velocity
	 */
	public Vektor getVelocity() {
		return velocity;
	}

	/**
	 * Der Luftwiderstandskoeffizient des Objekts ist eine Vereinfachung des
	 * Luftwiderstandsmodells.<br />
	 * F_W = 1/2 * c_W * A * rho * v^2<br />
	 * Heuristik: <br />
	 * F_W = luftwiderstandskoeffizient * v^2<br />
	 * 
	 * Der Koeffizient ist <b>nichtnegativ</b>.
	 */
	private float luftwiderstandskoeffizient = 40f;

	/**
	 * Der Collider für schnelle und effiziente Praekollisionstests.
	 */
	private KreisCollider collider;
	
	/**
	 * Konstruktor erstellt einen neuen Mechanik-Client.
	 * @param ziel das Ziel-Objekt für diesen Client.
	 */
	public MechanikClient(Raum ziel) {
		super(ziel);
		collider = ziel.dimension().umschliessenderKreis();
		einfluesseZuruecksetzen();
		MECH_TIMER.anmelden(this, ea.internal.gra.Zeichner.UPDATE_INTERVALL);
		CollisionHandling.anmelden(this);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void einfluesseZuruecksetzen() {
		force = Vektor.NULLVEKTOR;
		velocity = Vektor.NULLVEKTOR;
		lastVelocity = Vektor.NULLVEKTOR;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void geschwindigkeitSetzen(Vektor geschwindigkeit) {
		this.velocity = geschwindigkeit;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void kraftSetzen(Vektor kraft) {
		this.force = kraft;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void masseSetzen(float masse) {
		this.masse = masse;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beeinflussbarSetzen(boolean beeinflussbar) {
		this.beeinflussbar = beeinflussbar;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void luftwiderstandskoeffizientSetzen(float luftwiderstandskoeffizient) {
		if(luftwiderstandskoeffizient < 0) {
			throw new IllegalArgumentException("Der Luftwiderstandskoeffizient darf nicht negativ sein! Eingabe war " +
					luftwiderstandskoeffizient + ".");
		}
		this.luftwiderstandskoeffizient = luftwiderstandskoeffizient;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Vektor getForce() {
		return force;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getMasse() {
		return masse;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean istBeeinflussbar() {
		return beeinflussbar;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getLuftwiderstandskoeffizient() {
		return luftwiderstandskoeffizient;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void geschwindigkeitHinzunehmen(Vektor geschwindigkeit) {
		//v_neu = v_alt + delta v
		this.velocity = velocity.summe(geschwindigkeit);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void impulsHinzunehmen(Vektor impuls) {
		//Grundrechnung: 
		//p + delta p = m * v_neu
		//(m * v_alt) + delta p = m * v_neu
		//v_neu = v_alt + ([delta p] / m)
		this.velocity = velocity.summe(impuls.teilen(masse));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void kraftAnwenden(Vektor kraft, float t_kraftuebertrag) {
		//es gilt in dieser Heuristik: p = F * t_kraftübertrag
		//=>p = kraft * t_kraftübertrag
		//=> Impuls p anwenden.
		impulsHinzunehmen(kraft.multiplizieren(t_kraftuebertrag));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean bewegen(Vektor v) {
		ziel.verschieben(v);
		collider.verschieben(v);
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void aufloesen() {
		CollisionHandling.abmelden(this);
	}

	/**
	 * {@inheritDoc}
	 * Löst einen Impulssprung aus. Nur aus Kompatibilitätsgründen vorhanden.
	 * @return always <code>true</code>.
	 */
	@Override
	@Deprecated
	public boolean sprung(int kraft) {
		this.impulsHinzunehmen(new Vektor(60, 0));
		return true;
	}

	/**
	 * {@inheritDoc}
	 * Aktiviert / Deaktiviert eine Standardschwerkraft. Nur aus Kompatibilitätsgründen vorhanden.
	 */
	@Override
	@Deprecated
	public void schwerkraftAktivSetzen(boolean aktiv) {
		force = aktiv ? new Vektor(0, 10) : Vektor.NULLVEKTOR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void kritischeTiefeSetzen(int tiefe) {
		this.kritischeTiefe = tiefe;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fallReagierbarAnmelden(FallReagierbar f, int tiefe) {
		this.fallListener = f;
		this.kritischeTiefeSetzen(tiefe);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stehReagierbarAnmelden(StehReagierbar s) {
		this.sListener = s;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean steht() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public void schwerkraftSetzen(int schwerkraft) {
		this.kraftSetzen(new Vektor(0, 0.01f*schwerkraft));
	}

	/**
	 * In der <code>tick()</code>-Methode des Mechanik-Clients wird die 
	 * <b>diskrete Weiterrechnung</b> der verschiedenen Parameter realisiert
	 * sowie die Anwendung der Geschwindigkeit auf die aktuelle Position des
	 * Client-Objekts.
	 * Dies ist vergleichbar mit der <i>Methode der kleinen Schritte</i> aus
	 * der Physik.
	 */
	@Override
	public void tick() {
		//Kraftaenderung -> Kraft_aktuell = Kraft + Luftwiderstand
		//Luftwiderstand = 1/2 * c_W * A * rho * v^2
		//Heuristik: luftwiderstandskoeffizient * v^2
		Vektor momentanekraft = force.summe(velocity.gegenrichtung().multiplizieren((
				luftwiderstandskoeffizient*velocity.laenge())));
		
		//Beschleunigungsbestimmung -> a = F / m
		
		//Delta v bestimmen -> delta v = a * delta t = F * (delta t / m)
		//v_neu = v_alt + delta v
		velocity = velocity.summe(momentanekraft.multiplizieren(DELTA_T / masse));
		
		//Delta s bestimmen -> delta s = v_neu * delta t + [1/2 * a_neu * (delta t)^2]
		// =~= v_neu * delta t  [heuristik]
		//bewegen um delta s
		bewegen(velocity.multiplizieren(DELTA_T).teilen(METER_PRO_PIXEL));
		//System.out.println("Move:" + velocity.multiplizieren(DELTA_T));
		
		//Critical Depth:
		if(ziel.positionY() > kritischeTiefe)
			fallListener.fallReagieren();
		
		//Genügend für Ende? -> Heuristik: |v| < d [mit d geschickt gewählt]
		Vektor dif = velocity.differenz(lastVelocity);
		if(dif.laenge() < 0.0001f && dif.laenge() != 0) {
			System.out.println("T");
			velocity = Vektor.NULLVEKTOR;
		}
		
		//Update: Lasvelocity für den nächsten Step ist die aktuelle
		lastVelocity = velocity;
	}

	/**
	 * Gibt den Collider zurück.
	 * @return	Der Collider des Elements.
	 */
	public KreisCollider collider() {
		return collider;
	}
	
}

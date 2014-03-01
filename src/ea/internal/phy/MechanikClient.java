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
	 * normalerweise ein sehr <b>kleiner</b> Wert (Standard: 0.1f).
	 */
	private static float METER_PRO_PIXEL = 0.1f;
	
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
	private static final float DELTA_T = (float)ea.internal.gra.Zeichner.UPDATE_INTERVALL * 0.001f;
	
	/**
	 * Die aktuelle Geschwindigkeit v des Client-Objekts.<br />
	 * <b>Einheit: m/s</b>
	 */
	private Vektor velocity;
	
	/**
	 * Die aktuelle Kraft F, die auf das Client-Objekt wirkt. <br />
	 * <b>Einheit: N = m/s^2</b>
	 */
	private Vektor force;
	
	/**
	 * Die aktuelle Masse m des Objekts. <br/>
	 * <b>Einheit: Kilogramm</b>
	 */
	private float masse = 1.0f;
	
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
	private float luftwiderstandskoeffizient = 1.0f;
	
	/**
	 * Konstruktor erstellt einen neuen Mechanik-Client.
	 * @param ziel das Ziel-Objekt für diesen Client.
	 */
	public MechanikClient(Raum ziel) {
		super(ziel);
		einfluesseZuruecksetzen();
		MECH_TIMER.anmelden(this, ea.internal.gra.Zeichner.UPDATE_INTERVALL);
	}
	
	/**
	 * Setzt alle Einfluesse auf das Client-Objekt zurück. Dies sind:
	 * <ul>
	 * <li>Die Kraft F, die gerade auf das Objekt wirkt.</li>
	 * <li>Die Geschwindigkeit v, die das Objekt gerade hat.</li>
	 * </ul>
	 */
	public void einfluesseZuruecksetzen() {
		force = Vektor.NULLVEKTOR;
		velocity = Vektor.NULLVEKTOR;
	}
	
	/**
	 * Setzt <b>hart</b> die Geschwindigkeit des Client-Objekts.
	 * Das bedeutet, dass die vorher gegoltene Geschwindikeit gelöscht wird
	 * ohne Rücksicht auf mögliche Implikationen/Probleme.
	 * @param geschwindigkeit	Die neue Geschwindigkeit für das
	 * 							Client-Objekt. <b>(in [m / s])</b>
	 */
	public void geschwindigkeitSetzen(Vektor geschwindigkeit) {
		this.velocity = geschwindigkeit;
	}
	
	/**
	 * Setzt <b>hart</b> die <b>konstante</b> Kraft, die auf das Client-Objekt wirkt.
	 * Das bedeutet, dass die vorher gegoltene Kraft gelöscht wird
	 * ohne Rücksicht auf mögliche Implikationen/Probleme.
	 * @param kraft	Die neue Kraft, die auf das
	 * 							Client-Objekt wirken soll.<b>(in [m / s^2] = [N])</b>
	 */
	public void kraftSetzen(Vektor kraft) {
		this.force = kraft;
	}
	
	/**
	 * Setzt die Masse des Clien-Objekts neu. Das kann auch mitten im Spiel geändert
	 * werden. Die Masse bestimmt zum Beispiel, wie sich das Objekt bei Kollisionen
	 * oder einem neuen Impuls verhält.
	 * @param masse die neue Masse des Client-Objekts.<b>(in [kg])</b>
	 */
	public void masseSetzen(float masse) {
		this.masse = masse;
	}
	
	/**
	 * Setzt, ob das Objekt ab sofort beeinflussbar sein soll. <br/>
	 * Das bedeutet:<br />
	 * <ul>
	 * <li>Beeinflussbare Objekte lassen sich verschieben.</li>
	 * <li>Unbeeinflussbare Objekte werden von Impulsen nicht beeindruckt und geben ihn so wie er
	 * ist zurück.</li>
	 * <li>Unbeeinflussbare Objekte sind, Wände, Decken, Ebenen, beeinflussbare sind meist Spielfiguren.</li>
	 * <li>Auch unbeeinflussbare Objekte sind <b>bewegbar und man kann Kräfte/Impulse auf sie Auswirken</b>.</li>
	 * <li>Kollidiert ein beeinflussbares Objekt mit einem nicht beeinflussbaren Objekt, so blockiert das
	 * unbeeinflussbare Objekt das beeinflussbare Objekt. Letzteres prallt evtl. leicht ab.</li>
	 * <li>Kollidieren 2 beeinflussbare Objekte, so prallen sie voneinander ab.</li>
	 * <li>Kollidieren 2 unbeeinflussbare Objekte, so passiert gar nichts. Ggf. durchschneiden sie sich gegenseitig.</li>
	 * </ul>
	 * @param beeinflussbar ist dieser Wert <code>true</code>, ist das Objekt ab sofort beeinflussbar. Sonst ist es
	 * 			nicht beeinflussbar.
	 */
	public void beeinflussbarSetzen(boolean beeinflussbar) {
		this.beeinflussbar = beeinflussbar;
	}
	
	/**
	 * Setzt den Luftwiderstandskoeffizienten für das Client-Objekt. Dieser bestimmt,
	 * <b>wie intensiv der Luftwiderstand das Objekt beeinträchtigt</b>. Je höher dieser
	 * Wert ist, desto <i>stärker</i> ist der Luftwiderstand. Ist er 0, gibt es <i>keinen</i>
	 * Luftwiderstand.
	 * @param luftwiderstandskoeffizient	Der Luftwiderstandskoeffizient. Darf nicht
	 * 					kleiner als 0 sein!
	 */
	public void luftwiderstandskoeffizientSetzen(float luftwiderstandskoeffizient) {
		if(luftwiderstandskoeffizient < 0) {
			throw new IllegalArgumentException("Der Luftwiderstandskoeffizient darf nicht negativ sein! Eingabe war " +
					luftwiderstandskoeffizient + ".");
		}
		this.luftwiderstandskoeffizient = luftwiderstandskoeffizient;
	}
	
	/**
	 * @return Die aktuelle Kraft, die auf das Objekt wirkt.
	 */
	public Vektor getForce() {
		return force;
	}

	/**
	 * @return Die Masse des Objekts.
	 */
	public float getMasse() {
		return masse;
	}

	/**
	 * @return ob das Objekt beeinflussbar ist.
	 */
	public boolean istBeeinflussbar() {
		return beeinflussbar;
	}

	/**
	 * @return Der Luftwiderstandskoeffizient
	 */
	public float getLuftwiderstandskoeffizient() {
		return luftwiderstandskoeffizient;
	}
	
	/**
	 * Addiert eine Geschwindigkeit v' zur aktuellen Geschwindigkeit v.
	 * Die neue Geschwindigkeit des Client-Objekts ist damit:<br />
	 * <code>v_neu = v + v'</code>
	 * @param geschwindigkeit	Die neue Geschwindigkeit v', die zur
	 * 			aktuellen Geschwindigkeit v hinzuaddiert werden soll.<b>(in [m / s])</b>
	 */
	public void geschwindigkeitHinzunehmen(Vektor geschwindigkeit) {
		//v_neu = v_alt + delta v
		this.velocity = velocity.summe(geschwindigkeit);
	}
	
	/**
	 * Berechnet einen <b>neuen Impuls</b> auf das Client-Objekt.
	 * @param impuls der neue Impuls, der auf das Objekt wirken soll. <b>(in [kg* (m / s)])</b>
	 */
	public void impulsHinzunehmen(Vektor impuls) {
		//Grundrechnung: 
		//p + delta p = m * v_neu
		//(m * v_alt) + delta p = m * v_neu
		//v_neu = v_alt + ([delta p] / m)
		this.velocity = velocity.summe(impuls.teilen(masse));
	}
	
	/**
	 * Wendet eine Kraft für einen bestimmten Zeitraum auf das Client-Objekt an.
	 * Hierdurch entsteht ein <b>neuer Impuls</b> auf das Objekt, der dessen
	 * Geschwindigkeit (und Richtung) ändern kann.<br />
	 * Wichtig: Dies ist eine <i>Heuristik</i>: Die Dauer sein <i>genügend klein</i>
	 * und die Kraft <i>konstant</i>, solange sie wirkt. Die rein physikalische Rechnung
	 * wäre wesentlich rechenintensiver.
	 * @param kraft	Die Kraft, die auf das Objekt anliegen soll. <b>(in [kg* (m / s^2)] = [N])</b>
	 * @param t_kraftuebertrag Die Dauer, für die die Kraft auf das Objekt wirkt. <b>(in [s)])</b>
	 */
	public void kraftAnwenden(Vektor kraft, float t_kraftuebertrag) {
		//es gilt in dieser Heuristik: p = F * t_kraftübertrag
		//=>p = kraft * t_kraftübertrag
		//=> Impuls p anwenden.
		impulsHinzunehmen(kraft.multiplizieren(t_kraftuebertrag));
	}

	@Override
	public boolean bewegen(Vektor v) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void aufloesen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean sprung(int kraft) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void schwerkraftAktivSetzen(boolean aktiv) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void kritischeTiefeSetzen(int tiefe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fallReagierbarAnmelden(FallReagierbar f, int tiefe) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stehReagierbarAnmelden(StehReagierbar s) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean steht() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void schwerkraftSetzen(int schwerkraft) {
		// TODO Auto-generated method stub
		
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
		bewegen(velocity.multiplizieren(DELTA_T));
	}
	
}

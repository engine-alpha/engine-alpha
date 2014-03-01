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
 * Objekte angewendet werden. Das ist Vergleichbar mit einem schnellen Stoß in eine bestimmte Richtung.</li>
 * <li>Es können Kräfte <b>dauerhaft</b> auf ein Objekt wirken, wie zum Beispiel die <i>Schwerkraft</i>.</li>
 * <li>Mehrere Objekte können <b>kollidieren</b>. Dann prallen sie <i>elastisch</i> voneinander ab. Dies funktioniert
 * intern über <i>Impulsrechnung</i>.</li>
 * </ul>
 * @author Michael Andonie
 *
 */
public class MechanikClient 
extends PhysikClient
implements Ticker {
	
	/**
	 * Das Intervall, in dem die Spielmechanik upgedated wird. 
	 */
	public static final int UPDATE_INTERVALL = ea.internal.gra.Zeichner.UPDATE_INTERVALL;
	
	/**
	 * Die aktuelle Geschwindigkeit v des Client-Objekts.
	 */
	private Vektor velocity = Vektor.NULLVEKTOR;
	
	/**
	 * Die aktuelle Kraft F, die auf das Client-Objekt wirkt. 
	 */
	private Vektor force = Vektor.NULLVEKTOR;
	
	/**
	 * Die aktuelle Masse m des Objekts.
	 */
	private float masse = 1.0f;
	
	/**
	 * Konstruktor erstellt einen neuen Mechanik-Client.
	 * @param ziel das Ziel-Objekt für diesen Client.
	 */
	public MechanikClient(Raum ziel) {
		super(ziel);
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

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}
	
	
}

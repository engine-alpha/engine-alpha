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

import ea.internal.collision.BoxCollider;
import ea.internal.collision.Collider;
import ea.internal.collision.NullCollider;
import ea.internal.gui.Fenster;
import ea.internal.phy.*;

import java.awt.*;
import java.util.Locale;

/**
 * Raum bezeichnet alles, was sich auf der Zeichenebene befindet.<br />
 * Dies ist die absolute Superklasse aller grafischen Objekte. Umgekehrt kann somit jedes
 * grafische Objekt die folgenden Methoden nutzen.
 * 
 * @author Michael Andonie, Niklas Keller
 */
public abstract class Raum implements java.io.Serializable, Comparable<Raum> {
	/**
	 * Die Serialisierungs-Konstante dieser Klasse. <b>In keiner Weise fuer die Programmierung mit der Engine bedeutsam!</b>
	 */
	private static final long serialVersionUID = 98L;
	
	/**
	 * Der Leuchtmacher fuer alle Raum-Objekte
	 */
	private static final LeuchtMacher macher = new LeuchtMacher();
	
	/**
	 * Der Animations-Manager, über den alle Animationen laufen.<br />
	 * Wird zum Löschen aller Referenzen auf dieses Objekt verwendet.
	 */
	private static final AnimationsManager animationsManager = AnimationsManager.getAnimationsManager();
	
	/**
	 * Ob die Kollisionstests Roh oder fein ablaufen sollen.
	 */
	protected static boolean roh = false;
	
	/**
	 * Die absolute Position des Raum-Objekts. Die Interpretation dieses Parameters
	 * hängt von den sich <b>ableitenden</b> Klassen ab.
	 * Er kann komplett irrelevant sein (Knoten), oder - im Regelfall - die linke
	 * obere Ecke des Objektes bezeichnen. Default
	 */
	protected Punkt position = Punkt.ZENTRUM;

	private Punkt lastMiddle;
	private double lastDrehung;
	
	/**
	 * Ein einfacher Farbzyklus, der fuer die Leucht-Animationen genommen wird
	 */
	public static final Color[] farbzyklus = {
		Color.white,
		Color.blue,
		Color.red,
		Color.yellow,
		Color.magenta,
		Color.cyan,
		Color.green,
		Color.orange,
	};
	
	/**
	 * Gibt an, ob das Objekt zur Zeit ueberhaupt sichtbar sein soll.<br />
	 * Ist dies nicht der Fall, so wird die Zeichenroutine direkt uebergangen.
	 */
	private boolean sichtbar = true;
	
	/**
	 * Gibt an, ob dieses Objekt mit Verzug ungleich von 0 gezeichnet wuerde. In
	 * diesem Fall wird es als statisch betrachtet. Ob dies tatsaechlich der Fall
	 * ist, ist irrelevant.
	 */
	private boolean statisch = false;
	
	/**
	 * Der Physik-Client, der die Physik dieses Raum-Objekts regelt.
	 */
	private PhysikClient phClient = new NullClient(this);
	
	/**
	 * Der aktuelle Collider dieses Raum-Objekts.
	 */
	private Collider collider = NullCollider.getInstance();
	
	/**
	 * Z-Index des Raumes, je höher, desto weiter oben wird der Raum gezeichnet
	 */
	private int zIndex = 1;
	
	/**
	 * Speichert die aktuelle Drehung des Raumes.
	 */
	private double drehung;
	
	/**
	 * Der eine und einziege Konstruktor fuer Objekte der Klasse Raum.
	 */
	public Raum() {
		//
	}
	
	/**
	 * Setzt den Z-Index dieses Raumes. Je größer, desto weiter vorne wird ein Raum gezeichnet.
	 * <b>Diese Methode muss ausgeführt werden, bevor der Raum zu einem Knoten hinzugefügt wird.</b>
	 * 
	 * @param z
	 *            zu setzender Index
	 */
	public void zIndex(int z) {
		zIndex = z;
	}
	
	/**
	 * Setzt, ob saemtliche Kollisionstests in der Engine Alpha grob oder fein sein sollen.
	 * @param heavy
	 *            Ist dieser Wert <code>true</code>, werden intern Kollisionstests genauer, aber rechenintensiver. Ist er <code>false</code>, werden diese Kollisionstests schneller, aber ungenauer.
	 * @see Game#rechenintensiveArbeitSetzen(boolean)
	 */
	public static void heavyComputingSetzen(boolean heavy) {
		roh = heavy;
	}
	
	/**
	 * Macht dieses <code>Raum</code>-Objekt fuer die Physik zu einem <i>Neutralen</i> Objekt, also einem
	 * Objekt das per se nicht an der Physik teilnimmt.
	 */
	public void neutralMachen() {
		phClient.aufloesen();
		phClient = new NullClient(this);
	}
	
	/**
	 * Macht dieses Objekt zu einem Passiv-Objekt. <br />
	 * Ab dem Aufruf dieser Methode verhaelt es sich als Boden-/Wand- bzw. Deckenelement
	 * und haelt Aktiv-Objekte auf.
	 */
	public void passivMachen() {
		phClient.aufloesen();
		phClient = new Passivator(this);
	}
	
	/**
	 * Macht dieses Objekt zu einem Aktiv-Objekt.<br />
	 * Ab dem Aufruf dieser Methode laesst es sich von Passiv-Objekten aufhalten und wird -
	 * solange dies nicht ueber den Methodenaufruf <code>schwerkraftAktivSetzen(false)</code> deaktiviert wird - von einer kuenstlichen Schwerkraft angezogen.
	 * 
	 * @see #schwerkraftAktivSetzen(boolean)
	 */
	public void aktivMachen() {
		phClient.aufloesen();
		phClient = new Gravitator(this);
	}
	
	/**
	 * TODO
	 */
	public void newtonschMachen() {
		phClient.aufloesen();
		phClient = new MechanikClient(this);
	}
	
	
	/**
	 * Laesst das <code>Raum</code>-Objekt einen Sprung von variabler Kraft machen. Dies funktioniert
	 * jedoch nur dann, wenn das Objekt auch ein <i>Aktiv-Objekt</i> ist. Ansonsten ist wird hier
	 * eine Fehlermeldung ausgegeben.
	 * 
	 * @param kraft
	 *            Die Kraft dieses Sprunges. Je hoeher dieser Wert, desto hoeher der Sprung.
	 * @return <code>true</code>, wenn das <code>Raum</code>-Objekt erfolgreich springen konnte. <code>false</code>,
	 *         wenn das <code>Raum</code>-Objekt <b>nicht</b> springen konnte.<br />
	 *         Zweiteres ist automatisch immer dann der Fall, wenn<br />
	 *         - das <code>Raum</code>-Objekt <b>kein Aktiv-Objekt mit aktivierter Schwerkraft (Standard)</b> ist oder <br />
	 *         - das <code>Raum</code>-Objekt als Aktiv-Objekt <b>nicht auf einem Passiv-Objekt</b> steht.
	 */
	public boolean sprung(int kraft) {
		return phClient.sprung(kraft);
	}
	
	
	
	/**
	 * Setzt, ob dieses <code>Raum</code>-Objekt von Schwerkraft beeinflusst wird. Macht nur dann Sinn,
	 * wenn das Objekt, an dem diese Methode ausgefuehrt wird, ein Aktiv-Objekt ist.
	 * 
	 * @param aktiv
	 *            <code>true</code>, wenn Schwerkraft aktiv sein soll, sonst <code>false</code>.
	 * @see #aktivMachen()
	 */
	public void schwerkraftAktivSetzen(boolean aktiv) {
		phClient.schwerkraftAktivSetzen(aktiv);
	}
	
	/**
	 * Setzt die Schwerkraft fuer dieses spezielle Objekt.<br />
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
	public void schwerkraftSetzen(int schwerkraft) {
		this.phClient.schwerkraftSetzen(schwerkraft);
	}
	
	/**
	 * Meldet einen <code>FallReagierbar</code>-Listener an.
	 *
	 * Dieser wird ab sofort immer dann informiert, wenn dieses <code>Raum</code>-Objekt unter eine bestimmte
	 * Höhe faellt. Diese wird als <b>kritische Tiefe</b> bezeichnet. Der Listener wird ab diesem Zeitpunkt
	 * <i>dauerhaft aufgerufen, solange das Objekt unterhalb dieser Toleranzgrenze ist</i>. Deshalb sollte in der
	 * implementierten Reaktionsmethode des <code>FallReagierbar</code>-Interfaces die Höhe so neu gesetzt werden, dass
	 * das <code>Raum</code>-Objekt nicht mehr unterhalb der <b>kritischen Tiefe</b> ist.
	 *
	 * <b>ACHTUNG!</b>
	 *
	 * Jedes <code>Raum</code>-Objekt hat <b>HÖCHSTENS</b> einen <code>FallReagierbar</code>-Listener. Das bedeutet, dass
	 * es <b>nicht möglich ist, dass mehrere <code>FallReagierbar</code>-Listener über ein Objekt informiert werden</b>.
	 *
	 * Die <b>kritische Tiefe</b> jedoch lässt sich problemlos immer wieder neu setzen, über die Methode <code>kritischeTiefeSetzen(int tiefe)</code>.
	 *
	 * Diese Methode mach natürlich nur Sinn, wenn sie an einem <i>Aktiv-Objekt</i> ausgeführt wird. Andernfalls gibt es eine Fehlermeldung!
	 * 
	 * @param f
	 *            Das anzumeldende <code>FallReagierbar</code>
	 * @param kritischeTiefe
	 *            Die Tiefe ab der der Listener <i>dauerhaft</i> durch den Aufruf seiner Reaktionsmethode
	 *            informiert wird, solange das <code>Raum</code>-Objekt hierunter ist.
	 * @see FallReagierbar
	 * @see #kritischeTiefeSetzen(int)
	 */
	public void fallReagierbarAnmelden(FallReagierbar f, int kritischeTiefe) {
		phClient.fallReagierbarAnmelden(f, kritischeTiefe);
	}
	
	/**
	 * Setzt die <b>kritische Tiefe</b> neu. Ab dieser Tiefe wird der <code>FallReagierbar</code>-Listener dieses <code>Raum</code>-Objektes
	 * aufgerufen - dauerhaft so lange, bis das <code>Raum</code>-Objekt <b>nicht mehr unterhalb dieser Tiefe ist</b>.
	 * 
	 * @param tiefe
	 *            Die neue kritische Tiefe. Die Tiefe ab der der Listener <i>dauerhaft</i> durch den Aufruf seiner Reaktionsmethode
	 *            informiert wird, solange das <code>Raum</code>-Objekt hierunter ist.
	 * @see FallReagierbar
	 * @see #fallReagierbarAnmelden(FallReagierbar, int)
	 */
	public void kritischeTiefeSetzen(int tiefe) {
		phClient.kritischeTiefeSetzen(tiefe);
	}
	
	/**
	 * Diese Methode meldet einen <code>StehReagierbar</code>-Listener neu an.<br />
	 * Dieser wird nach der Anmeldung immer dann <i>einmalig</i> durch den Aufruf seiner <code>stehReagieren()</code>-Methode informiert,
	 * wenn dieses <code>Raum</code>-Objekt nach dem Fall/Sprung wieder auf einem Passiv-Objekt zu stehen kommt.<br />
	 * <br />
	 * <b>ACHTUNG!</b><br />
	 * Ein <code>Raum</code>-Objekt kann <b>hoechstens einen <code>StehReagierbar</code>-Listener besitzen</b>!<br />
	 * <br />
	 * Diese Methode mach natuerlich nur Sinn, wenn sie an einem <i>Aktiv-Objekt</i> ausgefuehrt wird. Andernfalls gibt es eine Fehlermeldung!
	 * 
	 * @param s
	 *            Der <code>StehReagierbar</code>-Listener, der ab sofort bei jedem neuen zum Stehen kommen dieses <code>Raum</code>-Objekts
	 *            informiert wird.
	 * @see ea.StehReagierbar
	 */
	public void stehReagierbarAnmelden(StehReagierbar s) {
		phClient.stehReagierbarAnmelden(s);
	}
	
	/**
	 * Prueft, ob dieses Objekt als <i>Aktiv-Objekt</i> steht.<br />
	 * Diese Methode steht nicht in direktem Zusammenhang mit dem Interface <code>StehReagierbar</code>, denn durch diese Methode laesst
	 * sich zu jedem beliebigen Zeitpunkt erfragen, ob das <code>Raum</code>-Objekt steht, nicht jedoch - wie durch <code>StehReagierbar</code> -
	 * am genauen Zeitpunkt des zum Stehen kommens hierauf reagieren.<br />
	 * <br />
	 * Diese Methode macht natuerlich nur dann sinn, wenn sie an einem <i>Aktiv-Objekt</i> ausgefuehrt wird. Andernfalls gibt es eine Fehlermeldung!
	 * 
	 * @return <code>true</code>, wenn dieses <code>Raum</code>-Objekt als <i>Aktiv-Objekt</i> auf einem Passiv-Objekt steht. Steht dieses <code>Raum</code>-Objekt als <i>Aktiv-Objekt</i> nicht auf
	 *         einem Passiv-Objekt, oder ist dieses <code>Raum</code>-Objekt kein
	 *         <i>Aktiv-Objekt</i>, so ist die Rueckgabe <code>false</code>.
	 */
	public boolean steht() {
		return phClient.steht();
	}
	
	/**
	 * <b>Bewegt</b> dieses <code>Raum</code>-Objekt. Der Unterschied zum <b>Verschieben</b> ist folgender:<br />
	 * Ist dieses Objekt in der Physik beteiligt, so ist dies eine Bewegung innerhalb der Physik und kein Stumpfes Verschieben.<br />
	 * Ist dieses Objekt fuer die Physik neutral ist dies genauso wie <code>verschieben</code>.
	 * 
	 * @param v
	 *            Die Bewegung beschreibender Vektor
	 * @return <code>true</code>, wenn sich dieses <code>Raum</code>-Objekt ohne Probleme bewegen liess. Konnte es wegen der Physik
	 *         (Aktiv-Objekt von Passiv-Objekt geblockt) <b>nicht vollstaendig verschoben werden</b>, so wird <code>false</code> zurueckgegeben.<br />
	 *         Die Rueckgabe ist bei Passiv-Objekten und neutralen Objekten immer <code>true</code>, da diese Problemlos verschoben werden können.
	 *         
	 * @see #bewegen(int, int)
	 */
	public boolean bewegen(Vektor v) {
		synchronized (this) {
			return phClient.bewegen(v);
		}
	}
	
	/**
	 * <b>Bewegt</b> dieses <code>Raum</code>-Objekt. Der Unterschied zum <b>Verschieben</b> ist folgender:<br />
	 * Ist dieses Objekt in der Physik beteiligt, so ist dies eine Bewegung innerhalb der Physik und kein Stumpfes Verschieben.<br />
	 * Ist dieses Objekt fuer die Physik neutral ist dies genauso wie <code>verschieben</code>.
	 * 
	 * @param dX
	 *            Der X-Anteil der Verschiebung (Delta-X)
	 * @param dY
	 *            Der Y-Anteil der Verschiebung (Delta-Y)
	 * @see #bewegen(Vektor)
	 */
	public void bewegen(int dX, int dY) {
		phClient.bewegen(new Vektor(dX, dY));
	}
	
	/**
	 * Setzt die Meter pro Pixel für die Zeichenebene.
	 * Dies ist das <i>dynamische Bindeglied</i> zwischen der <b>physikalisch möglichst korrekten Berechnung
	 * innerhalb der Engine</b> sowie der <b>freien Wählbarkeit der Zeichenebene</b>.
	 * @param mpp	Die Anzahl an Metern, die auf einen Pixel fallen.<br/>
	 * Beispiele:<br />
	 * <ul>
	 * <li><code>10(.0f)</code> => Auf einen Pixel fallen <b>10</b> Meter. => Ein Meter = 0,1 Pixel</li>
	 * <li><code>0.1f</code> => Auf einen Pixel fallen <b>0,1</b> Meter. => Ein Meter = 10 Pixel</li>
	 * </ul>
	 * @see #newtonschMachen()
	 */
	public void setzeMeterProPixel(float mpp) {
		ea.internal.phy.MechanikClient.setzeMeterProPixel(mpp);
	}
	
	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Wirkt einen Impuls auf das <code>Raum</code>-Objekt aus. Dieser ändert - abhängig von seiner
     * Richtung, Intensität sowie von der <i>Mass</i> des </code>Raum</code>-Objekts eine
     * Geschwindigkeitsänderung.
     * @param impuls Der Impuls, der diesem <code>Raum</code>-Objekt zugeführt werden soll.<br />
     * <b>WICHTIG:</b> Die Einheiten für physikalische Größen innerhalb der Engine entsprechen denen
     * aus der klassischen Mechanik. Die Einheit für Impuls ist [kg * (m / s)]
     * @see #masseSetzen(float)
     * @see #kraftSetzen(Vektor)
     * @see #setzeMeterProPixel(float)
     * @see #newtonschMachen()
     */
	public void impulsHinzunehmen(Vektor impuls) {
		phClient.impulsHinzunehmen(impuls);
	}

	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Setzt <b>hart</b> (also ohne Rücksicht auf mögliche Umstände) die Geschwindigkeit dieses <code>Raum</code>-Objektes.
     * Es bewegt sich ab sofort mit dieser Geschwindigkeit weiter.
     * @param geschwindigkeit die Geschwindigkeit, die dieses <code>Raum</code>-Objekt ab sofort annehmen soll.
     * <b>WICHTIG:</b> Die Einheiten für physikalische Größen innerhalb der Engine entsprechen denen
     * aus der klassischen Mechanik. Die Einheit für Geschwindigkeit ist [m / s]
     * @see #masseSetzen(float)
     * @see #kraftSetzen(Vektor)
     * @see #setzeMeterProPixel(float)
     * @see #luftwiderstandskoeffizientSetzen(float)
     * @see #newtonschMachen()
     */
	public void geschwindigkeitHinzunehmen(Vektor geschwindigkeit) {
		phClient.geschwindigkeitHinzunehmen(geschwindigkeit);
	}

	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Gibt den Luftwiderstandskoeffizienten dieses <code>Raum-Objektes</code> aus.
     * @see #luftwiderstandskoeffizientSetzen(float)
     * @see #newtonschMachen()
     */
	public float luftwiderstandskoeffizient() {
		return phClient.getLuftwiderstandskoeffizient();
	}

	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Gibt aus, ob dieses <code>Raum</code>-Objekt beeinflussbar, also durch Impulse beweglich ist.
     * Was das heisst, kannst Du in der Setter-Methode nachlesen.
     * @return <code>true</code>, falls dieses <code>Raum</code>-Objekt beeinflussbar ist,
     * 		sonst <code>false</code>.
     * @see #beeinflussbarSetzen(boolean)
     * @see #newtonschMachen()
     */
	public boolean istBeeinflussbar() {
		return phClient.istBeeinflussbar();
	}

	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Gibt aus die Masse dieses <code>Raum</code>-Objektes aus. Diese ist relevant
     * Impulsrechnungen, z.B. wenn 2 Objekte kollidieren.
     * @return die Masse dieses <code>Raum</code>-Objektes in korrekter Einheit.
     * <b>WICHTIG:</b> Die Einheiten für physikalische Größen innerhalb der Engine entsprechen denen
     * aus der klassischen Mechanik. Die Einheit für Masse ist [kg]
     * @see #masseSetzen(float)
     * @see #newtonschMachen()
     */
	public float getMasse() {
		return phClient.getMasse();
	}

	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Gibt aus die Kraft aus, die auf dieses <code>Raum</code>-Objekt dauerhaft wirkt. So lässt sich
     * z.B. eine dynamische Schwerkraft realisieren.
     * @return die Kraft, die auf dieses <code>Raum</code>-Objektes konstant wirkt.
     * <b>WICHTIG:</b> Die Einheiten für physikalische Größen innerhalb der Engine entsprechen denen
     * aus der klassischen Mechanik. Die Einheit für Kraft ist [N] = [kg * (m / s^2)]
     * @see #beeinflussbarSetzen(boolean)
     * @see #newtonschMachen()
     */
	public Vektor getForce() {
		return phClient.getForce();
	}

	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Setzt den Luftwiderstandskoeffizienten für dieses <code>Raum</code>-Objekt.
     * Je größer dieser Wert ist, desto stärker ist der Luftwiderstand auf das <code>Raum-Objekt</code>.<br />
     * Der Luftwiderstand <b>nicht</b> über die vollständige Luftwiderstandsformel (u.a. mit Querschnittsfläche
     * des Körpers) berechnet. Der Luftwiderstand berechnet sich <b>ausschließlich aus der Geschwindigkeit
     * und dieses Luftwiderstandskoeffizienten</b>:<br />
     * <code> (F_w = luftwiderstandskoeffizient * v^2)</code>
     * @param luftwiderstandskoeffizient Der Luftwiderstandskoeffizient, der für dieses <code>Raum</code>-Objekt
     * 		gelten soll. Ist dieser Wert <code>0</code>, so wirkt kein Luftwiderstand auf das Objekt.
     * @see #luftwiderstandskoeffizient()
     * @see #newtonschMachen()
     */
	public void luftwiderstandskoeffizientSetzen(float luftwiderstandskoeffizient) {
		phClient.luftwiderstandskoeffizientSetzen(luftwiderstandskoeffizient);
	}

	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Setzt, ob dieses <code>Raum</code>-Objekt <i>beeinflussbar</i> sein soll für Impulse
     * von anderen Objekten, die mit diesem Kollidieren. Ist es <b>nicht beeinflussbar<b>, so prallen
     * (beeinflussbare) Objekte einfach an ihm ab. Typische unbeeinflussbare Objekte sind:<br />
     * <ul>
     * <li>Böden</li><li>Wände</li><li>Decken</li><li>bewegliche Plattformen</li>
     * </ul><br />
     * Ist ein <code>Raum</code>-Objekt <b>beeinflussbar</b>, so kann es an anderen Objekten abprallen
     * bzw. von ihnen blockiert werden. Es kann sie nicht verschieben. Typische beeinflussbare Objekte
     * sind: <br />
     * <ul>
     * <li>Spielfiguren</li>
     * </ul><br />
     * 
     * Diese Eigenschaft kann beliebig oft durchgewechselt werden.
     * 
     * @param beeinflussbar	ist dieser Wert <code>true</code>, so ist dieses Objekt ab sofort
     * 		<i>beeinflussbar</i>. Sonst ist es ab sofort <i>nicht beeinflussbar</i>.
     * @see #istBeeinflussbar()
     * @see #newtonschMachen()
     */
	public void beeinflussbarSetzen(boolean beeinflussbar) {
		phClient.beeinflussbarSetzen(beeinflussbar);
	}

	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Setzt die Masse für dieses <code>Raum</code>-Objekt. Es hat ab sofort diese Masse.
     * Diese hat Auswirkungen auf Impulsrechnung und die Dynamik dieses Objekts..
     * @param masse die Mass, die  dieses <code>Raum</code>-Objekt ab sofort haben soll.
     * <b>WICHTIG:</b> Die Einheiten für physikalische Größen innerhalb der Engine entsprechen denen
     * aus der klassischen Mechanik. Die Einheit für Masse ist [kg]
     * @see #getMasse()
     * @see #newtonschMachen()
     */
	public void masseSetzen(float masse) {
		phClient.masseSetzen(masse);
	}
	
	/**
	 * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
	 * Setzt die Kraft, die auf dieses <code>Raum</code>-Objekt dauerhaft wirken soll. So lässt sich
	 * z.B. eine dynamische Schwerkraft realisieren:<br />
	 * Die Schwerkraft, wäre eine Kraft, die dauerhaft nach unten wirkt, also zum Beispiel:
	 * <br /><code>
	 * Kreis ball = ...<br />
	 * [...] <br />
	 * ball.kraftSetzen(new Vektor(0,9.81)); // Setze eine Schwerkraft mit 9,81 kg * m/s^2
	 * </code>
	 * 
	 * @param kraft die Kraft, die auf dieses <code>Raum</code>-Objekt konstant wirken soll.
	 *         <b>WICHTIG:</b> Die Einheiten für physikalische Größen innerhalb der Engine entsprechen denen
	 *         aus der klassischen Mechanik. Die Einheit für Kraft ist [N] = [kg * (m / s^2)]
	 * @see #getForce()
	 * @see #newtonschMachen()
	 */
	public void kraftSetzen(Vektor kraft) {
		phClient.kraftSetzen(kraft);
	}

	/**
	 * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
	 * Setzt die Geschwindigkeit, die dieses <code>Raum</code>-Objekt haben soll.
	 *
	 * @param geschwindigkeit die Geschwindikeit, die auf dieses <code>Raum</code>-Objekt mit sofortiger Wirkung annehmen soll.
	 *         <b>WICHTIG:</b> Die Einheiten für physikalische Größen innerhalb der Engine entsprechen denen
	 *         aus der klassischen Mechanik. Die Einheit für Geschwindigkeit ist [m / s]
	 * @see #newtonschMachen()
	 */
	public void geschwindigkeitSetzen(Vektor geschwindigkeit) {
		phClient.geschwindigkeitSetzen(geschwindigkeit);
	}

	/**
     * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
     * Setzt alle Einflüsse auf dieses <code>Raum</code>-Objekt zurück. Dies bedeutet:
     * * die auf dieses Objekt einwirkende, konstante Kraft wird 0.
     * * die Geschwindigkeit dieses Objekts wird 0.
	 *
     * @see #newtonschMachen()
     */
	public void einfluesseZuruecksetzen() {
		phClient.einfluesseZuruecksetzen();
	}

	/**
	 * <b>Physik-Methode</b> - funktioniert nur bei <i>Newton'schen Raum-Objekten</i>
	 *
	 * Setzt einen neuen <i>Impuls</i> auf dieses <code>Raum</code>-Objekt, indem eine
	 * bestimmte <i>Kraft</i> für eine bestimmte <i>Zeit</i> auf dieses Objekt wirkt.
	 *
	 * Es gilt für <b>ausreichend kleines <code>t</code></b>: <code>p = F * t</code>
	 *
	 * Dies ist die grundlegende Berechnung für den Impuls.
	 *
	 * @param kraft		Eine Kraft, die (modellhaft) auf dieses <code>Raum</code>-Objekt wirken soll.
	 *      <b>WICHTIG:</b> Die Einheiten für physikalische Größen innerhalb der Engine entsprechen denen
	 *      aus der klassischen Mechanik. Die Einheit für Kraft ist <code>[N] = [kg * (m / s^2)]</code>
	 * @param t_kraftuebertrag	Die Zeit, über die die obige Kraft auf dieses <code>Raum</code>-Objekt
	 *      wirken soll.
	 *      <b>WICHTIG:</b> Die Einheiten für physikalische Größen innerhalb der Engine entsprechen denen
	 *      aus der klassischen Mechanik. Die Einheit für Zeit ist [s]
	 * @see #newtonschMachen()
	 */
	public void kraftAnwenden(Vektor kraft, float t_kraftuebertrag) {
		phClient.kraftAnwenden(kraft, t_kraftuebertrag);
	}
	
	/**
	 * Setzt die Sichtbarkeit des Objektes.
	 * 
	 * @param sichtbar
	 *            Ob das Objekt sichtbar sein soll oder nicht.<br />
	 *            Ist dieser Wert <code>false</code>, so wird es nicht im Fenster gezeichnet.<br />
	 *            <b>Aber:</b> Es existiert weiterhin ohne Einschraenkungen. <b>Allerdings</b> gilt ein Treffer mit einem unsichtbaren
	 *            Raum-Objekt in der Klasse <code>Physik</code> nicht als Kollision. Unsichtbare Raum-Objekte werden somit bei Trefferkollisionen ausgelassen.
	 * @see #sichtbar()
	 * @see Physik
	 */
	public final void sichtbarSetzen(boolean sichtbar) {
		this.sichtbar = sichtbar;
	}
	
	/**
	 * Gibt an, ob das Raum-Objekt sichtbar ist.
	 * 
	 * @return Ist <code>true</code>, wenn das Raum-Objekt zur Zeit sichtbar ist.
	 * @see #sichtbarSetzen(boolean)
	 */
	public final boolean sichtbar() {
		return this.sichtbar;
	}
	
	/**
	 * Diese Methode ordnet einem String ein Color-Objekt zu.<br />
	 * Hierdurch ist in den Klassen außerhalb der Engine keine awt-Klasse nötig.
	 * 
	 * @param t
	 *            Der Name der Farbe.<br />
	 *            Ein Katalog mit allen moeglichen Namen findet sich im <b>Handbuch</b>
	 * @return Das Farbobjekt zum String; ist Color.black bei unzuordnembaren String
	 */
	public static Color zuFarbeKonvertieren(String t) {
		Color c;
		
		switch (t.toLowerCase(Locale.GERMAN)) {
			case "gelb":
				c = Color.yellow;
				break;
			case "weiss":
				c = Color.white;
				break;
			case "orange":
				c = Color.orange;
				break;
			case "grau":
				c = Color.gray;
				break;
			case "gruen":
				c = Color.green;
				break;
			case "blau":
				c = Color.blue;
				break;
			case "rot":
				c = Color.red;
				break;
			case "pink":
				c = Color.pink;
				break;
			case "magenta":
			case "lila":
				c = Color.magenta;
				break;
			case "cyan":
			case "tuerkis":
				c = Color.cyan;
				break;
			case "dunkelgrau":
				c = Color.darkGray;
				break;
			case "hellgrau":
				c = Color.lightGray;
				break;
			default:
				c = Color.black;
				break;
		}
		
		return DateiManager.ausListe(c);
	}
	
	/**
	 * Erstellt eine Halbdurchsichtige Farbe mit den selben RGB-Werten, wie die eingegebene.<br />
	 * Diese Methode wird intern verwendet.
	 * 
	 * @param c
	 *            Die Farbe, deren im Alphawert gesenkte Instanz erstellt werden soll.
	 */
	public static final Color halbesAlpha(Color c) {
		return DateiManager.ausListe(new Color(c.getRed(), c.getGreen(), c.getBlue(), 178));
	}
	
	/**
	 * Meldet ein Leuchtend-Objekt an dem vorgesehenen LeuchtErsteller Objekt an.<br />
	 * Diese Methode ist dafür vorgesehen, dass sie <b>nur im Konstruktor der dieses Interface implementierenden Instanz aufgerufen wird</b>, und zwar mit dem <code>this</code>-Pointer, sprich:<br />
	 * :<code>super.leuchterAnmelden(this);</code>
	 *
	 * Prinzipiell sollte diese Methode nur innerhalb der Engine aufgerufen werden
	 * 
	 * @param l
	 *            Der anzumeldende Leuchter
	 */
	protected final void leuchterAnmelden(Leuchtend l) {
		macher.add(l);
	}
	
	/**
	 * Meldet ein Leuchtend-Objekt am vorgesehenen LeuchtMacher-Objekt ab.<br />
	 * Prinzipiell sollte diese Methode nur innerhalb der Engine aufgerufen werden
	 * 
	 * @param l
	 *            Der abzumeldende Leuchter
	 */
	protected final void leuchterAbmelden(Leuchtend l) {
		macher.entfernen(l);
	}
	
	/**
	 * Interne Testmethode, die ein mathematisch simples Konzept hat.<br />
	 * Es gibt kein Problem, wenn die Zahlen das selbe Vorzeichen haben
	 * oder wenn eine der beiden Zahlen gleich 0 ist.
	 * 
	 * @return <code>true</code>, falls diese Zahlenkonstellation ein Problem ist, sonst <code>false</code>.
	 */
	protected static boolean problem(int z1, int z2) {
		if (z1 == 0 || z2 == 0) {
			return false;
		}

		return (z1 < 0 ^ z2 < 0);
	}
	
	/**
	 * Die Basiszeichenmethode.<br />
	 * Sie schließt eine Fallabfrage zur Sichtbarkeit ein.
	 * Diese Methode wird bei den einzelnen Gliedern eines Knotens aufgerufen.
	 * 
	 * @param g
	 *            Das zeichnende Graphics-Objekt
	 * @param r
	 *            Das BoundingRechteck, dass die Kameraperspektive Repraesentiert.<br />
	 *            Hierbei soll zunaechst getestet werden, ob das Objekt innerhalb der Kamera liegt, und erst dann gezeichnet werden.
	 * @see #zeichnen(Graphics2D, BoundingRechteck)
	 */
	public final void zeichnenBasic(Graphics2D g, BoundingRechteck r) {
		statisch = (r.x == 0) && (r.y == 0);
		
		if (sichtbar) {
			zeichnen(g, r);
		}
	}
	
	/**
	 * Setzt die Position des Objektes gänzlich neu auf der Zeichenebene.
	 *
	 * Hierbei wird die abstrakte Methode verschieben() und dimension() angewandt, um eine zur
	 * vorherigen Position relative Verschiebung zu erreichen, die an die gewünschte Zielposition fährt.
	 *
	 * <b>ACHTUNG !!!</b>
	 *
	 * Bei den ALLEN Objekten ist die eingegebene Position die links-oben liegende Ecke des die Figur
	 * optimal umschreibenden zu den Fensterbegrenzungen parallelen Rechtecks.
	 *
	 * Das heißt, dass bei Kreisen zum Beispiel <b>nicht</b> die des Mittelpunktes
	 * ist! Hierfür gibt es die Sondermethode <code>mittelpunktSetzen(int x, int y)</code>.
	 * 
	 * @param p
	 *            Der neue Zielpunkt
	 * @see #positionSetzen(float, float)
	 */
	public void positionSetzen(Punkt p) {
		BoundingRechteck r = dimension();
		verschieben(new Vektor(p.x - r.x, p.y - r.y));
	}

	/**
	 * Setzt die Position des Objektes gänzlich neu auf der Zeichenebene.
	 *
	 * Hierbei wird die abstrakte Methode verschieben() und dimension() angewandt, um eine zur
	 * vorherigen Position relative Verschiebung zu erreichen, die an die gewünschte Zielposition fährt.
	 *
	 * <b>ACHTUNG !!!</b>
	 *
	 * Bei den ALLEN Objekten ist die eingegebene Position die links-oben liegende Ecke des die Figur
	 * optimal umschreibenden zu den Fensterbegrenzungen parallelen Rechtecks.
	 *
	 * Das heißt, dass bei Kreisen zum Beispiel <b>nicht</b> die des Mittelpunktes
	 * ist! Hierfür gibt es die Sondermethode <code>mittelpunktSetzen(int x, int y)</code>.
	 *
	 * @param x
	 *            neue <code>x</code>-Koordinate
	 * @param y
	 *            neue <code>y</code>-Koordinate
	 * @see #mittelpunktSetzen(int, int)
	 * @see #positionSetzen(Punkt)
	 */
	public void positionSetzen(float x, float y) {
		this.positionSetzen(new Punkt(x, y));
	}
	
	/**
	 * Verschiebt die Raum-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.
	 *
	 * Diese Methode arbeitet nach dem Mittelpunkt des das Objekt abdeckenden BoundingRechtecks
	 * durch den Aufruf der Methode <code>zentrum()</code>. Daher ist diese Methode in der Anwendung
	 * auf ein Knoten-Objekt nicht unbedingt sinnvoll.
	 * 
	 * @param x
	 *            Die <code>x</code>-Koordinate des neuen Mittelpunktes des Objektes
	 * @param y
	 *            Die <code>y</code>-Koordinate des neuen Mittelpunktes des Objektes
	 * @see #mittelpunktSetzen(Punkt)
	 * @see #verschieben(Vektor)
	 * @see #positionSetzen(float, float)
	 * @see #zentrum()
	 */
	public void mittelpunktSetzen(int x, int y) {
		this.mittelpunktSetzen(new Punkt(x, y));
	}
	
	/**
	 * Verschiebt die Raum-Figur so, dass ihr Mittelpunkt die eingegebenen Koordinaten hat.<br />
	 * Diese Methode Arbeitet nach dem Mittelpunkt des das Objekt abdeckenden BoundingRechtecks durch den Aufruf
	 * der Methode <code>zentrum()</code>. Daher ist diese Methode im Anwand auf ein Knoten-Objekt nicht unbedingt sinnvoll.<br />
	 * Macht dasselbe wie <code>mittelPunktSetzen(p.x, p.y)</code>.
	 * 
	 * @param p
	 *            Der neue Mittelpunkt des Raum-Objekts
	 * @see #mittelpunktSetzen(int, int)
	 * @see #verschieben(Vektor)
	 * @see #positionSetzen(float, float)
	 * @see #zentrum()
	 */
	public void mittelpunktSetzen(Punkt p) {
		this.verschieben(this.zentrum().nach(p));
	}
	
	/**
	 * Methode zum schnellen Herausfinden der Position des Raum-Objektes.<br />
	 * <b>Achtung:</b> Diese Methode gibt nur die Position der <b>linken, oberen Ecke</b> aus fuer mehr Informationen
	 * ist die Methode <code>dimension()</code> zu empfehlen, die mehr Information bietet.
	 * 
	 * @return Die Koordinaten des Punktes der linken, oberen Ecke in Form eines <code>Punkt</code>-Objektes
	 * @see #dimension()
	 */
	public Punkt position() {
		return position;
	}
	
	/**
	 * Methode zum schnellen Herausfinden des Mittelpunktes des Raum-Objektes.
	 * 
	 * @return Die Koordinaten des Mittelpunktes des Objektes
	 * @see #dimension()
	 * @see #position()
	 */
	public Punkt mittelPunkt() {
		BoundingRechteck b = this.dimension();
		return new Punkt(b.x + (b.breite / 2), b.y + (b.hoehe / 2));
	}
	
	/**
	 * Einfache Methode, die die X-Koordinate der linken oberen
	 * Ecke des das <code>Raum</code>-Objekt exakt umrandenden <code>BoundingRechteck</code>'s auf der Zeichenebene zurueckgibt.
	 * 
	 * @return Die die X-Koordinate der linken oberen Ecke auf der Zeichenebene
	 */
	public int positionX() {
		return (int) this.dimension().x;
	}
	
	/**
	 * Einfache Methode, die die Y-Koordinate der linken oberen
	 * Ecke des das <code>Raum</code>-Objekt exakt umrandenden <code>BoundingRechteck</code>'s auf der Zeichenebene zurueckgibt.
	 * 
	 * @return Die die Y-Koordinate der linken oberen Ecke auf der Zeichenebene
	 */
	public int positionY() {
		return (int) this.dimension().y;
	}
	
	/**
	 * Verschiebt das Objekt ohne Bedingungen auf der Zeichenebene.
	 * Dies ist die <b>zentrale</b> Methode zum 
	 * 
	 * @param v
	 *            Der Vektor, der die Verschiebung des Objekts angibt.
	 * @see Vektor
	 * @see #verschieben(int, int)
	 */
	public void verschieben(Vektor v) {
		position = position.verschobeneInstanz(v);
	}
	
	/**
	 * Verschiebt das Objekt.<br />
	 * Hierbei wird nichts anderes gemacht, als <code>verschieben(new Vektor(dX, dY))</code> auszufuehren. Insofern ist diese Methode dafuer gut, sich nicht mit der Klasse Vektor
	 * auseinandersetzen zu muessen.
	 * 
	 * @param dX
	 *            Die Verschiebung in Richtung X
	 * @param dY
	 *            Die Verschiebung in Richtung Y
	 * @see #verschieben(Vektor)
	 */
	public void verschieben(int dX, int dY) {
		this.verschieben(new Vektor(dX, dY));
	}
	
	/**
	 * Test, ob ein anderes Raum-Objekt von diesem geschnitten wird.
	 * 
	 * @param r
	 *            Das Objekt, das auf Kollision mit diesem getestet werden soll.
	 * @return TRUE, wenn sich beide Objekte schneiden.
	 */
	public final boolean schneidet(Raum r) {
		return this.aktuellerCollider().verursachtCollision(position, r.position, r.aktuellerCollider());
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
	public abstract void zeichnen(Graphics2D g, BoundingRechteck r);
	
	/**
	 * Dreht die Zeichenfläche um den Mittelpunkt des Raumes um die gegebenen Grad, bevor mit dem Zeichenn begonnen wird.<br />
	 * <b><i>Diese Methode sollte nicht außerhalb der Engine verwendet werden.</i></b>
	 * 
	 * @see #drehung
	 * @see #gibDrehung()
	 * @see #zeichnen(Graphics2D, BoundingRechteck)
	 * @see #afterRender(Graphics2D)
	 */
	public final void beforeRender(Graphics2D g) {
		lastMiddle = mittelPunkt();
		lastDrehung = Math.toRadians(drehung);

		g.rotate(lastDrehung, lastMiddle.x, lastMiddle.y);
	}
	
	/**
	 * Dreht die Zeichenfläche wieder zurück in den Ausgangszustand.
	 * <b><i>Diese Methode sollte nicht außerhalb der Engine verwendet werden.</i></b>
	 * 
	 * @see #drehung
	 * @see #gibDrehung()
	 * @see #zeichnen(Graphics2D, BoundingRechteck)
	 * @see #beforeRender(Graphics2D)
	 */
	public final void afterRender(Graphics2D g) {
		g.rotate(-lastDrehung, lastMiddle.x, lastMiddle.y);
	}
	
	/**
	 * Prueft, ob ein bestimmter Punkt innerhalb des Raum-Objekts liegt.
	 * 
	 * @param p
	 *            Der Punkt, der auf Inhalt im Objekt getestet werden soll.
	 * @return TRUE, wenn der Punkt innerhalb des Objekts liegt.
	 */
	public final boolean beinhaltet(Punkt p) {
		if (statisch) {
			BoundingRechteck b = Fenster.instanz().getCam().position();
			p = p.verschobeneInstanz(new Vektor(-b.x, -b.y));
		}

		BoundingRechteck[] dim = flaechen();

		for(BoundingRechteck r : dim) {
			if (r.istIn(p)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Methode zum Beschreiben der rechteckigen Flaeche, die dieses Objekt einnimmt.<br />
	 * Diese Methode wird zentral fuer die Trefferkollisionen innerhalb der Engine benutzt und gehoert zu den wichtigsten Methoden der Klasse und der Engine.
	 * 
	 * @return Ein BoundingRechteck mit dem minimal noetigen Umfang, um das Objekt <b>voll einzuschliessen</b>.
	 */
	public abstract BoundingRechteck dimension();
	
	/**
	 * Erzeugt einen neuen Collider für dieses Objekt. Diese Methode approximiert für das Objekt der jeweils implementierenden <code>Raum</code>-Klasse einen
	 * möglichst "guten" Collider; also einen solchen, der das tatsächliche Objekt möglichst genau umfängt, aber auch möglichst wenig
	 * Rechenarbeit beansprucht.
	 * @return	Ein möglichst optimaler Collider für dieses Raum-Objekt.
	 * @see #colliderSetzen(Collider)
	 */
	public abstract Collider erzeugeCollider();
	
	/**
	 * Erzeugt einen Collider auf <i>Lazy</i> Art: Es wird das durch die <code>dimension()</code>-Methode
	 * berechnete <code>BoundingRechteck</code> benutzt, um einen simplen <i>Box-Collider</i> zu erstellen.
	 */
	protected Collider erzeugeLazyCollider() {
		return BoxCollider.fromBoundingRechteck(Vektor.NULLVEKTOR, this.dimension());
	}
	
	/**
	 * Gibt den <i>aktuellen Collider</i> dieses <code>Raum</code>-Objekts zurück.
	 * @return der aktuelle Collider dieses <code>Raum</code>-Objekts, der für die Collision Detection
	 * 			verwendet wird.
	 * @see #colliderSetzen(Collider)
	 * @see #schneidet(Raum)
	 */
	public final Collider aktuellerCollider() {
		if (collider.istNullCollider()) {
			return collider = erzeugeCollider();
		}
		return collider;
	}
	
	/**
	 * Setzt einen neuen Collider für dieses <code>Raum</code>-Objekt. Nach Aufruf dieser Methode ist der <i>standardisierte</i> Collider, der
	 * intern automatisch gesetzt wird (jedoch meist nicht optimal ist), außer Kraft und nur noch der hier übergebene Collider ist für die
	 * <i>Collision Detection</i> relevant.
	 * @param collider	Der neue Collider, der für die Schnitt-Überprüfung verwendet wird.
	 * @see #schneidet(Raum)
	 * @see #boundsUebernehmen(Raum)
	 */
	public void colliderSetzen(Collider collider) {
		this.collider = collider;
	}
	
	/**
	 * Übernimmt für die Collision Detection die Bounds eines anderen <code>Raum</code>-Objektes.
	 * @param boundHilfe	Ein weiteres Raum-Objekt, dessen prinzipiellen Bounds übernommen werden sollen.<br />
	 * <br /><b>WICHTIG!</b>
	 * Die Entfernung des als Parameter übergebenen <code>Raum</code>-Objektes vom <i>Ursprung</i> der Zeichenebene aus
	 * entspricht dem <i>Offset</i> des Colliders <b>relativ zu diesem <code>Raum</code>-Objekt</b>.
	 * @see #colliderSetzen(Collider)
	 */
	public void boundsUebernehmen(Raum boundHilfe) {
		Collider c = boundHilfe.aktuellerCollider();
		c.offsetSetzen(boundHilfe.position().alsVektor());
		this.collider = c;
	}
	
	/**
	 * Berechnet exakter alle Rechteckigen Flaechen, auf denen dieses Objekt liegt.<br />
	 * Diese Methode wird von komplexeren Gebilden, wie geometrischen oder Listen ueberschrieben.
	 * 
	 * @return Alle Rechtecksflaechen, auf denen dieses Objekt liegt.
	 *         Ist standartisiert ein Array der Groesse 1 mit der <code>dimension()</code> als Inhalt.
	 * @see Knoten
	 */
	public BoundingRechteck[] flaechen() {
		return new BoundingRechteck[] {
			this.dimension()
		};
	}
	
	/**
	 * Berechnet, ob dieses Raum-Objekt <b>exakt ueber einem zweiten steht</b>.<br />
	 * Dies waere fuer die Engine ein Stehen auf diesem.
	 * 
	 * @param m
	 *            Das Raum-Objekt, fuer das getestet werden soll, ob dieses auf ihm steht,
	 * @return <code>true</code>, wenn dieses Objekt auf dem eingegeben steht, sonst <code>false</code>
	 */
	public boolean stehtAuf(Raum m) {
		return this.dimension().stehtAuf(m.dimension());
	}
	
	/**
	 * Berechnet das Zentrum des Raum-Objekts als Punkt auf der Zeichenebene.
	 *
	 * Das Zentrum wird über die Methode <code>dimension()</code> berechnet,
	 * und zwar über die Methode des resultierenden BoundingRechtecks:<br />
	 * <code>dimension().zentrum()</code>
	 * 
	 * @return Zentrum dieses Raumobjekts
	 */
	public Punkt zentrum() {
		return this.dimension().zentrum();
	}
	
	/**
	 * Berechnet den Höhenunterschied zwischen dem Fuß des höheren und dem Kopf des tieferen Raum-Objekts.
	 * 
	 * @param m
	 *            Das Raum-Objekt, dessen Höhenunterschied zu diesem gefunden werden soll
	 * @return Der <b>absolute (also niemals negative)</b> Unterschied in der Höhe zwiscchen den beiden Objekten. <b>Überlagern sie sich, so ist der Rückgabewert 0</b>!
	 */
	public int hoehenUnterschied(Raum m) {
		return (int) this.dimension().hoehenUnterschied(m.dimension());
	}
	
	/**
	 * Diese Methode loescht alle eventuell vorhandenen Referenzen innerhalb der Engine auf dieses Objekt, damit es problemlos geloescht werden kann.<br />
	 * <b>Achtung:</b> zwar werden hierdurch alle Referenzen geloescht, die <b>nur innerhalb</b> der Engine liegen (dies betrifft vor allem Animationen etc), jedoch nicht die
	 * innerhalb eines <code>Knoten</code>-Objektes!!!!!!!!!<br />
	 * Das heisst, wenn das Objekt an einem Knoten liegt (was <b>immer der Fall ist, wenn es auch gezeichnet wird (siehe die Wurzel des Fensters)</b>), muss es trotzdem
	 * selbst geloescht werden, <b>dies erledigt diese Methode nicht!!</b>.
	 */
	public void loeschen() {
		animationsManager.animationBeendenVon(this);
	}
	
	/**
	 * Prüft, ob dieses Raum-Objekt in ener bestimmten festen Fläche ist.
	 * 
	 * @param r
	 *            Die kritische Fläche, auf deren schneiden mit diesem Raum-Objekt getestet werden soll.
	 * @return <code>true</code>, wenn dieses Raum-Objekt sich mit dem BoundingRechteck schneidet, sonst <code>false</code>.
	 */
	public boolean inFlaeche(BoundingRechteck r) {
		BoundingRechteck[] fl = this.flaechen();
		for(BoundingRechteck fr : fl) {
			if (fr.schneidetBasic(r)) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Dreht ein Objekt auf die angegebene Gradzahl um den Mittelpunkt des Raumes.
	 * 
	 * @param grad
	 *            Grad, auf die gedreht werden soll.
	 */
	public void drehenAbsolut(double grad) {
		this.drehung = grad;
	}
	
	/**
	 * Gibt die aktuelle Drehung des Raumes in Grad zurück.
	 * 
	 * @return Gibt die aktuelle Drehung des Raumes in Grad zurück.
	 */
	public double gibDrehung() {
		return drehung;
	}
	
	/**
	 * Dreht ein Objekt um die angegebene Gradzahl um den Mittelpunkt des Raumes.
	 * 
	 * @param grad
	 *            Grad, um die gedreht werden soll.
	 */
	public void drehenRelativ(double grad) {
		this.drehung -= grad;
	}
	
	/**
	 * Hilfsmethode für die Sortierung der Räume nach dem Z-Index.
	 * <b><i>Diese Methode sollte nicht außerhalb der Engine verwendet werden.</i></b>
	 * 
	 * @see #zIndex
	 * @see #zIndex(int)
	 */
	@Override
	public int compareTo(Raum r) {
		if (zIndex < r.zIndex) {
			return 1;
		}

		if (zIndex > r.zIndex) {
			return -1;
		}

		return 0;
	}
}

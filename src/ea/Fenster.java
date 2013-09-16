/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

import ea.graphic.SimpleGraphic;
import ea.graphic.Vektor;
import ea.graphic.geo.BoundingRechteck;
import ea.graphic.geo.Punkt;
import ea.graphic.geo.Raum;
import ea.input.Maus;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

/**
 * Das Fenster als Oberfenster.<br />
 * In ihm 'faengt sich die Maus, sie kann also das Fenster nicht mehr nach dem
 * Betreten verlassen.
 * 
 * @author Michael Andonie, Niklas Keller
 * @version 3.0 (September 2013)
 */
public class Fenster extends Frame {
	private static final long serialVersionUID = 1L;

	/**
	 * Gibt an, ob das Fenster im Vollbildmodus arbeitet
	 */
	private final boolean vollbild;

	/**
	 * Gibt an, ob die aktuelle (relative) Maus innerhalb des passablen
	 * Fensterbereiches liegt.<br />
	 * Gibt es keine solche ist dieser Wert irrelevant.
	 */
	private volatile boolean mausAusBild = false;

	/**
	 * Gibt an, ob der gerade Verarbeitete Klick mitzaehlt, also vom Benutzer
	 * selbst gemacht wurde.
	 */
	private boolean zaehlt = true;

	/**
	 * Das Panel, das fuer die Zeichenroutine verantwortlich ist.
	 */
	private Zeichner zeichner;

	/**
	 * Die Liste aller TastenListener.
	 */
	private java.util.ArrayList<TastenReagierbar> listener = new java.util.ArrayList<TastenReagierbar>();

	/**
	 * Die Liste aller TastenGedrueckt-Listener
	 */
	private java.util.ArrayList<TastenGedruecktReagierbar> gedrListener = new java.util.ArrayList<TastenGedruecktReagierbar>();

	/**
	 * Die Liste aller TastenLosgelassen-Listener
	 */
	private java.util.ArrayList<TastenLosgelassenReagierbar> losListener = new java.util.ArrayList<TastenLosgelassenReagierbar>();

	/**
	 * Die Maus, die in dem Fenster sichtbar ist.<br />
	 * Ist diese Referenz null, kann man keine Maus sehen.
	 */
	private volatile Maus maus = null;

	/**
	 * Ein Roboter, der die Maus bei austritt im Fenster haelt.
	 */
	private Robot robot;

	/**
	 * Das Bild der Maus.
	 */
	private Raum mausBild;

	/**
	 * Ein boolean-Array als die Tastentabelle, nach der fuer die
	 * gedrueckt-Methoden vorgegangen wird.<br />
	 * Ist ein Wert <code>true</code>, so ist die Taste dieses Indexes
	 * heruntergedrueckt.
	 */
	private volatile boolean[] tabelle;

	/**
	 * Statische Hilfsinstanz zur Vereinfachung der Frameabhaengigen Abfragen
	 */
	public static Fenster instanz = null;

	/**
	 * Konstruktor fuer Objekte der Klasse Fenster.
	 * 
	 * @param breite
	 *            Die Breite des Fensters. (Bei erfolgreichem Vollbild die neue
	 *            Standartbildschirmbreite)
	 * @param hoehe
	 *            Die Hoehe des Fensters. (Bei erfolgreichem Vollbild die neue
	 *            Standartbildschirmhoehe)
	 * @param titel
	 *            Der Titel, der auf dem Fenster gezeigt wird (Auch bei Vollbild
	 *            nicht zu sehen).Wenn kein Titel erwuenscht ist, kann ein
	 *            leerer String (<code>""</code>) eingegeben werden.
	 * @param vollbild
	 *            Ob das Fenster ein echtes Vollbild sein soll, sprich den
	 *            gesamten Bildschirm ausfuellen soll und nicht mehr wie ein
	 *            Fenster aussehen soll.<br />
	 *            Es kann sein, dass Vollbild zum Beispiel aufgrund eines
	 *            Veralteten Javas oder inkompatiblen PCs nicht moeglich ist,
	 *            <b>in diesem Fall wird ein normales Fenster mit den eingegeben
	 *            Werten erzeugt</b>.<br />
	 *            Daher ist das x/y - Feld eine Pflichteingabe.
	 * @param fensterX
	 *            Die X-Koordinate des Fensters auf dem Computerbildschirm.
	 * @param fensterY
	 *            Die Y-Koordinate des Fensters auf dem Computerbildschirm.
	 */
	public Fenster(int breite, int hoehe, String titel, boolean vollbild,
			int fensterX, int fensterY) {
		super(titel);

		int WINDOW_FRAME = 1;
		int WINDOW_FULLSCREEN = 2;
		int WINDOW_FULLSCREEN_FRAME = 4;

		int windowMode = vollbild ? WINDOW_FULLSCREEN : WINDOW_FRAME;

		tabelle = new boolean[45];

		this.vollbild = vollbild;
		this.setSize(breite, hoehe);
		this.setResizable(false);

		// ------------------------------------- //

		GraphicsEnvironment env = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = env.getScreenDevices();
		Dimension screenSize = getToolkit().getScreenSize();

		if (vollbild) {
			if (devices[0].isFullScreenSupported()) {
				breite = screenSize.width;
				hoehe = screenSize.height;

				windowMode = WINDOW_FULLSCREEN;
			} else {
				System.err.println("Achtung!");
				System.err
						.println("Vollbild war nicht m�glich, weil dieser PC dies nicht unterst�tzt!");

				windowMode = WINDOW_FULLSCREEN_FRAME;
			}
		} else {
			int x = screenSize.width / 8, y = screenSize.height / 8;

			if (fensterX > -1 && fensterX < screenSize.width)
				x = fensterX;
			if (fensterY > -1 && fensterY < screenSize.height)
				y = fensterY;

			setLocation(x, y);

			windowMode = WINDOW_FRAME;
		}

		// ------------------------------------- //

		if (windowMode == WINDOW_FULLSCREEN) {
			setUndecorated(true);
			devices[0].setFullScreenWindow(this);

			// Resolution-Check
			boolean success = false;

			if (devices[0].isDisplayChangeSupported()) {
				DisplayMode[] displayMode = devices[0].getDisplayModes();

				System.out.println("DisplayModes: " + displayMode.length);

				for (int i = 0; i < displayMode.length; i++) {
					System.out.println((i + 1) + ": " + "Breite: "
							+ displayMode[i].getWidth() + ", H�he: "
							+ displayMode[i].getHeight());

					if (displayMode[i].getWidth() == breite
							&& displayMode[i].getHeight() == hoehe) {
						devices[0].setDisplayMode(new DisplayMode(breite,
								hoehe, displayMode[i].getBitDepth(),
								displayMode[i].getRefreshRate()));
						System.out.println("SET!");
						success = true;
						break;
					}
				}

				if (!success) {
					System.err.println("Achtung!");
					System.err
							.println("Die angegebene Aufl�sung wird von diesem Bildschirm nicht unterst�tzt!");
					System.err
							.println("Nur besondere Aufl�sungen sind m�glich, z.B. 800 x 600.");
					System.err
							.println("Diese sollten in der Konsole vor dieser Fehlerausgabe gelistet sein.");
				}
			} else {
				System.err
						.println("Dieser Bildschirm unterst�tzt keine Aufl�sungs�nderung!");
			}

			if (!success) {
				System.err
						.println("Die gew�nschte Aufl�sung wird nicht vom Hauptbildschirm des Computers unterst�tzt!");
			}
		}

		else if (windowMode == WINDOW_FULLSCREEN_FRAME) {
			setVisible(true);

			setBounds(env.getMaximumWindowBounds());
			setExtendedState(MAXIMIZED_BOTH);

			Insets insets = getInsets();
			breite = getWidth() - insets.left - insets.right;
			hoehe = getHeight() - insets.top - insets.bottom;
		}

		else if (windowMode == WINDOW_FRAME) {
			setVisible(true);
		}

		zeichner = new Zeichner(breite, hoehe, new Kamera(breite, hoehe,
				new Zeichenebene()));
		add(zeichner);
		zeichner.init();

		if (windowMode == (WINDOW_FULLSCREEN_FRAME | WINDOW_FRAME))
			pack();

		// ------------------------------------- //

		// Der Roboter
		try {
			robot = new Robot(devices[0]);
		} catch (AWTException e) {
			System.err.println("Achtung!");
			System.err.println("Es war nicht m�glich ein GUI-Controlobjekt zu erstelllen!");
			System.err.println("Zentrale Funktionen der Maus-Interaktion werden nicht funktionieren.");
			System.err.println("Grund: Dies liegt an diesem Computer.");
		}

		// Die Listener
		addKeyListener();
		addMouseListener();
		addMouseMotionListener();
		addWindowListener(new Adapter(this));
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				e.getWindow().setVisible(false);
				e.getWindow().dispose();
			}
		});
		addCursor();

		// Mausfang
		Manager.standard.anmelden(new Ticker() {
			public void tick() {
				if (hatMaus() && !maus.absolut() && maus.bewegend()) {
					try {
						BoundingRechteck r = mausBild.dimension();
						Punkt hs = maus.hotSpot();
						BoundingRechteck praeferenz = mausPlatz();
						Punkt p = new Punkt(r.x + hs.x, r.y + hs.y);
						if (!praeferenz.istIn(p) && maus.bewegend()) {
							getCam().verschieben(
									(new Vektor(praeferenz.zentrum(), p)
											.teilen(20)));
						}
					} catch (NullPointerException e) {
						// Einfangen der maximal einmaligen RuntimeException zum
						// sichern
						// ???
					}
				}
				for (TastenGedruecktReagierbar t : gedrListener) {
					for (int i = 0; i < tabelle.length; i++) {
						if (tabelle[i]) {
							t.tasteGedrueckt(i);
						}
					}
				}
			}
		}, 50);

		instanz = this;
	}

	private void addKeyListener() {
		KeyListener keyListener = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				tastenAktion(e);
			}

			public void keyReleased(KeyEvent e) {
				int i = zuordnen(e.getKeyCode());

				if (i == -1)
					return;

				tabelle[i] = false;

				for (TastenLosgelassenReagierbar l : losListener)
					l.tasteLosgelassen(i);
			}

			public void keyTyped(KeyEvent e) {
			}
		};
		
		addKeyListener(keyListener);
		zeichner.addKeyListener(keyListener);
	}

	private void addMouseListener() {
		this.addMouseListener(new MouseListener() {
			public void mouseExited(MouseEvent e) {
			}

			public void mouseEntered(MouseEvent e) {
				if (hatMaus()) {
					Point po = getLocation();

					int startX = (getWidth() / 2);
					int startY = (getHeight() / 2);

					robot.mouseMove(startX + po.x, startY + po.y);
				}

				zaehlt = false;

				robot.mousePress(InputEvent.BUTTON1_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
			}

			public void mouseReleased(MouseEvent e) {
				mausAktion(e, true);
			}

			public void mousePressed(MouseEvent e) {
				mausAktion(e, false);
			}

			public void mouseClicked(MouseEvent e) {
			}
		});
	}

	private void addMouseMotionListener() {
		this.addMouseMotionListener(new MouseMotionListener() {
			public void mouseMoved(MouseEvent e) {
				mausBewegung(e);
			}

			public void mouseDragged(MouseEvent e) {
				mausBewegung(e);
			}
		});
	}

	private void addCursor() {
		this.setCursor(getToolkit().createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
				new Point(0, 0), "NOCURSOR"));
	}

	/**
	 * Einfacher Alternativkonstruktor.<br />
	 * Erstellt ein normales Fenster mit der eingegeben Groesse.
	 * 
	 * @param x
	 *            Die Breite
	 * @param y
	 *            Die Hoehe
	 */
	public Fenster(int x, int y) {
		this(x, y, "EngineAlpha - Ein Projekt von Michael Andonie", false, 50,
				50);
	}

	/**
	 * Minimiert das Fenster (bringt es in die Taskleiste).
	 */
	public void minimieren() {
		setState(ICONIFIED);
	}

	/**
	 * Maximiert das Fenster (bringt es aus der Taskleiste wieder auf den
	 * Bildschirm)
	 */
	public void maximieren() {
		setState(NORMAL);
	}

	/**
	 * Gibt zurueck, ob dieses Fenster ein Vollbild ist oder nicht.
	 * 
	 * @return <code>true</code>, wenn das Fenster ein Vollbild ist, sonst
	 *         <code>false</code>.
	 */
	public boolean vollbild() {
		return vollbild;
	}

	/**
	 * Meldet den hintergrund dieses Fensters und damit des Spiels an.<br />
	 * Gibt es bereits einen, so wird dieser fortan nicht mehr gezeichnet,
	 * dafuer nun dieser. Sollten mehrere Objekte erwuenscht sein, gezeichnet zu
	 * werden, so empfiehlt es sich, diese in einem <code>Knoten</code>-Objekt
	 * zu sammeln und dann anzumelden.<br />
	 * <b>Achtung!</b><br />
	 * Diese Objekte sollten nicht an der Physik angemeldet werden, dies fuehrt
	 * natuerlich zu ungewollten Problemen!
	 * 
	 * @param hintergrund
	 *            Der anzumeldende Hintergrund
	 */
	public void hintergrundAnmelden(Raum hintergrund) {
		zeichner.hintergrundAnmelden(hintergrund);
	}

	/**
	 * Meldet einen TastenReagierbar - Listener an.
	 * 
	 * @param t
	 *            Der neu anzumeldende Listener.
	 */
	public void anmelden(TastenReagierbar t) {
		if (t == null) {
			System.err.println("Der Listener war null !!!");
			return;
		}

		listener.add(t);
	}

	/**
	 * Meldet einen TastenGedruecktReagierbar-Listener an.<br />
	 * <b>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ACHTUNG!!!!!!!!!!!!!!!!!!!!!!!!
	 * !!!!!!!!!!!!!!!</b><br />
	 * TastenReagierbar und TastenGedruecktReagierbar sind 2 vollkommen
	 * unterschiedliche Interfaces! Das eine wird bei jedem Druck aufs neue
	 * aktiviert, waehrend das andere gleichmaessig aufgerufen wird, solange die
	 * Taste heruntergedrueckt ist.
	 * 
	 * @param t
	 *            Der listener, der ab sofort ueber gedrueckte Tasten informiert
	 *            wird.
	 */
	public void tastenGedruecktAnmelden(TastenGedruecktReagierbar t) {
		if (t == null) {
			System.err.println("Der Listener war null !!!");
			return;
		}

		gedrListener.add(t);
	}

	/**
	 * Meldet einen TastenGedruecktReagierbar-Listener an.<br />
	 * Diese Methode warappt lediglich
	 * <code>tastenGedruecktAnmelden(TastenGedruecktReagierbar)</code> und dient
	 * der Praevention unnoetiger Compilermeldungen wegen nichtfindens einer
	 * Methode.
	 * 
	 * @param t
	 *            Der listener, der ab sofort ueber gedrueckte Tasten informiert
	 *            wird.
	 * @see tastenGedruecktAnmelden(TastengedruecktReagierbar)
	 */
	public void tastenGedruecktReagierbarAnmelden(TastenGedruecktReagierbar t) {
		this.tastenGedruecktAnmelden(t);
	}

	/**
	 * Meldet einen TastenLosgelassenReagierbar-Listener an.<br />
	 * <b>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ACHTUNG!!!!!!!!!!!!!!!!!!!!!!!!
	 * !!!!!!!!!!!!!!!</b><br />
	 * TastenReagierbar und TastenLosgelassenReagierbar sind 2 vollkommen
	 * unterschiedliche Interfaces! Das eine wird beim Runterdruecken, das
	 * andere beim Loslassen der Tasten aktiviert.
	 */
	public void tastenLosgelassenAnmelden(TastenLosgelassenReagierbar t) {
		if (t == null) { // TODO Exception? => breaks backwards comp.
			System.err.println("Der Listener war null !!!");
			return;
		}

		losListener.add(t);
	}

	/**
	 * Meldet einen TastenLosgelassenReagierbar-Listener an als exakt parallele
	 * Methode zu <code>tastenLosgelassenAnmelden()</code>, jedoch eben ein
	 * etwas laengerer, aber vielleicht auch logischerer Name; fuehrt jedoch
	 * exakt die selbe Methode aus!<br />
	 * <b>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ACHTUNG!!!!!!!!!!!!!!!!!!!!!!!!
	 * !!!!!!!!!!!!!!!</b><br />
	 * <code>TastenReagierbar</code> und
	 * <code>TastenLosgelassenReagierbar</code> sind 2 vollkommen
	 * unterschiedliche Interfaces! Das eine wird beim Runterdruecken, das
	 * andere beim Loslassen der Tasten aktiviert.
	 * 
	 * @see #tastenLosgelassenAnmelden(TastenLosgelassenReagierbar)
	 */
	public void tastenLosgelassenReagierbarAnmelden(
			TastenLosgelassenReagierbar t) {
		this.tastenLosgelassenAnmelden(t);
	}

	/**
	 * Meldet eine Maus an.<br />
	 * Im Gegensatz zu den TastenReagierbar-Listenern kann nur eine Maus am
	 * Fenster angemeldet sein.
	 * 
	 * @param m
	 *            Die anzumeldende Maus
	 */
	public void anmelden(Maus m) {
		if (hatMaus()) {
			System.err.println("Es ist bereits eine Maus angemeldet!");
		} else {
			maus = m;

			BoundingRechteck r = maus.getImage().dimension();
			maus.getImage().positionSetzen(((getWidth() - r.breite) / 2),
					(getHeight() - r.hoehe) / 2);
			// mausBild = new Bild((d.width-i.getWidth(this))/2,
			// (d.height-i.getHeight(this))/2, i);
			mausBild = maus.getImage();

			zeichner.anmelden(mausBild);
		}
	}

	/**
	 * Loescht das Maus-Objekt des Fensters.<br />
	 * Hatte das Fenster keine, ergibt sich selbstredend keine Aenderung.
	 */
	public void mausLoeschen() {
		this.setCursor(getToolkit().createCustomCursor(
				new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB),
				new Point(0, 0), "NOCURSOR"));
		maus = null;
	}

	/**
	 * Testet, ob eine Maus im Spiel vorhanden ist.
	 * 
	 * @return TRUE, wenn eine Maus im Spiel ist.
	 */
	public boolean hatMaus() {
		return (maus != null);
	}

	/**
	 * Gibt an, ob die Maus den Bildschirm bewegen kann.
	 * 
	 * @return TRUE, wenn die Maus den Bildschirm bewegen kann, FALSE, wenn
	 *         nicht.<br />
	 *         Ist keine Maus angemeldet, ist das Ergebnis ebenfalls FALSE.
	 */
	public boolean mausBewegt() {
		if (hatMaus()) {
			return maus.bewegend();
		}

		return false;
	}

	/**
	 * @return Die Kamera, passend zu diesem Fenster
	 */
	public Kamera getCam() {
		return zeichner.cam();
	}

	/**
	 * @return Der Statische Basisknoten.
	 */
	public Knoten getStatNode() {
		return zeichner.statNode();
	}
	
	/**
	* Gibt die Maus aus.
	* @return Die aktuelle Maus. Kann <code>null</code> sein!!
	*/
    public Maus getMaus() {
    	return maus;
    }

	/**
	 * Gibt die Fenstermasse in einem BoundingRechteck an.
	 * 
	 * @return ein BoundingRechteck mit Position (0|0), dessen Hoehe & Breite
	 *         die Masse des Fensters in Pixel angeben.
	 * @see ea.graphic.geo.BoundingRechteck
	 */
	public BoundingRechteck fenstermasse() {
		return zeichner.masse();
	}

	/**
	 * Statische Methode zum Oeffentlichen Berechnen der Fontmetriken des
	 * offenen Fensters.
	 * 
	 * @param f
	 *            Der zu ueberpruefende Font
	 * @return Das zu dem Font und aktiven Fenster gehoerende FontMetrics-Objekt
	 */
	public static FontMetrics metrik(Font f) {
		return instanz.getFontMetrics(f);
	}

	/**
	 * Gibt die aktuellste Instanz dieser KLasse wieder.
	 * 
	 * @return Das aktuellste Fenster
	 */
	public static Fenster instanz() {
		return instanz;
	}

	/**
	 * Gibt den Zeichner des Fensters aus.
	 * 
	 * @return Der Zeichner des Fensters.
	 */
	public Zeichner zeichner() {
		return zeichner;
	}

	/**
	 * Entfernt ein simples Grafikobjekt.
	 * 
	 * @param g
	 *            Das darzustellende Grafikobjekt
	 */
	public void removeSimple(SimpleGraphic g) {
		zeichner.removeSimple(g);
	}

	/**
	 * Fuellt ein simples Grafikobjekt in die anzeige.
	 * 
	 * @param g
	 *            Das darzustellende Grafikobjekt.
	 */
	public void fillSimple(SimpleGraphic g) {
		zeichner.addSimple(g);
	}

	/**
	 * Loescht das Fenster und terminiert damit das Spiel.<br />
	 * <b>Daher nur dann einsetzen, wenn die Anwendung beendet werden soll!! Der
	 * vorherige Zustand ist nicht wiederherstellbar!!</b><br />
	 * Als alternative Methode zum ausschliesslichen Loeschen des Fensters steht
	 * <code>softLoeschen()</code> zur Verfuegung.
	 */
	public void loeschen() {
		this.setVisible(false);
		this.dispose();
		System.exit(0);
	}

	/**
	 * Faehrt das Fenster runter, ohne die virtuelle Maschine zu beenden.
	 */
	public void softLoeschen() {
		zeichner.kill();
		zeichner = null;

		setVisible(false);
		dispose();
	}

	/**
	 * Gibt das gespeicherte Bild-Objekt der Maus wieder.
	 * 
	 * @return Das Bild mit seiner Position und Groesse von der Maus.
	 */
	public Raum mausBild() {
		return this.mausBild;
	}

	/**
	 * @return Das BoundingRechteck, dass den Spielraum der Maus ohne Einbezug
	 *         der Relativen Koordinaten (Kameraposition)<br />
	 *         Das Rechteck ist die Masse des Fensters mal 3/4.<br />
	 *         Dies ist natuerlich nur dann im Fenster gebraucht, wenn eine
	 *         relative Maus angemeldet ist.
	 */
	private BoundingRechteck mausPlatz() {
		Dimension d = this.getSize();
		int x = (d.width / 4) * 3;
		int y = (d.height / 4) * 3;
		return new BoundingRechteck((d.width / 8), (d.height / 8), x, y);
	}

	/**
	 * Deaktiviert den eventuell vorhandenen gemerkten Druck auf allen Tasten.<br />
	 * Wird innerhalb der Engine benutzt, sobald das Fenster deaktiviert etc.
	 * wurde.
	 */
	public void druckAufheben() {
		for (int i = 0; i < tabelle.length; i++) {
			tabelle[i] = false;
		}
	}

	/**
	 * Diese Methode wird ausgefuehrt, wenn die Maus bewegt wird.
	 * 
	 * @param e
	 *            Das ausloesende Event
	 */
	private void mausBewegung(MouseEvent e) {
		if (hatMaus()) {
			int startX = getWidth() / 2;
			int startY = getHeight() / 2;
			Point loc = getLocation();
			Point click = e.getPoint();

			if (maus.bewegend()) {
				if (maus.absolut()) {
					getCam().verschieben(
							new Vektor(click.x - startX, click.y - startY));
				}
			}

			if (!maus.absolut()) {
				int x = click.x - startX;
				int y = click.y - startY;

				BoundingRechteck bounds = mausBild.dimension();
				Punkt spot = maus.hotSpot();
				Punkt hx = new Punkt(bounds.x + spot.x + x, bounds.y + spot.y);
				Punkt hy = new Punkt(bounds.x + spot.x, bounds.y + spot.y + y);

				if (!zeichner.masse().istIn(hx))
					x = 0;
				if (!zeichner.masse().istIn(hy))
					y = 0;

				mausBild.verschieben(new Vektor(x, y));
			}

			robot.mouseMove(startX + loc.x, startY + loc.y);
		}
	}

	/**
	 * Diese Methode wird immer dann ausgefuehrt, wenn ein einfacher Linksklick
	 * der Maus ausgefuehrt wird.
	 * 
	 * @param e
	 *            Das MausEvent
	 * @paran losgelassen Ist dieser Wert TRUE, wurde die Maus eigentlich
	 *        losgelassen und nicht geklickt.
	 */
	private void mausAktion(MouseEvent e, boolean losgelassen) {
		if (!zaehlt) {
			zaehlt = true;
			return;
		}

		// Linksklick? 1: Links - 2: Mausrad? - 3: Rechts
		final boolean links = !(e.getButton() == MouseEvent.BUTTON3);

		if (hatMaus()) {
			if (!maus.absolut()) {
				BoundingRechteck r = mausBild.dimension();
				Punkt p = maus.hotSpot();

				maus.klick(r.x + p.x + getCam().getX(), r.y + p.y
						+ getCam().getY(), links, losgelassen); // Mit
																// zur�ckrechnen
																// auf die
																// Bildebene!
			} else {
				Dimension dim = this.getSize();
				int startX = (dim.width / 2);
				int startY = (dim.height / 2);
				maus.klick(startX + getCam().getX(), startY + getCam().getY(),
						links, losgelassen);
			}
		}
	}

	/**
	 * Die Listener-Methode, die vom Fenster selber bei jeder gedrueckten Taste
	 * aktiviert wird.<br />
	 * Hiebei wird die Zuordnung zu einer Zahl gemacht, und diese dann an alle
	 * Listener weitergereicht, sofern die Taste innerhalb der Kennung des
	 * Fensters liegt.<br />
	 * Hierzu: Die Liste der Tasten mit Zuordnung zu einem Buchstaben; sie ist
	 * im <b>Handbuch</b> festgehalten.
	 * 
	 * @param e
	 *            Das ausgeloeste KeyEvent zur Weiterverarbeitung.
	 */
	private void tastenAktion(KeyEvent e) {
		int z = zuordnen(e.getKeyCode());
		if (z == -1) {
			return;
		}
		if (tabelle[z]) {
			return;
		}
		for (TastenReagierbar r : listener) {
			r.reagieren(z);
		}
		tabelle[z] = true;
	}

	/**
	 * Ordnet vom JAVA-KeyCode System in das EA-System um.
	 * 
	 * @param keyCode
	 *            Der JAVA-KeyCode
	 * @return Der EA-KeyCode
	 */
	public int zuordnen(int keyCode) {
		int z = -1;
		// ANALYSIS
		// <editor-fold defaultstate="collapsed" desc="Fallunterscheidung">
		switch (keyCode) {
		case KeyEvent.VK_A:
			z = 0;
			break;
		case KeyEvent.VK_B:
			z = 1;
			break;
		case KeyEvent.VK_C:
			z = 2;
			break;
		case KeyEvent.VK_D:
			z = 3;
			break;
		case KeyEvent.VK_E:
			z = 4;
			break;
		case KeyEvent.VK_F:
			z = 5;
			break;
		case KeyEvent.VK_G:
			z = 6;
			break;
		case KeyEvent.VK_H:
			z = 7;
			break;
		case KeyEvent.VK_I:
			z = 8;
			break;
		case KeyEvent.VK_J:
			z = 9;
			break;
		case KeyEvent.VK_K:
			z = 10;
			break;
		case KeyEvent.VK_L:
			z = 11;
			break;
		case KeyEvent.VK_M:
			z = 12;
			break;
		case KeyEvent.VK_N:
			z = 13;
			break;
		case KeyEvent.VK_O:
			z = 14;
			break;
		case KeyEvent.VK_P:
			z = 15;
			break;
		case KeyEvent.VK_Q:
			z = 16;
			break;
		case KeyEvent.VK_R:
			z = 17;
			break;
		case KeyEvent.VK_S:
			z = 18;
			break;
		case KeyEvent.VK_T:
			z = 19;
			break;
		case KeyEvent.VK_U:
			z = 20;
			break;
		case KeyEvent.VK_V:
			z = 21;
			break;
		case KeyEvent.VK_W:
			z = 22;
			break;
		case KeyEvent.VK_X:
			z = 23;
			break;
		case KeyEvent.VK_Y:
			z = 24;
			break;
		case KeyEvent.VK_Z:
			z = 25;
			break;
		case KeyEvent.VK_UP:
			z = 26;
			break;
		case KeyEvent.VK_RIGHT:
			z = 27;
			break;
		case KeyEvent.VK_DOWN:
			z = 28;
			break;
		case KeyEvent.VK_LEFT:
			z = 29;
			break;
		case KeyEvent.VK_SPACE:
			z = 30;
			break;
		case KeyEvent.VK_ENTER:
			z = 31;
			break;
		case KeyEvent.VK_ESCAPE:
			z = 32;
			break;
		case KeyEvent.VK_0:
			z = 33;
			break;
		case KeyEvent.VK_1:
			z = 34;
			break;
		case KeyEvent.VK_2:
			z = 35;
			break;
		case KeyEvent.VK_3:
			z = 36;
			break;
		case KeyEvent.VK_4:
			z = 37;
			break;
		case KeyEvent.VK_5:
			z = 38;
			break;
		case KeyEvent.VK_6:
			z = 39;
			break;
		case KeyEvent.VK_7:
			z = 40;
			break;
		case KeyEvent.VK_8:
			z = 41;
			break;
		case KeyEvent.VK_9:
			z = 42;
			break;
		case KeyEvent.VK_PLUS:
			z = 43;
			break;
		case KeyEvent.VK_MINUS:
			z = 44;
			break;
		}// </editor-fold>
		return z;
	}
}
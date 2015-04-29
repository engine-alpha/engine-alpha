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

package ea.internal.gui;

import ea.*;
import ea.internal.gra.Zeichenebene;
import ea.internal.gra.Zeichner;
import ea.internal.util.Logger;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Das Fenster als Oberfenster.<br /> In ihm fängt sich die Maus, sie kann also das Fenster nicht
 * mehr nach dem Betreten verlassen.
 *
 * @author Michael Andonie, Niklas Keller <me@kelunik.com>
 */
public class Fenster extends Frame {
	private static final long serialVersionUID = 1L;

	/**
	 * Statische Hilfsinstanz zur Vereinfachung der frameabhängigen Abfragen
	 */
	public static Fenster instanz = null;

	/**
	 * Counter, der die Anzahl der effektiv vorhandenen Frames zählt.
	 */
	private static volatile int frameCount = 0;

	/**
	 * Gibt an, ob das Fenster im Vollbildmodus arbeitet
	 */
	private final boolean vollbild;

	/**
	 * Das Panel, das fuer die Zeichenroutine verantwortlich ist.
	 */
	private final Zeichner zeichner;

	/**
	 * Die Liste aller TastenListener.
	 */
	private final java.util.List<TastenReagierbar> listener = new ArrayList<>();

	/**
	 * Die Liste aller TastenLosgelassen-Listener
	 */
	private final List<TastenLosgelassenReagierbar> losListener = new ArrayList<>();

	/**
	 * Gibt an, ob die aktuelle (relative) Maus innerhalb des passablen Fensterbereiches liegt.<br
	 * /> Gibt es keine solche ist dieser Wert irrelevant.
	 */
	private volatile boolean mausAusBild = false;

	/**
	 * Gibt an, ob der gerade Verarbeitete Klick mitzählt, also vom Benutzer selbst gemacht wurde.
	 */
	private boolean zaehlt = true;

	/**
	 * Die Maus, die in dem Fenster sichtbar ist.<br /> Ist diese Referenz <code>null</code>, kann
	 * man keine Maus sehen.
	 */
	private volatile Maus maus = null;

	/**
	 * Ein Roboter, der die Maus bei Austritt im Fenster hält.
	 */
	private Robot robot;

	/**
	 * Das Bild der Maus.
	 */
	private Raum mausBild;

	/**
	 * Ein Array als die Tastentabelle, nach der für die gedrueckt-Methoden vorgegangen wird.<br />
	 * Ist ein Wert <code>true</code>, so ist die Taste dieses Indexes heruntergedrückt.
	 */
	private volatile boolean[] tabelle;

	/**
	 * letzte Maus-Position
	 */
	private Point lastMousePosition;

	/**
	 * Einfacher Alternativkonstruktor.<br /> Erstellt ein normales Fenster mit der eingegeben
	 * Groesse.
	 *
	 * @param x
	 * 		Die Breite
	 * @param y
	 * 		Die Hoehe
	 */
	public Fenster (int x, int y) {
		this(x, y, "EngineAlpha - Ein Projekt von Michael Andonie", false, 50, 50);
	}

	/**
	 * Konstruktor fuer Objekte der Klasse Fenster.
	 *
	 * @param breite
	 * 		Die Breite des Fensters. (Bei erfolgreichem Vollbild die neue Standartbildschirmbreite)
	 * @param hoehe
	 * 		Die Hoehe des Fensters. (Bei erfolgreichem Vollbild die neue Standartbildschirmhoehe)
	 * @param titel
	 * 		Der Titel, der auf dem Fenster gezeigt wird (Auch bei Vollbild nicht zu sehen).Wenn kein
	 * 		Titel erwuenscht ist, kann ein leerer String (<code>""</code>) eingegeben werden.
	 * @param vollbild
	 * 		Ob das Fenster ein echtes Vollbild sein soll, sprich den gesamten Bildschirm ausfuellen
	 * 		soll und nicht mehr wie ein Fenster aussehen soll.<br /> Es kann sein, dass Vollbild zum
	 * 		Beispiel aufgrund eines Veralteten Javas oder inkompatiblen PCs nicht moeglich ist, <b>in
	 * 		diesem Fall wird ein normales Fenster mit den eingegeben Werten erzeugt</b>.<br /> Daher
	 * 		ist das x/y - Feld eine Pflichteingabe.
	 * @param fensterX
	 * 		Die X-Koordinate des Fensters auf dem Computerbildschirm.
	 * @param fensterY
	 * 		Die Y-Koordinate des Fensters auf dem Computerbildschirm.
	 */
	public Fenster (int breite, int hoehe, String titel, boolean vollbild, int fensterX, int fensterY) {
		super(titel);

		frameCount++;

		int WINDOW_FRAME = 1;
		int WINDOW_FULLSCREEN = 2;
		int WINDOW_FULLSCREEN_FRAME = 4;

		int windowMode = vollbild ? WINDOW_FULLSCREEN : WINDOW_FRAME;

		tabelle = new boolean[45];

		this.vollbild = vollbild;
		this.setSize(breite, hoehe);
		this.setResizable(false);

		// ------------------------------------- //

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] devices = env.getScreenDevices();
		Dimension screenSize = getToolkit().getScreenSize();

		if (vollbild) {
			if (devices[0].isFullScreenSupported()) {
				breite = screenSize.width;
				hoehe = screenSize.height;
			} else {
				Logger.error("Achtung!");
				Logger.error("Vollbild war nicht möglich, weil dieser PC dies nicht unterstützt!");

				windowMode = WINDOW_FULLSCREEN_FRAME;
			}
		} else {
			int x = screenSize.width / 8, y = screenSize.height / 8;

			if (fensterX > -1 && fensterX < screenSize.width) {
				x = fensterX;
			}

			if (fensterY > -1 && fensterY < screenSize.height) {
				y = fensterY;
			}

			setLocation(x, y);
		}

		// ------------------------------------- //

		if (windowMode == WINDOW_FULLSCREEN) {
			setUndecorated(true);
			devices[0].setFullScreenWindow(this);

			// Resolution-Check
			boolean success = false;

			if (devices[0].isDisplayChangeSupported()) {
				DisplayMode[] displayMode = devices[0].getDisplayModes();

				Logger.info("DisplayModes: " + displayMode.length);

				for (int i = 0; i < displayMode.length; i++) {
					Logger.info((i + 1) + ": " + "Breite: " + displayMode[i].getWidth() + ", Höhe: " + displayMode[i].getHeight());

					if (displayMode[i].getWidth() == breite && displayMode[i].getHeight() == hoehe) {
						devices[0].setDisplayMode(new DisplayMode(breite, hoehe, displayMode[i].getBitDepth(), displayMode[i].getRefreshRate()));
						Logger.info("SET!");
						success = true;
						break;
					}
				}

				if (!success) {
					Logger.error("Achtung!" + "\n" + "Die angegebene Auflösung wird von diesem Bildschirm nicht unterstützt!" + "\n" + "Nur besondere Auflösungen sind möglich, z.B. 800 x 600." + "\n" + "Diese sollten in der Konsole vor dieser Fehlerausgabe gelistet sein.");
				}
			} else {
				Logger.error("Dieser Bildschirm unterstützt keine Auflösungsänderung!");
			}

			if (!success) {
				Logger.error("Die gewünschte Auflösung wird nicht vom Hauptbildschirm des Computers unterstützt!");
			}
		} else if (windowMode == WINDOW_FULLSCREEN_FRAME) {
			setVisible(true);

			setBounds(env.getMaximumWindowBounds());
			setExtendedState(MAXIMIZED_BOTH);

			Insets insets = getInsets();
			breite = getWidth() - insets.left - insets.right;
			hoehe = getHeight() - insets.top - insets.bottom;
		} else {
			setVisible(true);
		}

		this.zeichner = new Zeichner(breite, hoehe, new Kamera(breite, hoehe, new Zeichenebene()));
		this.add(zeichner);
		this.zeichner.init();

		if ((windowMode & (WINDOW_FULLSCREEN_FRAME | WINDOW_FRAME)) > 0) {
			this.pack();
		}

		// ------------------------------------- //

		// Der Roboter
		try {
			robot = new Robot(devices[0]);
		} catch (AWTException e) {
			Logger.error("Achtung!" + "\n" + "Es war nicht möglich ein GUI-Controlobjekt zu erstelllen!" + "\n" + "Zentrale Funktionen der Maus-Interaktion werden nicht funktionieren." + "\n" + "Grund: Dies liegt an diesem Computer.");
		}

		// Die Listener
		addKeyListener();
		addMouseListener();
		addMouseMotionListener();
		addWindowListener(new Adapter(this));
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing (WindowEvent e) {
				loeschen();
			}
		});
		removeCursor();

		// Mausfang
		Manager.standard.anmelden(new Ticker() {
			private static final long serialVersionUID = -7552570027596373694L;

			@Override
			public void tick () {
				if (hatMaus() && !maus.absolut() && maus.bewegend()) {
					try {
						BoundingRechteck r = mausBild.dimension();
						Punkt hs = maus.hotSpot();
						BoundingRechteck praeferenz = mausPlatz();
						Punkt p = new Punkt(r.x + hs.realX(), r.y + hs.realY());
						if (!praeferenz.istIn(p) && maus.bewegend()) {
							getCam().verschieben((new Vektor(praeferenz.zentrum(), p).teilen(20)));
						}
					} catch (NullPointerException e) {
						// Einfangen der maximal einmaligen RuntimeException zum
						// sichern
						// TODO Wann tritt diese Exception auf? Das muss schöner gehen,
						// wenn sie immer auftritt.
					}
				}
			}
		}, 50);

		instanz = this;
	}

	private void addKeyListener () {
		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyTyped (KeyEvent e) {

			}

			@Override
			public void keyPressed (KeyEvent e) {
				tastenAktion(e);
			}

			@Override
			public void keyReleased (KeyEvent e) {
				int i = zuordnen(e.getKeyCode());

				if (i == -1) {
					return;
				}

				tabelle[i] = false;

				for (TastenLosgelassenReagierbar l : losListener) {
					l.tasteLosgelassen(i);
				}
			}
		};

		addKeyListener(keyListener);
		zeichner.addKeyListener(keyListener);
	}

	private void addMouseListener () {
		zeichner.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked (MouseEvent e) {

			}

			@Override
			public void mousePressed (MouseEvent e) {
				mausAktion(e, false);
			}

			@Override
			public void mouseReleased (MouseEvent e) {
				mausAktion(e, true);
			}

			@Override
			public void mouseEntered (MouseEvent e) {
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

			@Override
			public void mouseExited (MouseEvent e) {
			}
		});
	}

	private void addMouseMotionListener () {
		zeichner.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseDragged (MouseEvent e) {
				mausBewegung(e);
			}

			@Override
			public void mouseMoved (MouseEvent e) {
				mausBewegung(e);
			}
		});
	}

	/**
	 * Löscht das Fenster und terminiert damit das Spiel.<br /> <b>Daher nur dann einsetzen, wenn
	 * die Anwendung beendet werden soll!! Der vorherige Zustand ist nicht
	 * wiederherstellbar!!</b><br /> Als alternative Methode zum ausschliesslichen Loeschen des
	 * Fensters steht <code>softLoeschen()</code> zur Verfuegung.
	 */
	public void loeschen () {
		this.zeichner.kill();

		this.setVisible(false);
		this.dispose();

		frameCount--;

		if (frameCount == 0) System.exit(0);
	}

	private void removeCursor () {
		mausLoeschen();
	}

	/**
	 * Testet, ob eine Maus im Spiel vorhanden ist.
	 *
	 * @return TRUE, wenn eine Maus im Spiel ist.
	 */
	public boolean hatMaus () {
		return (maus != null);
	}

	/**
	 * @return Das BoundingRechteck, dass den Spielraum der Maus ohne Einbezug der Relativen
	 * Koordinaten (Kameraposition)<br /> Das Rechteck ist die Masse des Fensters mal 3/4.<br />
	 * Dies ist natuerlich nur dann im Fenster gebraucht, wenn eine relative Maus angemeldet ist.
	 */
	private BoundingRechteck mausPlatz () {
		Dimension d = this.getSize();
		int x = (d.width / 4) * 3;
		int y = (d.height / 4) * 3;
		return new BoundingRechteck((d.width / 8), (d.height / 8), x, y);
	}

	/**
	 * @return Die Kamera, passend zu diesem Fenster
	 */
	public Kamera getCam () {
		return zeichner.cam();
	}

	/**
	 * Die Listener-Methode, die vom Fenster selber bei jeder gedrueckten Taste aktiviert wird.<br
	 * /> Hiebei wird die Zuordnung zu einer Zahl gemacht, und diese dann an alle Listener
	 * weitergereicht, sofern die Taste innerhalb der Kennung des Fensters liegt.<br /> Hierzu: Die
	 * Liste der Tasten mit Zuordnung zu einem Buchstaben; sie ist im <b>Handbuch</b> festgehalten.
	 *
	 * @param e
	 * 		Das ausgeloeste KeyEvent zur Weiterverarbeitung.
	 */
	private void tastenAktion (KeyEvent e) {
		int z = zuordnen(e.getKeyCode());

		if (z == -1 || tabelle[z]) {
			return;
		}

		for (TastenReagierbar r : listener) {
			r.reagieren(z);
		}

		tabelle[z] = true;
	}

	/**
	 * Ordnet vom Java-KeyCode-System in das EA-System um.
	 * <p/>
	 * Seit Version 3.0.3 ersetzt durch {@link ea.Taste#vonJava(int)}.
	 *
	 * @param keyCode
	 * 		Der Java-KeyCode
	 *
	 * @return Entsprechender EA-KeyCode oder <code>-1</code>, falls es keinen passenden EA-KeyCode
	 * gibt.
	 *
	 * @deprecated Seit v3.0.3. Durch {@link ea.Taste#vonJava(int)} ersetzt.
	 */
	@Deprecated
	public int zuordnen (int keyCode) {
		return Taste.vonJava(keyCode);
	}

	/**
	 * Diese Methode wird immer dann ausgeführt, wenn ein einfacher Linksklick der Maus ausgeführt
	 * wird.
	 *
	 * @param e
	 * 		Das MausEvent
	 *
	 * @param losgelassen Ist dieser Wert <code>true</code>, wurde die Maus eigentlich losgelassen und nicht
	 * geklickt.
	 */
	private void mausAktion (MouseEvent e, boolean losgelassen) {
		if (!zaehlt) {
			zaehlt = true;
			return;
		}

		// Linksklick? 1: Links - 2: Mausrad? - 3: Rechts
		final boolean links = e.getButton() != MouseEvent.BUTTON3;

		if (hatMaus()) {
			if (maus.absolut() || maus.bewegend()) {
				Punkt pu = maus.klickAufZeichenebene();
				maus.klick(pu, links, losgelassen);
			} else { // FIXME REVIEW BUG
				//maus.klick(maus.getImage().positionX() + getCam().getX(), maus.getImage().positionY() + getCam().getY(), links, losgelassen);
				maus.klick(maus.getImage().position().verschobeneInstanz(getCam().position().position().alsVektor()), links, losgelassen);
			} 
		}
	}

	/**
	 * Diese Methode wird ausgefuehrt, wenn die Maus bewegt wird.
	 *
	 * @param e
	 * 		Das ausloesende Event
	 */
	private void mausBewegung (MouseEvent e) {
		if (hatMaus()) {
			Insets insets = this.getInsets();

			float centerX = zeichner.getWidth() / 2;
			float centerY = zeichner.getHeight() / 2;

			Point windowLocation = getLocation();
			Point mousePosition = e.getPoint();

			if (maus.absolut()) {
				if (maus.bewegend()) {
					Vektor offset = new Vektor(mousePosition.x - centerX, mousePosition.y - centerY);

					getCam().verschieben(offset);
				}

				if (lastMousePosition != null) {
					maus.bewegt(new Vektor(mousePosition.x - lastMousePosition.x, mousePosition.y - lastMousePosition.y));
				}
			} else {
				float dx = mousePosition.x - centerX;
				float dy = mousePosition.y - centerY;

				BoundingRechteck bounds = mausBild.dimension();
				Punkt hotspot = maus.hotSpot();

				Punkt hotspotX = new Punkt(bounds.x + hotspot.realX() + dx, bounds.y + hotspot.realY());

				Punkt hotspotY = new Punkt(bounds.x + hotspot.realX(), bounds.y + hotspot.realY() + dy);

				// FIXME Maus bis zum Rand bewegen, aber nicht hinaus.
				// Maus bewegt sich nicht direkt an den Rand, wenn die Bewegung größer als der
				// Abstand zum Rand ist!
				if (!zeichner.masse().istIn(hotspotX)) {
					dx = 0;
				}

				if (!zeichner.masse().istIn(hotspotY)) {
					dy = 0;
				}
				
				Vektor bewegung = new Vektor(dx, dy);
				mausBild.verschieben(bewegung);
				maus.bewegt(bewegung);
			}

			robot.mouseMove((int)(windowLocation.x + insets.left + centerX), (int)(windowLocation.y + insets.top + centerY));
			lastMousePosition = new Point((int)centerX, (int)centerY);
		}
	}

	/**
	 * Loescht das Maus-Objekt des Fensters.<br /> Hatte das Fenster keine, ergibt sich selbstredend
	 * keine Aenderung.
	 */
	public void mausLoeschen () {
		this.setCursor(getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "NOCURSOR"));
		maus = null;
	}

	/**
	 * Statische Methode zum Oeffentlichen Berechnen der Fontmetriken des offenen Fensters.
	 *
	 * @param f
	 * 		Der zu ueberpruefende Font
	 *
	 * @return Das zu dem Font und aktiven Fenster gehoerende FontMetrics-Objekt
	 */
	public static FontMetrics metrik (Font f) {
		return instanz.getFontMetrics(f);
	}

	/**
	 * Gibt die aktuellste Instanz dieser KLasse wieder.
	 *
	 * @return Das aktuellste Fenster
	 */
	public static Fenster instanz () {
		return instanz;
	}

	/**
	 * Minimiert das Fenster (bringt es in die Taskleiste).
	 */
	public void minimieren () {
		setState(ICONIFIED);
	}

	/**
	 * Maximiert das Fenster (bringt es aus der Taskleiste wieder auf den Bildschirm)
	 */
	public void maximieren () {
		setState(NORMAL);
	}

	/**
	 * Gibt zurueck, ob dieses Fenster ein Vollbild ist oder nicht.
	 *
	 * @return <code>true</code>, wenn das Fenster ein Vollbild ist, sonst <code>false</code>.
	 */
	public boolean vollbild () {
		return vollbild;
	}

	/**
	 * Meldet den hintergrund dieses Fensters und damit des Spiels an.<br /> Gibt es bereits einen,
	 * so wird dieser fortan nicht mehr gezeichnet, dafuer nun dieser. Sollten mehrere Objekte
	 * erwuenscht sein, gezeichnet zu werden, so empfiehlt es sich, diese in einem
	 * <code>Knoten</code>-Objekt zu sammeln und dann anzumelden.<br /> <b>Achtung!</b><br /> Diese
	 * Objekte sollten nicht an der Physik angemeldet werden, dies fuehrt natuerlich zu ungewollten
	 * Problemen!
	 *
	 * @param hintergrund
	 * 		Der anzumeldende Hintergrund
	 */
	public void hintergrundAnmelden (Raum hintergrund) {
		zeichner.hintergrundAnmelden(hintergrund);
	}

	/**
	 * Meldet einen TastenReagierbar - Listener an.
	 *
	 * @param t
	 * 		Der neu anzumeldende Listener.
	 */
	public void anmelden (TastenReagierbar t) {
		if (t == null) {
			throw new IllegalArgumentException("Listener darf nicht NULL sein.");
		}

		listener.add(t);
	}

	/**
	 * Meldet einen TastenLosgelassenReagierbar-Listener an als exakt parallele Methode zu
	 * <code>tastenLosgelassenAnmelden()</code>, jedoch eben ein etwas laengerer, aber vielleicht
	 * auch logischerer Name; fuehrt jedoch exakt die selbe Methode aus!<br />
	 * <b>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ACHTUNG!!!!!!!!!!!!!!!!!!!!!!!!
	 * !!!!!!!!!!!!!!!</b><br /> <code>TastenReagierbar</code> und <code>TastenLosgelassenReagierbar</code>
	 * sind 2 vollkommen unterschiedliche Interfaces! Das eine wird beim Runterdruecken, das andere
	 * beim Loslassen der Tasten aktiviert.
	 *
	 * @see #tastenLosgelassenAnmelden(TastenLosgelassenReagierbar)
	 */
	public void tastenLosgelassenReagierbarAnmelden (TastenLosgelassenReagierbar t) {
		this.tastenLosgelassenAnmelden(t);
	}

	/**
	 * Meldet einen TastenLosgelassenReagierbar-Listener an.<br /> <b>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ACHTUNG!!!!!!!!!!!!!!!!!!!!!!!!
	 * !!!!!!!!!!!!!!!</b><br /> TastenReagierbar und TastenLosgelassenReagierbar sind 2 vollkommen
	 * unterschiedliche Interfaces! Das eine wird beim Runterdruecken, das andere beim Loslassen der
	 * Tasten aktiviert.
	 */
	public void tastenLosgelassenAnmelden (TastenLosgelassenReagierbar t) {
		if (t == null) {
			throw new IllegalArgumentException("Listener darf nicht NULL sein.");
		}

		losListener.add(t);
	}

	/**
	 * Meldet eine Maus an.<br /> Im Gegensatz zu den TastenReagierbar-Listenern kann nur eine Maus
	 * am Fenster angemeldet sein.
	 *
	 * @param m
	 * 		Die anzumeldende Maus
	 */
	public void anmelden (Maus m) {
		if (hatMaus()) {
			Logger.error("Es ist bereits eine Maus angemeldet!");
		} else {
			maus = m;
			maus.fensterSetzen(this);

			BoundingRechteck r = maus.getImage().dimension();
			maus.getImage().positionSetzen(((getWidth() - r.breite) / 2), (getHeight() - r.hoehe) / 2);
			mausBild = maus.getImage();

			zeichner.anmelden(mausBild);
		}
	}

	/**
	 * Gibt an, ob die Maus den Bildschirm bewegen kann.
	 *
	 * @return TRUE, wenn die Maus den Bildschirm bewegen kann, FALSE, wenn nicht.<br /> Ist keine
	 * Maus angemeldet, ist das Ergebnis ebenfalls FALSE.
	 */
	public boolean mausBewegt () {
		return hatMaus() &&  maus.bewegend();
	}

	/**
	 * @return Der Statische Basisknoten.
	 */
	public Knoten getStatNode () {
		return zeichner.statNode();
	}

	/**
	 * Gibt die Maus aus.
	 *
	 * @return Die aktuelle Maus. Kann <code>null</code> sein!!
	 */
	public Maus getMaus () {
		return maus;
	}

	/**
	 * Gibt die Fenstermasse in einem BoundingRechteck an.
	 *
	 * @return ein BoundingRechteck mit Position (0|0), dessen Hoehe & Breite die Masse des Fensters
	 * in Pixel angeben.
	 *
	 * @see ea.BoundingRechteck
	 */
	public BoundingRechteck fenstermasse () {
		return zeichner.masse();
	}

	/**
	 * Gibt den Zeichner des Fensters aus.
	 *
	 * @return Der Zeichner des Fensters.
	 */
	public Zeichner zeichner () {
		return zeichner;
	}

	/**
	 * Entfernt ein simples Grafikobjekt.
	 *
	 * @param g
	 * 		Das darzustellende Grafikobjekt
	 */
	public void removeSimple (SimpleGraphic g) {
		zeichner.removeSimple(g);
	}

	/**
	 * Fuellt ein simples Grafikobjekt in die anzeige.
	 *
	 * @param g
	 * 		Das darzustellende Grafikobjekt.
	 */
	public void fillSimple (SimpleGraphic g) {
		zeichner.addSimple(g);
	}

	/**
	 * Gibt das gespeicherte Bild-Objekt der Maus wieder.
	 *
	 * @return Das Bild mit seiner Position und Groesse von der Maus.
	 */
	public Raum mausBild () {
		return this.mausBild;
	}

	/**
	 * Deaktiviert den eventuell vorhandenen gemerkten Druck auf allen Tasten.<br /> Wird innerhalb
	 * der Engine benutzt, sobald das Fenster deaktiviert etc. wurde.
	 */
	public void druckAufheben () {
		for (int i = 0; i < tabelle.length; i++) {
			tabelle[i] = false;
		}
	}

	/**
	 * Überprüft, ob eine bestimmte Taste auf der Tastatur heruntergedrückt wurde.
	 *
	 * @param tastencode
	 * 		Der Code der Taste, für die getestet werden soll.
	 *
	 * @return <code>true</code>, falls die entsprechende Taste gerade heruntergedrückt wurde, sonst
	 * <code>false</code>.
	 *
	 * @throws java.lang.ArrayIndexOutOfBoundsException
	 * 		Sollte einen Zahl < 0 oder > 44 verwendet werden.
	 */
	public boolean istGedrueckt (int tastencode) {
		return tabelle[tastencode];
	}
}
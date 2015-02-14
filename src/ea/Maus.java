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

import ea.internal.ano.API;
import ea.internal.gui.Fenster;
import ea.internal.util.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Klasse zur Kontrolle der Maus
 *
 * @author Michael Andonie, Niklas Keller <me@kelunik.com>
 */
public class Maus {
	/* -------------------- Konstanten -------------------- */

	/**
	 * Standard-Maus-Zeiger der Engine Alpha
	 */
	public static final int TYPE_STANDARD = 0;

	/**
	 * Ein Fadenkreuz
	 */
	public static final int TYPE_FADENKREUZ = 1;

	/**
	 * Ein klassischer Zeiger, der eine Hand darstellt mit einem Zeigefinger
	 */
	public static final int TYPE_HAND = 2;

	/**
	 * Ein klassischer Zeiger, wie unter Windows gewohnt
	 */
	public static final int TYPE_KLASSIK = 3;

	/* -------------------- /Konstanten -------------------- */

	/**
	 * Die Art der Maus.
	 * <p/>
	 * Eine Maus kann ein klassischer Pfeil, ein Textbearbeitungszeichen, eine Sanduhr, ein
	 * Fadenkreuz oder jedes andere beliebiges Raum-Objekt sein. Gerade kreative Mäuse werden für
	 * Spiele verwendet.
	 * <p/>
	 * Die Liste aller Mausarten ist im Konstruktor sowie im <b>Handbuch</b> festgehalten.
	 */
	private final int type;

	/**
	 * Gibt an, ob die Maus fixiert ist oder nicht.
	 * <p/>
	 * In diesem Fall ist der Mauszeiger immer Mittelpunkt des Bildes, das Bild bewegt sich dann
	 * immer gemäß der Mausbewegung.
	 */
	private final boolean fixed;

	/**
	 * Gibt an, ob die Maus den angezeigten Bildschirmbereich verschieben kann.
	 */
	private final boolean bewegend;

	/**
	 * Die Liste aller Raum-Klick-Auftraege
	 */
	private final ArrayList<Auftrag> mausListe = new ArrayList<>();

	/**
	 * Eine Liste aller angemeldeten KlickReagierbar Objekte.
	 */
	private final ArrayList<KlickReagierbar> klickListeners = new ArrayList<>();

	/**
	 * Eine Liste aller angemeldeten RechtsKlickReagierbar Objekte.
	 */
	private final ArrayList<RechtsKlickReagierbar> rechtsKlickListeners = new ArrayList<>();

	/**
	 * Eine Liste aller angemeldeten MausLosgelassenReagierbar Objekte.
	 */
	private final ArrayList<MausLosgelassenReagierbar> mausLosgelassenListeners = new ArrayList<>();

	/**
	 * Eine Liste aller angemeldeten MausBewegungReagierbar Objekte.
	 */
	private final ArrayList<MausBewegungReagierbar> mausBewegungListeners = new ArrayList<>();

	/**
	 * Das Mausbild. Wichtig bei einer individuellen Maus.
	 */
	private final Raum bild;

	/**
	 * Der individuelle Hotspot
	 */
	private final Punkt hotspot;

	/**
	 * Das Fenster, in dem diese Maus aktiv ist. <code>null</code>, falls es kein solches Fenster
	 * gibt.
	 */
	private Fenster fenster;

	/**
	 * Dieser Konstruktor ist lediglich eine Vereinfachung. Siehe Dokumentation des vollständigen
	 * Konstruktors für eine Dokumentation der Parameter: {@link #Maus(Raum, Punkt, boolean,
	 * boolean)}
	 * <p/>
	 * <code>bewegend</code> wird bei diesem Konstruktor auf <code>false</code> gesetzt.
	 *
	 * @param mausbild
	 * 		siehe {@link #Maus(Raum, Punkt, boolean, boolean)}
	 * @param hotspot
	 * 		siehe {@link #Maus(Raum, Punkt, boolean, boolean)}
	 * @param fixed
	 * 		siehe {@link #Maus(Raum, Punkt, boolean, boolean)}
	 *
	 * @see #Maus(Raum, Punkt, boolean, boolean)
	 * @see Bild
	 * @see Kamera
	 */
	@API
	public Maus (Raum mausbild, Punkt hotspot, boolean fixed) {
		this(mausbild, hotspot, fixed, false);
	}

	/**
	 * Dieser Konstruktor ermöglicht es, ein eigenes Mausbild einzubringen.
	 * <p/>
	 * Hierfür muss klar sein, was ein Hotspot ist:<br> Ein Hotspot ist ein Punkt, relativ entfernt
	 * von der linken oberen Ecke des Mausbildes. Dieser Punkt ist der eigentliche Klickpunkt.
	 * <p/>
	 * Angenommen das Mausbild misst 20 x 20 Pixel und der Hotspot ist ein Punkt mit den Koordinaten
	 * (10|10), so wäre der Hotspot in der Mitte des Bildes und bei einem Klick wären diese
	 * Klick-Koordinaten genau in der Mitte. Wäre der Hotspot (0|0), so wäre der Hotspot genau die
	 * linke obere Ecke des Mausbildes.
	 *
	 * @param mausbild
	 * 		Das Objekt, das ab sofort das Mausbild sein wird und auch dementsprechend bewegt wird.
	 * @param hotspot
	 * 		Der bereits beschriebene Hotspot
	 * @param fixed
	 * 		Ob diese Maus fixiert sein soll: Ist dieser Wert <code>true</code>, so ist die Maus immer
	 * 		in der Mitte. Bei <code>false</code> verhält sich diese Maus genauso wie gewohnt.
	 * @param bewegend
	 * 		Regelt, ob die Kamera bei Mausbewegungen bewegt wird.
	 * 		<p/>
	 * 		Bei <code>true</code>: <ul> <li>Falls die Maus fixiert ist, bleibt sie weiterhin in der
	 * 		Mitte und die Kamera wird bei jeder Bewegung bewegt.</li> <li>Falls die Maus nicht fixiert
	 * 		ist, so wird die Kamera erst bewegt, wenn die Maus den Rand des Fensters beführt.</li>
	 * 		</ul>
	 * 		<p/>
	 * 		Bei <code>false</code> wird die Kamera nie automatisch bewegt. Allerdings ist dies über das
	 * 		Interface {@link ea.MausBewegungReagierbar} manuell realisierbar. Ohne dieses Interface
	 * 		gibt <code>false</code> bei einer fixierten Maus wenig Sinn.
	 *
	 * @see ea.Bild
	 * @see ea.Kamera
	 */
	@API
	public Maus (Raum mausbild, Punkt hotspot, boolean fixed, boolean bewegend) {
		this.type = -1;
		this.bild = mausbild;
		this.hotspot = hotspot;
		this.fixed = fixed;
		this.bewegend = bewegend;
	}

	/**
	 * Erstellt eine Maus, die die Kamera nicht bewegt und nicht fixiert ist.
	 * <p/>
	 * Weitere Erläuterungen: {@link #Maus(Raum, Punkt, boolean, boolean)}
	 *
	 * @param mausbild
	 * 		siehe {@link #Maus(Raum, Punkt, boolean, boolean)}
	 * @param hotspot
	 * 		siehe {@link #Maus(Raum, Punkt, boolean, boolean)}
	 *
	 * @see #Maus(Raum, Punkt, boolean)
	 */
	@API
	public Maus (Raum mausbild, Punkt hotspot) {
		this(mausbild, hotspot, false, false);
	}

	/**
	 * Vereinfachter Konstruktor für eine Standard-Maus.
	 * <p/>
	 * Hierbei gibt es verschiedene Mäuse, die über ihren Index gewählt werden können. Die
	 * verlinkten Konstanten sollten verständlich dokumentiert sein. Ansonsten ist an dieser Stelle
	 * auch das Wiki hilfreich.
	 * <p/>
	 * Die Maus ist dabei weder fixiert noch wird die Kamera durch ihre Bewegung bewegt.
	 *
	 * @param type
	 * 		Die Art der Maus. Jeder Wert steht für eine andere Maus.
	 * 		<p/>
	 * 		{@link #TYPE_STANDARD}, {@link #TYPE_FADENKREUZ}, {@link #TYPE_HAND} oder {@link
	 * 		#TYPE_KLASSIK}
	 */
	@API
	public Maus (int type) {
		this(type, false, false);
	}

	/**
	 * Vollständiger Konstruktor für eine Standard-Maus.
	 * <p/>
	 * Hierbei gibt es verschiedene Mäuse, die über ihren Index gewählt werden können. Die
	 * verlinkten Konstanten sollten verständlich dokumentiert sein. Ansonsten ist an dieser Stelle
	 * auch das Wiki hilfreich.
	 *
	 * @param type
	 * 		Die Art der Maus. Jeder Wert steht für eine andere Maus.
	 * 		<p/>
	 * 		{@link #TYPE_STANDARD}, {@link #TYPE_FADENKREUZ}, {@link #TYPE_HAND} oder {@link
	 * 		#TYPE_KLASSIK}
	 * @param fixed
	 * 		Ob diese Maus fixiert sein soll: Ist dieser Wert <code>true</code>, so ist die Maus immer
	 * 		in der Mitte. Bei <code>false</code> verhält sich diese Maus genauso wie gewohnt.
	 * @param bewegend
	 * 		Ob die Maus die Kamera bewegen koennen soll oder nicht.
	 */
	@API
	public Maus (int type, boolean fixed, boolean bewegend) {
		this.type = type;
		this.fixed = fixed;
		this.bewegend = bewegend;
		this.bild = getImage();
		this.hotspot = hotSpot();
	}

	/**
	 * <b>Berechnet</b> das Bild von der Maus, das in der Engine dargestellt wird.
	 *
	 * @return Das Bild des Mauscursors.
	 */
	public Raum getImage () {
		if (bild != null) {
			return bild;
		}

		BufferedImage ret = null;
		String verzeichnis = "/assets/mouse/"; // Das Verzeichnis der Mauskomponenten

		switch (type) {
			case TYPE_STANDARD:
				verzeichnis += "blau.gif";
				break;
			case TYPE_FADENKREUZ:
				verzeichnis += "fadenkreuz.gif";
				break;
			case TYPE_HAND:
				verzeichnis += "hand.gif";
				break;
			case TYPE_KLASSIK:
				verzeichnis += "klassisch.gif";
				break;
			default:
				// TODO Die Datei existierte nicht, gibt es da noch einzweites Fadenkreuz?
				verzeichnis += "fadenkreuz.gif";
				break;
		}

		InputStream in = Maus.class.getResourceAsStream(verzeichnis);

		try {
			ret = ImageIO.read(in);
		} catch (IOException ex) {
			Logger.error("Achtung! Das zu ladende Standard-Mausbild konnte nicht geladen werden.");
		}

		return new Bild(0, 0, ret);
	}

	/**
	 * @return der Punkt auf dem Mausbild, der als Hotspot fungiert.
	 */
	public Punkt hotSpot () {
		if (hotspot != null) {
			return hotspot;
		}

		int x = 0, y = 0;

		switch (type) {
			case TYPE_FADENKREUZ:
				x = y = 12;
				break;
			case TYPE_HAND:
				x = 5;
				y = 0;
				break;
			case 3: // Hotspot bleibt auf (0|0)
			default:
				break;
		}

		return new Punkt(x, y);
	}

	/**
	 * Setzt die Referenz auf das Fenster, in dem diese Maus sitzt, neu. <b>ACHTUNG:</b> Sollte
	 * nicht von Außen benutzt werden, falls man sich nicht genau mit der Struktur der Engine
	 * auskennt.
	 *
	 * @param f
	 * 		Die neue Fensterreferenz.
	 */
	public void fensterSetzen (Fenster f) {
		this.fenster = f;
	}

	/**
	 * Alternativmethopde zum anmelden. Hierbei gibt es keinen Code-Parameter; dieser wird
	 * automatisch auf <code>0</code> gesetzt.
	 *
	 * @param m
	 * 		Der anzumeldende Listener
	 * @param objekt
	 * 		Das zu auf Klicks zu ueberwachende Objekt
	 *
	 * @see #anmelden(MausReagierbar, Raum, int)
	 */
	public void mausReagierbarAnmelden (MausReagierbar m, Raum objekt) {
		this.anmelden(m, objekt, 0);
	}

	/**
	 * Meldet ein Raumobjekt zum Ueberwachen an einem bestimmten Listener an, unter Eingabe eines
	 * bestimmten Codes.
	 *
	 * @param m
	 * 		Das Listener-Objekt, dass bei Kollision benachrichtigt werden soll.
	 * @param objekt
	 * 		Das auf Klick zu ueberwachende Raum-Objekt
	 * @param code
	 * 		Der Code, mit dem ein Klick auf dieses Objekt assoziiert wird.<br /> Diese Zahl wird als
	 * 		Argument bei der Reaktionsmethode mitgegeben, wodurch, bei korrekten
	 * 		Programmierorganisation dieser Zahlen, eine sehr gute Unterscheidung ohne groesseren
	 * 		Methodenaufwand noetig ist.
	 *
	 * @see MausReagierbar#mausReagieren(int)
	 */
	public void anmelden (MausReagierbar m, Raum objekt, int code) {
		mausListe.add(new Auftrag(m, code, objekt));
	}

	/**
	 * Uebernimmt alle Listener von einer anderen Maus.
	 *
	 * @param von
	 * 		Von dieser Maus, werden alle Listener-Listen übernommen. Bereits vorhandene Listener
	 * 		bleiben dabei erhalten, werden aber eventuell <b>DOPPELT</b> eingefügt.
	 */
	public void uebernehmeAlleListener (Maus von) {
		for (Auftrag a : von.mausListe) {
			this.anmelden(a.listener, a.koll, a.signal);
		}

		for (KlickReagierbar kr : von.klickListeners) {
			this.klickReagierbarAnmelden(kr);
		}

		for (RechtsKlickReagierbar rkr : von.rechtsKlickListeners) {
			this.rechtsKlickReagierbarAnmelden(rkr);
		}

		for (MausLosgelassenReagierbar mlr : mausLosgelassenListeners) {
			this.mausLosgelassenReagierbarAnmelden(mlr);
		}

		for (MausBewegungReagierbar mbr : mausBewegungListeners) {
			this.mausBewegungReagierbarAnmelden(mbr);
		}
	}

	/**
	 * Meldet ein KlickReagierbar bei der Maus an.<br /> Ab dann wird es bei jedem Mausklick
	 * (Linksklick) in der Engine benachrichtigt.
	 *
	 * @param k
	 * 		Das anzumeldende KlickReagierbar-Interface
	 */
	public void klickReagierbarAnmelden (KlickReagierbar k) {
		klickListeners.add(k);
	}

	/**
	 * Meldet ein RechtsKlickReagierbar-Objekt bei der Maus an.<br /> Ab dann wird es bei jedem
	 * Rechtsklick benachrichtigt.
	 *
	 * @param k
	 * 		Das anzumeldende <code>RechtsKlickReagierbar</code>-Interface
	 */
	public void rechtsKlickReagierbarAnmelden (RechtsKlickReagierbar k) {
		rechtsKlickListeners.add(k);
	}

	/**
	 * Meldet ein <code>MausLosgelassenReagierbar</code>-Objekt bei der Maus an. <br /> Ab dann wird
	 * es jedes mal durch Aufruf seiner Methode benachrichtigt, wenn eine Maustaste losgelassen
	 * wird.
	 *
	 * @param m
	 * 		Listener-Objekt
	 */
	public void mausLosgelassenReagierbarAnmelden (MausLosgelassenReagierbar m) {
		mausLosgelassenListeners.add(m);
	}

	/**
	 * Meldet ein <code>MausBewegungReagierbar</code>-Objekt bei der Maus an. <br /> Ab dann wird es
	 * jedes mal durch Aufruf seiner Methode benachrichtigt, wenn die Maus bewegt wird.
	 *
	 * @param m
	 * 		Listener-Objekt
	 */
	public void mausBewegungReagierbarAnmelden (MausBewegungReagierbar m) {
		mausBewegungListeners.add(m);
	}
	// TODO NotUsed. Soll das für bewegt und klick simuliert werden können?
	// Ansonsten muss mit @NoExternalUse annotiert werden.

	/**
	 * Bei einer angemeldeten Maus wird bei einem Klick diese Methode aufgerufen.<br /> Theoretisch
	 * liessen sich so Tastenklicks "simulieren".
	 *
	 * @param p der respektive Punkt für den simulierten Mausklick.
	 * @param links
	 * 		War der Klick ein Linksklick, ist dieser Wert <code>true</code>. Fuer jede andere Klickart
	 * 		ist dieser Wert <code>false</code>. In diesem Fall wird mit einem Rechtsklick gerechnet.
	 *
	 * @see ea.Maus#klick(int, int, boolean, boolean)
	 */
	public void klick (Punkt p, boolean links) {
		this.klick(p, links, false);
	}

	/**
	 * Bei einer angemeldeten Maus wird bei einem Klick diese Methode aufgerufen.<br /> So lassen
	 * sich auch Klicks auf die Maus "simulieren".
	 *
	 * @param p der Punkt des Klicks
	 * @param links
	 * 		War der Klick ein Linksklick, ist dieser Wert <code>true</code>. Fuer jede andere Klickart
	 * 		ist dieser Wert <code>false</code>. In diesem Fall wird mit einem Rechtsklick gerechnet.
	 * @param losgelassen
	 * 		ist dieser Wert <code>true</code>, so wird dies als losgelassene Taste behandelt.
	 */
	public void klick (Punkt p, boolean links, boolean losgelassen) {
		p = p.verschobeneInstanz(hotspot.alsVektor());
		if (losgelassen) {
			for (MausLosgelassenReagierbar m : mausLosgelassenListeners) {
				m.mausLosgelassen(p, links);
			}

			return;
		}

		if (links) {
			for (Auftrag a : mausListe) {
				a.klick(p);
			}

			for (KlickReagierbar k : klickListeners) {
				k.klickReagieren(p);
			}
		} else {
			for (RechtsKlickReagierbar k : rechtsKlickListeners) {
				k.rechtsKlickReagieren(p);
			}
		}
	}

	/**
	 * Benachrichtigt alle Listener, falls die Bewegung nicht (0|0) war.
	 *
	 * @param bewegung der Vektor, der die Bewegung der Maus beschreibt.
	 */
	public void bewegt (Vektor bewegung) {
		if (bewegung.unwirksam()) {
			return;
		}
		
		for (MausBewegungReagierbar m : mausBewegungListeners) {
			m.mausBewegt(bewegung);
		}
	}

	/**
	 * Entfernt ein bestimmtes MausReagierbar-Interface <b>gaenzlich</b> von Kollisionstest.<br />
	 * Heisst das also, dass mehrere verschiedene Raum-Objekte an dem uebergebenen Objekt Ueberwacht
	 * werden, so wird ab sofort fuer keines mehr eine Benachrichtigung stattfinden.
	 *
	 * @param g
	 * 		Das gaenzlich von Klick-Tests zu entfernende <code>MausReagierbar</code>-Interface
	 */
	public void entfernen (MausReagierbar g) {
		ArrayList<Auftrag> l = new ArrayList<>();
		for (Auftrag a : mausListe) {
			if (a.benachrichtigt(g)) {
				l.add(a);
			}
		}
		for (Auftrag a : l) {
			mausListe.remove(a);
		}
	}

	/**
	 * @return Ob die Maus den Bildschirm bewegen kann
	 */
	public boolean bewegend () {
		return this.bewegend;
	}

	/**
	 * Gibt den <i>Punkt auf der Zeichenebene</i> aus, auf den die Maus bei einem Klick zeigen
	 * würde. Diese Methode rechnet alle Umstände der Maus (z.B. relativ bzw. fixed) mit ein und
	 * gibt die genaue Position des Klicks zurück.
	 *
	 * @return Der genaue Punkt auf der Zeichenebene, auf den diese Maus bei einem Klick deuten
	 * würde.
	 */
	public Punkt klickAufZeichenebene () {
		if (absolut()) {
			BoundingRechteck r = bild.dimension();
			Punkt p = hotSpot();

			return new Punkt((int) (r.x + p.realX() + fenster.getCam().getX()), (int) (r.y + p.realY() + fenster.getCam().getY())); // Mit
			// zurückrechnen
			// auf die
			// Bildebene!
		} else {
			//Fenster Dimension
			Dimension dim = fenster.getSize();
			int startX = (dim.width / 2);
			int startY = (dim.height / 2);
			return new Punkt(startX + fenster.getCam().getX(), startY + fenster.getCam().getY());
		}
	}

	/**
	 * @return Ob die Maus fixed ist oder nicht.
	 */
	public boolean absolut () {
		return this.fixed;
	}

	/**
	 * Gibt die Liste aller Auftraege (interne Klasse!!) fuer MausReagierbar- Interfaces <br/> <br
	 * /> <b>ACHTUNG</b> Die ArrayList ist verantwortlich fuer die MausReagierbar-Aktionen. Daher
	 * stellt die Verwendung dieses Objekts durchaus eine Fehlerquelle dar.
	 *
	 * @return die Liste aller MausReagierbar-Auftraege
	 *
	 * @see ea.Maus.Auftrag
	 */
	@SuppressWarnings ( "unused" )
	public ArrayList<Auftrag> mausReagierbarListe () {
		return mausListe;
	}

	/**
	 * Diese Klasse sammelt die Auftraege der KlickReagierbar-Interfaces.
	 */
	public final class Auftrag { // TODO Wird diese Klasse wirklich genutzt? Für was?
		//

		/**
		 * Der Listener
		 */
		private final MausReagierbar listener;

		/**
		 * Das Kollisionszubeobachtende Raum-Objekt
		 */
		private final Raum koll;

		/**
		 * Das auszufuehrende Signal
		 */
		private int signal;

		/**
		 * Konstruktor
		 *
		 * @param listen
		 * 		Der Listener
		 * @param signal
		 * 		Der Signalcode
		 * @param m
		 * 		Das Kollisionsobjekt
		 */
		public Auftrag (MausReagierbar listen, int signal, Raum m) {
			listener = listen;
			this.signal = signal;
			koll = m;
		}

		/**
		 * Setzt den Wert des Signals, das beim Klick auf das Ziel ausgeloest wird neu.
		 *
		 * @param signal
		 * 		Das neue Signal
		 */
		@SuppressWarnings ( "unused" )
		public void signalSetzen (int signal) {
			this.signal = signal;
		}

		/**
		 * Uebertrag des Klick auf den Auftrag
		 *
		 * @param p
		 * 		Der Klickpunkt
		 */
		public void klick (Punkt p) {
			if (koll.beinhaltet(p)) {
				listener.mausReagieren(signal);
			}
		}

		/**
		 * @return TRUE, wenn dieser Listener benachrichtigt wird
		 */
		public boolean benachrichtigt (MausReagierbar r) {
			return r == listener;
		}

		/**
		 * @return <code>true</code>, wenn das Objekt beobachtet wird, sonst <code>false</code>.
		 */
		@SuppressWarnings ( "unused" )
		public boolean beobachtet (Raum m) {
			return m == koll;
		}

		/**
		 * Gibt das Signal zurück.
		 *
		 * @return Das Signal, das beim Klick auf das Zielobjekt gesendet wird.
		 */
		@SuppressWarnings ( "unused" )
		public int signal () {
			return signal;
		}
	}
}

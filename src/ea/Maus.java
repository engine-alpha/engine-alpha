/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
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

import ea.internal.gui.Fenster;
import ea.internal.util.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Die objektmäßige Repräsentation der Maus.
 *
 * @author Michael Andonie
 */
public class Maus {

	/**
	 * Der derzeitige Maximalindex an Standard-Mäusen.
	 */
	private static final int MAX_ART_INDEX = 3;

	/**
	 * Gibt an, ob die Maus absolut ist.<br /> In diesem Fall ist der Mauszeiger immer Mittelpunkt
	 * des Bildes, das Bild bewegt sich dann immer gemäß der Mausbewegung.
	 */
	private final boolean absolut;

	/**
	 * Die Liste aller Raum-Klick-Auftraege
	 */
	private final ArrayList<Auftrag> mausListe = new ArrayList<>();

	/**
	 * Eine Liste aller angemeldeten KlickReagierbar Objekte.
	 */
	private final ArrayList<KlickReagierbar> klickListe = new ArrayList<>();

	/**
	 * Eine Liste aller angemeldeten RechtsKlickReagierbar Objekte.
	 */
	private final ArrayList<RechtsKlickReagierbar> rechtsKlickListe = new ArrayList<>();

	/**
	 * Eine Liste aller angemeldeten MausLosgelassenReagierbar Objekte.
	 */
	private final ArrayList<MausLosgelassenReagierbar> loslassListe = new ArrayList<>();

	/**
	 * Eine Liste aller angemeldeten MausBewegungReagierbar Objekte.
	 */
	private final ArrayList<MausBewegungReagierbar> bewegungReagierbarListe = new ArrayList<>();

	/**
	 * Die Art der Maus.<br /> Eine Maus kann ein klassischer Pfeil, ein Textbearbeitungszeichen,
	 * eine Sanduhr, ein Fadenkreuz, oder alle moeglichen weniger bekannten Arten sein.<br /> Gerade
	 * kreative Maeuse werden fuer Spiele verwendet.<br /> Die Liste aller Mausarten ist im
	 * Konstruktor sowie im <b>Handbuch</b> festgehalten.
	 */
	private final int mausArt;

	/**
	 * Gibt an, ob die Maus den angezeigten Bildschirmbereich verschieben kann.
	 */
	private final boolean bewegend;

	/**
	 * Das Mausbild. Wichtig bei einer individuellen Maus.
	 */
	private final Raum bild;

	/**
	 * Der individuelle Hotspot
	 */
	private final Punkt hotspot;

	/**
	 * Das Fenster, in dem diese Maus aktiv ist (<code>null</code>, falls es kein solches Fenster
	 * gibt).
	 */
	private Fenster fenster;

	/**
	 * Unabhängiger Konstruktor. Dieser Konstruktor ermöglicht es, ein eigenes Mausbild
	 * einzubringen.<br /> Hierfuer muss klar sein, was ein Hotspot ist:<br /> <br /> Ein Hotspot
	 * ist ein Punkt, relativ entfernt von der linken oberen Ecke des Mausbildes.<br /> Dieser Punkt
	 * gibt an, welcher Punkt exakt der "Klickpunkt" ist.<br /> <br />
	 * <p/>
	 * Angenommen das Mausbild misst 20 x 20 Pixel, und der Hotspot ist ein Punkt mit den
	 * Koordinaten (10|10), so waere der Hotspot in der Mitte des Bildes und bei einem Klick waere
	 * diese "klick"-Koordinate genau in der Mitte.<br /> Wäre der Punkt (0|0), so waere der Hotspot
	 * genau in der linken oberen Ecke des Mausbildes.
	 *
	 * @param mausbild
	 * 		Das Bild-Objekt, das ab sofort das Mausbild sein wird und auch dementsprechend bewegt
	 * 		wird.
	 * @param hotspot
	 * 		Der bereits eingaengig beschriebene Hotspot-Punkt
	 * @param absolut
	 * 		Ob diese Maus absolut sein soll; ist dieser Wert <code>true</c>, so ist die Maus immer in
	 * 		der Mitte, dafuer bewegt sich die <code>Kamera</code> bei Mausverschiebung. Bei
	 * 		<code>false</code>verhaelt sich diese Maus genauso wie eine gewohnte Computermaus.
	 * @param bewegend
	 * 		Ist dieser Wert <code>true</code>, so wird die Maus, wenn sie sich aus dem Fenster bewegt
	 * 		(bei einer absoluten Maus bei jeder Bewegung) das Fenster verschieben; ist dieser Wert
	 * 		<code>false</code>, so ist dies nich moeglich, und die Maus ist in den Fenzergrenzen
	 * 		gefangen (Was besonders bei einer absoluten Maus wenig Sinn macht).
	 *
	 * @see ea.Bild
	 * @see ea.Kamera
	 */
	public Maus (Raum mausbild, Punkt hotspot, boolean absolut, boolean bewegend) {
		this.mausArt = -1;
		this.bild = mausbild;
		this.hotspot = hotspot;
		this.absolut = absolut;
		this.bewegend = bewegend;
	}

	/**
	 * Unabhaengiger Konstruktor. Dieser Konstruktor ermoeglicht es, ein eigenes, die Kamera nicht
	 * bewegendes, Mausbild einzubringen.<br /> Hierfuer muss klar sein, was ein Hotspot ist:<br />
	 * <br /> Ein Hotspot ist ein Punkt, relativ entfernt von der linken oberen Ecke des
	 * Mausbildes.<br /> Dieser Punkt gibt an, welcher Punkt exakt der "Klickpunkt" ist.<br /> <br
	 * />
	 * <p/>
	 * Angenommen das Mausbild misst 20 x 20 Pixel, und der Hotspot ist ein Punkt mit den
	 * Koordinaten (10|10), so waere der Hotspot in der Mitte des Bildes und bei einem Klick waere
	 * diese "klick"-Koordinate genau in der Mitte.<br /> Waere der Punkt (0|0), so waere der
	 * Hotspot genau in der linken oberen Ecke des Mausbildes.
	 *
	 * @param mausbild
	 * 		Das Bild-Objekt, das ab sofort das Mausbild sein wird und auch dementsprechend bewegt
	 * 		wird.
	 * @param hotspot
	 * 		Der bereits eingaengig beschriebene Hotspot-Punkt
	 * @param absolut
	 * 		Ob diese Maus absolut sein soll; ist dieser Wert <code>true</c>, so ist die Maus immer in
	 * 		der Mitte, dafuer bewegt sich die <code>Kamera</code> bei Mausverschiebung. Bei
	 * 		<code>false</code>verhaelt sich diese Maus genauso wie eine gewohnte Computermaus.
	 *
	 * @see #Maus(Raum, Punkt, boolean, boolean)
	 * @see Bild
	 * @see Kamera
	 */
	public Maus (Raum mausbild, Punkt hotspot, boolean absolut) {
		this(mausbild, hotspot, absolut, false);
	}

	/**
	 * Erstellt eine Maus, die die Kamera nicht bewegen kann und nicht absolut ist.<br /> Fuer
	 * naehere Erlaeuterung siehe die groesseren Konstruktoren.
	 *
	 * @param mausbild
	 * 		Das Bild der Maus.
	 * @param hotspot
	 * 		Der Hotspot.
	 *
	 * @see #Maus(Raum, Punkt, boolean)
	 */
	public Maus (Raum mausbild, Punkt hotspot) {
		this(mausbild, hotspot, false, false);
	}

	/**
	 * Voller Konstruktor für eine Standard-Maus.<br /> Hierbei gibt es ganz verschiedene Maeuse,
	 * die ueber ihren Index gewählt werden können. Hierzu ist das <i>Handbuch</i> zu konsultieren.
	 *
	 * @param mausArt
	 * 		Die Art der Maus. Jeder Wert steht fuer eine eigene Maus.
	 * @param absolut
	 * 		Ob die Maus absolut (immer in der Mitte) oder relativ (auf dem Bildschirm frei beweglich)
	 * 		sein soll.
	 * @param bewegend
	 * 		Ob die Maus die Kamera bewegen koennen soll oder nicht.
	 */
	public Maus (int mausArt, boolean absolut, boolean bewegend) {
		if (mausArt > MAX_ART_INDEX || mausArt < 0) {
			Logger.error("ACHTUNG! Die eingegene Mausart war nicht im erlaubten Rahmen (" + mausArt + "). Sie existiert nicht.");
		}
		this.mausArt = mausArt;
		this.absolut = absolut;
		this.bewegend = bewegend;
		this.bild = getImage();
		this.hotspot = hotSpot();
	}

	public Maus (int mausArt) {
		this(mausArt, false, false);
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
	 * Meldet ein KlickReagierbar bei der Maus an.<br /> Ab dann wird es bei jedem Mausklick
	 * (Linksklick) in der Engine benachrichtigt.
	 *
	 * @param k
	 * 		Das anzumeldende KlickReagierbar-Interface
	 */
	public void klickReagierbarAnmelden (KlickReagierbar k) {
		klickListe.add(k);
	}

	/**
	 * Meldet ein RechtsKlickReagierbar-Objekt bei der Maus an.<br /> Ab dann wird es bei jedem
	 * Rechtsklick benachrichtigt.
	 *
	 * @param k
	 * 		Das anzumeldende <code>RechtsKlickReagierbar</code>-Interface
	 */
	public void rechtsKlickReagierbarAnmelden (RechtsKlickReagierbar k) {
		rechtsKlickListe.add(k);
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
		loslassListe.add(m);
	}

	/**
	 * Meldet ein <code>MausBewegungReagierbar</code>-Objekt bei der Maus an. <br /> Ab dann wird es
	 * jedes mal durch Aufruf seiner Methode benachrichtigt, wenn die Maus bewegt wird.
	 *
	 * @param m
	 * 		Listener-Objekt
	 */
	public void mausBewegungReagierbarAnmelden (MausBewegungReagierbar m) {
		bewegungReagierbarListe.add(m);
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

		for (KlickReagierbar kr : von.klickListe) {
			this.klickReagierbarAnmelden(kr);
		}

		for (RechtsKlickReagierbar rkr : von.rechtsKlickListe) {
			this.rechtsKlickReagierbarAnmelden(rkr);
		}

		for (MausLosgelassenReagierbar mlr : loslassListe) {
			this.mausLosgelassenReagierbarAnmelden(mlr);
		}

		for (MausBewegungReagierbar mbr : bewegungReagierbarListe) {
			this.mausBewegungReagierbarAnmelden(mbr);
		}
	}

	/**
	 * Bei einer angemeldeten Maus wird bei einem Klick diese Methode aufgerufen.<br /> So lassen
	 * sich auch Klicks auf die Maus "simulieren".
	 *
	 * @param x
	 * 		Die X-Koordinate des Klicks
	 * @param y
	 * 		Die Y-Koordinate des Klicks
	 * @param links
	 * 		War der Klick ein Linksklick, ist dieser Wert <code>true</code>. Fuer jede andere Klickart
	 * 		ist dieser Wert <code>false</code>. In diesem Fall wird mit einem Rechtsklick gerechnet.
	 * @param losgelassen
	 * 		ist dieser Wert <code>true</code>, so wird dies als losgelassene Taste behandelt.
	 */
	public void klick (int x, int y, boolean links, boolean losgelassen) {
		Punkt p = new Punkt(x, y);
		if (losgelassen) {
			for (MausLosgelassenReagierbar m : loslassListe) {
				m.mausLosgelassen(x, y, links);
			}
			return;
		}
		if (links) {
			for (Auftrag a : mausListe) {
				a.klick(p);
			}
			for (KlickReagierbar k : klickListe) {
				k.klickReagieren(x, y);
			}
		} else {
			for (RechtsKlickReagierbar k : rechtsKlickListe) {
				k.rechtsKlickReagieren(x, y);
			}
		}
	}

	/**
	 * Bei einer angemeldeten Maus wird bei einem Klick diese Methode aufgerufen.<br /> Theoretisch
	 * liessen sich so Tastenklicks "simulieren".
	 *
	 * @param x
	 * 		Die X-Koordinate des Klicks
	 * @param y
	 * 		Die Y-Koordinate des Klicks
	 * @param links
	 * 		War der Klick ein Linksklick, ist dieser Wert <code>true</code>. Fuer jede andere Klickart
	 * 		ist dieser Wert <code>false</code>. In diesem Fall wird mit einem Rechtsklick gerechnet.
	 *
	 * @see ea.Maus#klick(int, int, boolean, boolean)
	 */
	public void klick (int x, int y, boolean links) {
		this.klick(x, y, links, false);
	}

	/**
	 * TODO Dokumentation
	 *
	 * @param dx
	 * 		Bewegte Pixel in x-Richtung
	 * @param dy
	 * 		Bewegte Pixel in y-Richtung
	 */
	public void bewegt (int dx, int dy) {
		if (dx == 0 && dy == 0) {
			return;
		}

		for (MausBewegungReagierbar m : bewegungReagierbarListe) {
			m.mausBewegt(dx, dy);
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
	 * @return Ob die Maus absolut ist oder nicht.
	 */
	public boolean absolut () {
		return this.absolut;
	}

	/**
	 * @return Ob die Maus den Bildschirm bewegen kann
	 */
	public boolean bewegend () {
		return this.bewegend;
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
		String verzeichnis = ""; // Das Verzeichnis der Mauskomponenten
		switch (mausArt) {
			case 0: // Standart
				verzeichnis += "blau.gif";
				break;
			case 1: // Fadenkreuz futuristisch
				verzeichnis += "fadenkreuz1.gif";
				break;
			case 2: // Hand mit Zeigefinger
				verzeichnis += "hand.gif";
				break;
			case 3: // Klassischer Zeiger
				verzeichnis += "klassisch.gif";
				break;
			default:
				verzeichnis += "fadenkreuz.gif";
				break;
		}
		InputStream in = Maus.class.getResourceAsStream(verzeichnis);
		try {
			ret = ImageIO.read(in);
		} catch (IOException ex) {
			System.err.println("Achtung! Das zu ladende Standard-Mausbild konnte nicht geladen werden.");
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

		switch (mausArt) {
			case 1: // Fadenkreuz
				x = 12;
				y = 12;
				break;
			case 2: // Hand
				x = 5;
				y = 0;
				break;
			case 3: // Zeiger - bleibt (0|0)
				break;
			default:
				//
				break;
		}

		return new Punkt(x, y);
	}

	/**
	 * Gibt den <i>Punkt auf der Zeichenebene</i> aus, auf den die Maus bei einem Klick zeigen
	 * würde. Diese Methode rechnet alle Umstände der Maus (z.B. relativ bzw. absolut) mit ein und
	 * gibt die genaue Position des Klicks zurück.
	 *
	 * @return Der genaue Punkt auf der Zeichenebene, auf den diese Maus bei einem Klick deuten
	 * würde.
	 */
	public Punkt klickAufZeichenebene () {
		if (absolut()) {
			BoundingRechteck r = bild.dimension();
			Punkt p = hotSpot();

			return new Punkt((int) (r.x + p.realX() + fenster.getCam().getX()), (int) (r.y + p.realY()
					+ fenster.getCam().getY())); // Mit
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
	public final class Auftrag {
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

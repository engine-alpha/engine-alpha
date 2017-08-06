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

package ea.mouse;

import ea.Punkt;
import ea.Vektor;
import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.gui.Fenster;
import ea.internal.ui.KlickEvent;
import ea.internal.ui.MausBewegungEvent;
import ea.raum.Bild;
import ea.raum.Raum;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Klasse zur Kontrolle der Maus
 *
 * @author Michael Andonie, Niklas Keller <me@kelunik.com>
 */
public class Maus {


    /* -------------------- INTERNAL STRUCTURES -------------------- */

    /**
     * Beschreibt, das Verfahren, nach dem eine Maus erstellt wurde
     */
    private static enum MausTyp {
        Standard, Custom
    }

	/* -------------------- Konstanten -------------------- */

	/**
	 * Diese Konstante gehört zum Standard-Cursor des jeweiligen Betriebssystems.
	 */
	public static final int TYP_STANDARD = Cursor.DEFAULT_CURSOR;

	/**
	 * Diese Konstante gehört zum Fadenkreuz-Cursor
	 */
	public static final int TYP_FADENKREUZ = Cursor.CROSSHAIR_CURSOR;

	/**
	 * Ein klassischer Zeiger, der eine Hand darstellt mit einem Zeigefinger
	 */
	public static final int TYP_HAND = Cursor.HAND_CURSOR;

	/**
	 * Ein klassischer Zeiger, wie unter Windows gewohnt
	 */
	public static final int TYP_MOVE = Cursor.MOVE_CURSOR;

    /**
     * Diese Konstante gehört zu einem komplett unsichtbaren Cursor.
     */
    private static final int TYP_UNSICHTBAR = -10;

    /**
     * Konstante für Linksklick. Wird manchen Reagierbar-Listenern mitgegeben.
     */
    public static final int LINKSKLICK = MouseEvent.BUTTON1;

    /**
     * Konstante für einen Klick mit dem Mausrad. Wird manchen Reagierbar-Listenern mitgegeben.
     */
    public static final int MAUSRAD = MouseEvent.BUTTON2;

    /**
     * Konstante für Rechtsklick. Wird manchen Reagierbar-Listenern mitgegeben.
     */
    public static final int RECHTSKLICK = MouseEvent.BUTTON1;



	/* -------------------- /Konstanten -------------------- */

    /* -------------------- FIELDS -------------------- */

    public ArrayList<KlickReagierbar> getKlickListeners() {
        return klickListeners;
    }

    public ArrayList<RechtsKlickReagierbar> getRechtsKlickListeners() {
        return rechtsKlickListeners;
    }

    public ArrayList<MausLosgelassenReagierbar> getMausLosgelassenListeners() {
        return mausLosgelassenListeners;
    }

    public ArrayList<MausBewegungReagierbar> getMausBewegungListeners() {
        return mausBewegungListeners;
    }

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
	 * Gibt an, ob die Maus fixiert ist oder nicht.
	 * <p/>
	 * In diesem Fall ist der Mauszeiger immer Mittelpunkt des Bildes, das Bild bewegungSimulieren sich dann
	 * immer gemäß der Mausbewegung.
	 */
	private boolean fixed;

	/**
	 * Gibt an, ob die Maus den angezeigten Bildschirmbereich verschieben kann.
	 */
	private boolean bewegend;

    private MausTyp mausTyp;

	/**
	 * Das Fenster, in dem diese Maus aktiv ist. <code>null</code>, falls es kein solches Fenster
	 * gibt.
	 */
	private final Fenster fenster;


    /* -------------------- CONSTRUCTOR -------------------- */

	/**
	 * Erstellt einen Maus-Handle.
     * Der Cursor ist der System-Standardcursor
	 */
	@NoExternalUse
	public Maus (Fenster fenster) {
        this.fenster = fenster;
        standardCursorSetzen(TYP_STANDARD);
	}



    /* -------------------- METHODS -------------------- */

    /**
     * Setzt einen Standard-Cursor für die Maus.
     * @param cursorTyp Der Code, der den gewünschten Cursor-Typ beschreibt.
     *                  Die zugehörigen Konstanten liegen in dieser Klasse.
     *                  Zum Beispiel <code>Maus.TYP_HAND</code>.
     */
    @API
    public void standardCursorSetzen(int cursorTyp) {
        if(!fixed) {
            mausTyp = MausTyp.Standard;

            Cursor cursor = cursorTyp == TYP_UNSICHTBAR ?
                    fenster.getToolkit().createCustomCursor(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB), new Point(0, 0), "NOCURSOR")
                        : Cursor.getPredefinedCursor(cursorTyp);
            fenster.setCursor(cursor);
        } else {
            throw new UnsupportedOperationException("Implementierung des fixed Cursor steht aus");
        }
    }

    /**
     * Setzt einen Custom-Cursor aus einem beliebigen Bild für die Maus.
     * @param cursorBild    Das Bild für den Cursor. (Die Position des Bild-Objektes ist egal)
     * @param hotSpot       Der Hotspot des Cursors relativ zur linken oberen Ecke des Cursor-Bildes. Die Koordinaten
     *                      des Punktes sollten ganzzahlig sein und werden in Pixel gemessen.
     */
    public void customCursorSetzen(Bild cursorBild, Punkt hotSpot) {
        if(!fixed) {
            fenster.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorBild.bild(), new Point((int) hotSpot.x, (int) hotSpot.y), cursorBild.toString()));
        } else {
            throw new UnsupportedOperationException("Implementierung des fixed Cursor steht aus");
        }
    }


	/**
	 * Alternativmethopde zum tastenReagierbarAnmelden. Hierbei gibt es keinen Code-Parameter; dieser wird
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
	 * jedes mal durch Aufruf seiner Methode benachrichtigt, wenn die Maus bewegungSimulieren wird.
	 *
	 * @param m
	 * 		Listener-Objekt
	 */
	public void mausBewegungReagierbarAnmelden (MausBewegungReagierbar m) {
		mausBewegungListeners.add(m);
	}
	// TODO NotUsed. Soll das für bewegungSimulieren und klick simuliert werden können?
	// Ansonsten muss mit @NoExternalUse annotiert werden.

	/**
	 * Simuliert einen Mausklick (mit dieser Maus) an einem bestimmten Punkt.
	 *
	 * @param p der respektive Punkt für den simulierten Mausklick.
	 * @param links
	 * 		War der Klick ein Linksklick, ist dieser Wert <code>true</code>. Fuer jede andere Klickart
	 * 		ist dieser Wert <code>false</code>. In diesem Fall wird mit einem Rechtsklick gerechnet.
	 *
	 * @see Maus#klick(Punkt, int, int, boolean)
	 */
	@API
	public void klick (Punkt p, boolean links) {
		this.klick(p, links ? LINKSKLICK : RECHTSKLICK, 1, false);
	}

	/**
	 * Simuliert einen Klick
	 *
	 * @param p die Ausgangsposition für die Maus
	 */
	public void klick (Punkt p, int klicktyp, int anzahlKlicks, boolean loslassen) {
		p = this.positionAufZeichenebene();

        KlickEvent e = new KlickEvent(fenster, p, klicktyp, anzahlKlicks, loslassen);

        fenster.getFrameThread().addUIEvent(e);
	}

	/**
	 * Simuliert eine Bewegung der Maus.
     *
     * @param bewegung  Der Bewegungsvektor <b>auf der Zeichenebene</b>, den die Maus zurückgelegt
     *                  hat.
	 */
	public void bewegungSimulieren(Vektor bewegung) {
		if (bewegung.unwirksam()) {
			return;
		}

        MausBewegungEvent event = new MausBewegungEvent(fenster, bewegung);
        fenster.getFrameThread().addUIEvent(event);
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
	 * @return Der genaue Punkt auf der Zeichenebene, auf den diese Maus bei einem Klick deuten würde.
     *          Ist <code>null</code>, wenn die Maus sich gerade nicht über der Zeichenebene befindet.
	 * würde.
	 */
    @SuppressWarnings("unused")
    @API
	public Punkt positionAufZeichenebene () {
        /*if(true) {
            throw new UnsupportedOperationException("Klick ist nicht auf Cursor gemappt.");
        }
		if (absolut()) {
			Punkt r = bild.position.get();
			Punkt p = hotSpot();

			return new Punkt((int) (r.x + p.realX() + real_fenster.getCam().getX()), (int) (r.y + p.realY() + real_fenster.getCam().getY())); // Mit
			// zurückrechnen
			// auf die
			// Bildebene!
		} else {
			//Fenster Dimension
			Dimension dim = real_fenster.getSize();
			int startX = (dim.width / 2);
			int startY = (dim.height / 2);
			return new Punkt(startX + real_fenster.getCam().getX(), startY + real_fenster.getCam().getY());
		}*/

        //TODO absolute implementation
        //TODO Kamera einberechnen

        //PointerInfo pointerInfo = MouseInfo.getPointerInfo();
        //Point pointOnScreen = pointerInfo.getLocation();

        Point p = fenster.zeichner().getMousePosition();
        if(p == null)
            return null;
        float zoom = fenster.zeichner().cam().getZoom();
        return new Punkt(p.x/zoom, p.y/zoom).verschobeneInstanz(
        		fenster.zeichner().cam().position().position().alsVektor());
	}

	/**
	 * @return Ob die Maus fix ist oder nicht.
	 */
	public boolean istFix () {
		return this.fixed;
	}

	/**
	 * Gibt die Liste aller Auftraege (interne Klasse!!) fuer MausReagierbar- Interfaces <br/> <br
	 * /> <b>ACHTUNG</b> Die ArrayList ist verantwortlich fuer die MausReagierbar-Aktionen. Daher
	 * stellt die Verwendung dieses Objekts durchaus eine Fehlerquelle dar.
	 *
	 * @return die Liste aller MausReagierbar-Auftraege
	 *
	 * @see Maus.Auftrag
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

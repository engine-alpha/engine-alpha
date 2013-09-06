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

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.awt.event.*;
/**
 * Das Fenster als Oberfenster.<br />
 * In ihm 'faengt sich die Maus, sie kann also das Fenster nicht mehr nach dem Betreten verlassen.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Fenster extends JFrame
{
    /**
     * Gibt an, ob das Fenster im Vollbildmodus arbeitet
     */
    private final boolean vollbild;

    /**
     * Gibt an, ob die aktuelle (relative) Maus innerhalb des passablen Fensterbereiches liegt.<br />
     * Gibt es keine solche ist dieser Wert irrelevant.
     */
    private volatile boolean mausAusBild = false;
    
    /**
     * Gibt an, ob der gerade Verarbeitete Klick mitzaehlt, also vom Benutzer selbst gemacht wurde.
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
     * Ein boolean-Array als die Tastentabelle, nach der fuer die gedrueckt-Methoden vorgegangen wird.<br />
     * Ist ein Wert <code>true</code>, so ist die Taste dieses Indexes heruntergedrueckt.
     */
    private volatile boolean[] tabelle;
    
    /**
     * Statische Hilfsinstanz zur Vereinfachung der Frameabhaengigen Abfragen
     */
    public static Fenster instanz = null;
    
    /**
     * Konstruktor fuer Objekte der Klasse Fenster.
     * @param   x   Die Breite des Fensters. (Bei erfolgreichem Vollbild die neue Standartbildschirmbreite)
     * @param   y   Die Hoehe des Fensters. (Bei erfolgreichem Vollbild die neue Standartbildschirmhoehe)
     * @param   titel   Der Titel, der auf dem Fenster gezeigt wird (Auch bei Vollbild nicht zu sehen).Wenn kein Titel erwuenscht ist, kann ein leerer String (<code>""</code>) eingegeben werden.
     * @param   vollbild    Ob das Fenster ein echtes Vollbild sein soll, sprich den gesamten Bildschirm ausfuellen soll und nicht mehr wie ein Fenster aussehen soll.<br />
     *  Es kann sein, dass Vollbild zum Beispiel aufgrund eines Veralteten Javas oder inkompatiblen PCs nicht moeglich ist, <b>in diesem Fall wird ein normales Fenster mit den eingegeben Werten erzeugt</b>.<br />
     *  Daher ist das x/y - Feld eine Pflichteingabe.
     * @param   fensterX    Die X-Koordinate des Fensters auf dem Computerbildschirm.
     * @param   fensterY    Die Y-Koordinate des Fensters auf dem Computerbildschirm.
     */
    public Fenster(int x, int y, String titel, boolean vollbild, int fensterX, int fensterY)
    {
        super(titel);
        this.setSize(x, y);
        tabelle = new boolean[45];
        for(int i = 0; i < tabelle.length; i++) {
            tabelle[i] = false;
        }
        this.vollbild = vollbild;
        GraphicsEnvironment env = GraphicsEnvironment.
            getLocalGraphicsEnvironment();
        GraphicsDevice[] devices = env.getScreenDevices();
        boolean isFullScreen = devices[0].isFullScreenSupported();
        Dimension screenSize = getToolkit().getScreenSize();
        if(vollbild && isFullScreen) {
            x = screenSize.width;
            y = screenSize.height;
        } else {
            int fx = screenSize.width / 8, fy = screenSize.height / 8;
            if(fensterX >= 0) {
                fx = fensterX;
            }
            if(fensterY >= 0) {
                fy = fensterY;
            }
            this.setLocation(fx, fy);
            if(vollbild && !isFullScreen) {
                System.err.println("Achtung! Es war trotz Wunsch nicht moeglich, das Fenster als Vollbild zu instanziieren! Vollbild wird von diesem PC nicht unterstuetzt!");
                this.setLocation(0, 0);
                x = screenSize.width;
                y = screenSize.height-30;
                this.setLocation(0, 0);
            }
        }
        zeichner = new Zeichner(x, y, new Kamera(x, y, new Zeichenebene()));
        this.add(zeichner);
        this.setResizable(false);
        //DER ROBOTER
        try {
            robot = new Robot(devices[0]);
        }catch (AWTException e) {
            System.err.println("ACHTUNG - es war nicht moeglich, ein GUI-Controlobjekt zu erstelllen! Zentrale Funktionen der Maus" +
                    "-Interaktion werden nicht funktionieren! Dies liegt an diesem PC.");
        }
        //DIE LISTENER
        this.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                tastenAktion(e);
            }
            public void keyReleased(KeyEvent e) {
                int i = zuordnen(e.getKeyCode());
                if(i == -1) {
                    return;
                }
                tabelle[i] = false;
                for(TastenLosgelassenReagierbar r : losListener) {
                    r.tasteLosgelassen(i);
                }
            }
            public void keyTyped(KeyEvent e) {
                //
            }
        });
        this.addMouseListener(new MouseListener() {
            public void mouseExited(MouseEvent e) {
                //
            }
            public void mouseEntered(MouseEvent e) {
                if(hatMaus()) {
                    Point po = getLocation();
                    Dimension dim = getSize();
                    int startX = (dim.width/2);
                    int startY = (dim.height/2);
                    robot.mouseMove(startX+po.x, startY+po.y);
                }
                zaehlt = false;
                robot.mousePress(InputEvent.BUTTON1_MASK);
                robot.mouseRelease(InputEvent.BUTTON1_MASK);
            }
            public void mouseReleased(MouseEvent e) {
                mausAktion(e, true);
            }/**Die Methode, die Klicks abfaengt.*/
            public void mousePressed(MouseEvent e) {
                mausAktion(e, false);
            }
            public void mouseClicked(MouseEvent e) {
                //
            }
        });
        this.addMouseMotionListener(new MouseMotionListener() {
            public void mouseMoved(MouseEvent e) {
                mausBewegung(e);
            }
            public void mouseDragged(MouseEvent e) {
                mausBewegung(e);
            }
        });
        this.setCursor(java.awt.Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR),new java.awt.Point(0,0),"NOCURSOR"));
        //
        this.addWindowListener(new Adapter(this));
        if(vollbild) {
            setUndecorated(isFullScreen);
            if (isFullScreen) {
                // Full-screen mode
                devices[0].setFullScreenWindow(this);

                //Aufloesung
                boolean erfolg = false;
                
                if(devices[0].isDisplayChangeSupported()) {
                    //DisplayMode disp = new DisplayMode(x, y, DisplayMode.BIT_DEPTH_MULTI, DisplayMode.REFRESH_RATE_UNKNOWN);
                    //devices[0].setDisplayMode(disp);
                    DisplayMode[] disp = devices[0].getDisplayModes();
                    System.out.println("Disp: " + disp.length);
                    for(int i = 0; i < disp.length; i++) {
                        System.out.println("Mode " + i + "H: " + disp[i].getHeight() +" - B: " + disp[i].getWidth());
                        if(disp[i].getHeight()==y && disp[i].getWidth()==x) {
                            System.out.println("SET!");
                            if(devices[0].isDisplayChangeSupported()) {
                                devices[0].setDisplayMode(new DisplayMode(x, y, DisplayMode.BIT_DEPTH_MULTI, DisplayMode.REFRESH_RATE_UNKNOWN));
                                erfolg = true;
                            } else {
                                System.err.println("Achtung! Dieser Bildschirm laesst seine Aufloesung nicht veraendern.");
                            }
                            break;
                        }
                    }
                } else {
                    System.err.println("Die Bildschirmgroesse konnte an diesem Computer nicht angepasst werden. \n"
                            + "Nur besondere Aufloesungen sind moeglich, z.B. 800 x 600.");
                }
                if(!erfolg) {
                    System.err.println("Die gewuenschte Aufloesung wird nicht von dem Hauptbildschirm des "
                            + "Computers unterstützt!");
                }
            } else {
                // Windowed mode
                pack();
                setVisible(true);
            }
        } else {
            this.setVisible(true);
        }
        Manager.standard.anmelden(new Ticker() {
            public void tick() {
                if(hatMaus() && !maus.absolut() && maus.bewegend()) {
                    try {
                        BoundingRechteck r = mausBild.dimension();
                        Punkt hs = maus.hotSpot();
                        BoundingRechteck praeferenz = mausPlatz();
                        Punkt p = new Punkt(r.x + hs.x, r.y+hs.y);
                        if(!praeferenz.istIn(p) && maus.bewegend()) {
                            getCam().verschieben((new Vektor(praeferenz.zentrum(), p).teilen(20)));
                        }
                    } catch(NullPointerException e) {
                        //Einfangen der maximal einmaligen RuntimeException zum sichern
                    }
                }
                for(TastenGedruecktReagierbar t : gedrListener) {
                    for(int i = 0; i < tabelle.length; i++) {
                        if(tabelle[i]) {
                            t.tasteGedrueckt(i);
                        }
                    }
                }
            }
        }, 50);
        instanz = this;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.validate();
    }
    
    /**
     * Einfacher Alternativkonstruktor.<br />
     * Erstellt ein normales Fenster mit der eingegeben Groesse.
     * @param   x   Die Breite
     * @param   y   Die Hoehe
     */
    public Fenster(int x, int y) {
        this(x, y, "EngineAlpha - Ein Projekt von Michael Andonie", false, 50, 50);
    }

    /**
     * Minimiert das Fenster (bringt es in die Taskleiste).
     */
    public void minimieren() {
        setState(JFrame.ICONIFIED);
    }

    /**
     * Maximiert das Fenster (bringt es aus der Taskleiste wieder auf den Bildschirm)
     */
    public void maximieren() {
        setState(JFrame.NORMAL);
    }

    /**
     * Gibt zurueck, ob dieses Fenster ein Vollbild ist oder nicht.
     * @return  <code>true</code>, wenn das Fenster ein Vollbild ist, sonst <code>false</code>.
     */
    public boolean vollbild() {
        return vollbild;
    }
    
    /**
     * Meldet den hintergrund dieses Fensters und damit des Spiels an.<br />
     * Gibt es bereits einen, so wird dieser fortan nicht mehr gezeichnet, dafuer nun dieser. Sollten mehrere Objekte erwuenscht sein, gezeichnet zu werden, so empfiehlt es sich, 
     * diese in einem <code>Knoten</code>-Objekt zu sammeln und dann anzumelden.<br />
     * <b>Achtung!</b><br />
     * Diese Objekte sollten nicht an der Physik angemeldet werden, dies fuehrt natuerlich zu ungewollten Problemen!
     * @param   hintergrund Der anzumeldende Hintergrund
     */
    public void hintergrundAnmelden(Raum hintergrund) {
        zeichner.hintergrundAnmelden(hintergrund);
    }
    
    /**
     * Meldet einen TastenReagierbar - Listener an.
     * @param   t   Der neu anzumeldende Listener.
     */
    public void anmelden(TastenReagierbar t) {
        if(t == null) {
            System.err.println("Der Listenr war null !!!");
            return;
        }
        listener.add(t);
    }
    
    /**
     * Meldet einen TastenGedruecktReagierbar-Listener an.<br />
     * <b>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ACHTUNG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</b><br />
     * TastenReagierbar und TastenGedruecktReagierbar sind 2 vollkommen unterschiedliche Interfaces! Das eine wird bei jedem Druck aufs 
     * neue aktiviert, waehrend das andere gleichmaessig aufgerufen wird, solange die Taste heruntergedrueckt ist.
     * @param   t   Der listener, der ab sofort ueber gedrueckte Tasten informiert wird.
     */
    public void tastenGedruecktAnmelden(TastenGedruecktReagierbar t) {
        if(t == null) {
            System.err.println("Der Listenr war null !!!");
            return;
        }
        gedrListener.add(t);
    }
    
    /**
     * Meldet einen TastenGedruecktReagierbar-Listener an.<br />
     * Diese Methode warappt lediglich <code>tastenGedruecktAnmelden(TastenGedruecktReagierbar)</code> und dient der Praevention unnoetiger 
     * Compilermeldungen wegen nichtfindens einer Methode.
     * @param   t   Der listener, der ab sofort ueber gedrueckte Tasten informiert wird.
     * @see tastenGedruecktAnmelden(TastengedruecktReagierbar)
     */
    public void tastenGedruecktReagierbarAnmelden(TastenGedruecktReagierbar t) {
        this.tastenGedruecktAnmelden(t);
    }

    /**
     * Meldet einen TastenLosgelassenReagierbar-Listener an.<br />
     * <b>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ACHTUNG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</b><br />
     * TastenReagierbar und TastenLosgelassenReagierbar sind 2 vollkommen unterschiedliche Interfaces! Das eine wird beim Runterdruecken, das andere 
     * beim Loslassen der Tasten aktiviert.
     */
    public void tastenLosgelassenAnmelden(TastenLosgelassenReagierbar t) {
        if(t == null) {
            System.err.println("Der Listenr war null !!!");
            return;
        }
        losListener.add(t);
    }
    
    /**
     * Meldet einen TastenLosgelassenReagierbar-Listener an als exakt parallele Methode zu <code>tastenLosgelassenAnmelden()</code>, jedoch 
     * eben ein etwas laengerer, aber vielleicht auch logischerer Name; fuehrt jedoch exakt die selbe Methode aus!<br />
     * <b>!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!ACHTUNG!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!</b><br />
     * <code>TastenReagierbar</code> und <code>TastenLosgelassenReagierbar</code> sind 2 vollkommen unterschiedliche Interfaces!
     * Das eine wird beim Runterdruecken, das andere beim Loslassen der Tasten aktiviert.
     * @see #tastenLosgelassenAnmelden(TastenLosgelassenReagierbar)
     */
    public void tastenLosgelassenReagierbarAnmelden(TastenLosgelassenReagierbar t) {
        this.tastenLosgelassenAnmelden(t);
    }

    /**
     * Meldet eine Maus an.<br />
     * Im Gegensatz zu den TastenReagierbar-Listenern kann nur eine Maus am Fenster angemeldet sein.
     * @param m Die anzumeldende Maus
     */
    public void anmelden(Maus m) {
        if(hatMaus()) {
            System.err.println("Es ist bereits eine Maus angemeldet!");
        } else {
            maus = m;
            Dimension d = this.getSize();
            Raum b = maus.getImage();
            BoundingRechteck dim = b.dimension();
            b.positionSetzen(((d.width-dim.breite)/2), (d.height-dim.hoehe)/2);
            //mausBild = new Bild((d.width-i.getWidth(this))/2, (d.height-i.getHeight(this))/2, i);
            mausBild = b;
            zeichner.anmelden(mausBild);
        }
    }
    
    /**
     * Loescht das Maus-Objekt des Fensters.<br />
     * Hatte das Fenster keine, ergibt sich selbstredend keine Aenderung.
     */
    public void mausLoeschen() {
        this.setCursor(java.awt.Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(1,1,BufferedImage.TYPE_4BYTE_ABGR),new java.awt.Point(0,0),"NOCURSOR"));
        maus = null;
    }
    
    /**
     * Testet, ob eine Maus im Spiel vorhanden ist.
     * @return  TRUE, wenn eine Maus im Spiel ist.
     */
    public boolean hatMaus() {
        return (maus != null);
    }
    
    /**
     * Gibt an, ob die Maus den Bildschirm bewegen kann.
     * @return TRUE, wenn die Maus den Bildschirm bewegen kann, FALSE, wenn nicht.<br />
     * Ist keine Maus angemeldet, ist das Ergebnis ebenfalls FALSE.
     */
    public boolean mausBewegt() {
        if(hatMaus()) {
            return maus.bewegend();
        }
        return false;
    }
    
    /**
     * @return  Die Kamera, passend zu diesem Fenster
     */
    public Kamera getCam() {
        return zeichner.cam();
    }
    
    /**
     * @return  Der Statische Basisknoten.
     */
    public Knoten getStatNode() {
        return zeichner.statNode();
    }
    
    /**
     * Gibt die Fenstermasse in einem BoundingRechteck an.
     * @return ein BoundingRechteck mit Position (0|0), dessen Hoehe & Breite die Masse des Fensters in Pixel angeben.
     * @see ea.BoundingRechteck
     */
    public BoundingRechteck fenstermasse() {
        return zeichner.masse();
    }

    /**
     * Statische Methode zum Oeffentlichen Berechnen der Fontmetriken des offenen Fensters.
     * @param   f   Der zu ueberpruefende Font
     * @return  Das zu dem Font und aktiven Fenster gehoerende FontMetrics-Objekt
     */
    public static FontMetrics metrik(Font f) {
        return instanz.getFontMetrics(f);
    }

    /**
     * Gibt die aktuellste Instanz dieser KLasse wieder.
     * @return  Das aktuellste Fenster
     */
    public static Fenster instanz() {
        return instanz;
    }
    
    /**
     * Gibt den Zeichner des Fensters aus.
     * @return Der Zeichner des Fensters.
     */
    public Zeichner zeichner() {
        return zeichner;
    }

    /**
     * Entfernt ein simples Grafikobjekt.
     * @param g Das darzustellende Grafikobjekt
     */
    public void removeSimple(SimpleGraphic g) {
        zeichner.removeSimple(g);
    }

    /**
     * Fuellt ein simples Grafikobjekt in die anzeige.
     * @param g Das darzustellende Grafikobjekt.
     */
    public void fillSimple(SimpleGraphic g) {
        zeichner.addSimple(g);
    }
    
    /**
     * Loescht das Fenster und terminiert damit das Spiel.<br />
     * <b>Daher nur dann einsetzen, wenn die Anwendung beendet werden soll!! Der vorherige Zustand ist nicht wiederherstellbar!!</b><br />
     * Als alternative Methode zum ausschliesslichen Loeschen des Fensters steht
     * <code>softLoeschen()</code> zur Verfuegung.
     */
    public void loeschen() {
        this.dispose();
        System.exit(0);
    }
    
    /**
     * Faehrt das Fenster runter, ohne die virtuelle Maschine zu beenden.
     */
    public void softLoeschen() {
        zeichner.kill();
        zeichner = null;
        this.dispose();
    }
    
    /**
     * Gibt das gespeicherte Bild-Objekt der Maus wieder.
     * @return  Das Bild mit seiner Position und Groesse von der Maus.
     */
    public Raum mausBild() {
        return this.mausBild;
    }
    
    /**
     * @return  Das BoundingRechteck, dass den Spielraum der Maus ohne Einbezug der Relativen Koordinaten (Kameraposition)<br />
     * Das Rechteck ist die Masse des Fensters mal 3/4.<br />
     * Dies ist natuerlich nur dann im Fenster gebraucht, wenn eine relative Maus angemeldet ist.
     */
    private BoundingRechteck mausPlatz() {
        Dimension d = this.getSize();
        int x = (d.width/4)*3;
        int y = (d.height/4)*3;
        return new BoundingRechteck((d.width/8), (d.height/8), x, y);
    }
    
    /**
     * Deaktiviert den eventuell vorhandenen gemerkten Druck auf allen Tasten.<br />
     * Wird innerhalb der Engine benutzt, sobald das Fenster deaktiviert etc. wurde.
     */
    public void druckAufheben() {
        for(int i = 0; i < tabelle.length; i++) {
            tabelle[i] = false;
        }
    }
    
    /**
     * Diese Methode wird ausgefuehrt, wenn die Maus bewegt wird.
     * @param   e   Das ausloesende Event
     */
    private void mausBewegung(MouseEvent e) {
        if(hatMaus()) {
            Dimension dim = this.getSize();
            int startX = (dim.width/2);
            int startY = (dim.height/2);
            Point po = getLocation();
            Point p = e.getPoint();
            if(maus.bewegend()) {
                if(maus.absolut()) {
                    getCam().verschieben(new Vektor(p.x-startX, p.y-startY));
                }
            }
            if(!maus.absolut()) {
                int x = p.x-startX;
                int y = p.y-startY;
                BoundingRechteck r = mausBild.dimension();
                Punkt spot = maus.hotSpot();
                Punkt hx = new Punkt(r.x + spot.x, r.y+spot.y);
                Punkt hy = new Punkt(r.x + spot.x, r.y+spot.y);
                hx = hx.verschobenerPunkt(new Vektor(x, 0));
                hy = hy.verschobenerPunkt(new Vektor(0, y));
                if(!zeichner.masse().istIn(hx)) {
                    x = 0;
                }
                if(!zeichner.masse().istIn(hy)) {
                    y = 0;
                }
                mausBild.verschieben(new Vektor(x, y));
            }
            robot.mouseMove(startX+po.x, startY+po.y);
        }
    }
    
    /**
     * Diese Methode wird immer dann ausgefuehrt, wenn ein einfacher Linksklick der Maus ausgefuehrt wird.
     * @param   e   Das MausEvent
     * @paran   losgelassen Ist dieser Wert TRUE, wurde die Maus eigentlich losgelassen und nicht 
     * geklickt.
     */
    private void mausAktion(MouseEvent e, boolean losgelassen) {
        if(!zaehlt) {
            zaehlt = true;
            return;
        }
        final boolean links; //Ist dies ein Linksklick? 1: Links  --- 3: Rechts
        links = !(e.getButton() == e.BUTTON3);
        if(hatMaus()) {
            if(!maus.absolut()) {
                BoundingRechteck r = mausBild.dimension();
                Punkt p = maus.hotSpot();
                maus.klick(r.x+p.x+getCam().getX(), r.y+p.y+getCam().getY(), links, losgelassen); //Mit zurueckrechnen auf die Bildebene!
            } else {
                Dimension dim = this.getSize();
                int startX = (dim.width/2);
                int startY = (dim.height/2);
                maus.klick(startX+getCam().getX(), startY+getCam().getY(), links, losgelassen);
            }
        }
    }
    
    /**
     * Die Listener-Methode, die vom Fenster selber bei jeder gedrueckten Taste aktiviert wird.<br />
     * Hiebei wird die Zuordnung zu einer Zahl gemacht, und diese dann an alle Listener weitergereicht, sofern die Taste innerhalb der Kennung des Fensters liegt.<br />
     * Hierzu: Die Liste der Tasten mit Zuordnung zu einem Buchstaben; sie ist im <b>Handbuch</b> festgehalten.
     * @param   e   Das ausgeloeste KeyEvent zur Weiterverarbeitung.
     */
    private void tastenAktion(KeyEvent e) {
        int z = zuordnen(e.getKeyCode());
        if(z == -1) {
            return;
        } 
        if(tabelle[z]) {
            return;
        }
        for(TastenReagierbar r : listener) {
            r.reagieren(z);
        }
        tabelle[z] = true;
    }

    /**
     * Ordnet vom JAVA-KeyCode System in das EA-System um.
     * @param   keyCode Der JAVA-KeyCode
     * @return  Der EA-KeyCode
     */
    public int zuordnen(int keyCode) {
        int z = -1;
        //ANALYSIS
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
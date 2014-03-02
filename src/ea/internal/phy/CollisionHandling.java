package ea.internal.phy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import ea.*;
import ea.internal.gui.Fenster;
import ea.internal.util.Logger;

/**
 * Diese Klasse fungiert als Modul zum Behandeln von <b>Kollisionen</b> zwischen
 * <i>mechanischen <code>Raum</code>-Objekten</i>.
 * Es arbeitet daher zusammen mit dem Mechanik-Client.
 * 
 * @author Michael Andonie
 */
public class CollisionHandling {
	
	static {
		Manager.standard.anmelden(new CollisionRoutine(), ea.internal.gra.Zeichner.UPDATE_INTERVALL);
		new CollisionConsumer().start();
	}
	
	private static class CollisionConsumer
			extends Thread {
		private CollisionConsumer() {
			super("Collision Handling");
			this.setDaemon(true);
		}
		
		@Override
		public void run() {
			while (!interrupted()) {
				synchronized (colQueue) {
					if (colQueue.isEmpty())
						try {
							colQueue.wait();
						} catch (InterruptedException e) {
							// Nothing to do.
							Logger.error("Unerwarteter Fehler im Collision Handling Thread");
						}
					colQueue.remove().abarbeiten();
				}
			}
		}
	}
	
	@SuppressWarnings("serial")
	private static class CollisionRoutine
			implements Ticker {
		
		@Override
		public void tick() {
			int i = 0;
			synchronized (liste) {
				for (Auftrag a : liste) {
					int j = 0;
					for (Auftrag a2 : liste) {
						if (j > i && a.probablehit(a2) && a.serioushit(a2)) {
							synchronized (colQueue) {
								colQueue.add(new Collision(new Auftrag[] { a, a2 }));
								colQueue.notify();
							}
						}
						j++;
					}
					i++;
				}
			}
		}
		
	}
	
	/**
	 * Diese Klasse speichert die wesentlichen Daten zu einem Auftrag:
	 * Collider für schnelle Berechnung sowie der dahinterliegende
	 * Controller.
	 * 
	 * @author Michael Andonie
	 * 
	 */
	private static class Auftrag {
		final MechanikClient parent;
		
		Auftrag(MechanikClient p) {
			parent = p;
		}
		
		public boolean serioushit(Auftrag a) {
			return parent.ziel().schneidet(a.parent.ziel());
		}
		
		public boolean probablehit(Auftrag a) {
			return parent.collider().schneidet(a.parent.collider());
		}
	}
	
	/**
	 * Diese Klasse repräsentiert eine Kollision zwischen zwei physikalischen
	 * Objekten. Sie wird von einem Consumer-Thread abgearbeitet.
	 * 
	 * @author Andonie
	 * 
	 */
	private static class Collision {
		final Auftrag[] clients;
		
		public Collision(Auftrag[] clients) {
			this.clients = clients;
		}
		
		public void abarbeiten() {
			final MechanikClient c1 = clients[0].parent;
			final MechanikClient c2 = clients[1].parent;
			
			//Fenster.instanz.getCam().wurzel().add(c1.ziel().dimension().ausDiesem(),c2.ziel().dimension().ausDiesem());
			//System.out.println("work: " + c1.ziel() + " - " + c2.ziel());
			// First of all: Clients voneinander lösen
			if (c1.istBeeinflussbar() || c2.istBeeinflussbar()) {
				/*float clean = (c1.collider().getRadius() + c2.collider().getRadius()
						- new Punkt(c1.collider().getX(), c1.collider().getY()).abstand(
						new Punkt(c2.collider().getX(), c2.collider().getY())));
				// Vektor "von 1 nach 2"
				Vektor v1n2 = new Vektor(c2.collider().getX() - c1.collider().getX(),
						c2.collider().getY() - c1.collider().getY());
				Vektor normiert = v1n2.normiert();
				c2.bewegen(normiert.multiplizieren(clean / 2));
				c1.bewegen(normiert.multiplizieren(clean / 2).gegenrichtung());*/
				c1.bewegen(c1.getVelocity().gegenrichtung().multiplizieren(MechanikClient.DELTA_T));
				c2.bewegen(c2.getVelocity().gegenrichtung().multiplizieren(MechanikClient.DELTA_T));
			}
			if (!c1.istBeeinflussbar()) {
				if (!c2.istBeeinflussbar()) {
					// Nothing to do
					return;
				} else {
					ungleichlogik(c2, c1);
				}
			} else {
				if (!c2.istBeeinflussbar()) {
					ungleichlogik(c1, c2);
				} else {
					doppelaktivlogik(c1, c2);
				}
			}
		}
		
		/**
		 * Abarbeiten: Beeinflussbar auf unbeeinflussbar.
		 * 
		 * @param beeinflussbar
		 *            Das beeinflussbare Element.
		 * @param unbeeinflussbar
		 *            Das unbeeinflussbare Element.
		 */
		public void ungleichlogik(MechanikClient beeinflussbar, MechanikClient unbeeinflussbar) {
			System.out.println("Passive!");
			
			//stupid logic: nur parallel zum Fenster.
			Punkt zmov = beeinflussbar.ziel().zentrum();
			BoundingRechteck bounds = unbeeinflussbar.ziel().dimension();
			
			Vektor vneu = beeinflussbar.getVelocity();
			
			if(zmov.realX() <= bounds.x + bounds.breite/2) {
				//Aktiv LINKS von Passiv
				if(zmov.realX() > bounds.x) {
					//Apprall oben / unten
					System.out.println("o/u!");
					vneu = new Vektor(vneu.x, -vneu.y);
				} else {
					//Abprall rechts
					System.out.println("r!");
					vneu = new Vektor(-vneu.x, vneu.y);
				}
			} else {
				//Aktiv RECHTS von Passiv
				if(zmov.realX() < bounds.x + bounds.breite) {
					//Abprall oben / unten
					System.out.println("o/u!");
					vneu = new Vektor(vneu.x, -vneu.y);
				} else {
					//Abprall links
					System.out.println("l!");
					vneu = new Vektor(-vneu.x, vneu.y);
				}
			}
			
			beeinflussbar.geschwindigkeitSetzen(vneu);
		}
		
		/**
		 * Abarbeiten: 2 beeinflussbare Objekte prallen aufeinander. ->Impulsspaß
		 * 
		 * @param c1
		 *            Client 1
		 * @param c2
		 *            Client 2
		 */
		public void doppelaktivlogik(MechanikClient c1, MechanikClient c2) {
			// Elastischer Stoß! -> Impulserhaltung
			Vektor v1 = c1.getVelocity(), v2 = c2.getVelocity();
			float m1 = c1.getMasse(), m2 = c2.getMasse();
			float vx1 = v1.realX(), vx2 = v2.realX(), vy1 = v1.realY(), vy2 = v2.realY();
			
			float vx1neu = (m1 * vx1 + m2 * (2 * vx2 - vx1)) / (m1 + m2);
			float vy1neu = (m1 * vy1 + m2 * (2 * vy2 - vy1)) / (m1 + m2);
			
			float vx2neu = (m2 * vx2 + m1 * (2 * vx1 - vx2)) / (m1 + m2);
			float vy2neu = (m2 * vy2 + m1 * (2 * vy1 - vy2)) / (m1 + m2);
			
			// c1.geschwindigkeitSetzen(v1.multiplizieren(m1).summe(v2.multiplizieren(2).differenz(v1).multiplizieren(m2)).teilen(m1+m2));
			// c2.geschwindigkeitSetzen(v2.multiplizieren(m2).summe(v1.multiplizieren(2).differenz(v2).multiplizieren(m1)).teilen(m1+m2));
			c1.geschwindigkeitSetzen(new Vektor(vx1neu, vy1neu));
			c2.geschwindigkeitSetzen(new Vektor(vx2neu, vy2neu));
		}
	}
	
	private static ArrayList<Auftrag> liste = new ArrayList<Auftrag>();
	
	private static Queue<Collision> colQueue = new LinkedList<Collision>();
	
	/**
	 * Meldet einen Client für ein Newton'sches Objekt an.
	 * Nach der Anmeldung sind <i>Collisions-Checks</i> sowie
	 * </i>Collision-Handling</i> aktiv.
	 * 
	 * @param c
	 *            der anzumeldende Client.
	 */
	public static void anmelden(MechanikClient c) {
		synchronized (liste) {
			liste.add(new Auftrag(c));
		}
	}
	
	/**
	 * Meldet einen Client direkt vom Handling ab. Er blockiert danach 
	 * keine Rechenzeit und keinen Speicherplatz mehr im Collision Handling.d
	 * @param c Der abzumeldende Client.
	 */
	public static void abmelden(MechanikClient c) {
		Auftrag a = null;
		for (Auftrag auf : liste) {
			if(auf.parent == c) {
				a = auf;
				break;
			}
		}
		if(a != null) {
			synchronized(liste) {
				liste.remove(a);
			}
		}
	}
}

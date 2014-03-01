package ea.internal.phy;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import ea.*;
import ea.internal.util.Logger;

/**
 * Diese Klasse fungiert als Modul zum Behandeln von <b>Kollisionen</b> zwischen
 * <i>mechanischen <code>Raum</code>-Objekten</i>.
 * Es arbeitet daher zusammen mit dem Mechanik-Client.
 * @author Michael Andonie
 */
public class CollisionHandling {
	
	static {
		
	}
	
	private static class CollisionConsumer
	extends Thread {
		public CollisionConsumer() {
			super("Collision Handling");
			this.setDaemon(true);
			start();
		}
		
		@Override
		public void run() {
			while(!interrupted()) {
				synchronized(colQueue) {
					if(colQueue.isEmpty())
						try {
							colQueue.wait();
						} catch (InterruptedException e) {
							//Nothing to do.
							Logger.error("Unerwarteter Fehler im Collision Handling Thread");
						}
					colQueue.remove().abarbeiten();
				}
			}
		}
	}
	
	private static class CollisionRoutine 
	implements Ticker {

		@Override
		public void tick() {
			int i = 0;
			synchronized(liste) {
				for(Auftrag a : liste) {
					int j = 0;
					for(Auftrag a2 : liste) {
						if(j > i && a.probablehit(a2) && a.serioushit(a2)) {
							synchronized(colQueue) {
								colQueue.add(new Collision(new MechanikClient[]{a.parent, a2.parent}));
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
	 * @author Michael Andonie
	 *
	 */
	private static class Auftrag {
		final KreisCollider collider;
		final MechanikClient parent;
		
		Auftrag(KreisCollider c, MechanikClient p) {
			collider = c;
			parent = p;
		}
		
		public boolean serioushit(Auftrag a) {
			return parent.ziel().schneidet(a.parent.ziel());
		}

		public boolean probablehit(Auftrag a) {
			return collider.schneidet(a.collider);
		}
	}
	
	/**
	 * Diese Klasse repräsentiert eine Kollision zwischen zwei physikalischen
	 * Objekten. Sie wird von einem Consumer-Thread abgearbeitet.
	 * @author Andonie
	 *
	 */
	private static class Collision {
		final MechanikClient[] clients;
		
		public Collision(MechanikClient[] clients) {
			this.clients = clients;
		}
		
		public void abarbeiten() {
			final MechanikClient c1 = clients[0];
			final MechanikClient c2 = clients[1];
			if(!c1.istBeeinflussbar()) {
				if(!c2.istBeeinflussbar()) {
					//Nothing to do
					return;
				} else {
					ungleichlogik(c2, c1);
				}
			} else {
				if(!c2.istBeeinflussbar()) {
					ungleichlogik(c1, c2);
				} else {
					doppelaktivlogik(c1, c2);
				}
			}
		}
		
		/**
		 * Abarbeiten: Beeinflussbar auf unbeeinflussbar.
		 * @param beeinflussbar		Das beeinflussbare Element.
		 * @param unbeeinflussbar	Das unbeeinflussbare Element.
		 */
		public void ungleichlogik(MechanikClient beeinflussbar, MechanikClient unbeeinflussbar) {
			
		}
		
		/**
		 * Abarbeiten: 2 beeinflussbare Objekte prallen aufeinander.
		 * @param c1	Client 1
		 * @param c2	Client 2
		 */
		public void doppelaktivlogik(MechanikClient c1, MechanikClient c2) {
			
		}
	}
	
	private static ArrayList<Auftrag> liste = new ArrayList<Auftrag>();
	
	private static Queue<Collision> colQueue = new LinkedList<Collision>();
	
	private static void anmelden(MechanikClient c) {
		synchronized(liste) {
			liste.add(new Auftrag(c.ziel().dimension().umschliessenderKreis(), c));
		}
	}
}

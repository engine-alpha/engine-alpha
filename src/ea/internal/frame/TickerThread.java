package ea.internal.frame;

import ea.Ticker;
import ea.internal.util.Logger;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Der Ticker-Thread ist ein Subthread der Frame-Logik. Er bestimmt innerhalb seiner Frame-Logik alle Ticker,
 * die in dem respektiven Frame ausgeführt werden sollen.
 * Created by andonie on 15.02.15.
 */
public class TickerThread
extends ProducerThread {

    /**
     * Thread Counter für die exakte Thread-Bezeichnung
     */
    private static int ttcnt = 1;

    /**
     * Die Liste mit allen Ticker-Jobs.
     */
    private final ArrayList<TickerJob> tickerJobs;

    /**
     * Erstellt einen neuen TickerThread.
     * @param master    Der Master FrameThread, dem dieser Thread zuarbeitet.
     * @param queue     Die Queue, die der Dispatcher sequentiell abarbeitet.
     */
    protected TickerThread(FrameThread master, Queue queue) {
        super(master, "Ticker Thread #" + ttcnt++, queue);
        tickerJobs = new ArrayList<TickerJob>();
    }

    /**
     * Nimmt einen Ticker in die frameweise Abarbeitung mit auf.
     * @param ticker    Ein Ticker
     * @param intervall Das Intervall, in dem der Ticker ab sofort (frameweise) ausgeführt werden soll.
     */
    public void addTicker(Ticker ticker, int intervall) {
        tickerJobs.add(new TickerJob(ticker, intervall));
        Logger.debug("Ticker", String.format("Neuer Ticker mit Intervall %s ms angemeldet.", intervall));
    }

    public void removeTicker(Ticker ticker) {
        if(!tickerJobs.remove(ticker)) {
            Logger.error("Der abzumeldende Ticker war nicht angemeldet.");
        }
    }

    @Override
    public void frameLogic() {
        int ms = master.getLastFrameTime(); // Tatsächlich vergangene Zeit seit letztem Frame
        for(TickerJob tj : tickerJobs) {
            tj.discount(ms);
        }
    }

    /**
     * Die interne TickerJob-Klasse speichert ein Ticker-Tupel (ticker|intervall) und übernimmt
     * die Abarbeitung der einzelnen Ticks.
     */
    public class TickerJob implements Dispatchable {

        /**
         * Der Ticker
         */
        private final Ticker ticker;

        /**
         * Das zugehörige Intervall, in dem der Ticker aufgerufen werden soll (in ms)
         */
        private final int intervall;

        /**
         * Der Countdown. Gibt an, wie viele ms für den nächsten Tick noch übrig sind.
         */
        private int countdown;

        private TickerJob(Ticker ticker, int intervall) {
            this.ticker = ticker;
            this.intervall = intervall;
            this.countdown = intervall;
        }

        /**
         * Wird pro Frame einmal vom Producer-Thread aufgerufen. Rechnet eine bestimmte Anzahl an Millisekunden für die
         * Tick-Abfrage runter. Ggf. meldet sich dieses Objekt in der <code>dispatcherQueue</code> an (ggf. auch
         * mehrfach).
         * @param ms    Die Anzahl an Millisekunden, die seit dem letzten Mal vergangen sind.
         */
        private void discount(int ms) {
            if(countdown > ms) {
                //Es ist nicht genug Zeit vergangen, um einen Tick auszuführen.
                countdown -= ms;
            } else {
                //Es ist genug Zeit vergangen, um mindestens einen Tick auszuführen.
                int rest = ms-countdown; //Overhead berechnen
                countdown = intervall; //countdown zurücksetzen
                synchronized (dispatcherQueue) {
                    dispatcherQueue.add(TickerThread.TickerJob.this);
                }
                //nochmal mit overhead ausführen
                discount(rest);
            }
        }

        /**
         * In der Dispatch-Methode wird einfach der Ticker ausgeführt.
         */
        @Override
        public void dispatch() {
            ticker.tick();
        }
    }
}

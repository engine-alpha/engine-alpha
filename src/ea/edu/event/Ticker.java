package ea.edu.event;

import ea.edu.Spiel;
import ea.internal.annotations.API;

@API
public interface Ticker {

    /**
     * Die Tick-Methode. Sie wird in einem regelmäßigen Intervall aufgerufen, sobald dieser Ticker gestartet wurde.
     *
     * @see #starteTickerNeu(double)
     */
    @API
    void tick();

    /**
     * Started den Ticker innerhalb der aktuellen Scene.
     *
     * @param intervalInS Das Interval in s, in dem dieser Ticker ausgeführt werden soll.
     *
     * @see #stoppeTicker()
     */
    @API
    default void starteTickerNeu(double intervalInS) {
        Spiel.getActiveScene().addEduTicker((float) intervalInS, this);
    }

    /**
     * Stoppt den Ticker.
     *
     * @see #starteTickerNeu(double)
     */
    @API
    default void stoppeTicker() {
        Spiel.getActiveScene().removeEduTicker(this);
    }
}

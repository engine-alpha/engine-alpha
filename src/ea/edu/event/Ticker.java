package ea.edu.event;

import ea.edu.Spiel;
import ea.internal.annotations.API;

@API
public interface Ticker {

    /**
     * Die Tick-Methode. Sie wird in einem regelmäßigen Intervall aufgerufen, sobald dieser Ticker gestartet wurde.
     *
     * @see #starteTickerNeu(float)
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
    default void starteTickerNeu(float intervalInS) {
        Spiel.getActiveScene().addEduTicker(intervalInS, this);
    }

    /**
     * Stoppt den Ticker.
     *
     * @see #starteTickerNeu(float)
     */
    @API
    default void stoppeTicker() {

    }
}

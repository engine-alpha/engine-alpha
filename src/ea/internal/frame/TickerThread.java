package ea.internal.frame;

import ea.Ticker;

import java.util.ArrayList;
import java.util.Queue;

/**
 * Der Ticker-Thread ist ein Subthread der Frame-Logik. Er bestimmt innerhalb seiner Frame-Logik alle Ticker,
 * die in dem respektiven Frame ausgeführt werden sollen.
 * Created by andonie on 15.02.15.
 */
public class TickerThread
extends ProducerThread {



    protected TickerThread(Queue queue) {
        super(queue, "Ticker");
    }
}

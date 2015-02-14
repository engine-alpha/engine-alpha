package ea.internal.frame;

import java.util.Queue;

/**
 * Dieser Producer-Thread arbeitet alle UI-Kommandos ab.git
 * Created by andonie on 14.02.15.
 */
public class UIProducer
extends ProducerThread {

    public UIProducer(Queue<Dispatchable> queue) {
        super(queue, "UI");

    }

    @Override
    public Dispatchable nextEvent() {
        return null;
    }
}

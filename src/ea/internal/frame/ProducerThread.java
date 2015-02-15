package ea.internal.frame;

import java.util.Queue;

/**
 * Created by andonie on 15.02.15.
 */
public abstract class ProducerThread
extends FrameSubthread {
    /**
     * Die Queue, in der die Dispatchables eingeräumt werden sollen. Diese Queue lehrt der Dispatcher sukzessive aus.
     */
    protected final Queue<Dispatchable> dispatcherQueue;

    public ProducerThread(String threadname, Queue<Dispatchable> queue) {
        super(threadname);
        this.dispatcherQueue = queue;
    }
}

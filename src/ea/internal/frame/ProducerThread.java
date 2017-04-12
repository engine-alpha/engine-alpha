package ea.internal.frame;

import java.util.Queue;

public abstract class ProducerThread extends FrameSubthread {
    /**
     * Die Queue, in der die Dispatchables eingeräumt werden sollen. Diese Queue lehrt der Dispatcher sukzessive aus.
     */
    protected final Queue<Dispatchable> dispatcherQueue;

    public ProducerThread(FrameThread master, String threadname, Queue<Dispatchable> queue) {
        super(master, threadname);
        this.dispatcherQueue = queue;
    }
}

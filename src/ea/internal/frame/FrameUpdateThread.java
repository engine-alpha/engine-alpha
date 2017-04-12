package ea.internal.frame;

import ea.FrameUpdateReagierbar;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

public class FrameUpdateThread extends ProducerThread {
    private static int futcnt = 1;

    private class UpdateHandle implements Dispatchable {
        /**
         * Das FUR, um das sich dieser Helper kümmert.
         */
        private final FrameUpdateReagierbar reagierbar;

        private UpdateHandle(FrameUpdateReagierbar reagierbar) {
            this.reagierbar = reagierbar;
        }

        @Override
        public void dispatch() {
            reagierbar.frameUpdate(lastFrameSeconds);
        }
    }

    /**
     * Die Sekunden, die seit dem letzten Frame vergangen sind.
     */
    private float lastFrameSeconds = Float.NaN;

    /**
     * Die Liste aller FrameUpdate-Reagierbar, die innerhalb der Logik dieses
     * Frame Thread frameweise aufgerufen werden sollen.
     */
    private final Collection<UpdateHandle> toUpdate = new CopyOnWriteArrayList<>();

    public FrameUpdateThread(FrameThread master, Queue<Dispatchable> queue) {
        super(master, "Frame-Update Thread #" + futcnt++, queue);
    }

    public void addFrameUpdateReagierbar(FrameUpdateReagierbar reagierbar) {
        toUpdate.add(new UpdateHandle(reagierbar));
    }

    public void removeFrameUpdateReagierbar(FrameUpdateReagierbar reagierbar) {
        while (toUpdate.remove(reagierbar)) ;
    }

    @Override
    public void dispatchFrame() {
        lastFrameSeconds = master.getLastFrameTime() / 1000f;

        synchronized (dispatcherQueue) {
            dispatcherQueue.addAll(toUpdate);
            dispatcherQueue.notifyAll();
        }
    }
}

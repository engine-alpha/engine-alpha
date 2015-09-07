package ea.internal.frame;

import ea.FrameUpdateReagierbar;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by andonie on 06.09.15.
 */
public class FrameUpdateThread
extends ProducerThread{


    private static int futcnt = 1;

    private class UpdateHandle
    implements Dispatchable {

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
    private final CopyOnWriteArrayList<UpdateHandle> toUpdate = new CopyOnWriteArrayList<>();

    public FrameUpdateThread(FrameThread master, Queue<Dispatchable> queue) {
        super(master, "Frame-Update Thread #" + futcnt++, queue);
    }

    public void addFrameUpdateReagierbar(FrameUpdateReagierbar reagierbar) {
        toUpdate.add(new UpdateHandle(reagierbar));
    }

    public void removeFrameUpdateReagierbar(FrameUpdateReagierbar reagierbar) {
        ArrayList<UpdateHandle> toErase = new ArrayList<>();
        for(UpdateHandle uh : toUpdate) {
            if(uh.reagierbar == reagierbar) {
                toErase.add(uh);
            }
        }
        for(UpdateHandle eraseMe : toErase) {
            toUpdate.remove(toErase);
        }
    }

    @Override
    public void frameLogic() {
        lastFrameSeconds = master.getLastFrameTime()/1000f;
        for(UpdateHandle uh : toUpdate) {
            synchronized (dispatcherQueue) {
                dispatcherQueue.add(uh);
            }
        }
    }
}

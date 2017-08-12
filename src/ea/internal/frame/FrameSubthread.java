package ea.internal.frame;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public abstract class FrameSubthread extends Thread {
    private CyclicBarrier frameBarrier;

    protected FrameSubthread(String name, CyclicBarrier frameBarrier) {
        super(name);

        this.frameBarrier = frameBarrier;
        this.setDaemon(true);
    }

    /**
     * Implementiert die frameweise Synchronisation mit dem Master-Thread. Auf Signal von diesem
     * wird die {@link #dispatchFrame()}-Routine des Threads ausgeführt.
     */
    @Override
    public final void run() {
        while (!interrupted()) {
            try {
                frameBarrier.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                break;
            }

            dispatchFrame();
        }
    }

    /**
     * In dieser Methode wird die jeweilige (frameweise) Logik des Threads ausgeführt.
     */
    public abstract void dispatchFrame();
}
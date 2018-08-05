package ea.internal.frame;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public abstract class FrameSubthread extends Thread {
    private CyclicBarrier frameBarrierStart;
    private CyclicBarrier frameBarrierEnd;

    protected FrameSubthread(String name, CyclicBarrier frameBarrierStart, CyclicBarrier frameBarrierEnd) {
        super(name);

        this.frameBarrierStart = frameBarrierStart;
        this.frameBarrierEnd = frameBarrierEnd;
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
                frameBarrierStart.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                break;
            }

            dispatchFrame();

            try {
                frameBarrierEnd.await();
            } catch (BrokenBarrierException | InterruptedException e) {
                break;
            }
        }
    }

    /**
     * In dieser Methode wird die jeweilige (frameweise) Logik des Threads ausgeführt.
     */
    public abstract void dispatchFrame();
}
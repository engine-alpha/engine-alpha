package ea.internal.frame;

import java.util.concurrent.Phaser;

public abstract class FrameSubthread extends Thread {
    private Phaser frameBarrierStart;
    private Phaser frameBarrierEnd;

    protected FrameSubthread(String name, Phaser frameBarrierStart, Phaser frameBarrierEnd) {
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
            frameBarrierStart.arriveAndAwaitAdvance();

            dispatchFrame();

            frameBarrierEnd.arriveAndAwaitAdvance();
        }
    }

    /**
     * In dieser Methode wird die jeweilige (frameweise) Logik des Threads ausgeführt.
     */
    public abstract void dispatchFrame();
}
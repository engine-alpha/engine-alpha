package ea.internal;

import ea.Game;

import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;

aspect ThreadSync {
    pointcut syncThread(): execution(* *(..)) && within(ea.internal.physics.*) || call(ea.internal.physics.BodyHandler.new(..));

    Object around(): syncThread() {
        if (!Game.isGameThread()) {
            Phaser start = ThreadSyncHelper.getStart();
            Phaser end = ThreadSyncHelper.getEnd();

            AtomicBoolean enqueueState;
            synchronized (ThreadSyncHelper.BACKGROUND) {
                ThreadSyncHelper.setSynced(true);

                enqueueState = ThreadSyncHelper.getEnqueueState();
                if (enqueueState.compareAndSet(false, true)) {
                    Game.enqueue(() -> {
                        start.arriveAndAwaitAdvance();
                        end.arriveAndAwaitAdvance();
                    });

                    start.arriveAndAwaitAdvance();
                }
            }

            try {
                return proceed();
            } finally {
                synchronized (ThreadSyncHelper.BACKGROUND) {
                    ThreadSyncHelper.setSynced(false);
                    ThreadSyncHelper.delayAwait(enqueueState, end);
                }
            }
        } else {
            return proceed();
        }
    }
}
package ea.animation;

import ea.FrameUpdateListener;
import ea.event.EventListeners;
import ea.event.FrameUpdateListenerContainer;

/**
 * @author Niklas Keller
 */
public abstract class AggregateFrameUpdateListener implements FrameUpdateListener, FrameUpdateListenerContainer {

    private final EventListeners<FrameUpdateListener> listeners = new EventListeners<>();

    @Override
    public void onFrameUpdate(float deltaSeconds) {
        listeners.invoke(listener -> listener.onFrameUpdate(deltaSeconds));
    }

    @Override
    public EventListeners<FrameUpdateListener> getFrameUpdateListeners() {
        return listeners;
    }
}

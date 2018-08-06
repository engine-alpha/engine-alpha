package ea.animation;

import ea.FrameUpdateListener;
import ea.actor.Actor;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

/**
 * Eine <code>ActorAnimation</code> beschreibt eine Animation, die ein <code>Actor</code>-Objekt auf eine bestimmte
 * Art animiert.
 * @author Michael Andonie
 */
public abstract class ActorAnimation
implements FrameUpdateListener {

    /**
     * Der zu animierende Actor
     */
    protected final Actor actor;

    private final CopyOnWriteArrayList<ValueAnimator<?>> animators = new CopyOnWriteArrayList<>();

    /**
     * Erstellt eine Actor-Animation
     * @param actor
     */
    protected ActorAnimation(Actor actor) {
        this.actor = actor;
    }

    @Override
    public void onFrameUpdate(int ms) {
        for(ValueAnimator<?> animator : animators) {
            animator.onFrameUpdate(ms);
        }
        if(animators.isEmpty()) {
            //ICH BIN FERTIG --> TODO Remove implementation
        }
    }

    /**
     * Fügt einen <code>ValueAnimator</code> zu dieser Animation hinzu.
     * @param animator  Der hinzuzufügende Animator.
     * @param <E>       Ein beliebiger Typ.
     */
    protected final <E> void addAnimator(final ValueAnimator<E> animator) {
        animators.add(animator);
        animator.addCompletionListener(new Consumer<E>() {
            @Override
            public void accept(E e) {
                animators.remove(animator);
            }
        });
    }
}

package ea.actor;

import ea.internal.FixtureBuilder;
import ea.internal.annotations.API;
import ea.internal.annotations.Internal;
import ea.internal.graphics.AnimationFrame;

import java.awt.Graphics2D;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Ein animierter Actor, der mehrere Zustände haben kann (laufen (links/rechts), stehen(links/rechts), springen
 * (links/rechts), etc.).
 *
 * <h2>Anwendungsbeispiel</h2>
 * <code>
 * StatefulAnimation sf = new StatefulAnimation(); sf.addState(Animation.createFromAnimatedGif("running.gif",
 * "running"); sf.addState(Animation.createFromAnimatedGif("jumping.gif", "jumping"); scene.add(sf);
 * sf.setState("running");
 * </code>
 *
 * @param <State> Typ der Zustände, zwischen denen in der Animation gewechselt werden soll.
 * @author Michael Andonie
 * @see Animation
 */
public class StatefulAnimation<State> extends Actor {
    /**
     * Speichert die Frames (= "Animation") zu jedem State
     */
    private final Map<State, AnimationFrame[]> states = new ConcurrentHashMap<>();

    /**
     * Speichert den Übergang zum Folgestate von jedem State. Ordnet standardmäßig jedem State sich selbst als
     * Folge-State zu ("loop"). Kann jedoch über {@link #setStateTransition(State, State)} angepasst werden.
     */
    private final Map<State, State> stateTransitions = new ConcurrentHashMap<>();

    private State currentState = null;
    private AnimationFrame[] currentAnimation = null;

    private float currentTime = 0;
    private int currentIndex = 0;

    private float width;
    private float height;

    private boolean flipHorizontal = false;
    private boolean flipVertical = false;

    private boolean animationPaused = false;

    public StatefulAnimation(float width, float height) {
        super(() -> FixtureBuilder.createSimpleRectangularFixture(width, height));

        this.width = width;
        this.height = height;

        addFrameUpdateListener(this::internalOnFrameUpdate);
    }

    @API
    public float getWidth() {
        return width;
    }

    @API
    public float getHeight() {
        return height;
    }

    /**
     * Fügt dieser Animation einen neuen Zustand zu hinzu.
     *
     * @param state          Der Name für den neu hinzuzufügenden State. Unter diesem Namen wird er ab sofort in der
     *                       Figur beschrieben.
     * @param stateAnimation Die Animation für diesen Zustand. Kann normal eingeladen werden, allerdings sollte das
     *                       übergebene Objekt <b>nicht selbst in einer Scene angemeldet sein</b>.
     *
     * @see Animation
     */
    @API
    public void addState(State state, Animation stateAnimation) {
        if (states.containsKey(state)) {
            throw new RuntimeException("Zustandsname wird bereits in diesem Objekt genutzt: " + state);
        }

        AnimationFrame[] frames = stateAnimation.getFrames();

        states.put(state, frames);

        // Add default loop transition rule for state
        stateTransitions.put(state, state);

        if (currentState == null) {
            currentState = state;
            currentAnimation = frames;
        }
    }

    /**
     * Setzt den Zustand der Animation. Die Animation des neuen Zustands beginnt in jedem Fall von vorne.
     *
     * @param state Der Name des Zustands, der gesetzt werden soll.
     *
     * @see #changeState(State)
     */
    @API
    public void setState(State state) {
        if (!states.containsKey(state)) {
            throw new RuntimeException("Zustand nicht nicht vorhanden: " + state);
        }

        this.currentIndex = 0;
        this.currentState = state;
        this.currentTime = 0;
        this.currentAnimation = states.get(state);
    }

    /**
     * Ändert den Zustand der Animation. Die Animation des neuen Zustands beginnt nur von vorne, wenn der gesetzte
     * Zustand <b>nicht derselbe ist, wie der aktuelle Zustand</b>.
     *
     * @param state Der Name des Zustands, der gesetzt werden soll.
     *
     * @see #setState(State)
     */
    @API
    public void changeState(State state) {
        if (!state.equals(currentState)) {
            setState(state);
        }
    }

    /**
     * Gibt an, ob ein bestimmer Zustandsname bereits in dieser Animation genutzt wird.
     *
     * @param state Der zu testende State.
     *
     * @return <code>true</code>: Diese Animation hat einen Zustand mit dem Namen <code>stateName</code>. <br />
     * <code>false</code>: Diese Animation hat keinen Zustand mit dem Namen <code>stateName</code>.
     */
    @API
    public boolean hasState(State state) {
        return states.containsKey(state);
    }

    /**
     * Gibt den aktuellen Zustand der Animation aus.
     *
     * @return Der aktuelle Zustand der Animation. Dies ist der {@link String}, der beim Hinzufügen der aktuell aktiven
     * Animation als State-Name angegeben wurde. Ist <code>null</code>, wenn die Animation noch keine Zustände hat.
     */
    @API
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Setzt, ob alle Animationen horizontal gespiegelt dargestellt werden sollen. Hiermit lassen sich zum Beispiel
     * Bewegungsrichtungen (links/rechts) einfach umsetzen.
     *
     * @param flipHorizontal Ob die Animation horizontal geflippt dargestellt werden soll.
     *
     * @see #setFlipVertical(boolean)
     */
    @API
    public void setFlipHorizontal(boolean flipHorizontal) {
        this.flipHorizontal = flipHorizontal;
    }

    /**
     * Setzt, ob alle Animationen vertikal gespiegelt dargestellt werden sollen.
     *
     * @param flipVertical Ob die Animation horizontal geflippt dargestellt werden soll.
     *
     * @see #setFlipVertical(boolean)
     */
    @API
    public void setFlipVertical(boolean flipVertical) {
        this.flipVertical = flipVertical;
    }

    /**
     * Gibt an, ob das Objekt horizontal gespiegelt ist.
     *
     * @return <code>true</code>, wenn das Objekt gerade horizontal gespiegelt ist. Sonst <code>false</code>.
     */
    @API
    public boolean isFlipHorizontal() {
        return flipHorizontal;
    }

    /**
     * Gibt an, ob das Objekt vertikal gespiegelt ist.
     *
     * @return <code>true</code>, wenn das Objekt gerade vertikal gespiegelt ist. Sonst <code>false</code>.
     */
    @API
    public boolean isFlipVertical() {
        return flipVertical;
    }

    /**
     * Setzt, ob diese <code>StatefulAnimation</code> animiert werden soll.
     *
     * @param animationPaused Ist dieser Wert <code>true</code>, so läuft die Animation normal weiter.
     *                        Ist dieser Wert <code>false</code>, so läuft die Animation nicht und keine automatischen
     *                        Zustandsübergänge passieren.
     *
     * @see #isAnimationPaused()
     */
    @API
    public void setAnimationPaused(boolean animationPaused) {
        this.animationPaused = animationPaused;
    }

    /**
     * Gibt an, ob die Animation derzeit pausiert ist.
     *
     * @return <code>true</code>, wenn die Animation gerade pausiert ist. Sonst <code>false</code>.
     *
     * @see #setAnimationPaused(boolean)
     */
    @API
    public boolean isAnimationPaused() {
        return this.animationPaused;
    }

    /**
     * Setzt eine neue Übergangsregel für die Zustände der Animation.<br> Jedes Mal, wenn die Animation vom
     * <b>Von-Zustand</b> einmal durchlaufen wurde, geht die Animation automatisch in den <b>Ziel-Zustand</b> über. <br>
     * Per Default gilt: Ein zugefügter Zustand geht nach Abschluss seiner Animation "in sich selbst" über. Also
     * <code>Von-Zustand = Ziel-Zustand</code>. Effektiv ist das ein Loop.<br>
     * Diese Methode überschreibt die bisherige Übergangsregel für den entsprechenden Von-Zustand.
     *
     * @param stateFrom Der Von-Zustand.
     * @param stateTo   Der Ziel-Zustand.
     */
    @API
    public void setStateTransition(State stateFrom, State stateTo) {
        if (!states.containsKey(stateFrom)) {
            throw new RuntimeException("Der Von-Zustand ist nicht in dieser Animation eingepflegt: " + stateFrom);
        }

        if (!states.containsKey(stateTo)) {
            throw new RuntimeException("Der To-Zustand ist nicht in dieser Animation eingepflegt: " + stateTo);
        }

        // Remove old transition rule
        stateTransitions.remove(stateFrom);
        // Add new transition rule
        stateTransitions.put(stateFrom, stateTo);
    }

    /**
     * Setzt die Dauer, die ein Frame einer bestimmten Animation verweilt.
     *
     * @param state         Der State, für den die Frame-Dauer neu gesetzt werden soll.
     * @param frameDuration Die Zeit (in Sekunden), die jeder einzelne Frame der Animation des entsprechenden
     *                      States verweilen soll, bis der Frame gewechselt wird.
     */
    @API
    public void setFrameDuration(State state, float frameDuration) {
        if (!states.containsKey(state)) {
            throw new RuntimeException("Der Zustand ist nicht bekannt: " + state);
        }

        for (AnimationFrame frame : states.get(state)) {
            frame.setDuration(frameDuration);
        }
    }

    /**
     * Methode wird frameweise über einen anonymen Listener aufgerufen.
     */
    @Internal
    private void internalOnFrameUpdate(float frameDuration) {
        if (currentAnimation == null || currentAnimation.length == 0 || animationPaused) {
            return; // we don't have a state yet - or the animation is paused
        }

        currentTime += frameDuration;

        AnimationFrame currentFrame = currentAnimation[currentIndex];

        while (this.currentTime > currentFrame.getDuration()) {
            this.currentTime -= currentFrame.getDuration();

            if (this.currentIndex + 1 == this.currentAnimation.length) {
                // Animation cycle has ended. -> Transition to next state
                currentIndex = 0;

                State nextState = stateTransitions.get(currentState);
                AnimationFrame[] nextAnimation = states.get(nextState);

                currentState = nextState;
                currentAnimation = nextAnimation;
            } else {
                // Animation cycle has not ended -> simply move on to next frame
                this.currentIndex++;
            }
        }
    }

    /**
     * Setzt die Höhe und Breite der Animation neu. Ändert die physikalischen Eigenschaften (Masse etc.).
     *
     * @param width  Neue Breite für das Rechteck.
     * @param height Neue Höhe für das Rechteck.
     */
    @API
    public void setSize(float width, float height) {
        assertPositiveWidthAndHeight(width, height);

        this.width = width;
        this.height = height;

        this.setFixture(() -> FixtureBuilder.createSimpleRectangularFixture(width, height));
    }

    @Internal
    @Override
    public void render(Graphics2D g, float pixelPerMeter) {
        if (currentAnimation == null || currentAnimation.length == 0) {
            return; // we don't have a state yet
        }

        currentAnimation[currentIndex].render(g, width * pixelPerMeter, height * pixelPerMeter, flipHorizontal, flipVertical);
    }
}

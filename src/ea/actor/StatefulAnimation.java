package ea.actor;

import ea.FrameUpdateListener;
import ea.internal.ano.NoExternalUse;
import ea.internal.gra.Frame;
import ea.internal.ano.API;
import org.jbox2d.collision.shapes.Shape;

import java.awt.*;
import java.util.HashMap;

/**
 * Ein animierter Actor, der mehrere Zustände haben kann (laufen (links/rechts), stehen(links/rechts), springen
 * (links/rechts), etc.).
 *
 * <h2>Anwendungsbeispiel</h2>
 * <code>
 *     StatefulAnimation sf = new StatefulAnimation();
 *     sf.addState(Animation.createFromAnimatedGif("running.gif", "running");
 *     sf.addState(Animation.createFromAnimatedGif("jumping.gif", "jumping");
 *     scene.add(sf);
 *     sf.setState("running");
 * </code>
 * @author Michael Andonie
 * @see Animation
 */
public class StatefulAnimation
extends Actor
implements FrameUpdateListener {

    /**
     * Speichert die Frames (= "Animation") zu jedem State
     */
    private final HashMap<String, Frame[]> states = new HashMap<>();

    /**
     * Speichert den Übergang zum Folgestate von jedem State.
     * Ordnet standardmäßig jedem State sich selbst als Folge-State zu ("loop"). Kann jedoch über
     * {@link #setStateTransition(String, String)} angepasst werden.
     */
    private final HashMap<String, String> stateTransitions = new HashMap<>();

    private String currentState=null;
    private Frame[] currentAnimation=null;

    private int currentTime=0;
    private int currentIndex=0;

    private int width=-1, height=-1;

    /**
     * Fügt dieser Animation einen neuen Zustand zu hinzu.
     *
     * @param stateAnimation    Die Animation für diesen Zustand. Kann normal eingeladen werden, allerdings sollte das
     *                          übergebene Objekt <b>nicht selbst in einer Scene angemeldet sein</b>.
     * @param stateName         Der Name für den neu hinzuzufügenden State. Unter diesem Namen wird er ab sofort
     *                          in der Figur beschrieben.
     * @see Animation
     */
    @API
    public void addState(Animation stateAnimation, String stateName) {
        //if() TODO Check if stateAnimation is already attached to a scene -> if so, throw exception / error log
        if(states.containsKey(stateName)) {
            throw new RuntimeException("Zustandsname wird bereits in diesem Objekt genutzt: " + stateName);
        }
        //Add Name->Animation pairing
        states.put(stateName, stateAnimation.getFrames());
        //Add loop transition rule for state (default)
        stateTransitions.put(stateName, stateName);
        if(currentState==null) {
            //No state set yet --> Make first state default state
            currentState=stateName;
            currentAnimation=stateAnimation.getFrames();

            //While we're at it, take over width&height
            this.width = stateAnimation.getFrames()[0].getImage().getWidth();
            this.height = stateAnimation.getFrames()[0].getImage().getHeight();
        }
    }

    /**
     * Setzt den Zustand der Animation. Die Animation des neuen Zustands beginnt in jedem Fall von vorne.
     * @param stateName Der Name des Zustands, der gesetzt
     */
    @API
    public void setState(String stateName) {
        if(!states.containsKey(stateName)) {
            throw new RuntimeException("Zustandsname nicht nicht vorhanden: " + stateName);
        }
        this.currentIndex=0;
        this.currentState=stateName;
        this.currentTime=0;
        this.currentAnimation=states.get(stateName);
    }

    /**
     * Setzt eine neue Übergangsregel für die Zustände der Animation.<br />
     * Jedes Mal, wenn die Animation vom <b>Von-Zustand</b> einmal durchlaufen wurde, geht die Animation automatisch
     * in den <b>Ziel-Zustand</b> über. <br />
     * Per Default gilt: Ein zugefügter Zustand geht nach Abschluss seiner Animation "in sich selbst" über. Also
     * <code>Von-Zustand = Ziel-Zustand</code>. Effektiv ist das ein Loop.<br />
     * Diese Methode überschreibt die bisherige Übergangsregel für den entsprechenden Von-Zustand.
     * @param stateFrom Der Von-Zustand.
     * @param stateTo   Der Ziel-Zustand.
     */
    @API
    public void setStateTransition(String stateFrom, String stateTo) {
        if(!states.containsKey(stateFrom)) {
            throw new RuntimeException("Der Von-Zustand ist nicht in dieser Animation eingepflegt: " + stateFrom);
        }
        if(!states.containsKey(stateTo)) {
            throw new RuntimeException("Der To-Zustand ist nicht in dieser Animation eingepflegt: " + stateTo);
        }
        //Remove old transition rule
        stateTransitions.remove(stateFrom);
        //Add new transition rule
        stateTransitions.put(stateFrom, stateTo);
    }

    @NoExternalUse
    @Override
    public final void onFrameUpdate(int frameDuration) {
        currentTime += frameDuration;

        Frame currentFrame = currentAnimation[currentIndex];

        while (this.currentTime > currentFrame.getDuration()) {
            this.currentTime -= currentFrame.getDuration();

            if(this.currentIndex+1 == this.currentAnimation.length) {
                //Animation cycle has ended. -> Transition to next state
                currentIndex=0;

                String nextState = stateTransitions.get(currentState);
                Frame[] nextAnimation = states.get(nextState);

                currentState = nextState;
                currentAnimation = nextAnimation;
            } else {
                //Animation cycle has not ended -> simply move on to next frame
                this.currentIndex++;
            }
        }
    }

    /* ~~ ACTOR FUNCTIONALITY ~~ */

    @NoExternalUse
    @Override
    public void render(Graphics2D g) {
        currentAnimation[currentIndex].render(g);
    }

    @NoExternalUse
    @Override
    public Shape createShape(float pixelProMeter) {
        if(width==1 || height == -1) {
            throw new RuntimeException("Animation wurde in die Scene eingebracht, bevor ein Animation-Zustand vorlag.");
        }
        return this.berechneBoxShape(pixelProMeter, width, height);
    }
}

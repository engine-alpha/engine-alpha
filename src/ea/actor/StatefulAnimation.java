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

    private boolean flipHorizontal=false;
    private boolean flipVertical=false;

    /**
     * Fügt dieser Animation einen neuen Zustand zu hinzu.
     *
     * @param stateName         Der Name für den neu hinzuzufügenden State. Unter diesem Namen wird er ab sofort
     *                          in der Figur beschrieben.
     * @param stateAnimation    Die Animation für diesen Zustand. Kann normal eingeladen werden, allerdings sollte das
     *                          übergebene Objekt <b>nicht selbst in einer Scene angemeldet sein</b>.
     * @see Animation
     */
    @API
    public void addState(String stateName, Animation stateAnimation) {
        //if() TODO Check if stateAnimation is already attached to a scene -> if so, throw exception / error log
        if(states.containsKey(stateName)) {
            throw new RuntimeException("Zustandsname wird bereits in diesem Objekt genutzt: " + stateName);
        }
        addStateRaw(stateName, stateAnimation.getFrames());
    }

    @NoExternalUse
    protected void addStateRaw(String stateName, Frame[] frames) {
        //Add Name->Animation pairing
        states.put(stateName, frames);
        //Add loop transition rule for state (default)
        stateTransitions.put(stateName, stateName);
        if(currentState==null) {
            //No state set yet --> Make first state default state
            currentState=stateName;
            currentAnimation=frames;

            //While we're at it, take over width&height
            this.width = frames[0].getImage().getWidth();
            this.height = frames[0].getImage().getHeight();
        }
    }

    /**
     * Setzt den Zustand der Animation. Die Animation des neuen Zustands beginnt in jedem Fall von vorne.
     * @param stateName Der Name des Zustands, der gesetzt werden soll.
     * @see #changeState(String)
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
     * Ändert den Zustand der Animation. Die Animation des neuen Zustands beginnt nur von vorne, wenn der gesetzte
     * Zustand <b>nicht derselbe ist, wie der aktuelle Zustand</b>.
     * @param stateName Der Name des Zustands, der gesetzt werden soll.
     * @see #setState(String)
     */
    @API
    public void changeState(String stateName) {
        if(!stateName.equals(currentState)) setState(stateName);
    }

    /**
     * Gibt den aktuellen Zustand der Animation aus.
     * @return  Der aktuelle Zustand der Animation. Dies ist der {@link String}, der beim Hinzufügen der aktuell
     *          aktiven Animation als State-Name angegeben wurde. Ist <code>null</code>, wenn die Animation noch
     *          keine Zustände hat.
     */
    public String getCurrentState() {
        return currentState;
    }

    /**
     * Setzt, ob alle Animationen horizontal gespiegelt dargestellt werden sollen.
     * Hiermit lassen sich zum Beispiel Bewegungsrichtungen (links/rechts) einfach umsetzen.
     * @param flipHorizontal    Ob die Animation horizontal geflippt dargestellt werden soll.
     * @see #setFlipVertical(boolean)
     */
    @API
    public void setFlipHorizontal(boolean flipHorizontal) {
        this.flipHorizontal = flipHorizontal;
    }

    /**
     * Setzt, ob alle Animationen vertikal gespiegelt dargestellt werden sollen.
     * @param flipVertical    Ob die Animation horizontal geflippt dargestellt werden soll.
     * @see #setFlipVertical(boolean)
     */
    @API
    public void setFlipVertical(boolean flipVertical) {
        this.flipVertical = flipVertical;
    }

    /**
     * Gibt an, ob das Objekt horizontal gespiegelt ist.
     * @return  <code>true</code>, wenn das Objekt gerade horizontal gespiegelt ist. Sonst <code>false</code>.
     */
    @API
    public boolean isFlipHorizontal() {
        return flipHorizontal;
    }

    /**
     * Gibt an, ob das Objekt vertikal gespiegelt ist.
     * @return  <code>true</code>, wenn das Objekt gerade vertikal gespiegelt ist. Sonst <code>false</code>.
     */
    @API
    public boolean isFlipVertical() {
        return flipVertical;
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

    /**
     * Setzt die Dauer, die ein Frame einer bestimmten Animation verweilt.
     * @param stateName     Der State, für den die Frame-Dauer neu gesetzt werden soll.
     * @param frameDuration Die Zeit (in Millisekunden), die jeder einzelne Frame der Animation des entsprechenden
     *                      States verweilen soll, bis der Frame gewechselt wird.
     */
    @API
    public void setFrameDurationsOf(String stateName, int frameDuration) {
        if(!states.containsKey(stateName)) {
            throw new RuntimeException("Der Zustand ist nicht bekannt: " + stateName);
        }
        Frame[] frames = states.get(stateName);
        for(Frame frame : frames) {
            frame.setDuration(frameDuration);
        }
    }

    /* ~~ Internal Functions ~~ */

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
        currentAnimation[currentIndex].render(g, flipHorizontal, flipVertical);
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

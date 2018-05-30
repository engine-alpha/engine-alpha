package ea.example.basics;

import ea.*;
import ea.actor.Animation;
import ea.actor.Rectangle;
import ea.actor.StatefulAnimation;
import ea.actor.Text;
import ea.keyboard.Key;
import ea.keyboard.KeyListener;
import ea.mouse.MouseWheelAction;
import ea.mouse.MouseWheelListener;

import java.awt.*;

public class MinimalDemo
extends Scene
implements KeyListener, MouseWheelListener, FrameUpdateListener {

    /**
     * Main-Methode startet die Demo.
     * @param args  command-line params (irrelevant)
     */
    public static void main(String[] args) {
        MinimalDemo demo = new MinimalDemo();
        Game.start(1020, 520, demo);
        Game.setTitle("Minimale Demo");
        EngineAlpha.setDebug(true);
    }

    /**
     * Ein einfacher Demo
     */
    private Rectangle rectangle;

    private StatefulAnimation character;

    /**
     * Die Geschwindigkeit, in der sich die Kamera bewegt (pro Millisekunde)
     */
    private final float CAM_SPEED = 0.6f;

    public MinimalDemo() {
        rectangle = new Rectangle(50, 100);
        rectangle.setColor(Color.BLUE);
        add(rectangle);
        addKeyListener(this);
        addMouseWheelListener(this);
        addFrameUpdateListener(this);

        Animation animation = Animation.createFromAnimatedGif("game-assets\\jump\\fx_explosion_b_anim.gif");
        animation.position.set(200, 200);
        //animation.setOneTimeOnly();
        add(animation);

        Text text =  new Text("Hallo!");
        text.position.set(-100, -100);
        text.setColor(Color.MAGENTA);
        add(text);

        //Stateful Animation

        //"leerer Automat" -> Erstellt StatefulAnimation ohne Zustände
        character = new StatefulAnimation();

        final String pathbase = "game-assets\\jump\\spr_m_traveler_";


        Animation idle = Animation.createFromAnimatedGif(pathbase+"idle_anim.gif");
        character.addState("idle", idle);

        character.addState("walking", Animation.createFromAnimatedGif(pathbase+"walk_anim.gif"));
        character.addState("running", Animation.createFromAnimatedGif(pathbase+"run_anim.gif"));
        character.addState("jumpingUp", Animation.createFromAnimatedGif(pathbase+"jump_1up_anim.gif"));
        character.addState("midair", Animation.createFromAnimatedGif(pathbase+"jump_2midair_anim.gif"));
        character.addState("falling", Animation.createFromAnimatedGif(pathbase+"jump_3down_anim.gif"));
        character.addState("landing", Animation.createFromAnimatedGif(pathbase+"jump_4land_anim.gif"));

        character.setStateTransition("landing", "idle");

        add(character);
    }


    @Override
    public void onKeyDown(int key) {
        switch (key) {
            case Key.RECHTS:
                rectangle.position.move(50, 0);
                break;
            case Key.LINKS:
                rectangle.position.move(-50, 0);
                break;
            case Key.C:
                character.setState("midair");
                break;
        }
    }

    @Override
    public void onKeyUp(int code) {

    }

    @Override
    public void onMouseWheelMove(MouseWheelAction mouseWheelAction) {
        float newZoom = getCamera().getZoom() + (mouseWheelAction.getPreciseWheelRotation()*-0.2f);
        if(newZoom > 0) getCamera().setZoom(newZoom);
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        if(Game.isKeyPressed(Key.W)) {
            //W ist gedrückt -> Kamera nach oben bewegen.
            getCamera().move(0, -CAM_SPEED*frameDuration);
        }
        if(Game.isKeyPressed(Key.S)) {
            getCamera().move(0, CAM_SPEED*frameDuration);
        }
        if(Game.isKeyPressed(Key.A)) {
            getCamera().move(-CAM_SPEED*frameDuration, 0);
        }
        if(Game.isKeyPressed(Key.D)) {
            getCamera().move(CAM_SPEED*frameDuration, 0);
        }
    }
}

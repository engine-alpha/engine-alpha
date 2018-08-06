package ea.example.basics;

import ea.FrameUpdateListener;
import ea.Game;
import ea.Scene;
import ea.actor.Animation;
import ea.actor.Rectangle;
import ea.actor.StatefulAnimation;
import ea.actor.Text;
import ea.input.KeyListener;
import ea.input.MouseWheelAction;
import ea.input.MouseWheelListener;

import java.awt.*;
import java.awt.event.KeyEvent;

public class MinimalDemo extends Scene implements KeyListener, MouseWheelListener, FrameUpdateListener {
    private static final int WIDTH = 1020;
    private static final int HEIGHT = 520;

    /**
     * Main-Methode startet die Demo.
     *
     * @param args command-line params (irrelevant)
     */
    public static void main(String[] args) {
        Game.setDebug(true);
        Game.start(WIDTH, HEIGHT, new MinimalDemo());
        Game.setTitle("Minimale Demo");
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
        rectangle = new Rectangle(this, 50, 100);
        rectangle.setColor(Color.BLUE);
        add(rectangle);
        addKeyListener(this);
        addMouseWheelListener(this);
        addFrameUpdateListener(this);

        Animation animation = Animation.createFromAnimatedGif(this, "game-assets/jump/fx_explosion_b_anim.gif");
        animation.position.set(200, 200);
        //animation.setOneTimeOnly();
        add(animation);

        Text text = new Text(this, "Hallo!");
        text.position.set(-100, -100);
        text.setColor(Color.MAGENTA);
        add(text);

        //Stateful Animation

        //"leerer Automat" -> Erstellt StatefulAnimation ohne Zustände
        character = new StatefulAnimation(this, 64, 64);

        final String pathbase = "game-assets/jump/spr_m_traveler_";

        Animation idle = Animation.createFromAnimatedGif(this, pathbase + "idle_anim.gif");
        character.addState("idle", idle);

        character.addState("walking", Animation.createFromAnimatedGif(this, pathbase + "walk_anim.gif"));
        character.addState("running", Animation.createFromAnimatedGif(this, pathbase + "run_anim.gif"));
        character.addState("jumpingUp", Animation.createFromAnimatedGif(this, pathbase + "jump_1up_anim.gif"));
        character.addState("midair", Animation.createFromAnimatedGif(this, pathbase + "jump_2midair_anim.gif"));
        character.addState("falling", Animation.createFromAnimatedGif(this, pathbase + "jump_3down_anim.gif"));
        character.addState("landing", Animation.createFromAnimatedGif(this, pathbase + "jump_4land_anim.gif"));

        character.setStateTransition("landing", "idle");

        add(character);
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                rectangle.position.move(50, 0);
                break;
            case KeyEvent.VK_LEFT:
                rectangle.position.move(-50, 0);
                break;
            case KeyEvent.VK_C:
                character.setState("midair");
                break;
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {

    }

    @Override
    public void onMouseWheelMove(MouseWheelAction mouseWheelAction) {
        float newZoom = getCamera().getZoom() + (mouseWheelAction.getPreciseWheelRotation() * -0.2f);
        if (newZoom > 0) getCamera().setZoom(newZoom);
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        if (Game.isKeyPressed(KeyEvent.VK_W)) {
            //W ist gedrückt -> Kamera nach oben bewegen.
            getCamera().move(0, -CAM_SPEED * frameDuration);
        }
        if (Game.isKeyPressed(KeyEvent.VK_S)) {
            getCamera().move(0, CAM_SPEED * frameDuration);
        }
        if (Game.isKeyPressed(KeyEvent.VK_A)) {
            getCamera().move(-CAM_SPEED * frameDuration, 0);
        }
        if (Game.isKeyPressed(KeyEvent.VK_D)) {
            getCamera().move(CAM_SPEED * frameDuration, 0);
        }
    }
}

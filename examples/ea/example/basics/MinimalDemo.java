package ea.example.basics;

import ea.FrameUpdateListener;
import ea.Game;
import ea.Scene;
import ea.actor.Animation;
import ea.actor.Rectangle;
import ea.actor.StatefulAnimation;
import ea.actor.Text;
import ea.event.KeyListener;
import ea.event.MouseWheelEvent;
import ea.event.MouseWheelListener;

import java.awt.Color;
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
     * Die Geschwindigkeit, in der sich die Kamera bewegt (pro Sekunde)
     */
    private final float CAM_SPEED = 600;

    public MinimalDemo() {
        rectangle = new Rectangle(50, 100);
        rectangle.setColor(Color.BLUE);
        add(rectangle);

        Animation animation = Animation.createFromAnimatedGif("game-assets/jump/fx_explosion_b_anim.gif", 1, 1);
        animation.setPosition(200, 200);
        //animation.setOneTimeOnly();
        add(animation);

        Text text = new Text("Hallo!", 2);
        text.setPosition(-100, -100);
        text.setColor(Color.MAGENTA);
        add(text);

        //Stateful Animation

        //"leerer Automat" -> Erstellt StatefulAnimation ohne Zustände
        character = new StatefulAnimation(64, 64);

        final String pathbase = "game-assets/dude/char/spr_m_traveler_";

        Animation idle = Animation.createFromAnimatedGif(pathbase + "idle_anim.gif", 1, 1);
        character.addState("idle", idle);

        character.addState("walking", Animation.createFromAnimatedGif(pathbase + "walk_anim.gif", 1, 1));
        character.addState("running", Animation.createFromAnimatedGif(pathbase + "run_anim.gif", 1, 1));
        character.addState("jumpingUp", Animation.createFromAnimatedGif(pathbase + "jump_1up_anim.gif", 1, 1));
        character.addState("midair", Animation.createFromAnimatedGif(pathbase + "jump_2midair_anim.gif", 1, 1));
        character.addState("falling", Animation.createFromAnimatedGif(pathbase + "jump_3down_anim.gif", 1, 1));
        character.addState("landing", Animation.createFromAnimatedGif(pathbase + "jump_4land_anim.gif", 1, 1));

        character.setStateTransition("landing", "idle");

        add(character);
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_RIGHT:
                rectangle.moveBy(50, 0);
                break;
            case KeyEvent.VK_LEFT:
                rectangle.moveBy(-50, 0);
                break;
            case KeyEvent.VK_C:
                character.setState("midair");
                break;
        }
    }

    @Override
    public void onMouseWheelMove(MouseWheelEvent mouseWheelEvent) {
        float newZoom = getCamera().getZoom() + (mouseWheelEvent.getPreciseWheelRotation() * -0.2f);
        if (newZoom > 0) {
            getCamera().setZoom(newZoom);
        }
    }

    @Override
    public void onFrameUpdate(float deltaSeconds) {
        if (Game.isKeyPressed(KeyEvent.VK_W)) {
            //W ist gedrückt -> Kamera nach oben bewegen.
            getCamera().moveBy(0, CAM_SPEED * deltaSeconds);
        }
        if (Game.isKeyPressed(KeyEvent.VK_S)) {
            getCamera().moveBy(0, -CAM_SPEED * deltaSeconds);
        }
        if (Game.isKeyPressed(KeyEvent.VK_A)) {
            getCamera().moveBy(-CAM_SPEED * deltaSeconds, 0);
        }
        if (Game.isKeyPressed(KeyEvent.VK_D)) {
            getCamera().moveBy(CAM_SPEED * deltaSeconds, 0);
        }
    }
}

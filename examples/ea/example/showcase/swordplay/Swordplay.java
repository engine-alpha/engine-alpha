package ea.example.showcase.swordplay;

import ea.Scene;
import ea.example.showcase.ShowcaseDemo;
import ea.keyboard.Key;
import ea.keyboard.KeyListener;

/**
 * Eine kleine Spieldemo.
 *
 * Vielen Dank an <a href="https://rvros.itch.io/animated-pixel-hero">rvros</a>
 */
public class Swordplay
extends ShowcaseDemo
implements KeyListener {

    private SwordFighter player;

    public Swordplay(Scene parent, int widht, int height) {
        super(parent);

        player = new SwordFighter(this);
        addKeyListener(this);
    }

    @Override
    public void onKeyDown(int key) {
        switch (key) {
            case Key.F:
                player.setState("attack2");
                break;
            case Key.I:
                player.setState("idle");
                break;
        }
    }

    @Override
    public void onKeyUp(int code) {

    }
}

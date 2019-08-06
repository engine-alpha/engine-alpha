package ea.example.showcase.dude;

import ea.Layer;
import ea.actor.Rectangle;
import ea.actor.Text;
import ea.input.KeyListener;

import java.awt.Color;
import java.awt.event.KeyEvent;

public class PauseLayer extends Layer implements KeyListener {

    public PauseLayer() {
        setLayerPosition(1000);

        //Setup:
        Rectangle back = new Rectangle(DudeDemo.GAME_WIDTH_PX, DudeDemo.GAME_HEIGHT_PX);
        back.setColor(new Color(100, 200, 255, 120));
        back.position.setCenter(0, 0);
        add(back);

        Text announce = new Text("Pause.", "Monospaced", 120, 0);
        announce.position.setCenter(0, 0);
        add(announce);

        setParallaxPosition(0, 0);
        setParallaxZoom(0);
        setParallaxRotation(0);
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        if (!isVisible()) {
            //Pause-Menü ist nicht aktiv
            return;
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        //Ignore.
    }
}

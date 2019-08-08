package ea.example.showcase.dude;

import ea.Layer;
import ea.actor.Rectangle;
import ea.actor.Text;

import java.awt.Color;

public class PauseLayer extends Layer {
    private static final Color BACKGROUND_COLOR = new Color(100, 200, 255, 120);

    public PauseLayer() {
        setLayerPosition(1000);

        Rectangle back = new Rectangle(DudeDemo.GAME_WIDTH_PX, DudeDemo.GAME_HEIGHT_PX);
        back.setColor(BACKGROUND_COLOR);
        back.setCenter(0, 0);
        add(back);

        Text announce = new Text("Pause.", 10, "Monospaced");
        announce.setCenter(0, 0);
        add(announce);

        setParallaxPosition(0, 0);
        setParallaxZoom(0);
        setParallaxRotation(0);
    }
}

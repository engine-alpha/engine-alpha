package ea.example.showcase.dude;

import ea.FrameUpdateListener;
import ea.Layer;
import ea.Scene;

/**
 * Das HUD gibt einige Spieldaten über dem Rest der Szenen-Objekte wieder
 */
public class HUD extends Layer implements FrameUpdateListener {

    /**
     * Referenz zur Darstellung relevanter Infos zum PC.
     */
    private final PlayerCharacter playerCharacter;

    private final HUDDisplay display;

    public HUD(Scene scene, PlayerCharacter playerCharacter) {
        super(scene);
        this.playerCharacter = playerCharacter;
        this.setParallaxZoom(0);
        this.setParallaxPosition(0, 0);
        this.setParallaxRotation(0);

        display = new HUDDisplay(scene);
        add(display);
        display.position.set(-DudeDemo.GAME_WIDTH / 2 + 20, DudeDemo.GAME_HEIGHT / 2 - 80);

        scene.addFrameUpdateListener(this);
    }

    public void setMoneyValue(int val) {

    }

    /**
     * Das HUD wird jeden Frame upgedated.
     *
     * @param frameDuration Die Zeit <b>in Millisekunden</b>, die seit dem letzten Update vergangen
     */
    @Override
    public void onFrameUpdate(int frameDuration) {

    }
}

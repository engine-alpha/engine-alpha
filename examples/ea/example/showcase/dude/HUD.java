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
    private PlayerCharacter playerCharacter;

    private final HUDDisplay display;

    public HUD(Scene scene) {
        super(scene);
        this.playerCharacter = playerCharacter;
        this.setParallaxZoom(0);
        this.setParallaxPosition(0, 0);
        this.setParallaxRotation(0);

        display = new HUDDisplay(scene);
        add(display);
        display.position.set(-DudeDemo.GAME_WIDTH / 2 + 20, DudeDemo.GAME_HEIGHT / 2 - 130);

        scene.addFrameUpdateListener(this);
    }

    public void setPlayerCharacter(PlayerCharacter playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    public void setMoneyValue(int val) {
        display.setDisplayNumber(val);
    }

    public void setManaValue(float rel) {
        display.setLineDisplay(1, rel);
    }

    /**
     * Das HUD wird jeden Frame upgedated.
     *
     * @param frameDuration Die Zeit <b>in Millisekunden</b>, die seit dem letzten Update vergangen
     */
    @Override
    public void onFrameUpdate(int frameDuration) {
        if (playerCharacter == null) {
            return;
        }
        float vel = playerCharacter.physics.getVelocity().getLength();

        display.setLineDisplay(2, Math.min(1, vel / 80));
    }
}

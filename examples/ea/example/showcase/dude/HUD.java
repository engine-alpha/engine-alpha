package ea.example.showcase.dude;

import ea.FrameUpdateListener;
import ea.Layer;
import ea.actor.Actor;

/**
 * Das HUD gibt einige Spieldaten über dem Rest der Szenen-Objekte wieder
 */
public class HUD extends Layer implements FrameUpdateListener {
    private final HUDDisplay display;
    private final GameData gameData;

    public HUD(GameData gameData) {
        this.setParallaxZoom(0);
        this.setParallaxPosition(0, 0);
        this.setParallaxRotation(0);

        this.gameData = gameData;
        this.display = new HUDDisplay(-DudeDemo.GAME_WIDTH_PX / 2 + 20, DudeDemo.GAME_HEIGHT_PX / 2 - 130);

        add(display.getActors().toArray(new Actor[0]));
    }

    @Override
    public void onFrameUpdate(float deltaSeconds) {
        display.setLineDisplay(0, 1);
        display.setLineDisplay(1, (float) gameData.getMana() / GameData.MAX_MANA);
        display.setLineDisplay(2, Math.min(1, gameData.getPlayerVelocity() / 80));
        display.setDisplayNumber(gameData.getMoney());
    }
}

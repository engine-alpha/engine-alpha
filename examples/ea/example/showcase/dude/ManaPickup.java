package ea.example.showcase.dude;

import ea.actor.Animation;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;

/**
 * Danke an <a href="https://sorceressgamelab.itch.io/">SorceressGameLab</a> für die Assets!
 */
public class ManaPickup extends Animation implements CollisionListener<PlayerCharacter> {
    private static final float SIZE = .4f;
    private static final float PICKUP_DELAY = 5;

    private boolean active = true;

    public ManaPickup() {
        super(Animation.createFromSpritesheet(.1f, "game-assets/dude/gem_blue.png", 6, 1, SIZE, SIZE));
    }

    @Override
    public void onCollision(CollisionEvent<PlayerCharacter> collisionEvent) {
        if (!active) {
            return;
        }

        // Ich wurde aufgesammelt!
        collisionEvent.getColliding().gotItem(Item.ManaPickup);
        this.setActive(false);
        active = false;

        delay(3, () -> setActive(true));
        animateOpacity(2, 1f);
    }

    private void setActive(boolean b) {
        active = b;
        setOpacity(b ? 1 : 0);
    }
}

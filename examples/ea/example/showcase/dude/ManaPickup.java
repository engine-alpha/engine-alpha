package ea.example.showcase.dude;

import ea.Scene;
import ea.actor.Animation;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;

/**
 * Danke an <a href="https://sorceressgamelab.itch.io/">SorceressGameLab</a> für die Assets!
 */
public class ManaPickup extends Animation implements CollisionListener<PlayerCharacter> {

    public ManaPickup() {
        super(Animation.createFromSpritesheet(100, "game-assets/dude/gem_blue.png", 6, 1, .2f, .2f));
    }

    @Override
    public void onCollision(CollisionEvent<PlayerCharacter> collisionEvent) {
        // Ich wurde aufgesammelt!
        collisionEvent.getColliding().gotItem(Item.ManaPickup);
        getScene().remove(this);
    }

    @Override
    public void onCollisionEnd(CollisionEvent<PlayerCharacter> collisionEvent) {

    }
}

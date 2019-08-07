package ea.example.showcase.dude;

import ea.actor.Animation;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;

/**
 * Danke an <a href="https://sorceressgamelab.itch.io/">SorceressGameLab</a> für die Assets!
 */
public class ManaPickup extends Animation implements CollisionListener<PlayerCharacter> {

    private boolean active = true;

    public ManaPickup() {
        super(Animation.createFromSpritesheet(100, "game-assets/dude/gem_blue.png", 6, 1, .4f, .4f));
    }

    @Override
    public void onCollision(CollisionEvent<PlayerCharacter> collisionEvent) {
        //System.out.println("Collsiion");
        if (!active) {
            //Pickup ist gerade nicht aktiv
            return;
        }
        // Ich wurde aufgesammelt!
        collisionEvent.getColliding().gotItem(Item.ManaPickup);
        this.setActive(false);
        active = false;
        getLayer().getParent().addOneTimeCallback(5000, () -> setActive(true));
    }

    private void setActive(boolean b) {
        active = b;
        setVisible(b);
    }

    @Override
    public void onCollisionEnd(CollisionEvent<PlayerCharacter> collisionEvent) {

    }
}

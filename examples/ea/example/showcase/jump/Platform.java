package ea.example.showcase.jump;

import ea.actor.Rectangle;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;
import ea.handle.BodyType;

import java.awt.*;

public class Platform extends Rectangle implements CollisionListener<PlayerCharacter> {
    /**
     * Konstruktor.
     *
     * @param width  Die Breite des Rechtecks
     * @param height Die Höhe des Rechtecks
     */
    public Platform(PlayerCharacter character, float width, float height) {
        super(width, height);

        setColor(new Color(130, 140, 255, 200));
        setBodyType(BodyType.STATIC);
        physics.setRestitution(0);

        addCollisionListener(this, character);
    }

    @Override
    public void onCollision(CollisionEvent<PlayerCharacter> collisionEvent) {
        PlayerCharacter playerCharacter = collisionEvent.getColliding();
        if (playerCharacter.physics.getVelocity().y > 0) {
            collisionEvent.ignoreCollision();
        }
    }
}

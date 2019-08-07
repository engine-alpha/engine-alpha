package ea.example.showcase.jump;

import ea.FrameUpdateListener;
import ea.Scene;
import ea.Vector;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;

public class Enemy extends StatefulAnimation implements CollisionListener<PlayerCharacter> {

    private final Vector velocity;
    FrameUpdateListener enemyMover = new FrameUpdateListener() {
        @Override
        public void onFrameUpdate(float frameDuration) {
            Enemy.this.physics.setVelocity(velocity);
        }
    };

    public Enemy(Scene scene, PlayerCharacter pc, Vector velocity) {
        super(64, 64);

        Animation flying = Animation.createFromAnimatedGif("game-assets/jump/spr_toucan_fly_anim.gif", 1, 1);
        addState("flying", flying);

        this.velocity = velocity;
        scene.add(this);

        if (velocity.x < 0) {
            this.setFlipHorizontal(true);
        }

        scene.getFrameUpdateListeners().add(enemyMover);

        this.addCollisionListener(this, pc);
    }

    @Override
    public void onCollision(CollisionEvent<PlayerCharacter> collisionEvent) {
        PlayerCharacter pc = collisionEvent.getColliding();
        float playerY = pc.position.get().add(new Vector(0, 64)).y;
        if (playerY <= this.position.get().y + 5f) {
            System.out.println("WIN");
            //Treffer!
            pc.physics.applyImpulse(new Vector(0, -2500));

            getLayer().getFrameUpdateListeners().remove(enemyMover);
            getLayer().remove(this);
        } else {
            System.out.println("LOSE");
            //Verletzt
            pc.physics.applyImpulse(position.get().negate().add(pc.position.getCenter()).multiply(20));
        }
    }
}

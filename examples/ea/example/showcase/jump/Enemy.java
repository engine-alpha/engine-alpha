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
        public void onFrameUpdate(int frameDuration) {
            Enemy.this.physics.setVelocity(velocity);
        }
    };

    public Enemy(Scene scene, PlayerCharacter pc, Vector velocity) {
        super(scene, 64, 64);

        Animation flying = Animation.createFromAnimatedGif(scene, "game-assets/jump/spr_toucan_fly_anim.gif");
        addState("flying", flying);

        this.velocity = velocity;
        scene.add(this);


        if(velocity.x < 0) this.setFlipHorizontal(true);

        scene.addFrameUpdateListener(enemyMover);

        this.addCollisionListener(this, pc);
    }

    @Override
    public void onCollision(CollisionEvent<PlayerCharacter> collisionEvent) {
        PlayerCharacter pc = collisionEvent.getColliding();
        float playerY = pc.position.get().verschobeneInstanz(new Vector(0, 64)).y;
        if(playerY <= this.position.get().y + 5f) {
            System.out.println("WIN");
            //Treffer!
            pc.physics.applyImpulse(new Vector(0, -2500));

            getScene().removeFrameUpdateListener(enemyMover);
            destroy();
        } else {
            System.out.println("LOSE");
            //Verletzt
            pc.physics.applyImpulse(position.get().vectorFromThisTo(pc.position.getCenter()).multiply(20));
        }
    }
}

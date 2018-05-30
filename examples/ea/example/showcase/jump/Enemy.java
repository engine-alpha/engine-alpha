package ea.example.showcase.jump;

import ea.FrameUpdateListener;
import ea.Point;
import ea.Scene;
import ea.Vector;
import ea.actor.Animation;
import ea.actor.StatefulAnimation;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;

public class Enemy
extends StatefulAnimation
implements CollisionListener<PlayerCharacter> {

    private final Vector velocity;
    private final Scene parent;
    FrameUpdateListener enemyMover = new FrameUpdateListener() {
        @Override
        public void onFrameUpdate(int frameDuration) {
            Enemy.this.physics.setVelocity(velocity);
        }
    };

    public Enemy(Scene parent, PlayerCharacter pc, Vector velocity) {
        Animation flying = Animation.createFromAnimatedGif("game-assets\\jump\\spr_toucan_fly_anim.gif");
        addState("flying", flying);

        this.parent = parent;
        this.velocity = velocity;
        parent.add(this);


        if(velocity.x < 0) this.setFlipHorizontal(true);

        parent.addFrameUpdateListener(enemyMover);

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

            Point position = this.position.get();
            parent.removeFrameUpdateListener(enemyMover);
            //Animation kill = Animation.createFromAnimatedGif("game-assets\\jump\\fx_explosion_b_anim.gif");
            //parent.add(kill);
            parent.remove(this);
            //kill.setOneTimeOnly();
        } else {
            System.out.println("LOSE");
            //Verletzt
            pc.physics.applyImpulse(position.get().vectorFromThisTo(pc.position.getCenter()).multiply(20));
        }
    }
}

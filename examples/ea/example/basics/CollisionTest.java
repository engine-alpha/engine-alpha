package ea.example.basics;

import ea.FrameUpdateListener;
import ea.Game;
import ea.Scene;
import ea.Vector;
import ea.actor.Actor;
import ea.actor.Circle;
import ea.actor.Rectangle;
import ea.collision.CollisionEvent;
import ea.collision.CollisionListener;

import java.awt.*;

public class CollisionTest
extends Scene
implements FrameUpdateListener {

    Vector BALLSPEED_PER_MS = new Vector(0.2f, -0.002f);

    Rectangle wall = new Rectangle(20, 300);
    Circle ball = new Circle(20);

    public CollisionTest() {
        wall.setColor(Color.ORANGE);
        ball.setColor(Color.green);

        //ball.position.set(-200, 0);
        //wall.position.set(200, -200);

        add(wall, ball);

        ball.addCollisionListener(new CollisionListener<Rectangle>() {
            @Override
            public void onCollision(CollisionEvent<Rectangle> collisionEvent) {
                System.out.println("COLLISION");
            }
        }, wall);

        if(ball.overlaps(wall)) {
            System.out.println("OVERLAP");
        }

        addFrameUpdateListener(this);
    }


    public static void main(String[] args) {
        Game.start(500,500, new CollisionTest());
    }

    @Override
    public void onFrameUpdate(int frameDuration) {

        //ball.position.move(BALLSPEED_PER_MS.multiply(frameDuration));

    }
}

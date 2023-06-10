package ea.example.basics;

import ea.Game;
import ea.Scene;
import ea.actor.Circle;
import ea.actor.Rectangle;

import java.awt.Color;

public class CollisionTest extends Scene {

    Rectangle wall = new Rectangle(20, 300);
    Circle ball = new Circle(20);

    public CollisionTest() {
        wall.setColor(Color.ORANGE);
        ball.setColor(Color.green);

        //ball.position.set(-200, 0);
        //wall.position.set(200, -200);

        add(wall, ball);

        ball.addCollisionListener(wall, (collisionEvent) -> System.out.println("COLLISION"));

        if (ball.overlaps(wall)) {
            System.out.println("OVERLAP");
        }
    }

    public static void main(String[] args) {
        Game.start(500, 500, new CollisionTest());
    }
}

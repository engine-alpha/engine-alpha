package ea.example.showcase.shots;

import ea.*;
import ea.actor.BodyType;
import ea.actor.Image;
import ea.actor.Rectangle;
import ea.example.showcase.ShowcaseDemo;
import ea.example.showcase.Showcases;
import ea.internal.FixtureBuilder;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class Shots extends ShowcaseDemo implements FrameUpdateListener {
    private final Ball ball;
    private final Rectangle basket;
    private final List<Image> net = new ArrayList<>();

    public Shots(Scene parent) {
        super(parent);

        setGravity(new Vector(0, -9.81f));
        getCamera().setZoom(100);

        Vector ballPosition = new Vector(-1.7f, 0.5f);

        getMainLayer().add(ball = new Ball(ballPosition.getX(), ballPosition.getY()));
        getMainLayer().add(new Wall(-6, -4.5f, 12, 1));
        getMainLayer().add(new Wall(-6, 3.5f, 12, 1));
        getMainLayer().add(new Wall(-6.5f, -4.5f, 1, 9));
        getMainLayer().add(new Wall(5.5f, -4.5f, 1, 9));

        BallShadow ballShadow = new BallShadow(ballPosition.getX(), ballPosition.getY());
        ball.createDistanceJoint(ballShadow, new Vector(.15f, .15f), new Vector(.15f, .15f));
        getMainLayer().add(ballShadow);

        basket = new Rectangle(1.5f, 0.05f);
        basket.setColor(Color.RED);
        basket.setPosition(3, 0.5f);
        basket.setBodyType(BodyType.SENSOR);
        basket.setGravityScale(0);
        basket.addCollisionListener(ball, event -> defer(() -> basket.setX(-basket.getX())));

        getMainLayer().add(basket);

        addKeyListener(e -> {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                ball.setVelocity(new Vector(Math.signum(basket.getX()) * 2, 6));
            }
        });
    }

    public static void main(String[] args) {
        Game.start(Showcases.WIDTH, Showcases.HEIGHT, new Shots(null));
    }

    @Override
    public void onFrameUpdate(float deltaSeconds) {

    }

    private static class Ball extends Image {
        public Ball(float x, float y) {
            super("game-assets/shots/ball.png", 0.3f, 0.3f);

            setPosition(x + .15f, y + .15f);
            setFixture(() -> FixtureBuilder.createCircleShape(0.15f, 0.15f, 0.15f));
            setBodyType(BodyType.DYNAMIC);
            setRestitution(0.85f);
            setFriction(0.1f);
        }
    }

    private static class BallShadow extends Image {
        public BallShadow(float x, float y) {
            super("game-assets/shots/shadow.png", 0.3f, 0.3f);

            setPosition(x + .15f, y + .15f);
            setFixture(() -> FixtureBuilder.createCircleShape(0.15f, 0.15f, 0.15f));
            setBodyType(BodyType.SENSOR);
            setGravityScale(0);
            setRotationLocked(true);
        }
    }

    private static class Wall extends Rectangle {
        public Wall(float x, float y, float width, float height) {
            super(width, height);

            setPosition(x, y);
            setColor(Color.WHITE);
            setBodyType(BodyType.STATIC);
            setFriction(.05f);
            setRestitution(.3f);
            setDensity(150);
        }
    }
}

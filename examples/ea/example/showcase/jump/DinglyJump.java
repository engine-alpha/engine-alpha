package ea.example.showcase.jump;


import ea.FrameUpdateListener;
import ea.Scene;
import ea.Vector;
import ea.actor.Rectangle;
import ea.example.showcase.ShowcaseDemo;
import ea.handle.Physics;
import ea.keyboard.*;

import java.awt.*;


/**
 * Ein einfaches Demo-Spiel zur Demonstration der Engine 4.0: Ein <i>Doodle Jump</i>-Klon.
 * <br />
 * Tausend Dank an <a href="http://openpixelproject.com">Open Pixel Project</a> für die
 * Bereitstellung kostenfreier Assets für diese Demo!
 */
public class DinglyJump extends ShowcaseDemo
implements KeyListener, FrameUpdateListener {

    PlayerCharacter pc;

    public DinglyJump(Scene parent, int width, int height) {
        super(parent);

        pc = new PlayerCharacter(this);
        pc.physics.setType(Physics.Type.DYNAMIC);
        pc.physics.setRotationLocked(true);

        pc.physics.setGravity(new Vector(0, 12));

        Rectangle platform = new Rectangle(500, 10);
        platform.setColor(Color.WHITE);
        platform.position.set(-250, 300);
        add(platform);
        platform.physics.setType(Physics.Type.STATIC);
        platform.physics.setElasticity(0);

        addKeyListener(this);
        addFrameUpdateListener(this);

        setDebuggingEnabled(false);
    }


    @Override
    public void onKeyDown(int key) {
        switch(key) {
            case Key.K:
                pc.setState("running");
                break;
            case Key.LEERTASTE: //Sprungbefehl
                pc.tryJumping();
                break;
        }
    }

    @Override
    public void onKeyUp(int code) {
        switch (code) {

        }
    }

    @Override
    public void onFrameUpdate(int frameDuration) {
        pc.framewiseUpdate(frameDuration);
    }
}

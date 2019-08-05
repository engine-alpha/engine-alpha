package ea.example.showcase.dude;

import ea.Random;
import ea.Scene;
import ea.actor.Image;
import ea.handle.Physics;

public class Box extends Image {

    public Box(Scene scene) {
        this(scene, Random.nextInteger(9));
    }

    public Box(Scene scene, int type) {
        super(scene, boxPath(type), 1, 1);

        setBodyType(Physics.Type.DYNAMIC);
        physics.setMass(30);
        physics.setElasticity(0);
    }

    public static String boxPath(int type) {
        if (type < 0 || type > 9) {
            throw new RuntimeException("Box-Typ existiert nicht.");
        }
        String path;
        if (type > 4) {
            //Second half
            path = "game-assets/dude/box/obj_crate00";
        } else {
            path = "game-assets/dude/box/obj_box00";
        }

        return path + (type % 5 + 1) + ".png";
    }
}

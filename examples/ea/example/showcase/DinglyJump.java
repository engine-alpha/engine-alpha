package ea.example.showcase;


import ea.Scene;
import ea.actor.Animation;

/**
 * Ein einfaches Demo-Spiel zur Demonstration der Engine 4.0: Ein <i>Doodle Jump</i>-Klon.
 * <br />
 * Tausend Dank an <a href="http://openpixelproject.com">Open Pixel Project</a> für die
 * Bereitstellung kostenfreier Assets für diese Demo!
 */
public class DinglyJump extends ShowcaseDemo {



    public DinglyJump(Scene parent, int width, int height) {
        super(parent);

        Animation test1 = Animation.createFromAnimatedGif(
                "game-assets\\spr_m_traveler_run_anim.gif");
        add(test1);
    }


}

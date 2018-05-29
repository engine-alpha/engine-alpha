package ea.example.showcase;


import ea.EngineAlpha;
import ea.FrameUpdateListener;
import ea.Game;
import ea.Scene;
import ea.keyboard.Key;
import ea.keyboard.KeyListener;
import ea.mouse.MouseWheelAction;
import ea.mouse.MouseWheelListener;

public abstract class ShowcaseDemo
extends Scene {

    /**
     * Geschwindigkeit der Camerabewegung pro Frame
     */
    private static final float CAMERA_SPEED = 7f;

    //Ob Mausrad-Zoom erlaubt ist
    private boolean zoomEnabled = true;
    //Ob Kamerasteuerung per Pfeiltasten aktiviert ist.
    private boolean cameraControlEnabled = true;

    /**
     * Die Scene, zu der man zurückkehrt.
     */
    private final Scene parent;


    public ShowcaseDemo(Scene parent) {
        this.parent = parent;

        addKeyListener(new KeyListener() {
            @Override
            public void onKeyDown(int i) {
                switch (i) {
                    case Key.ESCAPE:
                        EngineAlpha.setDebug(false);
                        Game.transitionToScene(parent);
                        break;
                    case Key.D: //Toggle Debug
                        EngineAlpha.setDebug(!EngineAlpha.isDebug());
                        break;
                }
            }
            @Override
            public void onKeyUp(int i) {
                //NADA
            }
        });

        addFrameUpdateListener(new FrameUpdateListener() {
            @Override
            public void onFrameUpdate(int i) {
                if(!cameraControlEnabled) return;
                //Smooth Camera Movement
                float dX=0, dY=0;
                if(Game.isKeyPressed(Key.OBEN)) {
                    dY=-CAMERA_SPEED;
                } else if(Game.isKeyPressed(Key.UNTEN)) {
                    dY=CAMERA_SPEED;
                }
                if(Game.isKeyPressed(Key.LINKS)) {
                    dX = -CAMERA_SPEED;
                } else if(Game.isKeyPressed(Key.RECHTS)) {
                    dX = CAMERA_SPEED;
                }
                if(dX!=0 || dY!=0) getCamera().move(dX,dY);
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void onMouseWheelMove(MouseWheelAction mouseWheelAction) {
                if(!zoomEnabled) return;
                float newzoom = getCamera().getZoom() + (mouseWheelAction.getPreciseWheelRotation()*-0.1f);
                if(newzoom <= 0) return;
                getCamera().setZoom(newzoom);
            }
        });
    }

    public void setZoomEnabled(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
    }

    public void setCameraControlEnabled(boolean cameraControlEnabled) {
        this.cameraControlEnabled = cameraControlEnabled;
    }
}

package ea.example.showcase;

import ea.Game;
import ea.Scene;
import ea.input.KeyListener;

import java.awt.event.KeyEvent;

public abstract class ShowcaseDemo extends Scene {

    /**
     * Geschwindigkeit der Camerabewegung pro Frame
     */
    private static final float CAMERA_SPEED = 7f;

    //Ob Mausrad-Zoom erlaubt ist
    private boolean zoomEnabled = true;
    //Ob Kamerasteuerung per Pfeiltasten aktiviert ist.
    private boolean cameraControlEnabled = true;
    //Ob Debug-Toggling enabled ist
    private boolean debuggingEnabled = true;

    public ShowcaseDemo(Scene parent) {
        addKeyListener(new KeyListener() {
            @Override
            public void onKeyDown(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_ESCAPE:
                        Game.setDebug(false);
                        Game.transitionToScene(parent);
                        break;
                    case KeyEvent.VK_D: //Toggle Debug
                        if (debuggingEnabled) {
                            toggleDebug();
                        }
                        break;
                }
            }

            @Override
            public void onKeyUp(KeyEvent e) {
                // NADA
            }
        });

        addFrameUpdateListener(i -> {
            if (!cameraControlEnabled) {
                return;
            }
            //Smooth Camera Movement
            float dX = 0, dY = 0;
            if (Game.isKeyPressed(KeyEvent.VK_UP)) {
                dY = CAMERA_SPEED;
            } else if (Game.isKeyPressed(KeyEvent.VK_DOWN)) {
                dY = -CAMERA_SPEED;
            }
            if (Game.isKeyPressed(KeyEvent.VK_LEFT)) {
                dX = -CAMERA_SPEED;
            } else if (Game.isKeyPressed(KeyEvent.VK_RIGHT)) {
                dX = CAMERA_SPEED;
            }
            if (dX != 0 || dY != 0) {
                getCamera().move(dX, dY);
            }
        });

        addMouseWheelListener(mouseWheelAction -> {
            if (!zoomEnabled) {
                return;
            }
            float newzoom = getCamera().getZoom() + (mouseWheelAction.getPreciseWheelRotation() * -0.1f);
            if (newzoom <= 0) {
                return;
            }
            getCamera().setZoom(newzoom);
        });
    }

    public void setZoomEnabled(boolean zoomEnabled) {
        this.zoomEnabled = zoomEnabled;
    }

    public void setCameraControlEnabled(boolean cameraControlEnabled) {
        this.cameraControlEnabled = cameraControlEnabled;
    }

    protected void toggleDebug() {
        Game.setDebug(!Game.isDebug());
    }

    protected void setDebuggingEnabled(boolean debuggingEnabled) {
        this.debuggingEnabled = debuggingEnabled;
    }
}

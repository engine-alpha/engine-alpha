package ea.example.showcase;

import ea.*;
import ea.Point;
import ea.actor.ActorGroup;
import ea.actor.Rectangle;
import ea.actor.Text;
import ea.example.showcase.jump.DinglyJump;
import ea.example.showcase.swordplay.Swordplay;
import ea.mouse.MouseButton;
import ea.mouse.MouseClickListener;

import java.awt.*;
import java.util.function.Supplier;

/**
 * Diese Klasse beschreibt die Kontroll-Scene, in der man Demos auswählen und starten kann.
 */
public class Showcases extends Scene {

    /**
     * State für die interne TextBoxen
     */
    private enum TextboxState {
        NORMAL, PRESSED, HOVER;

        public Color toColor() {
            switch (this) {
                case NORMAL:
                    return BOX_NORMAL;
                case HOVER:
                    return BOX_HOVER;
                case PRESSED:
                    return BOX_PRESSED;
                default:
                    return null;
            }
        }
    }

    private static final Color BOX_NORMAL = new Color(50, 50, 255, 100);
    private static final Color BOX_HOVER = new Color(255, 50, 50, 100);
    private static final Color BOX_PRESSED = new Color(50, 255, 50, 100);
    private static final int BOX_WIDTH = 450;
    private static final int BOX_HEIGHT = 60;

    /**
     * Textbox-Element. Besteht aus einem Text mit Hintergrund-Box. Beim Mausklick auf die Box wird die zugewiesene
     * Scene gestartet.
     */
    private class TextBox extends ActorGroup implements MouseClickListener, FrameUpdateListener {

        //Box für den Text
        private Rectangle box;
        //Der sichtbare Text
        private Text text;
        //Runnable, das die Scene.
        private final Supplier<Scene> sceneCreator;
        //Der aktuelle State der TextBox
        private TextboxState state = TextboxState.NORMAL;

        public TextBox(String content, Supplier<Scene> sceneCreator) {
            box = new Rectangle(BOX_WIDTH, BOX_HEIGHT);
            text = new Text(content, 30);
            box.setZIndex(0);
            text.setZIndex(1);
            text.position.move(10, -10);
            add(box, text);

            this.sceneCreator = sceneCreator;
            //Showcases.this.addMouseClickListener(this);
            //Showcases.this.addFrameUpdateListener(this);
            updateUI();
        }

        /**
         * Stellt sicher, dass das UI optisch korrekt dargestellt wird. Wird bei jedem State-Wechsel aufgerufen und
         * ändert Farben im UI.
         */
        private void updateUI() {
            box.setColor(state.toColor());
        }

        @Override
        public void onMouseDown(Point point, MouseButton mouseButton) {
            if (box.contains(point)) {
                //CLICKED ME
                state = TextboxState.PRESSED;
                updateUI();
            }
        }

        @Override
        public void onMouseUp(Point point, MouseButton mouseButton) {
            if (box.contains(point)) {
                //CLICKED ME
                if (state == TextboxState.PRESSED) {
                    //I was pressed before ==> CHANGE SCENE
                    Game.transitionToScene(sceneCreator.get());
                }
            }
        }

        @Override
        public void onFrameUpdate(int i) {
            Point mousePosition = Showcases.this.getMousePosition();

            if (box.contains(mousePosition)) {
                //HOVER?
                if (state != TextboxState.PRESSED) {
                    state = TextboxState.HOVER;
                }
            } else {
                state = TextboxState.NORMAL;
            }
            updateUI();
        }
    }

    /**
     * Count der aktuellen Buttons in der Demo.
     */
    private int buttonCount = 0;

    public Showcases() {
        Text title = new Text("Engine Alpha: 4.0 Feature Showcase", 60);
        title.setColor(Color.WHITE);
        Text subtitle = new Text("Knopfdruck startet Demo. Escape-Taste bringt dich ins Menü zurück", 30);
        subtitle.setColor(Color.RED);

        title.position.set(10, 10);
        subtitle.position.set(15, 100);

        add(title, subtitle);
    }

    /**
     * Fügt eine Scene der Showcase hinzu.
     *
     * @param sceneSupplier Die hinzuzufügende Scene. Gewrappt in einen Creator, damit keine Ressourcen aufgebraucht
     *                      werden, bis die Scene tatsächlich angefragt wird.
     * @param title         Der Titel für die Textbox
     */
    public void addScene(Supplier<Scene> sceneSupplier, String title) {
        buttonCount++;

        boolean left = buttonCount % 2 == 1;
        int row = (buttonCount - 1) / 2;

        TextBox button = new TextBox(title, sceneSupplier);
        button.position.set(left ? -30 - BOX_WIDTH : 30, -1 * row * (BOX_HEIGHT + 5));
        add(button);
    }

    /**
     * Main-Methode. Startet die Demo.
     */
    public static void main(String[] args) {
        final int WIDTH = 1240, HEIGHT = 812;

        Showcases mainscene = new Showcases();

        mainscene.addScene(() -> new BallThrow(mainscene, WIDTH, HEIGHT), "Einfacher Ballwurf");
        mainscene.addScene(() -> new PhysicsSandbox(mainscene, WIDTH, HEIGHT), "Kräfte-Sandbox");
        mainscene.addScene(() -> new JointDemo(mainscene, WIDTH, HEIGHT), "Joints in der Engine");
        mainscene.addScene(() -> new MarbleDemo(mainscene, WIDTH, HEIGHT), "Murmel-Demo");
        mainscene.addScene(() -> new DinglyJump(mainscene), "Dingly Jump");
        mainscene.addScene(() -> new Swordplay(mainscene, WIDTH, HEIGHT), "Swordplay");

        EngineAlpha.setDebug(true);

        Game.setExitOnEsc(false);
        Game.setTitle("Engine Alpha: Feature Showcase");
        Game.start(WIDTH, HEIGHT, mainscene);
    }
}

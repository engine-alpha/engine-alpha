package ea.edu;

import ea.FrameUpdateListener;
import ea.Layer;
import ea.Scene;
import ea.Vector;
import ea.actor.Actor;
import ea.input.*;

import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class EduScene extends Scene implements KeyListener, MouseClickListener, FrameUpdateListener, MouseWheelListener {
    public static final String MAINLAYER_NAME = "Hauptebene";

    /* _____________________________ LISTENER LISTS _____________________________ */

    /**
     * Die Liste aller TICKER-Aufgaben
     */
    private final ArrayList<TickerAuftrag> sceneTickers = new ArrayList<>();

    /**
     * Die Liste aller TASTEN-Aufgaben
     */
    private final ArrayList<TastenAuftrag> sceneKeyListeners = new ArrayList<>();

    /**
     * Die Liste aller KLICK-Aufgaben
     */
    private final ArrayList<KlickAuftrag> sceneKlickListeners = new ArrayList<>();

    /**
     * Liste aller Framewise Update Aufträge
     */
    private final ArrayList<FrameUpdateAuftrag> sceneFrameUpdateListeners = new ArrayList<>();

    /**
     * Liste aller MouseWheelListener
     */
    private final ArrayList<MouseWheelAuftrag> sceneMouseWheelListeners = new ArrayList<>();

    private static final HashMap<String, String> primitiveTranslator;

    static {
        primitiveTranslator = new HashMap<>();
        primitiveTranslator.put("java.lang.Integer", "int");
        primitiveTranslator.put("java.lang.Float", "float");
        primitiveTranslator.put("java.lang.Boolean", "boolean");
    }


    /* _____________________________ SCENE AND LAYER FIELDS _____________________________ */

    /**
     * Name der Scene. Default ist null.
     * Eine Scene mit Name wird nicht automatisch gelöscht.
     */
    private String sceneName = null;

    public void setSceneName(String name) {
        this.sceneName = name;
    }

    public String getSceneName() {
        return sceneName;
    }

    private final HashMap<String, Layer> layerHashMap = new HashMap<>();

    private Layer activeLayer;

    public EduScene() {
        activeLayer = getMainLayer();
        layerHashMap.put(MAINLAYER_NAME, getMainLayer());

        setGravity(new Vector(0, -9.81f));

        super.getFrameUpdateListeners().add(this);
        super.getKeyListeners().add(this);
        super.getMouseClickListeners().add(this);
        super.getMouseWheelListeners().add(this);
    }

    public String[] layerNames() {
        return (String[]) layerHashMap.keySet().toArray();
    }

    private final void assertLayerHashMapContains(String key, boolean shouldContain) {
        if (shouldContain != layerHashMap.containsKey(key)) {
            throw new IllegalArgumentException(shouldContain ? "Diese Edu-Scene enthält keine Ebene mit dem Namen " + key : "Diese Edu-Scene enthält bereits eine Ebene mit dem Namen " + key);
        }
    }

    public void addLayer(String layerName, int layerPosition) {
        assertLayerHashMapContains(layerName, false);

        Layer layer = new Layer();
        layer.setLayerPosition(layerPosition);
        layerHashMap.put(layerName, layer);
    }

    public void setLayerParallax(String layerName, float px, float py, float pz) {
        assertLayerHashMapContains(layerName, true);

        Layer layer = layerHashMap.get(layerName);
        layer.setParallaxPosition(px, py);
        layer.setParallaxZoom(pz);
    }

    public void setActiveLayer(String layerName) {
        assertLayerHashMapContains(layerName, true);

        activeLayer = layerHashMap.get(layerName);
    }

    public void resetToMainLayer() {
        setActiveLayer(MAINLAYER_NAME);
    }

    /**
     * Fügt einen EduActor der Scene hinzu. Wird am derzeit aktiven Layer geadded.
     *
     * @param actor zu addender Actor
     */
    public void addEduActor(Actor actor) {
        activeLayer.add(actor);
    }

    /* _____________________________ Listener Addition & Removal _____________________________ */

    public void addEduClickListener(Object client, boolean linksklick) {
        addToClientableArrayList(sceneKlickListeners, new KlickAuftrag(client, linksklick));
    }

    public void removeEduClickListener(Object object) {
        removeFromArrayList(sceneKlickListeners, object);
    }

    public void addEduKeyListener(Object o) {
        addToClientableArrayList(sceneKeyListeners, new TastenAuftrag(o));
    }

    public void removeEduKeyListener(Object o) {
        removeFromArrayList(sceneKeyListeners, o);
    }

    public void addEduTicker(Object o, int intervall) {
        addToClientableArrayList(sceneTickers, new TickerAuftrag(o, intervall));
    }

    public void removeEduTicker(Object o) {
        removeFromArrayList(sceneTickers, o);
    }

    public void addEduFrameUpdateListener(Object o) {
        addToClientableArrayList(sceneFrameUpdateListeners, new FrameUpdateAuftrag(o));
    }

    public void removeEduFrameUpdateListener(Object o) {
        removeFromArrayList(sceneFrameUpdateListeners, o);
    }

    public void addEduMouseWheelListener(Object o) {
        addToClientableArrayList(sceneMouseWheelListeners, new MouseWheelAuftrag(o));
    }

    public void removeEduMouseWheelListener(Object o) {
        removeFromArrayList(sceneMouseWheelListeners, o);
    }

    private static final <E extends Clientable> boolean addToClientableArrayList(ArrayList<E> targetList, E toAdd) {
        Class<?> objectClass = toAdd.getClient().getClass();
        Method[] methods = objectClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equals(toAdd.getInvocationMethodName())) {
                //Correct Name, Check for correct parameters
                Class<?>[] targetParameters = toAdd.getInvocationMethodParameters();
                Class<?>[] parameters = methods[i].getParameterTypes();
                if (parameters.length != targetParameters.length) {
                    throw new IllegalArgumentException("Achtung! Das übergebene Objekt hatte eine Methode mit korrektem Namen (" + toAdd.getInvocationMethodName() + "), aber nicht die korrekte Parameteranzahl.");
                }
                for (int k = 0; k < targetParameters.length; k++) {
                    if (!compareParameters(targetParameters[k], parameters[k])) {
                        //Strange Case: Correct Method Name, wrong Parameters!
                        throw new IllegalArgumentException("Achtung! Übergebenes Objekt hatte korrekten Methodennamen, " + "aber nicht die korrekte Parameter. Fehler bei " + targetParameters[k].getName() + " (erwartet) vs. " + parameters[k].getName());
                    }
                }
                toAdd.setMethodToInvoke(methods[i]);
                targetList.add(toAdd);
                return true;
            }
        }
        return false;
    }

    private static final boolean compareParameters(Class<?> param1, Class<?> param2) {
        String alt1 = primitiveTranslator.get(param1.getName());
        if (alt1 != null && alt1.equals(param2.getName())) {
            return true;
        }
        String alt2 = primitiveTranslator.get(param2.getName());
        if (alt2 != null && alt2.equals(param1.getName())) {
            return true;
        }

        return param1.getName().equals(param2.getName());
    }

    private static final <E extends Clientable> void removeFromArrayList(ArrayList<E> list, Object object) {
        ArrayList<E> toRemove = new ArrayList<>();
        for (E e : list) {
            if (e.getClient().equals(object)) {
                toRemove.add(e);
            }
        }
        for (E e : toRemove) {
            list.remove(e);
        }
    }

    /* EA Listener Implementation */

    @Override
    public void onFrameUpdate(float frameDuration) {
        for (TickerAuftrag ta : sceneTickers) {
            ta.accountFrame(frameDuration);
        }
        for (FrameUpdateAuftrag a : sceneFrameUpdateListeners) {
            a.forwardFrameUpdate(frameDuration);
        }
    }

    @Override
    public void onKeyDown(KeyEvent e) {
        for (TastenAuftrag ta : sceneKeyListeners) {
            ta.ausfuehren(e.getKeyCode());
        }
    }

    @Override
    public void onKeyUp(KeyEvent e) {
        // Ignore.
    }

    @Override
    public void onMouseDown(Vector position, MouseButton button) {
        runMouseReactions(position, button, true);
    }

    @Override
    public void onMouseUp(Vector position, MouseButton button) {
        runMouseReactions(position, button, false);
    }

    private final void runMouseReactions(Vector position, MouseButton button, boolean down) {
        for (KlickAuftrag ka : sceneKlickListeners) {
            if (ka.linksklick && button == MouseButton.LEFT) {
                ka.ausfuehren(position.x, position.y, down);
            } else if (!ka.linksklick && button == MouseButton.RIGHT) {
                ka.ausfuehren(position.x, position.y, down);
            }
        }
    }

    @Override
    public void onMouseWheelMove(MouseWheelEvent mouseWheelEvent) {
        for (MouseWheelAuftrag mouseWheelAuftrag : sceneMouseWheelListeners) {
            mouseWheelAuftrag.forwardMouseWheelEvent(mouseWheelEvent);
        }
    }

    /* ~~~ Listener CLASSES ~~~ */

    private interface Clientable {
        Object getClient();
        void setMethodToInvoke(Method methodToInvoke);
        String getInvocationMethodName();
        Class<?>[] getInvocationMethodParameters();
    }

    private abstract class Auftrag implements Clientable {

        protected Method method;
        protected final Object client;

        public Auftrag(Object client) {
            this.client = client;
        }

        @Override
        public Object getClient() {
            return client;
        }

        @Override
        public void setMethodToInvoke(Method methodToInvoke) {
            this.method = methodToInvoke;
        }
    }

    /**
     * Ein TickerAuftrag regelt je einen Fake-Ticker.
     */
    private final class TickerAuftrag extends Auftrag {

        /**
         * Das Intervall
         */
        private final int intervall;

        private int counter;

        public TickerAuftrag(Object client, int intervall) {
            super(client);
            this.intervall = intervall;
            this.counter = intervall;
        }

        /**
         * Frameweise Abarbeitung
         */
        public final void accountFrame(float millis) {
            counter -= millis;
            if (counter > 0) {
                return;
            }
            try {
                method.invoke(client, new Object[0]);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            while (counter <= 0) {
                counter += intervall;
            }
        }

        /**
         * @return Das Intervall des gelagerten Objektes
         */
        public int intervall() {
            return intervall;
        }

        @Override
        public String getInvocationMethodName() {
            return "tick";
        }

        @Override
        public Class<?>[] getInvocationMethodParameters() {
            return new Class<?>[] {};
        }
    }

    /**
     * Ein TastenAuftrag regelt den Aufruf eines TastenReaktions-Interface.
     */
    private final class TastenAuftrag extends Auftrag {

        /**
         * Erstellt einen Tastenauftrag
         *
         * @param client Das Objekt, an dem der Job ausgefuehrt werden soll.
         */
        public TastenAuftrag(Object client) {
            super(client);
        }

        /**
         * Führt die Methode einmalig aus.
         *
         * @param code Der Tastaturcode, der mitgegeben wird.
         */
        public void ausfuehren(int code) {
            try {
                method.invoke(client, code);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (java.lang.IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getInvocationMethodName() {
            return "tasteReagieren";
        }

        @Override
        public Class<?>[] getInvocationMethodParameters() {
            return new Class<?>[] {int.class};
        }
    }

    /**
     * Auftrag für einen Klick-Listener
     */
    private class KlickAuftrag extends Auftrag {

        private final boolean linksklick;

        private KlickAuftrag(Object c, boolean linksklick) {
            super(c);
            this.linksklick = linksklick;
        }

        /**
         * Führt die Methode am Client aus.
         *
         * @param x Die zu uebergebene X-Koordinate des Klicks.
         * @param y Die zu uebergebene Y-Koordinate des Klicks.
         */
        private void ausfuehren(float x, float y, boolean press) {
            try {
                method.invoke(client, new Object[] {x, y, press});
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (java.lang.IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getInvocationMethodName() {
            return "klickReagieren";
        }

        @Override
        public Class<?>[] getInvocationMethodParameters() {
            return new Class<?>[] {float.class, float.class, boolean.class};
        }
    }

    private final class FrameUpdateAuftrag extends Auftrag {

        private FrameUpdateAuftrag(Object client) {
            super(client);
        }

        private void forwardFrameUpdate(float frameDuration) {
            try {
                method.invoke(client, frameDuration);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getInvocationMethodName() {
            return "frameUpdateReagieren";
        }

        @Override
        public Class<?>[] getInvocationMethodParameters() {
            return new Class<?>[] {Integer.class};
        }
    }

    private final class MouseWheelAuftrag extends Auftrag {

        public MouseWheelAuftrag(Object client) {
            super(client);
        }

        private void forwardMouseWheelEvent(MouseWheelEvent mouseWheelEvent) {
            try {
                method.invoke(client, new Object[] {mouseWheelEvent.getPreciseWheelRotation()});
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String getInvocationMethodName() {
            return "mausRadReagieren";
        }

        @Override
        public Class<?>[] getInvocationMethodParameters() {
            return new Class[] {float.class};
        }
    }
}

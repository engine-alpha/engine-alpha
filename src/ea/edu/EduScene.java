package ea.edu;

import ea.*;
import ea.actor.Actor;
import ea.edu.event.*;
import ea.event.EventListeners;
import ea.input.KeyListener;
import ea.input.MouseButton;
import ea.input.MouseClickListener;
import ea.input.MouseWheelListener;

import java.awt.event.KeyEvent;
import java.util.HashMap;

public class EduScene extends Scene {
    public static final String MAINLAYER_NAME = "Hauptebene";
    private static final float EXPLORE_BASE_MOVE_PER_SEC = 1f;

    /* _____________________________ LISTENER LISTS _____________________________ */

    /**
     * Die Liste aller TICKER-Aufgaben
     */
    private final HashMap<Ticker, FrameUpdateListener> sceneTickers = new HashMap<>();

    /**
     * Die Liste aller TASTEN-Aufgaben
     */
    private final HashMap<TastenReagierbar, KeyListener> sceneKeyListeners = new HashMap<>();

    /**
     * Die Liste aller KLICK-Aufgaben
     */
    private final HashMap<MausKlickReagierbar, MouseClickListener> sceneMouseClickListeners = new HashMap<>();

    /**
     * Liste aller Framewise Update Aufträge
     */
    private final HashMap<FrameUpdateReagierbar, FrameUpdateListener> sceneFrameUpdateListeners = new HashMap<>();

    /**
     * Liste aller MouseWheelListener
     */
    private final HashMap<MausRadReagierbar, MouseWheelListener> sceneMouseWheelListeners = new HashMap<>();

    private boolean exploreMode = false;


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

    private FrameUpdateListener exploreModeFrameUpdateListener = deltaSeconds -> {
        if (!exploreMode) {
            return;
        }

        float dX = 0, dY = 0;
        if (Game.isKeyPressed(KeyEvent.VK_LEFT)) {
            dX = -EXPLORE_BASE_MOVE_PER_SEC / getCamera().getZoom();
        } else if (Game.isKeyPressed(KeyEvent.VK_RIGHT)) {
            dX = EXPLORE_BASE_MOVE_PER_SEC / getCamera().getZoom();
        }

        if (Game.isKeyPressed(KeyEvent.VK_UP)) {
            dY = EXPLORE_BASE_MOVE_PER_SEC / getCamera().getZoom();
        } else if (Game.isKeyPressed(KeyEvent.VK_DOWN)) {
            dY = -EXPLORE_BASE_MOVE_PER_SEC / getCamera().getZoom();
        }

        Vector move = new Vector(dX, dY).multiply(deltaSeconds);

        getCamera().move(move.x, move.y);
    };

    private MouseWheelListener exploreModeMouseWheelListener = event -> {
        if (!exploreMode) {
            return;
        }
        float factor = event.getPreciseWheelRotation() > 0 ? 1 + .3f * event.getPreciseWheelRotation() : 1 / (1 - .3f * event.getPreciseWheelRotation());
        float newzoom = getCamera().getZoom() * factor;
        if (newzoom <= 0) {
            return;
        }
        getCamera().setZoom(newzoom);
    };

    public EduScene() {
        activeLayer = getMainLayer();
        layerHashMap.put(MAINLAYER_NAME, getMainLayer());
        activeLayer.getFrameUpdateListeners().add(exploreModeFrameUpdateListener);
        activeLayer.getMouseWheelListeners().add(exploreModeMouseWheelListener);

        setGravity(new Vector(0, -9.81f));
    }

    public void setExploreMode(boolean aktiv) {
        exploreMode = aktiv;
    }





    /* _____________________________ Layers, Addition & Co  _____________________________ */

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

    public void addEduClickListener(MausKlickReagierbar client) {
        addListener(client, sceneMouseClickListeners, activeLayer.getMouseClickListeners(), new MouseClickListener() {
            @Override
            public void onMouseDown(Vector e, MouseButton b) {
                client.klickReagieren(e.x, e.y);
            }

            @Override
            public void onMouseUp(Vector e, MouseButton b) {
                client.klickLosgelassenReagieren(e.x, e.y);
            }
        });
    }

    public void removeEduClickListener(MausKlickReagierbar object) {
        removeListener(object, sceneMouseClickListeners, activeLayer.getMouseClickListeners());
    }

    public void addEduKeyListener(TastenReagierbar o) {
        //addToClientableArrayList(sceneKeyListeners, new TastenAuftrag(o));
        KeyListener keyListener = new KeyListener() {
            @Override
            public void onKeyDown(KeyEvent e) {
                o.tasteReagieren(e.getKeyCode());
            }

            @Override
            public void onKeyUp(KeyEvent e) {
                o.tasteLosgelassenReagieren(e.getKeyCode());
            }
        };
        addListener(o, sceneKeyListeners, activeLayer.getKeyListeners(), keyListener);
    }

    public void removeEduKeyListener(TastenReagierbar o) {
        removeListener(o, sceneKeyListeners, activeLayer.getKeyListeners());
    }

    public void addEduTicker(Ticker o, float intervall) {
        FrameUpdateListener periodicTask = new PeriodicTask(intervall, o::tick);
        addListener(o, sceneTickers, activeLayer.getFrameUpdateListeners(), periodicTask);
    }

    public void removeEduTicker(Ticker o) {
        removeListener(o, sceneTickers, activeLayer.getFrameUpdateListeners());
    }

    public void addEduFrameUpdateListener(FrameUpdateReagierbar o) {
        addListener(o, sceneFrameUpdateListeners, activeLayer.getFrameUpdateListeners(), o::frameUpdateReagieren);
    }

    public void removeEduFrameUpdateListener(FrameUpdateReagierbar o) {
        removeListener(o, sceneFrameUpdateListeners, activeLayer.getFrameUpdateListeners());
    }

    public void addEduMouseWheelListener(MausRadReagierbar o) {
        addListener(o, sceneMouseWheelListeners, activeLayer.getMouseWheelListeners(), (mwe) -> o.mausRadReagieren(mwe.getPreciseWheelRotation()));
    }

    public void removeEduMouseWheelListener(MausRadReagierbar o) {
        removeListener(o, sceneMouseWheelListeners, activeLayer.getMouseWheelListeners());
    }

    private static <K, V> void removeListener(K eduListener, HashMap<K, V> transitionHashMap, EventListeners<V> engineListeners) {
        V fromHashMap = transitionHashMap.get(eduListener);
        if (fromHashMap == null) {
            // Wert war nicht in Liste enthalten
            throw new IllegalArgumentException("Ein Reagierbar-Objekt sollte entfernt werden, war aber nicht an diesem Layer in dieser Szene angemeldet.");
        }
        engineListeners.remove(fromHashMap);
        transitionHashMap.remove(eduListener);
    }

    private static <K, V> void addListener(K eduListener, HashMap<K, V> transitionHashMap, EventListeners<V> engineListeners, V engineListener) {
        transitionHashMap.put(eduListener, engineListener);
        engineListeners.add(engineListener);
    }
}

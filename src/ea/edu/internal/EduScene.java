/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2019 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea.edu.internal;

import ea.*;
import ea.actor.Actor;
import ea.edu.event.*;
import ea.event.*;
import ea.internal.PeriodicTask;
import ea.internal.annotations.Internal;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

@Internal
public class EduScene extends Scene {
    private static final String MAINLAYER_NAME = "Hauptebene";
    private static final float EXPLORE_BASE_MOVE_PER_SEC = 100;
    private static final float DEFAULT_GRAVITY = -9.81f;
    private static final float EXPLORE_ZOOM_FACTOR = .3f;

    /**
     * Die Liste aller TICKER-Aufgaben
     */
    private final Map<Ticker, FrameUpdateListener> sceneTickers = new HashMap<>();

    /**
     * Die Liste aller TASTEN-Aufgaben
     */
    private final Map<TastenReagierbar, KeyListener> sceneKeyListeners = new HashMap<>();

    /**
     * Die Liste aller KLICK-Aufgaben
     */
    private final Map<MausKlickReagierbar, MouseClickListener> sceneMouseClickListeners = new HashMap<>();

    /**
     * Liste aller Framewise Update Aufträge
     */
    private final Map<BildAktualisierungReagierbar, FrameUpdateListener> sceneFrameUpdateListeners = new HashMap<>();

    /**
     * Liste aller MouseWheelListener
     */
    private final Map<MausRadReagierbar, MouseWheelListener> sceneMouseWheelListeners = new HashMap<>();

    private boolean exploreMode = false;

    /**
     * Name der Scene. Default ist null.
     * Eine Scene mit Name wird nicht automatisch gelöscht.
     */
    private String name = null;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private final Map<String, Layer> layers = new HashMap<>();

    private Layer activeLayer;

    public EduScene() {
        activeLayer = getMainLayer();
        layers.put(MAINLAYER_NAME, getMainLayer());

        activeLayer.addFrameUpdateListener(deltaSeconds -> {
            if (!exploreMode) {
                return;
            }

            float dX = 0, dY = 0;
            if (Game.isKeyPressed(KeyEvent.VK_LEFT)) {
                dX -= EXPLORE_BASE_MOVE_PER_SEC / getCamera().getZoom();
            } else if (Game.isKeyPressed(KeyEvent.VK_RIGHT)) {
                dX += EXPLORE_BASE_MOVE_PER_SEC / getCamera().getZoom();
            }

            if (Game.isKeyPressed(KeyEvent.VK_UP)) {
                dY += EXPLORE_BASE_MOVE_PER_SEC / getCamera().getZoom();
            } else if (Game.isKeyPressed(KeyEvent.VK_DOWN)) {
                dY -= EXPLORE_BASE_MOVE_PER_SEC / getCamera().getZoom();
            }

            getCamera().moveBy(new Vector(dX, dY).multiply(deltaSeconds));
        });

        activeLayer.addMouseWheelListener(event -> {
            if (!exploreMode) {
                return;
            }

            float rotation = -event.getPreciseWheelRotation();
            float factor = rotation > 0 ? 1 + EXPLORE_ZOOM_FACTOR * rotation : 1 / (1 - EXPLORE_ZOOM_FACTOR * rotation);
            float zoom = getCamera().getZoom() * factor;

            if (zoom <= 0) {
                return;
            }

            getCamera().setZoom(zoom);
        });

        setGravity(new Vector(0, DEFAULT_GRAVITY));
    }

    @Internal
    public void setExploreMode(boolean aktiv) {
        exploreMode = aktiv;
    }

    @Internal
    public String[] getLayerNames() {
        return layers.keySet().toArray(new String[0]);
    }

    @Internal
    public void addLayer(String layerName, int layerPosition) {
        assertLayerMapDoesNotContain(layerName);

        Layer layer = new Layer();
        layer.setLayerPosition(layerPosition);
        addLayer(layer);
        layers.put(layerName, layer);
    }

    @Internal
    public void setLayerParallax(String layerName, float x, float y, float zoom) {
        assertLayerMapContains(layerName);

        Layer layer = layers.get(layerName);
        layer.setParallaxPosition(x, y);
        layer.setParallaxZoom(zoom);
    }

    @Internal
    public void setLayerTimeDistort(String layerName, float tpx) {
        assertLayerMapContains(layerName);

        Layer layer = layers.get(layerName);
        layer.setTimeDistort(tpx);
    }

    @Internal
    public void setActiveLayer(String layerName) {
        assertLayerMapContains(layerName);

        activeLayer = layers.get(layerName);
    }

    @Internal
    public Layer getActiveLayer() {
        return activeLayer;
    }

    @Internal
    public void resetToMainLayer() {
        setActiveLayer(MAINLAYER_NAME);
    }

    /**
     * Fügt einen EduActor der Scene hinzu. Wird am derzeit aktiven Layer geadded.
     *
     * @param actor zu addender Actor
     */
    @Internal
    public void addEduActor(Actor actor) {
        activeLayer.add(actor);
    }

    @Internal
    public void addEduClickListener(MausKlickReagierbar client) {
        addListener(client, sceneMouseClickListeners, activeLayer.getMouseClickListeners(), new MouseClickListener() {
            @Override
            public void onMouseDown(Vector e, MouseButton b) {
                client.klickReagieren(e.getX(), e.getY());
            }

            @Override
            public void onMouseUp(Vector e, MouseButton b) {
                client.klickLosgelassenReagieren(e.getX(), e.getY());
            }
        });
    }

    @Internal
    public void removeEduClickListener(MausKlickReagierbar object) {
        removeListener(object, sceneMouseClickListeners, activeLayer.getMouseClickListeners());
    }

    @Internal
    public void addEduKeyListener(TastenReagierbar o) {
        addListener(o, sceneKeyListeners, activeLayer.getKeyListeners(), new KeyListener() {
            @Override
            public void onKeyDown(KeyEvent e) {
                o.tasteReagieren(e.getKeyCode());
            }

            @Override
            public void onKeyUp(KeyEvent e) {
                o.tasteLosgelassenReagieren(e.getKeyCode());
            }
        });
    }

    @Internal
    public void removeEduKeyListener(TastenReagierbar o) {
        removeListener(o, sceneKeyListeners, activeLayer.getKeyListeners());
    }

    @Internal
    public void addEduTicker(float interval, Ticker ticker) {

        FrameUpdateListener periodicTask = new PeriodicTask(interval, ticker::tick);
        addListener(ticker, sceneTickers, activeLayer.getFrameUpdateListeners(), periodicTask);
    }

    @Internal
    public void removeEduTicker(Ticker ticker) {
        removeListener(ticker, sceneTickers, activeLayer.getFrameUpdateListeners());
    }

    @Internal
    public void addEduFrameUpdateListener(BildAktualisierungReagierbar bildAktualisierungReagierbar) {
        addListener(bildAktualisierungReagierbar, sceneFrameUpdateListeners, activeLayer.getFrameUpdateListeners(), bildAktualisierungReagierbar::bildAktualisierungReagieren);
    }

    @Internal
    public void removeEduFrameUpdateListener(BildAktualisierungReagierbar bildAktualisierungReagierbar) {
        removeListener(bildAktualisierungReagierbar, sceneFrameUpdateListeners, activeLayer.getFrameUpdateListeners());
    }

    @Internal
    public void addEduMouseWheelListener(MausRadReagierbar mausRadReagierbar) {
        addListener(mausRadReagierbar, sceneMouseWheelListeners, activeLayer.getMouseWheelListeners(), (mwe) -> mausRadReagierbar.mausRadReagieren(mwe.getPreciseWheelRotation()));
    }

    @Internal
    public void removeEduMouseWheelListener(MausRadReagierbar mausRadReagierbar) {
        removeListener(mausRadReagierbar, sceneMouseWheelListeners, activeLayer.getMouseWheelListeners());
    }

    @Internal
    private static <K, V> void removeListener(K eduListener, Map<K, V> transitionMap, EventListeners<V> engineListeners) {
        V fromHashMap = transitionMap.get(eduListener);
        if (fromHashMap == null) {
            throw new IllegalArgumentException("Ein Reagierbar-Objekt sollte entfernt werden, war aber nicht an diesem Layer in dieser Szene angemeldet.");
        }

        engineListeners.remove(fromHashMap);
        transitionMap.remove(eduListener);
    }

    @Internal
    private static <K, V> void addListener(K eduListener, Map<K, V> transitionHashMap, EventListeners<V> engineListeners, V engineListener) {
        transitionHashMap.put(eduListener, engineListener);
        engineListeners.add(engineListener);
    }

    @Internal
    private void assertLayerMapContains(String key) {
        if (!layers.containsKey(key)) {
            throw new IllegalArgumentException("Diese Edu-Scene enthält keine Ebene mit dem Namen " + key);
        }
    }

    @Internal
    private void assertLayerMapDoesNotContain(String key) {
        if (layers.containsKey(key)) {
            throw new IllegalArgumentException("Diese Edu-Scene enthält bereits eine Ebene mit dem Namen " + key);
        }
    }
}

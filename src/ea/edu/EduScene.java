package ea.edu;

import ea.FrameUpdateListener;
import ea.Scene;
import ea.Vector;
import ea.keyboard.KeyListener;
import ea.mouse.MouseButton;
import ea.mouse.MouseClickListener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class EduScene
extends Scene
implements KeyListener, MouseClickListener, FrameUpdateListener {

    /**
     * Die Liste aller TICKER-Aufgaben
     */
    private final ArrayList<TickerAuftrag> aufgabenT = new ArrayList<>();

    /**
     * Die Liste aller TASTEN-Aufgaben
     */
    private final ArrayList<TastenAuftrag> aufgaben = new ArrayList<>();

    /**
     * Die Liste aller KLICK-Aufgaben
     */
    private final ArrayList<KlickAuftrag> aufgabenKlick = new ArrayList<>();

    /**
     * Name der Scene. Default ist null.
     * Eine Scene mit Name wird nicht automatisch gelöscht.
     */
    private String sceneName=null;

    public void setSceneName(String name) {
        this.sceneName = name;
    }

    public String getSceneName() {
        return sceneName;
    }

    public EduScene() {
        super.addFrameUpdateListener(this);
        super.addKeyListener(this);
        super.addMouseClickListener(this);
    }

    /* Listener Addition */

    public void addEduClickListener (Object client, boolean linksklick) {
        Class<?> klasse = client.getClass();
        Method[] methoden = klasse.getMethods();
        for (int i = 0; i < methoden.length; i++) {
            if (methoden[i].getName().equals("klickReagieren")) {
                aufgabenKlick.add(new KlickAuftrag(client, methoden[i], linksklick));
                return;
            }
        }
    }

    public void addEduKeyListener (Object o) {
        Class<?> klasse = o.getClass();
        Method[] methoden = klasse.getMethods();
        for (int i = 0; i < methoden.length; i++) {
            if (methoden[i].getName().equals("tasteReagieren")) {
                aufgaben.add(new TastenAuftrag(o, methoden[i]));
                return;
            }
        }
    }

    public void addEduTicker (Object o, int intervall) {
        Class<?> klasse = o.getClass();
        Method[] methoden = klasse.getMethods();
        for (int i = 0; i < methoden.length; i++) {
            if (methoden[i].getName().equals("tick")) {
                aufgabenT.add(new TickerAuftrag(o, methoden[i], intervall));
                return;
            }
        }
    }

    public void removeEduTicker(Object o) {
        ArrayList<TickerAuftrag> toRemove = new ArrayList<>();
        for(TickerAuftrag ta : aufgabenT) {
            if(ta.client.equals(o)) toRemove.add(ta);
        }
        for(TickerAuftrag tr : toRemove) aufgabenT.remove(tr);
    }

    /* EA Listener Implementation */

    @Override
    public void onFrameUpdate(int frameDuration) {
        for(TickerAuftrag ta : aufgabenT) {
            ta.accountFrame(frameDuration);
        }
    }

    @Override
    public void onKeyDown(int key) {
        for(TastenAuftrag ta : aufgaben) {
            ta.ausfuehren(key);
        }
    }

    @Override
    public void onKeyUp(int code) {
        //Ignore.
    }

    @Override
    public void onMouseDown(Vector position, MouseButton button) {
        for(KlickAuftrag ka : aufgabenKlick) {
            if(ka.linksklick && button==MouseButton.LEFT) {
                ka.ausfuehren((int)position.x, (int)position.y);
            } else if(!ka.linksklick && button==MouseButton.RIGHT) {
                ka.ausfuehren((int)position.x, (int)position.y);
            }
        }
    }

    @Override
    public void onMouseUp(Vector position, MouseButton button) {
        // Ignore.
    }

    /* ~~~ Listener CLASSES ~~~ */

    /**
     * Ein TickerAuftrag regelt je einen Fake-Ticker.
     */
    private static final class TickerAuftrag {

        /**
         * Das Intervall
         */
        private final int intervall;

        private int counter;

        /**
         * Der Client, an dem der Tick aufgerufen wird
         */
        private final Object client;

        /**
         * Die aufzurufende TICK-MEthode
         */
        private final Method methode;

        public TickerAuftrag(Object client, Method tick, int intervall) {
            this.intervall = intervall;
            this.counter = intervall;
            this.client = client;
            methode = tick;
        }

        /**
         * Frameweise Abarbeitung
         */
        public final void accountFrame(int millis) {
            counter -= millis;
            if(counter > 0) return;
            try {
                methode.invoke(client, new Object[0]);
            } catch (InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
            while (counter <= 0) counter +=intervall;
        }

        /**
         * @return Das Intervall des gelagerten Objektes
         */
        public int intervall() {
            return intervall;
        }

        /**
         * @return Das Objekt, das als "Client"-Ticker immer wieder aufgerufen wird.
         */
        public Object client() {
            return client;
        }
    }

    /**
     * Ein TastenAuftrag regelt den Aufruf eines TastenReaktions-Interface.
     */
    private static final class TastenAuftrag {

        /**
         * Die aufzurufende Methode
         */
        private final Method methode;

        /**
         * Das Objekt, an dem diese Methode ausgefuehrt werden soll!
         */
        private final Object client;

        /**
         * Erstellt einen Tastenauftrag
         *
         * @param client
         * 		Das Objekt, an dem der Job ausgefuehrt werden soll.
         * @param m
         * 		Die auszufuehrende Methode.
         */
        public TastenAuftrag (Object client, Method m) {
            this.client = client;
            methode = m;
        }

        /**
         * Führt die Methode einmalig aus.
         *
         * @param code
         * 		Der Tastaturcode, der mitgegeben wird.
         */
        public void ausfuehren (int code) {
            try {
                methode.invoke(client, code);
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (java.lang.IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Auftrag für einen Klick-Listener
     */
    private final class KlickAuftrag {
        private final Method methode;

        private final Object client;

        private final boolean linksklick;

        private KlickAuftrag (Object c, Method m, boolean linksklick) {
            methode = m;
            client = c;
            this.linksklick = linksklick;
        }

        /**
         * Führt die Methode am Client aus.
         *
         * @param x
         * 		Die zu uebergebene X-Koordinate des Klicks.
         * @param y
         * 		Die zu uebergebene Y-Koordinate des Klicks.
         */
        private void ausfuehren (int x, int y) {
            try {
                methode.invoke(client, new Object[] {x, y});
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (java.lang.IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}

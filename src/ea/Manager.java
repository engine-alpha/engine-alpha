/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2017 Michael Andonie and contributors.
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

package ea;

import ea.internal.util.Logger;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * <p>Der Manager ist eine Standardklasse und eine der wichtigsten der Engine Alpha, die zur
 * Interaktion außerhalb der Engine benutzt werden kann.</p>
 * <p>Neben einer Liste aller möglichen Fonts handelt er auch das <b>Ticker-System</b>. Dies ist ein
 * relativ konsistentes System, das viele <b><code>Ticker</code></b>-Objekte - Interfaces mit einer
 * Methode, die in immergleichen Abständen immer wieder aufgerufen werden.</p>
 * <p>Bewusst leitet sich diese Klasse nicht von <code>Thread</code> ab. Hierdurch kann ein Manager
 * ohne großen Ressourcenaufwand erstellt werden, wobei der Thread (und damit Computerrechenzeit)
 * erst mit dem aktiven Nutzen erstellt wird.</p>
 *
 * @author Michael Andonie
 * @author Niklas Keller <me@kelunik.com>
 * @see Ticker
 */
public class Manager {
    /**
     * <p>Der Standard-Manager.</p>
     * <p>Dieser wird nur innerhalb des "ea"-Paketes-verwendet!</p>
     * <p>Er ist der Manager, der verschiedene Ticker-Bedürfnisse von einzelnen internen Klassen
     * deckt und seine Fassung ist exakt an der Anzahl der nötigen Ticker angeglichen.</p>
     * <p>Dieser ist für:
     * <ul>
     * <li>Die Fensterkontrollroutine</li>
     * <li>Die Kollisionskontrollroutine der Klasse <code>Physik</code></li>
     * <li>Die Figurenanimationsroutine</li>
     * <li>Die Leuchtanimationsroutine</li>
     * </ul>
     * </p>
     */
    @SuppressWarnings ( "StaticVariableOfConcreteClass" )
    public static final Manager standard = new Manager();

    /**
     * Alle möglichen Fontnamen des Systems, auf dem man sich gerade befindet.<br /> Hiernach werden
     * Überprüfungen gemacht, ob die gewünschte Schriftart auch auf dem hiesigen System vorhanden
     * ist.
     */
    public static final String[] fontNamen;

    static {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        fontNamen = ge.getAvailableFontFamilyNames();
    }

    /**
     * Prüft, ob ein Font auf diesem Computer existiert.
     *
     * @param name Der Name des zu ueberpruefenden Fonts
     *
     * @return <code>true</code>, falls der Font auf dem System existiert, sonst <code>false</code>
     */
    public static boolean fontExistiert(String name) {
        for (String s : fontNamen) {
            if (s.equals(name)) {
                return true;
            }
        }

        return false;
    }

    private final ScheduledExecutorService executor;
    private final Map<Ticker, ScheduledFuture<?>> jobs;
    private int activeJobs = 0;

    /**
     * Vereinfachter Konstruktor ohne Parameter.<br /> Bei einem normalen Spiel muss nicht extra ein
     * Manager erstellt werden. Dafür gibt es bereits eine Referenz<br /> <br /> <code>public final
     * Manager manager;</code><br /> <br /> in der Klasse <code>Game</code> und damit auch in jeder
     * spielsteurnden Klasse.
     */
    public Manager() {
        this.executor = Executors.newScheduledThreadPool(10);
        this.jobs = new HashMap<>();
    }

    /**
     * Konstruktor eines Managers.<br /> Bei einem normalen Spiel muss nicht extra ein Manager
     * erstellt werden. Dafuer gibt es bereits eine Referenz<br /> <br /> <code>public final Manager
     * manager;</code><br /> <br /> in der Klass <code>Game</code> und damit auch in jeder
     * spielsteurnden Klasse.
     *
     * @param name Der Name, den der Thread haben wird, über den dieser Manager läuft.<br />
     *                Inzwischen ignoriert.
     *
     * @see #Manager()
     * @deprecated Nutze {@link Manager#Manager()} stattdessen, der Name wird inzwischen ignoriert.
     */
    @Deprecated
    public Manager(String name) {
        this();
    }

    /**
     * Diese Methode prüft, ob zur Zeit <b>mindestens 1 Ticker</b> an diesem Manager ausgeführt
     * wird.
     *
     * @return <code>true</code>, wenn mindestens 1 Ticker an diesem Manager zur Zeit mit seiner
     * <code>tick()</code>-Methode ausgeführt wird. Sonst <code>false</code>.
     */
    public synchronized boolean hatAktiveTicker() {
        return this.activeJobs > 0;
    }

    /**
     * Meldet einen Ticker am Manager an. Ab sofort läuft er auf diesem Manager und damit wird auch
     * dessen <code>tick()</code>-Methode immer wieder aufgerufen.
     *
     * @param t Der anzumeldende Ticker
     *
     * @see #anmelden(Ticker)
     */
    public synchronized void anmelden(Ticker t, int intervall) {
        anmelden(t);
        starten(t, intervall);
    }

    /**
     * Macht diesem Manager einen Ticker bekannt, <b>OHNE</b> ihn aufzurufen.
     *
     * @param t Der anzumeldende Ticker
     *
     * @see #anmelden(Ticker, int)
     */
    public synchronized void anmelden(Ticker t) {
        if (istAngemeldet(t)) {
            Logger.warning("Der Ticker ist bereits an diesem Manager angemeldet und wird nicht erneut angemeldet.");
            return;
        }

        jobs.put(t, null);

        if (EngineAlpha.isDebug()) {
            Logger.info("Ticker wurde angemeldet: " + t.toString());
        }
    }

    /**
     * Startet einen Ticker, der <b>bereits an diesem Manager angemeldet ist</b>.<br /> Läuft der
     * Ticker bereits, passiert gar nichts. War der Ticker nicht angemeldet, kommt eine
     * Fehlermeldung.
     *
     * @param ticker         Der zu startende, <b>bereits am <code>Manager</code> angemeldete</b>
     *                  Ticker.
     * @param intervall Das Intervall im ms<sup>-1</sup>, in dem dieser Ticker ab sofort immer
     *                  wieder aufgerufen wird.
     *
     * @see #anhalten(Ticker)
     */
    public synchronized void starten(final Ticker ticker, int intervall) {
        if (!istAngemeldet(ticker)) {
            Logger.error("Der Ticker ist noch nicht angemeldet.");
            return;
        }

        if (jobs.get(ticker) != null) {
            Logger.error("Ticker ist bereits am Laufen!");
            return;
        }

        Runnable r = new Runnable() {
            @Override
            public void run() {
                // Otherwise these exceptions don'ticker show up anywhere!
                try {
                    ticker.tick();
                } catch (RuntimeException e) {
                    Logger.error("Kritischer Fehler im Ticker: Eine Ausnahme wurde nicht abgefangen. Der Ticker wurde angehalten. Die folgende StackTrace sollte dir weitere Informationen liefern.");
                    e.printStackTrace();
                    anhalten(ticker);
                }
            }
        };

        this.jobs.put(ticker, executor.scheduleAtFixedRate(r, intervall, intervall, TimeUnit.MILLISECONDS));
        this.activeJobs++;
    }

    /**
     * Prüft, ob ein Ticker t bereits angemeldet ist.
     *
     * @param t Der zu prüfende Ticker.
     *
     * @return <code>true</code>, falls der Ticker bereits an diesem <code>Manager</code> angemeldet
     * ist, sonst <code>false</code>.
     */
    public synchronized boolean istAngemeldet(Ticker t) {
        return this.jobs.containsKey(t);
    }

    /**
     * Hält einen Ticker an, der <b>bereits an diesem Manager angemeldet ist</b>.<br /> Ist der
     * Ticker bereits angehalten, passiert gar nichts. War der Ticker nicht angemeldet, kommt eine
     * Fehlermeldung.
     *
     * @param ticker Der anzuhaltende Ticker
     *
     * @see #starten(Ticker, int)
     */
    public synchronized void anhalten(Ticker ticker) {
        if (!istAngemeldet(ticker)) {
            Logger.error("Der Ticker ist noch nicht angemeldet.");
            return;
        }

        final ScheduledFuture<?> future = this.jobs.get(ticker);

        if (future != null) {
            this.jobs.get(ticker).cancel(false);
            this.jobs.put(ticker, null);
            this.activeJobs--;
        }

        if (EngineAlpha.isDebug()) {
            Logger.info("Ticker wurde angehalten: " + ticker.toString());
        }
    }

    /**
     * Meldet einen Ticker ab.<br /> War dieser Ticker nicht angemeldet, so passiert nichts &ndash;
     * außer einer Fehlermeldung.
     *
     * @param ticker abzumeldender Ticker
     */
    public synchronized void abmelden(Ticker ticker) {
        if (!istAngemeldet(ticker)) {
            Logger.error("Der Ticker ist noch nicht angemeldet.");
            return;
        }

        if (this.jobs.get(ticker) != null) {
            anhalten(ticker);
        }

        this.jobs.remove(ticker);

        if (EngineAlpha.isDebug()) {
            Logger.info("Ticker wurde abgemeldet: " + ticker.toString());
        }
    }

    /**
     * Beendet den Thread, den dieser Manager verwendet und damit den Manager selbst. Sollte
     * <b>nur</b> aufgerufen werden, wenn der Manager selbst gelöscht werden soll.
     */
    public final synchronized void kill() {
        alleAbmelden();
    }

    /**
     * Macht diesen Manager frei von allen aktiven Tickern, jedoch ohne ihn selbst zu beenden. Neue
     * Ticker können jederzeit wieder angemeldet werden.
     */
    public final synchronized void alleAbmelden() {
        for (Ticker ticker : jobs.keySet()) {
            if (jobs.get(ticker) != null) {
                anhalten(ticker);
            }
        }
    }
}

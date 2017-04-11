package ea;

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.phy.WorldHandler;
import ea.internal.util.Logger;

/**
 * Jedes <code>Game</code> hat eine Referenz auf ein Objekt dieser Klasse. Sie verwaltet
 * hierfür <b>alle Methoden zum "Weiterreichen" von allen Interfaces, die an die Core-Engine
 * gereicht werden sollen.</b>.
 * Created by andonie on 06.09.15.
 */
public class Anmelden {

    /**
     * Referenz auf das Game, für das dieses Objekt Anmeldemethoden bereitstellt.
     */
    private final Game game;

    /**
     * Erstellt ein Anmelden-Objekt.
     * @param game  Das Game-Objekt, um das sich dieses Anmelden-Objekt kümmern soll.
     */
    @NoExternalUse
    Anmelden(Game game) {
        this.game = game;
    }

    /**
     * Meldet ein <code>TastenReagierbar</code>-Objekt an. Ab diesem Moment wird seine
     * <code>reagieren</code>-Methode immer dann aufgerufen, wenn eine Taste heruntergedrueckt
     * wird.
     *
     * @param tastenReagierbar
     * 		Das anzumeldende <code>TastenReagierbar</code>-Objekt.
     */
    @API
    public void tastenReagierbar (TastenReagierbar tastenReagierbar) {
        if(tastenReagierbar instanceof Game) {
            Logger.error("ea.Anmelden", "Der Eingabe-Parameter tastenReagierbar leitet sich von der Klasse Game ab. Das würde für einen"
                    + " internen Fehler sorgen und ist daher nicht möglich. Stattdessen kann man die tasteReagieren-"
                    + "Methode verwenden oder über eine andere mit diesem Interface den selben Effekt erzeugen.");
            return;
        }
        game.real_fenster.tastenReagierbarAnmelden(tastenReagierbar);
    }

    /**
     * Meldet ein <code>TastenLosgelassenReagierbar</code>-Objekt an. Ab diesem Moment wird seine
     * <code>tasteGedrueckt</code>-Methode immer aufgerufen, wenn eine Taste losgelassen wird.
     *
     * @param g
     * 		Das anzumeldende <code>TastenLosgelassenReagierbar</code>-Objekt.
     */
    @API
    public void tastenLosgelassenReagierbar (TastenLosgelassenReagierbar g) {
        game.real_fenster.tastenLosgelassenReagierbarAnmelden(g);
    }

    /**
     * Meldet einen Ticker an. Nach Ausführung dieser Methode wird die <code>tick</code>-Methode
     * dieses Tickers regelmäßig und unerlässlich im angegebenen Intervall ausgeführt.
     * @param ticker		Der Ticker, dessen <code>tick</code>-Methode ab sofort regelmäßig
     * 						ausgeführt werden soll.
     * @param intervall		Das Intervall (<i>in Millisekunden</i>), in dem die <code>tick</code>-Methode
     * 						des angegebenen Tickers ausgeführt werden soll. Zwischen zwei
     * 						<code>tick</code>-Aufrufen vergehen <code>intervall</code> Millisekunden.
     * @see #tickerAbmelden(Ticker)
     * @see ea.Ticker
     */
    @API
    public void ticker(Ticker ticker, int intervall) {
        game.real_fenster.getFrameThread().tickerAnmelden(ticker, intervall);
    }

    /**
     * Meldet einen (aktiven) Ticker ab. Nach ausführen dieser Methode wird die <code>tick</code>-Methode
     * des übergebenen Ticker-Objekts nicht mehr ausgeführt, bis der Ticker erneut angemeldet wird.
     * @param ticker		Der Ticker, dessen <code>tick</code>-Methode ab sofort nicht mehr ausgeführt
     * 						werden soll. Ist dieser Ticker noch gar nicht angemeldet, wird eine Fehlermeldung
     * 						ausgegeben.
     * @see #ticker(Ticker, int)
     * @see ea.Ticker
     */
    @API
    public void tickerAbmelden(Ticker ticker) {
        game.real_fenster.getFrameThread().tickerAbmelden(ticker);
    }

    /**
     * Meldet ein <code>KollisionsReagierbar</code>-Interface für eine <b>spezifische Kollision zwischen
     * zwei spezifischen Raum-Objekten</b> an.
     *
     * @param reagierbar
     * 		Das anzumeldende <code>KollisionsReagierbar</code>-Interface, das ab sofort von
     * 		allen Kollisionen zwischen <code>actor</code> und <code>collider</code> informiert werden soll.
     * @param actor
     * 		Ein <code>Raum</code>-Objekt. In der logischen "Actor-Rolle".
     * @param collider
     * 		Ein zweites <code>Raum</code>-Objekt. In der logischen "Collider-Rolle"
     * @see #kollisionsReagierbar(KollisionsReagierbar, Raum)
     * TODO: Tutorial einbinden
     */
    @API
    public <E extends Raum> void kollisionsReagierbar (KollisionsReagierbar<E> reagierbar, Raum actor, E collider) {
        WorldHandler.spezifischesKollisionsReagierbarEingliedern(reagierbar, actor, collider);
    }

    /**
     * Meldet ein <code>KollisionsReagierbar</code>-Interface für eine <b>alle Kollisionen eines bestimmten
     * <code>Raum</code>-Objektes</b> an.
     * @param reagierbar
     *      Das anzumeldende <code>KollisionsReagierbar</code>-Interface, das ab sofort von
     * 		allen Kollisionen von <code>actor</code> informiert werden soll.
     * @param actor
     *      Ein <code>Raum</code>-Objekt. In der logischen "Actor-Rolle".
     * @see #kollisionsReagierbar(KollisionsReagierbar, Raum, Raum)
     * TODO: Tutorial einbinden
     */
    @API
    public void kollisionsReagierbar (KollisionsReagierbar<Raum> reagierbar, Raum actor) {
        WorldHandler.allgemeinesKollisionsReagierbarEingliedern(reagierbar, actor);
    }

    /**
     * Meldet ein <code>FrameUpdateReagierbar</code>-Objekt an.<br />
     * Nach Aufruf dieser Methode wird die <code>frameUpdate(float)</code>-Methode des
     * übergebenen Objektes in jedem Frame aufgerufen.
     * @param reagierbar    Das anzumeldende <code>FrameUpdateReagierbar</code>-Objekt.
     */
    @API
    public void frameUpdateReagierbar(FrameUpdateReagierbar reagierbar) {
        game.real_fenster.getFrameThread().frameUpdateReagierbarAnmelden(reagierbar);
    }

    /**
     * Meldet ein <code>FrameUpdateReagierbar</code>-Objekt ab.<br />
     * Nach Aufruf dieser Methode wird die <code>frameUpdate(float)</code>-Methode des
     * übergebenen Objektes nicht mehr in jedem Frame aufgerufen.
     * @param reagierbar    Das abzumeldende <code>FrameUpdateReagierbar</code>-Objekt.
     */
    @API
    public void frameUpdateReagierbarAbmelden(FrameUpdateReagierbar reagierbar) {
        game.real_fenster.getFrameThread().frameUpdateReagierbarAbmelden(reagierbar);
    }
}

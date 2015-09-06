package ea;

import ea.internal.ano.API;
import ea.internal.ano.NoExternalUse;
import ea.internal.phy.WorldHandler;
import ea.internal.util.Logger;

/**
 * Jedes <code>Game</code> hat eine Referenz auf ein Objekt dieser Klasse. Sie verwaltet
 * hierfür <b>alle Methoden zum Anmelden von Reagierbar-Instanzen</b>.
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
     * Meldet ein <code>KollisionsReagierbar</code>-Interface an. Ab sofort wird es mit dem
     * spezifizierten <code>code</code> aufgerufen, sollten sich die <code>Raum</code>-Objekte
     * <code>r1</code> und <code>r2</code> schneiden.
     *
     * @param reagierbar
     * 		Das anzumeldende <code>KollisionsReagierbar</code>-Interface, das ab sofort von Kollisionen
     * 		von <code>r1</code> und <code>r2</code> informiert werden soll.
     * @param r1
     * 		Ein <code>Raum</code>-Objekt
     * @param r2
     * 		Ein zweites <code>Raum</code>-Objekt
     * @param code
     * 		Ein beliebiger <code>int</code>-Wert als Code. Dieser kann verwendet werden, um mit einem Interface mehrere
     * 		Kollisionen <i>unterscheidbar</i> zu behandeln. Er wird im Aufruf der
     * 		<code>kollision(int)</code>-Methode als Parameter übergeben, wenn es sich bei der
     * 	   	Kollision um <code>r1</code> und <code>r2</code> handelt.
     */
    @API
    public void kollisionsReagierbar (KollisionsReagierbar reagierbar, Raum r1, Raum r2, int code) {
        WorldHandler.kollisionsReagierbarEingliedern(reagierbar, code, r1, r2);
    }
}

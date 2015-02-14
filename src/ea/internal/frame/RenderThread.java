package ea.internal.frame;

import java.awt.*;

/**
 * Ein <code>Render-Thread</code> führt beim Start (einmalig) seine Render-Routine aus.
 * Created by andonie on 14.02.15.
 */
public class RenderThread
extends Thread {

    /**
     * Das Graphics-Objekt, das (dauerhaft) zum Zeichnen verwendet wird.
     */
    private final Graphics2D graphics2D = null;

    public RenderThread() {
        //
    }

    /**
     * Führt <i>einen einzelnen Frame-Render-Auftrag</i> aus. Danach ist der Thread fertig.
     */
    @Override
    public void run() {

    }
}

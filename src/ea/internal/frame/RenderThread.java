package ea.internal.frame;

import ea.internal.gra.Zeichner;

import java.awt.*;
import java.awt.image.BufferStrategy;

/**
 * Ein <code>Render-Thread</code> führt beim Start (einmalig) seine Render-Routine aus.
 * Created by andonie on 14.02.15.
 */
public class RenderThread
extends FrameSubthread {

    /**
     * Counter für Anzahl an aktiven Render-Threads (f. Multi-Window)
     */
    private static int rtcnt = 1;

    /**
     * Das Graphics-Objekt, das (dauerhaft) zum Zeichnen verwendet wird.
     */
    private final Graphics2D graphics2D;

    /**
     * Die BufferStrategy zum Canvas.
     */
    private final BufferStrategy bufferStrategy;

    /**
     * Der Zeichner, der für das high-level Rendering verantwortlich ist.
     */
    private final Zeichner zeichner;

    /**
     * Erstellt einen Render-Thread.
     * @param zeichner  Das <code>Zeichner-Objekt</code>, dass alle relevanten Informationen für das Rendering
     *                  enthält.
     */
    public RenderThread(FrameThread master, Zeichner zeichner) {
        super(master, "Rendering Thread #" + rtcnt++);
        this.setDaemon(true);

        this.zeichner = zeichner;
        graphics2D = zeichner.getG();
        bufferStrategy = zeichner.getBs();
    }

    /**
     * Führt <i>einen einzelnen Frame-Render-Auftrag</i> aus. Danach ist der Thread fertig.
     */
    @Override
    public void frameLogic() {
        zeichner.render(graphics2D);
        try {
            bufferStrategy.show();
        } catch(IllegalStateException e) {
            e.printStackTrace();
        }
    }
}

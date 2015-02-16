package ea.internal.phy;

import ea.*;

import ea.internal.ano.NoExternalUse;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import sun.misc.Version;

/**
 * Die Physik-Klasse ist die (nicht objektgebundene) Middleware zwischen der JBox2D Engine und der EA.
 * Sie ist verantwortlich für:
 * <ul>
 *     <li>Den globalen "World"-Parameter aus der JBox2D Engine.</li>
 *     <li>Translation zwischen JB2D-Vektoren (SI-Basiseinheiten) und denen der Engine (Zeichengrößen)</li>
 * </ul>
 * Created by andonie on 14.02.15.
 */
public class Physik {

    /**
     * Die World dieser Physik. Hierin laufen globale Einstellungen (z.B. Schwerkraft) ein.
     */
    private final World world;

    /**
     * Umrechnungsgröße zwischen Größen der Physik-Engine und der Zeichenebene der EA.
     * Gibt an, wie viele Pixel genau einen Meter ausmachen.<br/>
     *
     * <b>Einheit: [px/m]</b>
     */
    private float pixelProMeter = 30f;

    /**
     * Flag, das angibt, ob die Pixel Pro Meter bereits angefragt wurden.
     */
    private boolean ppmRequested;

    /**
     * Gibt die Umrechnungsgröße zwischen Größen der Physik-Engine und der Zeichenebene der EA an.
     * @return  Gibt an, wie viele Pixel genau einen Meter ausmachen.<br />
     *          <b>Einheit: [px/m]</b>
     */
    public float getPixelProMeter() {
        ppmRequested = true;
        return pixelProMeter;
    }

    /**
     * Setzt die Umrechnungsgröße zwischen Größen der Physik-Engine und der Zeichenebene der EA.
     * @param pixelProMeter Die Anzahl an Pixeln, die genau einen Meter ausmachen.<br />
     *                      <i>Die Größe ist unabhängig vom Kamerazoom.</i>< br/>
     *                      <b>Einheit: [px/m]</b>
     */
    public void setPixelProMeter(float pixelProMeter) {
        if(ppmRequested)
            throw new IllegalStateException("Die Pixel-Pro-Meter Umrechnungszahl darf nach Arbeit mit den Raum-Objekten" +
                    " der entsprechenden Physik-Umgebung nicht geändert werden. Das Setzen der Konstante vor" +
                    " die Arbeit mit den Raum-Objekten verschieben.");
        this.pixelProMeter = pixelProMeter;
    }

    /**
     * Erstellt eine neue standardisierte Physik (Schwerkraft senkrecht nach unten, 9,81 m/s^2)
     */
    @NoExternalUse
    public Physik() {
        this.world = new World(new Vec2(0f, -9.81f)); //Erstelle standard-World mit Standard-Gravitation.
    }

    /**
     * Gibt den World-Parameter der Physik aus.
     * @return  Der JB2D-World-Parameter der Welt.
     */
    @NoExternalUse
    public World getWorld() {
        return world;
    }

    /**
     * Übersetzt einen EA-Vektor in einen JB2D-Vektor auf Basis des gesetzten Pixel/Meter-Verhältnisses.
     * @param eaV   Ein EA-Vektor.
     * @return      Der analoge Vektor in der JB2D-Engine.
     */
    @NoExternalUse
    public Vec2 fromVektor(Vektor eaV) {
        float x = eaV.x / pixelProMeter;
        float y = eaV.y / pixelProMeter;
        return new Vec2(x,y);
    }

    /**
     * Übersetzt einen JB2D-Vektor in einen EA-Vektor auf Basis des gesetzten Pixel/Meter-Verhältnisses.
     * @param jb2dV Ein JB2D-Vektor.
     * @return      Der analoge Vektor im EA-Format auf der Zeichenebene.
     */
    @NoExternalUse
    public Vektor fromVec2(Vec2 jb2dV) {
        float x = jb2dV.x * pixelProMeter;
        float y = jb2dV.y * pixelProMeter;
        return new Vektor(x,y);
    }
}

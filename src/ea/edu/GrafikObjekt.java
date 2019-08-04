package ea.edu;

import ea.actor.Actor;

/**
 * Hifs-Interface zum Wrappen eines Actors in BlueJ, ohne die volle (und englische) Methodenvielfalt sichtbar zu machen.
 * Wird von allen EDU Grafikklassen implementiert.
 */
public interface GrafikObjekt {
    /**
     * Gibt den zugehörigen, originalen Engine Actor aus.
     *
     * @return Das Actor Objekt, das tatsächlich durch das Grafikobjekt gerendert wird.
     */
    Actor getActor();
}

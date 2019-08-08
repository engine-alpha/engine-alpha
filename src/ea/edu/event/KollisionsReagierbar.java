package ea.edu.event;

import ea.edu.EduActor;
import ea.internal.annotations.API;

public interface KollisionsReagierbar {

    /**
     * Reagiert auf eine Kollision zwischen zwei EDU Actors. Zwischen dem EduActor, an dem dieses Interface angemeldet
     * wurde und einem weiteren EduActor <code>actor</code>, der als Parameter mitgegeben wird.
     *
     * @return Default ist <code>true</code>.
     */
    @API
    boolean kollisionReagieren(EduActor actor);
}

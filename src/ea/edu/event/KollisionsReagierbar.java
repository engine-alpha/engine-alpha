package ea.edu.event;

import ea.edu.EduActor;
import ea.internal.annotations.API;

public interface KollisionsReagierbar {

    /**
     * Reagiert auf eine Kollision zwischen zwei EDU Actors. Zwischen dem EduActor, an dem dieses Interface angemeldet
     * wurde und einem weiteren EduActor <code>actor</code>, der als Parameter mitgegeben wird.
     *
     * @return Gibt zurück, ob die Kollision aufgelöst werden soll. Default ist <code>true</code>. Ist die Rückgabe
     * <code>false</code>, so wird die Kollision von der Physics-Engine ignoriert.
     */
    @API
    boolean kollisionReagieren(EduActor actor);
}

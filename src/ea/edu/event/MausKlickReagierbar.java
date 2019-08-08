package ea.edu.event;

import ea.internal.annotations.API;

@API
public interface MausKlickReagierbar {

    @API
    void klickReagieren(float mx, float my);

    @API
    default void klickLosgelassenReagieren(float mx, float my) {
        // default empty
    }
}

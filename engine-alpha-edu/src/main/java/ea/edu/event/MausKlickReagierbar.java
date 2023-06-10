package ea.edu.event;

import ea.internal.annotations.API;

@API
public interface MausKlickReagierbar {

    @API
    void klickReagieren(double mx, double my);

    @API
    default void klickLosgelassenReagieren(double mx, double my) {
        // default empty
    }
}

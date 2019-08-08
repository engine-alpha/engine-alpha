package ea.edu.event;

import ea.internal.annotations.API;

@API
public interface TastenReagierbar {
    @API
    void tasteReagieren(int tastenCode);

    @API
    default void tasteLosgelassenReagieren(int tastenCode) {
        // default empty
    }
}

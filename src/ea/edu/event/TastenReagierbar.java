package ea.edu.event;

public interface TastenReagierbar {
    void tasteReagieren(int tastenCode);

    default void tasteLosgelassenReagieren(int tastenCode) {
        // default empty
    }
}

package ea.edu;

/**
 * Das Interface, das sämtliche grafischen EDU-Enduser-Klassen implementieren. Dies ermöglicht
 * Interoperabilität wesentlicher Funktionen mit eigentlichen <code>Actor</code>-Parametern
 * (Kollision-Checks, Abstände, etc.) ohne dass der für den Schüler erkennbare Funktionsumfang zu
 * unübersichtlich wird.
 */
public interface GrafikObjekt {

    /**
     * Gibt einen EDU-Actor zurück.
     * @return  Der EDU-Actor, der gerade vom entsprechenden EDU-Objekt gewrappt wird.
     */
    EduActor getActor();
}

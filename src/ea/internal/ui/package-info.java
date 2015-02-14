/**
 * Dieses Package generalisiert UI-Events. Die zentrale Klasse hierin ist die abstrakte Klasse
 * <code>UIEvent</code>. Ein UI-Event ist zum Beispiel:
 * <ul>
 *     <li>Der User macht einen Mausklick.</li>
 *     <li>Der User drückt auf eine Taste.</li>
 *     <li>Der User drückt auf eine bestimmte Stelle auf einem Touchscreen.</li>
 *     <li>Der User bewegt die Maus.</li>
 * </ul>
 *
 * Für jeden designierten, unterscheidbaren UI-Eventtyp gibt es eine <b>UI-Event-Klasse</b>, die sich aus der
 * Basisklasse <code>UIEvent</code> ableitet. Analog gibt es für jeden UI-Eventtyp auch ein eigenes
 * <i>Reagierbar-Interface</i>, das dem API-User die Möglichkeit gibt, auf einen bestimmten UI-Eventtyp zu reagieren.<br />
 * Ein <code>ProducerThread</code> mit UIEvent-Signatur ist innerhalb der Engine für die frameweise Vorbereitung der UI-Events verantwortlich,
 * der <code>DispatcherThread</code> für deren (streng) sequentielle Abararbeitung.
 * @see ea.internal.frame.ProducerThread
 * @see ea.internal.frame.DispatcherThread
 * Created by andonie on 14.02.15.
 */
package ea.internal.ui;
/**
 * <p>Dieses Paket implementiert eine minimal einfache Umgebung in der Engine, die es einem API-Anwender mit wenig bis
 * keinen Vorkenntnissen in Java, Programmierung und Softwaredesign ermöglichen, Funktionen der Engine zu nutzen.
 * Diese Version ist die EDU-Variante der Engine Alpha.</p>
 * <p>
 *     Die EDU-Variante ermöglicht die Nutzung der Engine ohne Kenntnisse über (unter Anderem) die folgenden Konzepte:
 *     <ul>
 *         <li>Frame-weise Abarbeitung einer Spielumgebung</li>
 *         <li>Nebenläufigkeiten und Parallelität</li>
 *         <li>Vererbung</li>
 *         <li>Gängige Software-Muster, unter anderem:
 *         <ul>
 *             <li>Observer (bzw. Listener)</li>
 *             <li>Model-View-Control</li>
 *             <li>Adapter</li>
 *             <li>Fassade</li>
 *             <li>Strategy</li>
 *         </ul>
 *         </li>
 *         <li>IO</li>
 *         <li>Collisions, Collider, Bounds, etc.</li>
 *         <li>Programmieren in englischer Sprache</li>
 *     </ul>
 * </p>
 * <p>Die EDU-Version wird als <b>Plug-And-Play-Lösung</b> in <a href="https://www.bluej.org/">BlueJ</a> angeboten.
 * Es ist keine Nachinstallation von Dependecies nötig: Das <a href="https://github.com/engine-alpha/edu-klassen">
 * auf Github gehostete BlueJ-Projekt</a> funktioniert umgehend.</p>
 * <p>In diesem Paket wird die Implementierung der hierzu nötigen Vereinfachungen umgesetzt. Sämtliche Funktionen
 * sind durch <b>Fassaden-Klassen</b> im BlueJ-Projekt verfügbar.</p>
 * @author Michael Andonie
 */
package ea.edu;
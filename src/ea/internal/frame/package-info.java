/**
 * Dieses Modul beinhaltet die zentrale <b>Frame-Logik</b>. Diese stellt die <i>frameweise Abarbeitung</i> aller
 * relevanten Spielaspekte sicher. Sie verhindert <i>asynchrone Objektveränderung</i> und ermöglicht <i>möglichst
 * effektive Parallelisierung</i>.<br />
 *
 * Die zentrale (high-level) Implementierung der frameweisen Abarbeitung findet sich in der Klasse
 * <code>FrameThread</code>. Diese ist der Ausgangspunkt der Frame-Arbeit.
 *
 * Created by andonie on 14.02.15.
 * @see ea.internal.frame.FrameThread
 */
package ea.internal.frame;
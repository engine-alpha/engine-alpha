package ea;

/**
 * Sorgt dafür, dass auf Mausbewegungen reagiert werden kann.
 *
 * @author Niklas Keller <me@kelunik.com>
 */
public interface MausBewegungReagierbar {
	/**
	 * Wird immer aufgerufen, wenn die Maus bewegt wurde.
	 *
	 * @param dx Delta-x
	 * @param dy Delta-y
	 */
	public void mausBewegt(int dx, int dy);
}

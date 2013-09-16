package ea.network;

public interface SenderInterface {

	/**
	 * Versendet einen String an den Kommunikationspartner.
	 * @param string Der String, der gesendet werden 
	 * 				soll.
	 */
	public abstract void sendeString(String string);

	/**
	 * Versendet einen Integer an den Kommunikationspartner.
	 * @param i Der int-Wert, der gesendet werden 
	 * 				soll.
	 */
	public abstract void sendeInt(int i);

	/**
	 * Versendet ein Byte an den Kommunikationspartner.
	 * @param b Das Byte, das gesendet werden 
	 * 				soll.
	 */
	public abstract void sendeByte(byte b);

	/**
	 * Versendet einen Double an den Kommunikationspartner.
	 * @param d Der double-Wert, der gesendet werden 
	 * 				soll.
	 */
	public abstract void sendeDouble(double d);

	/**
	 * Versendet einen Character an den Kommunikationspartner
	 * @param c Der char-Wert, der gesendet werden 
	 * 				soll.
	 */
	public abstract void sendeChar(char c);

	/**
	 * Versendet einen Booleschen Wert an den Kommunikationspartner
	 * @param b Der boolean-Wert, der gesendet werden 
	 * 				soll.
	 */
	public abstract void sendeBoolean(boolean b);

	/**
	 * Beendet die Verbindung. Nach dem Aufruf dieser Methode kann man keine
	 * Verbindung mehr aufbauen.
	 */
	public abstract void beendeVerbindung();

}
package ea.compat;

import ea.internal.util.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;

/**
 * Der Compat-Dateimanager implementiert die alten Lade-Methoden des Dateimanagers,
 * um den Dateimanager übersichtlicher zu halten - gleichzeitig aber alte Dateien
 * weiterhin korrekt zu laden.
 *
 * @author Niklas Keller, Michael Andonie
 */
public class CompatDateiManager {
	/**
	 * Liest eine <code>.eaa</code>-String-Array-Datei ein.
	 *
	 * @param pfad
	 *            Der Dateipfad, der sowohl das Verzeichnis wie auch den Dateinamen angibt.
	 *
	 *            Dieser sollte mit <code>.eaa</code> enden. Wenn nicht, wird dies
	 *            automatisch angehängt.
	 * @return Array, das eingelesen wurde oder <code>null</code>, wenn ein Fehler aufgetreten ist.
	 */
	public static String[] stringArrayEinlesen(String pfad) {
		String[] ret;

		try {
			String line;
			LineNumberReader f = new LineNumberReader(new FileReader(pfad));
			line = f.readLine();

			if (line.compareTo("typ:String") != 0) {
				System.err.println("Die geladene .eaa-Datei beschreibt kein String-Array oder ist beschädigt!");
				f.close();

				return null;
			}

			line = f.readLine();

			int length = Integer.valueOf(line.split(":")[1]);
			ret = new String[length];

			for (int i = 0; i < length; i++) {
				line = f.readLine();

				String[] split = line.split(":", 2);
				String erg;

				if (split[1].equals("%%")) {
					erg = null;
				} else if (split[1].equals("~~")) {
					erg = "";
				} else {
					erg = split[1];
				}

				ret[i] = erg;
			}

			f.close();

			return ret;
		} catch (IOException e) {
			Logger.error("Fehler beim Lesen der Datei. Existiert die Datei mit diesem Namen wirklich?\n" + pfad);
		}

		return null;
	}

	/**
	 * Liest eine <code>.eaa</code>-int-Array-Datei ein.
	 *
	 * @param pfad
	 *            Der Dateipfad, der sowohl das Verzeichnis wie auch den Dateinamen angibt.
	 *
	 *            Dieser sollte mit <code>.eaa</code> enden. Wenn nicht, wird dies
	 *            automatisch angehängt.
	 * @return Array, das eingelesen wurde oder <code>null</code>, wenn ein Fehler aufgetreten ist.
	 */
	public static int[] integerArrayEinlesen(String pfad) {
		LineNumberReader f = null;
		int[] ret = null;

		try {
			String line;
			f = new LineNumberReader(new FileReader(pfad));
			line = f.readLine();

			if (line.compareTo("typ:Integer") != 0) {
				System.err.println("Die geladene .eaa-Datei beschreibt kein int-Array oder ist beschädigt!");
				return null;
			}

			line = f.readLine();
			int length = Integer.valueOf(line.split(":")[1]);

			ret = new int[length];

			for (int i = 0; i < length; i++) {
				line = f.readLine();
				ret[i] = Integer.valueOf(line.split(":")[1]);
			}

			return ret;
		} catch (IOException e) {
			Logger.error("Fehler beim Lesen der Datei. Existiert die Datei mit diesem Namen wirklich?\n" + pfad);
		} finally {
			if(f != null) {
				try {
					f.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}
}

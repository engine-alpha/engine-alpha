/*
 * Engine Alpha ist eine anfängerorientierte 2D-Gaming Engine.
 *
 * Copyright (c) 2011 - 2014 Michael Andonie and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package ea.compat;

import ea.internal.ano.NoExternalUse;
import ea.internal.util.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Der Compat-Dateimanager implementiert die alten Methoden zum Laden von Dateien, um den
 * Dateimanager übersichtlicher zu halten - gleichzeitig aber alte Dateien weiterhin korrekt zu
 * laden zu können.
 *
 * @author Niklas Keller, Michael Andonie
 */
@NoExternalUse
public class CompatDateiManager {
	/**
	 * Liest eine <code>.eaa</code>-String-Array-Datei ein.
	 *
	 * @param pfad
	 * 		Der Dateipfad, der sowohl das Verzeichnis wie auch den Dateinamen angibt.
	 * 		<p/>
	 * 		Dieser sollte mit <code>.eaa</code> enden. Wenn nicht, wird dies automatisch angehängt.
	 *
	 * @return Array, das eingelesen wurde oder <code>null</code>, wenn ein Fehler aufgetreten ist.
	 */
	@NoExternalUse
	public static String[] stringArrayEinlesen (String pfad) {
		BufferedReader reader = null;
		String[] ret;

		try {
			// init
			String line;
			reader = new BufferedReader(new FileReader(pfad));
			line = reader.readLine();

			// check type info
			if (line.compareTo("typ:String") != 0) {
				Logger.error("IO", "Die geladene .eaa-Datei beschreibt kein String-Array oder ist beschädigt!");
				reader.close();

				return null;
			}

			// parse array length
			line = reader.readLine();
			int length = Integer.valueOf(line.split(":")[1]);

			// parse file data
			ret = new String[length];

			for (int i = 0; i < length; i++) {
				line = reader.readLine();

				String[] split = line.split(":", 2);
				String erg;

				switch (split[1]) {
					case "%%":
						erg = null;
						break;
					case "~~":
						erg = "";
						break;
					default:
						erg = split[1];
						break;
				}

				ret[i] = erg;
			}

			return ret;
		} catch (IOException e) {
			Logger.error("IO", "Fehler beim Lesen der Datei. Existiert die Datei mit diesem Namen wirklich?\n" + pfad);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	/**
	 * Liest eine <code>.eaa</code>-int-Array-Datei ein.
	 *
	 * @param pfad
	 * 		Der Dateipfad, der sowohl das Verzeichnis wie auch den Dateinamen angibt.
	 * 		<p/>
	 * 		Dieser sollte mit <code>.eaa</code> enden. Wenn nicht, wird dies automatisch angehängt.
	 *
	 * @return Array, das eingelesen wurde oder <code>null</code>, wenn ein Fehler aufgetreten ist.
	 */
	@NoExternalUse
	public static int[] integerArrayEinlesen (String pfad) {
		BufferedReader reader = null;
		int[] ret;

		try {
			// init
			String line;
			reader = new BufferedReader(new FileReader(pfad));
			line = reader.readLine();

			// check type info
			if (line.compareTo("typ:Integer") != 0) {
				Logger.error("IO", "Die geladene .eaa-Datei beschreibt kein int-Array oder ist beschädigt!");
				return null;
			}

			// parse array length
			line = reader.readLine();
			int length = Integer.valueOf(line.split(":")[1]);

			// parse file data
			ret = new int[length];

			for (int i = 0; i < length; i++) {
				line = reader.readLine();
				ret[i] = Integer.valueOf(line.split(":")[1]);
			}

			return ret;
		} catch (IOException e) {
			Logger.error("IO", "Fehler beim Lesen der Datei. Existiert die Datei mit diesem Namen wirklich?\n" + pfad);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}
}

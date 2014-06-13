/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
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

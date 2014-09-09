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

package ea.internal.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Logger für die Engine Alpha, damit Probleme bei Anwendern auch von Entwicklern nachvollzogen
 * werden können.
 *
 * @author Julien Gelmar <master@nownewstart.net>, Niklas Keller <me@kelunik.com>
 */
public class Logger {
	private static BufferedWriter writer;

	private Logger () {

	}

	static {
		try {
			writer = new BufferedWriter(new FileWriter("engine-alpha.log", false));
		} catch (IOException e) {
			File ea = new File("engine-alpha.log");

			if (ea.isDirectory()) {
				System.err.println("Logger konnte nicht initialisiert werden, da 'engine-alpha.log' ein Verzeichnis ist!");
				System.exit(1);
			} else if (!ea.canWrite()) {
				System.err.println("Logger konnte nicht initialisiert werden, da 'engine-alpha.log' nicht beschreibbar ist!");
				System.exit(1);
			} else {
				System.err.println("Logger konnte aus unbekannten Gründen nicht initialisiert werden!");
				System.exit(1);
			}
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run () {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Zeit im Log-Format
	 *
	 * @return gibt die Zeit für die Logs zurück
	 */
	private static String getTime () {
		return new Date().toString();
	}

	/**
	 * Logger-Funktion für Warnungen
	 *
	 * @param s
	 * 		Text der Warnung
	 */
	public static void warning (String s) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		write("WARNUNG", e.getFileName(), e.getLineNumber(), s);
	}

	/**
	 * Logger-Funktion für Fehler
	 *
	 * @param s
	 * 		Text des Fehlers
	 */
	public static void error (String s) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		write("ERROR", e.getFileName(), e.getLineNumber(), s, true);
	}

	/**
	 * Logger-Funktion für Informationen
	 *
	 * @param s
	 * 		Text der Information
	 */
	public static void info (String s) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		write("INFO", e.getFileName(), e.getLineNumber(), s);
	}

	private static String write (String type, String filename, int line, String message) {
		return write(type, filename, line, message, false);
	}

	private static String write (String type, String filename, int line, String message, boolean error) {
		String str = String.format("[%s][%s] %s (%s:%s)", getTime(), type, message, filename, Integer.toString(line));

		if (error) {
			System.err.println(str);
		} else {
			System.out.println(str);
		}

		return write(str);
	}

	/**
	 * Funktion in die Log-Datei zu schreiben
	 *
	 * @param text
	 * 		Meldungs-Text der zur Log übergeben wird
	 *
	 * @return Gibt den geschrieben Text zurück, im Fehlerfall <code>null</code>
	 */
	private static String write (String text) {
		try {
			writer.write(text);
			writer.newLine();

			return text;
		} catch (IOException e) {
			System.err.println("Logger konnte folgende Zeile nicht schreiben:\n" + text);

			return null;
		}
	}
}
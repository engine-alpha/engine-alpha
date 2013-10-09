package ea;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * TODO Docs
 * 
 * @author Julien Gelmar <master@nownewstart.net>, Niklas Keller
 */
public class Logger {
	private static BufferedWriter writer;
	
	static {
		try {
			writer = new BufferedWriter(new FileWriter("engine-alpha.log", false));
		} catch (IOException e) {
			File ea = new File("engine-alpha.log");
			
			if(ea.isDirectory()) {
				System.err.println("Logger konnte nicht initialisiert werden, da 'engine-alpha.log' ein Verzeichnis ist!");
				System.exit(1);
			} else if(!ea.canWrite()) {
				System.err.println("Logger konnte nicht initialisiert werden, da 'engine-alpha.log' nicht beschreibbar ist!");
				System.exit(1);
			} else {
				System.err.println("Logger konnte aus unbekannten Gründen nicht initialisiert werden!");
				System.exit(1);
			}
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					writer.close();
				} catch (IOException e) {
					// don't care!
				}
			}
		});
	}
	
	public static String getTime() {
		return new Date().toString();
	}
	
	/**
	 * Logger-Funktion für Warnungen
	 */
	public static void warning(String s) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		write("WARNUNG", e.getFileName(), e.getLineNumber(), s);
	}
	
	/**
	 * Logger-Funktion für Fehler
	 */
	public static void error(String s) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		write("ERROR", e.getFileName(), e.getLineNumber(), s, true);
	}
	
	/**
	 * Logger-Funktion für Informationen
	 */
	public static void info(String s) {
		StackTraceElement e = Thread.currentThread().getStackTrace()[2];
		write("INFO", e.getFileName(), e.getLineNumber(), s);
	}
	
	public static void write(String t, String f, int l, String s) {
		write(t, f, l, s, false);
	}
	
	public static void write(String t, String f, int l, String s, boolean error) {
		String str = "["+getTime()+"]["+t+"] "+s+" ("+f+":"+l+")";
		
		if(error) {
			System.err.println(str);
		} else {
			System.out.println(str);
		}
		
		write(str);
	}
	
	/**
	 * Funktion in die Log-Datei zu schreiben
	 * 
	 * @param text
	 *            Meldungs-Text der zur Log übergeben wird
	 */
	public static void write(String text) {
		try {
			writer.write(text);
			writer.newLine();
		} catch(IOException e) {
			System.err.println("Logger konnte folgende Zeile nicht schreiben:\n"+text);
		}
	}
}

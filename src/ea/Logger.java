package ea;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * @author Julien "NowNewStart" Gelmar <master@nownewstart.net>
 */
public class Logger {
    public static Date date() {
        Date dt = new Date();
        return dt;
    }

    /**
     * Logger-Funktion für Warnungen
     * @param text
     *            Text der Warnung
     * @param line
     *            Linie in der die Warnung auftritt
     * @param file
     *            Datei in der die Warnung auftritt
     * @throws IOException
     */
    public static void warn(String text, int line, String file) throws IOException  {
      System.out.println("[" + date() + "]" + " WARNUNG: " + text + ":" + file + " in der Linie " + line);
        write("[" + date() + "]" + " WARNUNG: " + text + ":" + file + " in der Linie " + line);
    }
    /**
     * Logger-Funktion für Fehler
     * @param text
     *            Text des Fehlers
     * @param line
     *            Linie in der der Fehler auftritt
     * @param file
     *            Datei in der der Fehler auftritt
     * @throws IOException
     */
    public static void error(String text, int line, String file) throws IOException  {
       System.out.println("[" + date() + "]" + " FEHLER: " + text + ":" + file + " in der Linie " + line);
        write("[" + date() + "]" + " FEHLER: " + text + ":" + file + " in der Linie " + line);
    }
    /**
     * Logger-Funktion für Informationen
     * @param text
     *            Text der Information
     * @param line
     *            Linie in der die Information auftritt
     * @param file
     *            Datei in der die Information auftritt
     * @throws IOException
     */
    public static void info(String text, int line, String file) throws IOException {
        System.out.println("[" + date() + "]" + " INFORMATION: " + text + ":" + file + " in der Linie " + line);
        write("[" + date() + "]" + " INFORMATION: " + text + ":" + file + " in der Linie " + line);
    }

    /**
     * Funktion in die Log-Datei zu schreiben
     * @param text
     *            Meldungs-Text der zur Log übergeben wird
     * @throws IOException
     */
    public static void write(String text) throws IOException {
        File f = new File("ea.log");
        try {
           f = new File("ea.log");
            if(!f.exists()) {
                error("Die Log-Datei für Engine Alpha existiert nicht ",58,"Logger.java");
                f.createNewFile();
            } else
            {
                if(!f.canWrite()) {
                    error("Die Log-Datei für Engine Alpha kann nicht beschrieben werden ",63,"Logger.java");
                    f.delete();
                    f.createNewFile();
                }
                else
                {
                    FileWriter fstream = new FileWriter(f,true);
                    BufferedWriter w = new BufferedWriter(fstream);
                    w.write(text.toString());
                    w.newLine();
                    w.close();
            }
        }
        }catch(IOException e) {
            error("Die Log-Datei für Engine Alpha kann nicht beschrieben werden ", 74, "Logger.java");
        }
    }

}

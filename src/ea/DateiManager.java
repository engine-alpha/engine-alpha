/*
 * Engine Alpha ist eine anfaengerorientierte 2D-Gaming Engine.
 *
 * Copyright (C) 2011  Michael Andonie
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ea;

import java.io.*;
import java.awt.Color;
import java.util.ArrayList;
/**
 * Der Dateimanager ist eine Klasse, die die Systemspezifischen Pfadregeln beachtend die jeweils korrekten Zeichenketten fuer die entsprechenden Dateiverzeichnisse kennt.<br />
 * Ausserdem kann sie die Informationen eines Pixelfeldes im EAF-Format (Engine-Alpha-Figure-Format speichern) 
 * sowie die eines String- oder Integer-Arrays im EAA-Format (Engine-Alpha-Array-Format) lesen und speichern.
 * 
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class DateiManager
{
    /**
     * Das allgemein gueltige Zeichen fuer einen Zeilenumbruch.
     */
    public static final String bruch = System.getProperty("line.separator");
    
    /**
     * Das allgemein gueltige Zeichen fuer ein Unterverzeichnis
     */
    public static final String sep = System.getProperty("file.separator");

    /**
     * Das grundlegende Verzeichnis. Dies ist die absolute Pfadangabe zu dieser Klasse.
     */
    public static final String verz = System.getProperty("user.dir") + sep;

    /**Eine Liste, die alle bereits verwendeten Farben einmalig listet*/
    private static final ArrayList<Color> list = new ArrayList<Color>();
    
    static {
        list.add(Color.red);
        list.add(Color.green);
        list.add(Color.blue);
        list.add(Color.yellow);
        list.add(Color.gray);
        list.add(Color.magenta);
        list.add(Color.cyan);
        list.add(Color.black);
        list.add(Color.orange);
        list.add(Color.lightGray);
    }
    
    /**
     * Konstruktor fuer Objekte der Klasse DateiManager. Ist Privat
     */
    private DateiManager() {
        //
    }
    
    /**
     * Schreibt ein String - Array (bzw. ein String[] - Objekt) als eigenstaendige Datei auf.<br />
     * Hierfuer wird das ".eaa"-Format verwendet ("Engine Alpha Array").
     * @param   array   Das zu schreibende Array.
     * @param   pfad    Der absolute (oder auch relative) Dateipfad, der sowohl das Verzeichnis wie auch den Dateinamen angibt.<br />
     * Dieser sollte mit ".eaf" enden, wenn nicht, wird dies automatisch angehaengt.<br />
     * <b>Sollte der String allerdings sonst ein "."-Zeichen enthalten</b>, wird nur eine Fehlermeldung ausgespuckt!
     * @return  Ist <code>true</code>, wenn die Datei erfolgreich geschrieben wurde, ansonsten <code>false</code>.
     */
    public static boolean stringArraySchreiben(String[] array, String pfad) {
        if(array == null) {
            System.err.println("Das Eingabearray war null!");
            return false;
        }
        if(!pfad.endsWith(".eaa")) {
            if(pfad.contains(".")) {
                System.err.println("Der Verzeichnisname ist ungueltig! Die Datei sollte mit '.eaf' enden und darf sonst keine '.'-Zeichen enthalten");
                return false;
            }
            pfad+=".eaa";
        }
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(verz+pfad));
            w.write("typ:String");//Typdeklaration
            w.newLine();
            w.write("len:"+array.length);
            for(int i = 0; i < array.length; i++) {
                w.newLine();
                String erg = array[i];
                if(erg == null) {
                    erg = "%%";
                } else if(erg.isEmpty()) {
                    erg = "~~";
                } else if(erg.equals("~~") || erg.equals("%%")) {
                    System.err.println("ACHTUNG! Aufgrund der internen Struktur des Dateiformates fuehrt die Abspeicherung der String" +
                            "literale \"%%\" sowie \"~~\" unter Garantie zu falschen Ergebnissen!!!!");
                }
                w.write("val:" + erg);
            }
            w.close();
            return true;
        } catch(IOException e) {
            System.err.println("Fehler beim Erstellen der Datei. Sind die Zugriffsrechte zu stark?");
            return false;
        }
    }
    
    /**
     * Liesst eine ".eaa" - String - Array - Datei ein.
     * @param   pfad Das Verzeichnis der einzulesenden Datei.<br />
     * Die Eingabe <b>muss</b> ein Dateiname mit dem ende ".eaf" sein. Dies kann ohne Ordnerangaben gemacht werden, wenn die Datei im Quelltextordner ist.
     * @return  Das Array, das eingelesen wurde.<br />
     * Ist <code>null</code>, wenn ein Fehler aufgetreten ist.
     */
    public static String[] stringArrayEinlesen(String pfad) {
        if(!pfad.endsWith(".eaa")) {
            System.err.println("Der Dateiname bezeichnet keine .eaa - Datei! Er muss mit \".eaa\" enden!");
            return null;
        }
        String[] ret = null;
        try {
            String line;
            LineNumberReader f = new LineNumberReader(new FileReader(verz+pfad));
            line = f.readLine();
            if(line.compareTo("typ:String") != 0) {
                System.err.println("Die geladene \".eaa\"-Datei beschreibt kein String-Array oder ist beschaedigt!");
                return null;
            }
            line = f.readLine();
            int length = Integer.valueOf(line.split(":")[1]);
            ret = new String[length];
            for(int i = 0; i < length; i++) {
                line = f.readLine();
                String[] split = line.split(":");
                String erg = "";
                if(split[1].equals("%%") && split.length == 2) {
                    erg = null;
                } else if (split[1].equals("~~")){
                    erg = "";
                } else {
                    for(int j = 1; j < split.length; j++) {
                        if(j != 1) {
                            erg += ":";
                        }
                        erg += split[j];
                    }
                }
                ret[i] = erg;
            }
            f.close();
        } catch(IOException e) {
            System.err.println("Fehler beim Lesen der Datei. Existiert die Datei mit diesem Namen wirklich?" + bruch +
                    pfad);
            return null;
        }
        return ret;
    }
    
    /**
     * Schreibt ein int- Array (bzw. ein int[] - Objekt) als eigenstaendige Datei auf.<br />
     * Hierfuer wird das ".eaa"-Format verwendet ("Engine Alpha Array").
     * @param   array   Das zu schreibende Array.
     * @param   pfad    Der absolute (oder auch relative) Dateipfad, der sowohl das Verzeichnis wie auch den Dateinamen angibt.<br />
     * Dieser sollte mit ".eaf" enden, wenn nicht, wird dies automatisch angehaengt.<br />
     * <b>Sollte der String allerdings sonst ein "."-Zeichen enthalten</b>, wird nur eine Fehlermeldung ausgespuckt!
     * @return  Ist <code>true</code>, wenn die Datei erfolgreich geschrieben wurde, ansonsten <code>false</code>.
     */
    public static boolean integerArraySchreiben(int[] array, String pfad) {
        if(array == null) {
            System.err.println("Das Eingabearray war null!");
            return false;
        }
        if(!pfad.endsWith(".eaa")) {
            if(pfad.contains(".")) {
                System.err.println("Der Verzeichnisname ist ungueltig! Die Datei sollte mit '.eaf' enden und darf sonst keine '.'-Zeichen enthalten");
                return false;
            }
            pfad+=".eaa";
        }
        try {
            BufferedWriter w = new BufferedWriter(new FileWriter(verz+pfad));
            w.write("typ:Integer");//Typdeklaration
            w.newLine();
            w.write("len:"+array.length);
            for(int i = 0; i < array.length; i++) {
                w.newLine();
                w.write("val:" + array[i]);
            }
            w.close();
            return true;
        } catch(IOException e) {
            System.err.println("Fehler beim Erstellen der Datei. Sind die Zugriffsrechte zu stark?" + bruch +
                    pfad);
            return false;
        }
    }
    
    /**
     * Liesst eine ".eaa" - int - Array - Datei ein.
     * @param   pfad Das Verzeichnis der einzulesenden Datei.<br />
     * Die Eingabe <b>muss</b> ein Dateiname mit dem ende ".eaf" sein. Dies kann ohne Ordnerangaben gemacht werden, wenn die Datei im Quelltextordner ist.
     * @return  Das Array, das eingelesen wurde.<br />
     * Ist <code>null</code>, wenn ein Fehler aufgetreten ist.
     */
    public static int[] integerArrayEinlesen(String pfad) {
        if(!pfad.endsWith(".eaa")) {
            System.err.println("Der Dateiname bezeichnet keine .eaa - Datei! Er muss mit \".eaa\" enden!");
            return null;
        }
        int[] ret = null;
        try {
            String line;
            LineNumberReader f = new LineNumberReader(new FileReader(verz+pfad));
            line = f.readLine();
            if(line.compareTo("typ:Integer") != 0) {
                System.err.println("Die geladene \".eaa\"-Datei beschreibt kein int-Array oder ist beschaedigt!");
                return null;
            }
            line = f.readLine();
            int length = Integer.valueOf(line.split(":")[1]);
            ret = new int[length];
            for(int i = 0; i < length; i++) {
                line = f.readLine();
                ret[i] = Integer.valueOf(line.split(":")[1]);
            }
            f.close();
        } catch(IOException e) {
            System.err.println("Fehler beim Lesen der Datei. Existiert die Datei mit diesem Namen wirklich?" + bruch +
                    pfad);
            return null;
        }
        return ret;
    }
    
    /**
     * Schreibt die ".eaf"-Datei zu einer Figur.<br />
     * Hierbei wird eine eventuell bestehende Datei dieses Namens rigoros geloescht, sofern moeglich.<br />
     * Diese Methode ggibt zurueck, ob das schreiben der Datei erfolgreich war oder nicht.
     * @param   f   Die zu schreibende Figur
     * @param   name    Der Name der Datei. Dieser sollte mit ".eaf" enden, wenn nicht, wird dies automatisch angehaengt.<br />
     * <b>Sollte der String allerdings sonst ein "."-Zeichen enthalten</b>, wird nur eine Fehlermeldung ausgespuckt!
     * @param   verzeichnis Das Verzeichnis, in dem die Datei gespeichert werden soll. Ist dies ein leerer String (""), so wird die Figur nur nach ihrem namen gespeichert.
     * @param relativ   Gibt an, ob das Verzeichnis relativ zum Spielprojekt geshen werden soll (standard)
     * @return  Ist <code>true</code>, wenn die Datei erfolgreich geschrieben wurde, ansonsten <code>false</code>.
     */
    public static boolean schreiben(Figur f, String name, String verzeichnis, boolean relativ) {
        BufferedWriter w;
        PixelFeld[] feld = f.animation();
        try {
            String verz;
            if(!name.endsWith(".eaf")) {
                if(name.contains(".")) {
                    System.err.println("Der Verzeichnisname ist ungueltig! Die Datei sollte mit '.eaf' enden und darf sonst keine '.'-Zeichen enthalten");
                    return false;
                }
                name += ".eaf";
            }
            if(verzeichnis.isEmpty()) {
                verz = name;
            } else {
                verz = verzeichnis + sep + name;
            }
            String add = DateiManager.verz;
            if(!relativ) {
                add = "";
            }
            w = new BufferedWriter(new FileWriter(add+verz));
            //Basics
            w.write("_fig_"); //Basisdeklaration
            w.newLine();
            w.write("an:" + feld.length); //Die Anzahl an PixelFeldern
            w.newLine();
            w.write("f:" + feld[0].faktor()); //Der Groessenfaktor
            w.newLine();
            w.write("x:" + feld[0].breiteN()); //Die X-Groesse
            w.newLine();
            w.write("y:" + feld[0].hoeheN()); //Die Y-Groesse
            w.newLine();
            w.write("p:" + f.dimension().x); //Die Position X
            w.newLine();
            w.write("q:" + f.dimension().y); //Die Position Y
            w.newLine();
            //Die Felder
            for(int i = 0; i < feld.length; i++) {
                w.write("-");
                w.newLine();
                w.write(feldInfo(feld[i]));
            }
            w.close();
            return true;
        } catch (IOException e) {
            System.err.println("Fehler beim Erstellen der Datei. Sind die Zugriffsrechte zu stark?" + bruch +
                    verzeichnis);
            return false;
        }
    }

    /**
     * Schreibt die ".eaf"-Datei zu einer Figur.<br />
     * Hierbei wird eine eventuell bestehende Datei dieses Namens rigoros geloescht, sofern moeglich.<br />
     * Diese Methode ggibt zurueck, ob das schreiben der Datei erfolgreich war oder nicht.
     * @param   f   Die zu schreibende Figur
     * @param   name    Der Name der Datei. Dieser sollte mit ".eaf" enden, wenn nicht, wird dies automatisch angehaengt.<br />
     * <b>Sollte der String allerdings sonst ein "."-Zeichen enthalten</b>, wird nur eine Fehlermeldung ausgespuckt!
     * @param   verzeichnis Das Verzeichnis, in dem die Datei gespeichert werden soll. Ist dies ein leerer String (""), so wird die Figur nur nach ihrem namen gespeichert.
     * @param relativ   Gibt an, ob das Verzeichnis relativ zum Spielprojekt geshen werden soll (standard)
     * @return  Ist <code>true</code>, wenn die Datei erfolgreich geschrieben wurde, ansonsten <code>false</code>.
     */
    public static boolean schreiben(Figur f, String verzeichnis, String name) {
        return schreiben(f, verzeichnis, name, true);
    }
    
    /**
     * Vereinfachte Version der Schreibmethode.<br />
     * Hierbei wird die eingegebene Figur nach dem selben Algorythmus geschrieben, jedoch gibt der eine Eingabeparameter den Namen und den 
     * gesamten Pfad an.
     * @param   f   Die zu schreibende Figur
     * @param   pfad    Der absolute (oder auch relative) Dateipfad, der sowohl das Verzeichnis wie auch den Dateinamen angibt.
     * @return  Ist <code>true</code>, wenn die Datei erfolgreich geschrieben wurde, ansonsten <code>false</code>.
     * @see schreiben(Figur, String, String)
     */
    public static boolean schreiben(Figur f, String pfad) {
        return schreiben(f, pfad, "");
    }


    /**
     * Liesst eine Figur ein und gibt die geladene Figur zurueck.<br />
     * Diese Methode macht nichts weiter als die Methode <code>figurEinlesen(String)</code>. Diese wurde aufgrund der
     * Namensnaehe zur Verhinderung ungeliebter Falschschreibungen hinzugefuegt und wrappt diese Methode lediglich.
     * @param   verzeichnis Das Verzeichnis der einzulesenden Datei.<br />
     * Die Eingabe <b>muss</b> ein Dateiname mit dem ende ".eaf" sein. Dies kann ohne Ordnerangaben gemacht werden, wenn die Datei im Quelltextordner ist.
     * @return  Die eingelesene Figur.<br />
     * <b>Tritt ein Fehler auf</b>, weil die Datei nicht einlesbar ist oder nicht existiert, ist dieser wert <code>null</code>.<br />
     * Trotzdem kann es sein, dass eine beschuedigte Datei nicht mehr korrekt einlesbar ist, dennoch ein Ergebnis liefert.
     * @see figurEinlesen(String)
     */
    public static Figur figurLaden(String verzeichnis) {
        return figurEinlesen(verzeichnis);
    }

    /**
     * Liesst eine Figur ein.
     * @param   verzeichnis Das Verzeichnis der einzulesenden Datei.<br />
     * Die Eingabe <b>muss</b> ein Dateiname mit dem ende ".eaf" sein. Dies kann ohne Ordnerangaben gemacht werden, wenn die Datei im Quelltextordner ist.
     * @param relativ Ob die Pfadangabe relativ zum Projektplatz ist (Standard)
     * @return  Die eingelesene Figur.<br />
     * <b>Tritt ein Fehler auf</b>, weil die Datei nicht einlesbar ist oder nicht existiert, ist dieser wert <code>null</code>.<br />
     * Trotzdem kann es sein, dass eine beschuedigte Datei nicht mehr korrekt einlesbar ist, dennoch ein Ergebnis liefert.
     */
    public static Figur figurEinlesen(File file) {
        /* ####################################In Erinnerung an die String-equals-Problematik####################################*/
        String verzeichnis = file.getAbsolutePath();
        if(!verzeichnis.endsWith(".eaf")) {
            System.err.println("Die Datei " + verzeichnis + " endet nicht mit dem korrekten Dateiformat(.eaf)");
            return null;
        }
        Figur fig  = new Figur();
        LineNumberReader f;
        String line;
        try {
            String add = "";
//            if(relativ) {
//                add = verz;
//            }
            f = new LineNumberReader(new FileReader(add + verzeichnis));
            line = f.readLine();
            if(line.equals(line.compareTo("_fig_") != 0)) { //Format bestaetigen
                System.err.println("Die Datei ist keine Figur-Datei!" + line);
                return null;
            }
            line = f.readLine();
            final int animationsLaenge = Integer.valueOf(line.substring(3)); //Die Anzahl an PixelFeldern
            //System.out.println("PixelFelder: " + animationsLaenge);
            line = f.readLine();
            final int fakt = Integer.valueOf(line.substring(2)); //Der Groessenfaktor
            //System.out.println("Der Groessenfaktor: " + fakt);
            line = f.readLine();
            final int x = Integer.valueOf(line.substring(2)); //Die X-Groesse
            line = f.readLine();
            final int y = Integer.valueOf(line.substring(2)); //Die Y-Groesse
            //System.out.println("X-Gr: " + x + "; Y-Gr: " + y);
            line = f.readLine();
            final int px = Integer.valueOf(line.substring(2)); // Die X-Position
            line = f.readLine();
            final int py = Integer.valueOf(line.substring(2)); // Die Y-Position
            //System.out.println("P-X: " + px + " - P-Y: " + py);
            
            PixelFeld[] ergebnis = new PixelFeld[animationsLaenge];
            for(int i = 0; i < ergebnis.length; i++) { //Felder basteln
                if((line= f.readLine()).compareTo("-") != 0) { //Sicherheitstest
                    System.err.println("Die Datei ist beschaedigt");
                }
                ergebnis[i] = new PixelFeld(x, y, fakt);
                for(int xT = 0; xT < x; xT++) { //X
                    for(int yT = 0; yT < y; yT++) { //Y
                        line = f.readLine();
                        Color c = farbeEinlesen(line.split(":")[1]);
                        if(c != null) {
                            c = ausListe(c);
                        }
                        ergebnis[i].farbeSetzen(xT, yT, c);
                    }
                }
            }
            fig.animationSetzen(ergebnis);
            fig.positionSetzen(px, py);
            fig.animiertSetzen((animationsLaenge != 1));
            f.close();
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei. Existiert die Datei mit diesem Namen wirklich?" + bruch +
                    verzeichnis);
            e.printStackTrace();
        }
        
        return fig;
    }

    public static Figur figurEinlesen(String verzeichnis, boolean relativ) {
        return figurEinlesen(new File(verzeichnis));
    }

    /**
     * Liesst eine Figur ein.
     * @param   verzeichnis Das Verzeichnis der einzulesenden Datei.<br />
     * Die Eingabe <b>muss</b> ein Dateiname mit dem ende ".eaf" sein. Dies kann ohne Ordnerangaben gemacht werden, wenn die Datei im Quelltextordner ist.
     * @return  Die eingelesene Figur.<br />
     * <b>Tritt ein Fehler auf</b>, weil die Datei nicht einlesbar ist oder nicht existiert, ist dieser wert <code>null</code>.<br />
     * Trotzdem kann es sein, dass eine beschuedigte Datei nicht mehr korrekt einlesbar ist, dennoch ein Ergebnis liefert.
     */
    public static Figur figurEinlesen(String verzeichnis) {
        return figurEinlesen(new File(verzeichnis));
    }

    /**
     * Berechnet aus einem PixelFeld die Informationen und schickt sie als String zurueck.<br />
     * <b>ACHTUNG:</b> Umbruchzeichen werden gesetzt, jedoch endet der String <b>NICHT</b> mit einem Zeilenumbruch, daher muss bei der Informationsbindung aus mehreren Feldern
     * eine Zeile nach dem verwenden dieses Strings geschaltet werden.
     */
    public static String feldInfo(PixelFeld f) {
        Color[][] farbe = f.getPic();
        String ret = "";
        for(int i = 0; i < farbe.length; i++) {
            for(int j = 0; j < farbe[0].length; j++) {
                //Eine Zeile arbeit
                ret += "Z" +i + "-" + j + ":" + farbeAnalysieren(farbe[i][j]) + bruch;
            }
        }
        return ret;
    }
    
    /**
     * Analysiert eine Farbe und weist ihr einen String zu.
     * @param   c   Die zu analysierende Farbe
     * @return  Die Stringrepraesentation der Farbe
     */
    public static String farbeAnalysieren(Color c) {
        if(c == null) {
            return "%%;";
        }
        String s = "";
        if(c == Color.black) {
            s = "schwarz;";
        } else if(c == Color.gray) {
            s = "grau;";
        }
        else if(c == Color.green) {
            s = "gruen;";
        }
        else if(c == Color.yellow) {
            s = "gelb;";
        }
        else if(c == Color.blue) {
            s = "blau;";
        }
        else if(c == Color.white) {
            s = "weiss;";
        }
        else if(c == Color.orange) {
            s = "orange;";
        }
        else if(c == Color.red) {
            s = "rot;";
        }
        else if(c == Color.pink) {
            s = "pink;";
        }
        else if(c == Color.magenta) {
            s = "magenta;";
        }
        else if(c == Color.cyan) {
            s = "cyan;";
        }
        else if(c == Color.darkGray) {
            s = "dunkelgrau;";
        }
        else if(c == Color.lightGray) {
            s = "hellgrau;";
        }
        else { //Andere Farbe
            s = "&"+c.getRed()+","+c.getGreen()+","+c.getBlue()+";";
        }
        return s;
    }
    
    /**
     * Liesst einen String ein und konvertiert ihn zu einer Farbe.
     * @param   s   Der zu konvertierende String
     * @return  Das Color-Objekt, das gelesen wurde.<br />
     * <b>Ist null</b>, wenn der String nicht eingelesen werden konnte!
     */
    public static Color farbeEinlesen(String s) {
        if( s.compareTo("%%;") == 0) {
            return null;
        }
        else if(s.charAt(0) != '&') {
            return Raum.zuFarbeKonvertieren(s.replace(";", ""));
        }
        else {
            Color c;
            int[] rgb = new int[3];
            int cnt = 0;
            int temp = 1;
            for(int i = 1; i < s.length(); i++) {
                if(s.charAt(i) == ',' || s.charAt(i) == ';') {
                    rgb[cnt++] = Integer.valueOf(s.substring(temp, i));
                    temp = i+1;
                }
            }
            c = new Color(rgb[0], rgb[1], rgb[2]);
            return c;
        }
    }
    
    /**
     * Die Listenmethode beim Figureinlesen und fuer das speicherarme Raum-Objekt-Faerben<br />
     * Diese Methode wird verwendet um den Speicher zu entlasten, da  Farbobjekte, die bereits in der Liste enthalten sind, 
     * nicht zurueckgegeben werden, sondern durch den vorhandenen Farbewert ersetzt werden.<br />
     * Somit hat jede Farbe beim Einlesen genau eine Instanz innerhalb der gesamten Engine.
     * @param   farbe   Die Farbe, die auf existenz in der Liste geprueft werden soll.
     * @return  Das zurueckgegebene Farbobjekt ist vom Zustand her genau das selbe wie das Eingegebene.<br />
     * Jedoch bleibt dank dieser Methode fuer jede Farbe nur ein Farbobjekt, was Speicherplatz schafft.
     */
    public static Color ausListe(Color farbe) {
        for(Color c : list) {
            if(c.equals(farbe)) {
                return c;
            }
        }
        list.add(farbe);
        return farbe;
    }
}

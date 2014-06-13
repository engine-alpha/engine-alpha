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

package ea.internal.gui;

import java.awt.event.*;

/**
 * Der Adapter bietet die Möglickeit, bei Deaktivierung des Fenster die essentiellen Listenermethode abzuschalten, um
 * unerwartete Ausführungen dieser Methoden zu verhindern.
 * 
 * @author Michael Andonie
 */
public class Adapter
extends WindowAdapter {
    /**
     * Das Fenster, das hiermit stillgelegt werden kann.
     */
    private Fenster fenster;
    
    /**
     * Erstellt einen Adapter.
     * @param   fenster Das Fenster, das deaktiviert werden soll.
     */
    public Adapter(Fenster fenster) {
        this.fenster = fenster;
    }
    
    /**
     * Die deactivated-Methode.<br />
     * Hierbei wird im Fenster der Tastendruck etc. aufgeloest.
     */
    public void windowDeactivated(WindowEvent e) {
        fensterStillLegen();
    }
    
    /**
     * Deaktiviert alle Funktionen des Fensters, die bei nichtaufmerksamkeit des Spielers unerwuenscht sind.
     */
    private void fensterStillLegen() {
        fenster.druckAufheben();
    }
}
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

package ea.keyboard;

import java.awt.event.KeyEvent;

/**
 * Konstanten für die Tastatur-Codes
 *
 * @author Niklas Keller <me@kelunik.com>
 */
public class Taste {
	public static final int INVALID = -1;

	public static final int A = 0;

	public static final int B = 1;

	public static final int C = 2;

	public static final int D = 3;

	public static final int E = 4;

	public static final int F = 5;

	public static final int G = 6;

	public static final int H = 7;

	public static final int I = 8;

	public static final int J = 9;

	public static final int K = 10;

	public static final int L = 11;

	public static final int M = 12;

	public static final int N = 13;

	public static final int O = 14;

	public static final int P = 15;

	public static final int Q = 16;

	public static final int R = 17;

	public static final int S = 18;

	public static final int T = 19;

	public static final int U = 20;

	public static final int V = 21;

	public static final int W = 22;

	public static final int X = 23;

	public static final int Y = 24;

	public static final int Z = 25;

	public static final int OBEN = 26;

	public static final int RECHTS = 27;

	public static final int UNTEN = 28;

	public static final int LINKS = 29;

	public static final int LEERTASTE = 30;

	public static final int ENTER = 31;

	public static final int ESCAPE = 32;

	public static final int _0 = 33;

	public static final int _1 = 34;

	public static final int _2 = 35;

	public static final int _3 = 36;

	public static final int _4 = 37;

	public static final int _5 = 38;

	public static final int _6 = 39;

	public static final int _7 = 40;

	public static final int _8 = 41;

	public static final int _9 = 42;

	public static final int PLUS = 43;

	public static final int MINUS = 44;

	/**
	 * Gibt den Namen der Konstante für eine bestimmte Taste aus.
	 *
	 * @param code
	 * 		Ein Tastencode.
	 *
	 * @return Der Name der Konstante, die diesen Tastencode beschreibt oder "INVALID".
	 */
	public static final String nameVon (int code) {
		switch (code) {
			case A:
				return "A";
			case B:
				return "B";
			case C:
				return "C";
			case D:
				return "D";
			case E:
				return "E";
			case F:
				return "F";
			case G:
				return "G";
			case H:
				return "H";
			case I:
				return "I";
			case J:
				return "J";
			case K:
				return "K";
			case L:
				return "L";
			case M:
				return "M";
			case N:
				return "N";
			case O:
				return "O";
			case P:
				return "P";
			case Q:
				return "Q";
			case R:
				return "R";
			case S:
				return "S";
			case T:
				return "T";
			case U:
				return "U";
			case V:
				return "V";
			case W:
				return "W";
			case X:
				return "X";
			case Y:
				return "Y";
			case Z:
				return "Z";
			case _0:
				return "_0";
			case _1:
				return "_1";
			case _2:
				return "_2";
			case _3:
				return "_3";
			case _4:
				return "_4";
			case _5:
				return "_5";
			case _6:
				return "_6";
			case _7:
				return "_7";
			case _8:
				return "_8";
			case _9:
				return "_9";
			case LEERTASTE:
				return "LEERTASTE";
			case ESCAPE:
				return "ESCAPE";
			case ENTER:
				return "ENTER";
			case PLUS:
				return "PLUS";
			case MINUS:
				return "MINUS";
			case OBEN:
				return "OBEN";
			case UNTEN:
				return "UNTEN";
			case LINKS:
				return "LINKS";
			case RECHTS:
				return "RECHTS";
			default:
				return "INVALID";
		}
	}

	/**
	 * Ordnet vom Java-KeyCode-System in das EA-System um.
	 *
	 * @param code
	 * 		Der Java-KeyCode
	 *
	 * @return Entsprechender EA-KeyCode oder <code>-1</code>, falls es keinen passenden EA-KeyCode
	 * gibt.
	 */
	public static final int vonJava (int code) {
		int z = INVALID;

		switch (code) {
			case KeyEvent.VK_A:
				z = A;
				break;
			case KeyEvent.VK_B:
				z = B;
				break;
			case KeyEvent.VK_C:
				z = C;
				break;
			case KeyEvent.VK_D:
				z = D;
				break;
			case KeyEvent.VK_E:
				z = E;
				break;
			case KeyEvent.VK_F:
				z = F;
				break;
			case KeyEvent.VK_G:
				z = G;
				break;
			case KeyEvent.VK_H:
				z = H;
				break;
			case KeyEvent.VK_I:
				z = I;
				break;
			case KeyEvent.VK_J:
				z = J;
				break;
			case KeyEvent.VK_K:
				z = K;
				break;
			case KeyEvent.VK_L:
				z = L;
				break;
			case KeyEvent.VK_M:
				z = M;
				break;
			case KeyEvent.VK_N:
				z = N;
				break;
			case KeyEvent.VK_O:
				z = O;
				break;
			case KeyEvent.VK_P:
				z = P;
				break;
			case KeyEvent.VK_Q:
				z = Q;
				break;
			case KeyEvent.VK_R:
				z = R;
				break;
			case KeyEvent.VK_S:
				z = S;
				break;
			case KeyEvent.VK_T:
				z = T;
				break;
			case KeyEvent.VK_U:
				z = U;
				break;
			case KeyEvent.VK_V:
				z = V;
				break;
			case KeyEvent.VK_W:
				z = W;
				break;
			case KeyEvent.VK_X:
				z = X;
				break;
			case KeyEvent.VK_Y:
				z = Y;
				break;
			case KeyEvent.VK_Z:
				z = Z;
				break;
			case KeyEvent.VK_UP:
				z = OBEN;
				break;
			case KeyEvent.VK_RIGHT:
				z = RECHTS;
				break;
			case KeyEvent.VK_DOWN:
				z = UNTEN;
				break;
			case KeyEvent.VK_LEFT:
				z = LINKS;
				break;
			case KeyEvent.VK_SPACE:
				z = LEERTASTE;
				break;
			case KeyEvent.VK_ENTER:
				z = ENTER;
				break;
			case KeyEvent.VK_ESCAPE:
				z = ESCAPE;
				break;
			case KeyEvent.VK_0:
				z = _0;
				break;
			case KeyEvent.VK_1:
				z = _1;
				break;
			case KeyEvent.VK_2:
				z = _2;
				break;
			case KeyEvent.VK_3:
				z = _3;
				break;
			case KeyEvent.VK_4:
				z = _4;
				break;
			case KeyEvent.VK_5:
				z = _5;
				break;
			case KeyEvent.VK_6:
				z = _6;
				break;
			case KeyEvent.VK_7:
				z = _7;
				break;
			case KeyEvent.VK_8:
				z = _8;
				break;
			case KeyEvent.VK_9:
				z = _9;
				break;
			case KeyEvent.VK_PLUS:
				z = PLUS;
				break;
			case KeyEvent.VK_MINUS:
				z = MINUS;
				break;
		}

		return z;
	}
}

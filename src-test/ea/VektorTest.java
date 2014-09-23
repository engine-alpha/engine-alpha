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

package ea;

import org.junit.Test;

import static org.junit.Assert.*;

public class VektorTest {
	@Test
	public void punktToPunkt () {
		Punkt p1 = new Punkt(10, 10);
		Punkt p2 = new Punkt(30, 20);

		Vektor vektor = new Vektor(p1, p2);

		assertEquals(vektor.dX(), 20);
		assertEquals(vektor.dY(), 10);
	}

	@Test
	public void senkrecht () {
		Vektor v1 = new Vektor(1, 0);
		Vektor v2 = new Vektor(0, 1);
		Vektor v3 = new Vektor(2, 1);

		assertEquals(v1.skalarprodukt(v2), 0, 0);
		assertNotEquals(v1.skalarprodukt(v3), 0, 0);
		assertNotEquals(v2.skalarprodukt(v3), 0, 0);
	}

	@Test
	public void punktGleichVektor () {
		Vektor v1 = new Vektor(1, 1);
		Punkt p1 = new Punkt(1, 1);

		assertEquals(p1, v1.alsPunkt());
	}

	@Test
	public void differenz () {
		Vektor v1 = new Vektor(3, 3);
		Vektor v2 = new Vektor(2, 2);

		assertEquals(v1.differenz(v2), new Vektor(1, 1));
	}

	@Test
	public void multiplizieren () {
		Vektor vektor = new Vektor(1, 2);
		assertEquals(vektor.multiplizieren(2), new Vektor(2, 4));
	}

	@Test
	public void normiert () {
		Vektor vektor = new Vektor(10, 100);
		assertEquals(vektor.normiert().laenge(), 1, 0);
	}

	@Test (expected = ArithmeticException.class)
	public void teilenDurch0 () {
		new Vektor(0, 0).teilen(0);
	}

	@Test
	public void laenge () {
		assertEquals(new Vektor(1, 1).laenge(), Math.sqrt(2), 0.00001);
	}

	@Test
	public void gegenrichtung () {
		assertEquals(new Vektor(1, 1).gegenrichtung(), new Vektor(-1, -1));
	}

	@Test
	public void summe () {
		assertEquals(new Vektor(1, 1).summe(new Vektor(1, 1)), new Vektor(2, 2));
	}

	@Test
	public void unwirksam () {
		assertFalse(new Vektor(1, 1).unwirksam());
		assertFalse(new Vektor(1, 0).unwirksam());
		assertFalse(new Vektor(0, 1).unwirksam());
		assertTrue(new Vektor(0, 0).unwirksam());
	}

	@Test
	public void istEchtGanzzahlig() {
		assertTrue(new Vektor(1, 1).istEchtGanzzahlig());
		assertFalse(new Vektor(.5f, .5f).istEchtGanzzahlig());
		assertFalse(new Vektor(.5f, 1).istEchtGanzzahlig());
		assertFalse(new Vektor(1, .5f).istEchtGanzzahlig());
	}

	@Test
	public void realX() {
		float x = .013f;
		assertEquals(new Vektor(x, 0).realX(), x, 0.00001f);
	}

	@Test
	public void realY() {
		float y = .013f;
		assertEquals(new Vektor(0, y).realY(), y, 0.00001f);
	}

	@Test
	public void testToString() {
		assertEquals("ea.Vektor [x = 1.0; y = 1.0]", new Vektor(1, 1).toString());
	}

	@Test
	public void cloneable() {
		Vektor vektor = new Vektor(1, 1);
		assertEquals(vektor, vektor.clone());
	}

	@Test
	public void testEquals() {
		Vektor vektor = new Vektor(1, 1);

		assertNotEquals(new Vektor(1, 0), vektor);
		assertNotEquals(new Vektor(0, 1), vektor);
		assertNotEquals(new Vektor(0, 0), vektor);

		assertNotEquals(vektor, new Object());
	}
}
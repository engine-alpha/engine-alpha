package ea;

import org.junit.After;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class DateimanagerTest {
	@After
	public void cleanUp() {
		try {
			Files.deleteIfExists(Paths.get("test.eaa"));
		} catch(IOException e) {
			fail("Konnte Datei nicht löschen, obwohl sie existiert.");
		}
	}

	@Test
	public void stringArrayIO() {
		String[] write = {"Single Line", "Line 1\nLine 2", null, "", "~~", "%%"};
		DateiManager.stringArraySchreiben(write, "test.eaa");

		String[] read = DateiManager.stringArrayEinlesen("test.eaa");

		assertArrayEquals("Gelesenes Array ist nicht gleich mit geschriebenem Array", write, read);
	}

	@Test
	public void stringArrayWriteWrongFileExtension() {
		String[] write = {"Hallo Welt"};
		DateiManager.stringArraySchreiben(write, "test");
		assertTrue(Files.exists(Paths.get("test.eaa")));
	}

	@Test
	public void stringArrayReadWrongFileExtension() {
		String[] write = {"Hallo Welt"};
		DateiManager.stringArraySchreiben(write, "test.eaa");
		assertNotNull(DateiManager.stringArrayEinlesen("test"));
	}

	@Test (expected = IllegalArgumentException.class)
	public void stringArrayWriteNull() {
		DateiManager.stringArraySchreiben(null, "test.eaa");
	}

	@Test
	public void intArrayIO() {
		int[] write = {0, -125, 2351, 90235};
		DateiManager.integerArraySchreiben(write, "test.eaa");

		int[] read = DateiManager.integerArrayEinlesen("test.eaa");

		assertArrayEquals("Gelesenes Array ist nicht gleich mit geschriebenem Array", write, read);
	}

	@Test (expected = IllegalArgumentException.class)
	public void intArrayWriteNull() {
		DateiManager.integerArraySchreiben(null, "test.eaa");
	}

	@Test
	public void intArrayWriteWrongFileExtension() {
		int[] write = {0};
		DateiManager.integerArraySchreiben(write, "test");
		assertTrue(Files.exists(Paths.get("test.eaa")));
	}

	@Test
	public void intArrayReadWrongFileExtension() {
		int[] write = {0};
		DateiManager.integerArraySchreiben(write, "test.eaa");
		assertNotNull(DateiManager.integerArrayEinlesen("test"));
	}
}
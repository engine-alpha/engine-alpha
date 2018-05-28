package ea;

import ea.actor.ActorGroup;
import ea.actor.Rectangle;
import org.junit.Test;

import static org.junit.Assert.*;

public class ActorGroupTest {
	@Test
	public void basic () {
		Rectangle r = new Rectangle(100, 100);
		r.position.set(100,100);
		ActorGroup k = new ActorGroup();

		k.add(r);
		assertTrue(k.contains(r));

		k.remove(r);
		assertFalse(k.contains(r));

		// TODO: Für Schüler evtl. unerwartetes Verhalten - Exception?
		k.add(r, r);
		assertEquals(2, k.getMembers().length);

		k.position.move(new Vector(10, 10));
		assertEquals(new Point(120, 120), r.position.get());

		r.position.move(new Vector(10, 10));
		assertEquals(new Point(130, 130), r.position.get());

		k.remove(r);
		assertEquals(0, k.getMembers().length);
	}
}

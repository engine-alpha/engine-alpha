package ea;

import ea.internal.util.Optimizer;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class OptimizerTest {
	@Test
	public void optimizeImage () {
		BufferedImage img = null;

		try {
			img = ImageIO.read(EngineAlpha.class.getResource("/assets/logo.png"));
		} catch (Exception e) {
		}

		assertNotNull(img);

		BufferedImage opt = Optimizer.toCompatibleImage(img);
		assertNotNull(opt);

		assertEquals(img.getWidth(), opt.getWidth());
		assertEquals(img.getHeight(), opt.getHeight());

		BufferedImage opt2 = Optimizer.toCompatibleImage(opt);
		assertEquals(opt.getColorModel(), opt2.getColorModel());
	}
}

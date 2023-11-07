package ea;

import ea.internal.util.Logger;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertTrue;

public class LoggerTest {
    @Test
    public void fileExists () {
        Logger.error("LoggerTest", "lorem ipsum");
        assertTrue(Files.exists(Paths.get("engine-alpha.log")));
    }
}

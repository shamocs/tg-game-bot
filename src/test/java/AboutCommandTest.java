import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AboutCommandTest {

    private final AboutCommand command = new AboutCommand();

    @Test
    void testGetName() {
        assertEquals("/about", command.getName());
    }

    @Test
    void testExecute() {
        String result = command.execute("");
        assertNotNull(result);
        assertTrue(result.contains("tg-game-bot"));
    }
}
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthorsCommandTest {

    private final AuthorsCommand command = new AuthorsCommand();

    @Test
    void testGetName() {
        assertEquals("/authors", command.getName());
    }

    @Test
    void testExecute() {
        String result = command.execute("");
        assertNotNull(result);
        assertTrue(result.contains("Кб-202"));
    }
}
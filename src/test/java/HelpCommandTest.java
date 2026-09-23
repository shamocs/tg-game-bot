import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class HelpCommandTest {

    private final HelpCommand command = new HelpCommand();

    @Test
    @DisplayName("Проверка имени команды")
    void testGetName() {
        assertEquals("/help", command.getName());
    }

    @Test
    @DisplayName("Вызов без аргументов возвращает общую справку")
    void testExecuteWithEmptyArg() {
        String result = command.execute("");
        assertNotNull(result);
        assertTrue(result.contains("Доступные команды:"));
    }

    @Test
    @DisplayName("Передача null не вызывает NullPointerException")
    void testExecuteWithNullArg() {
        String result = command.execute(null);
        assertNotNull(result);
        assertTrue(result.contains("Доступные команды:"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"help", "about", "authors"})
    @DisplayName("Проверка справки по всем существующим подкомандам")
    void testExecuteWithValidArgs(String arg) {
        String result = command.execute(arg);
        assertNotNull(result);
        assertTrue(result.contains("Справка по команде"));
    }

    @Test
    @DisplayName("Вызов с несуществующим аргументом")
    void testExecuteWithUnknownArg() {
        String result = command.execute("unknown_arg");
        assertEquals("Нет справки по команде unknown_arg", result);
    }
}
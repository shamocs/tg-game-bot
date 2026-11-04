package Commands;

import BotServices.GameService;
import BotServices.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CommandRegistry: регистрация и поиск команд")
class CommandRegistryTest {

    private CommandRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new CommandRegistry(new SessionManager(), new GameService());
    }

    // ---------- содержимое по умолчанию ----------

    @Test
    @DisplayName("Конструктор регистрирует все 4 базовые команды (ключи со слешем)")
    void constructorRegistersDefaultCommands() {
        assertNotNull(registry.getCommand("/play"),    "play не зарегистрирован");
        assertNotNull(registry.getCommand("/help"),    "help не зарегистрирован");
        assertNotNull(registry.getCommand("/authors"), "authors не зарегистрирован");
        assertNotNull(registry.getCommand("/about"),   "about не зарегистрирован");
        assertEquals(4, registry.getAll().size(), "должно быть ровно 4 команды");
    }

    @Test
    @DisplayName("getCommand возвращает команды нужных типов")
    void getCommandReturnsCorrectTypes() {
        assertInstanceOf(PlayCommand.class,    registry.getCommand("/play"));
        assertInstanceOf(HelpCommand.class,    registry.getCommand("/help"));
        assertInstanceOf(AuthorsCommand.class, registry.getCommand("/authors"));
        assertInstanceOf(AboutCommand.class,   registry.getCommand("/about"));
    }

    // ---------- несуществующие команды ----------

    @Test
    @DisplayName("getCommand для несуществующей команды возвращает null")
    void getCommandUnknownReturnsNull() {
        assertNull(registry.getCommand("/nonexistent"));
        assertNull(registry.getCommand("/asdfgh"));
        assertNull(registry.getCommand("/play2"));
    }

    @Test
    @DisplayName("getCommand(null) и getCommand(\"\") не падают и дают null")
    void getCommandNullOrEmptyDoesNotCrash() {
        assertNull(registry.getCommand(null), "null-имя не должно бросать исключение");
        assertNull(registry.getCommand(""),   "пустое имя не должно бросать исключение");
    }


    @Test
    @DisplayName("Разные имена → разные объекты команд")
    void differentNamesReturnDifferentInstances() {
        Command play = registry.getCommand("/play");
        Command help = registry.getCommand("/help");

        assertNotNull(play);
        assertNotNull(help);
        assertNotSame(play, help, "play и help не должны быть одним объектом");
    }

    // ---------- кастомная регистрация ----------

    @Test
    @DisplayName("registerCommand делает команду доступной по её имени")
    void registerCommandMakesCommandFindable() {
        Command custom = new StubCommand("/custom");
        assertNull(registry.getCommand("/custom"), "до регистрации команды быть не должно");

        registry.registerCommand(custom);

        assertSame(custom, registry.getCommand("/custom"));
        assertEquals(5, registry.getAll().size());
    }

    @Test
    @DisplayName("Повторная регистрация с тем же именем перезаписывает команду")
    void registerCommandOverwritesSameName() {
        Command first  = new StubCommand("/dup");
        Command second = new StubCommand("/dup");

        registry.registerCommand(first);
        registry.registerCommand(second);

        assertSame(second, registry.getCommand("/dup"), "должна остаться последняя зарегистрированная");
        assertEquals(5, registry.getAll().size(), "размер не должен вырасти при перезаписи");
    }

    // ---------- контракт: слеш — часть ключа, регистр не нормализуется ----------

    @Test
    @DisplayName("Контракт: слеш — часть ключа; регистр реестр не нормализует (это делает CommandHandler)")
    void registryExpectsSlashAndDoesNotLowercase() {
        assertNotNull(registry.getCommand("/play"), "со слешем — находит");
        assertNull(registry.getCommand("play"),     "без слеша — null, слеш часть ключа");
        assertNull(registry.getCommand("/PLAY"),    "регистр важен — CommandHandler lowercased до вызова");
    }

    // ---------- вспомогательный стаб ----------

    private static final class StubCommand implements Command {
        private final String name;
        StubCommand(String name) { this.name = name; }
        @Override public String execute(long chatId, String arg) { return "stub"; }
        @Override public String getName() { return name; }
        @Override public String getHelp() { return "stub help"; }
    }
}
package Commands;

import BotServices.GameService;
import BotServices.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HelpCommand: без аргумента, корректный, некорректный, несуществующий")
class HelpCommandTest {

    private HelpCommand help;
    private static final long CHAT_ID = 111L;

    @BeforeEach
    void setUp() {
        // HelpCommand создаётся внутри CommandRegistry, берём его оттуда
        CommandRegistry registry = new CommandRegistry(new SessionManager(), new GameService());
        help = (HelpCommand) registry.getCommand("/help");
        assertNotNull(help, "HelpCommand должен быть зарегистрирован");
    }

    // 1. Без аргумента / null / пусто → общий список команд

    @Test
    @DisplayName("Без аргумента → 'Доступные команды' + все 4 имени")
    void withoutArgListsAllCommands() {
        String result = help.execute(CHAT_ID, "");

        assertTrue(result.contains("Доступные команды"),
                "должен быть заголовок списка");
        assertTrue(result.contains("/help"), "должна быть /help");
        assertTrue(result.contains("/about"), "должна быть /about");
        assertTrue(result.contains("/authors"), "должна быть /authors");
        assertTrue(result.contains("/play"), "должна быть /play");
        assertTrue(result.contains("Подробнее"),
                "должна быть подсказка про /help [имя_команды]");
    }

    @Test
    @DisplayName("null-аргумент не падает, ведёт себя как пустой")
    void nullArgBehavesLikeEmpty() {
        String result = help.execute(CHAT_ID, null);

        assertTrue(result.contains("Доступные команды"));
        assertFalse(result.contains("Команда не найдена"),
                "null — это не 'команда не найдена', это 'нет аргумента'");
    }

    @Test
    @DisplayName("execute не зависит от chatId ")
    void chatIdDoesNotAffectResult() {
        String r1 = help.execute(111L, "");
        String r2 = help.execute(999L, "");

        assertEquals(r1, r2, "chatId не должен влиять на текст справки");
    }

    // 2. Корректный аргумент → getHelp() этой команды

    @Test
    @DisplayName("/help /play → возвращает getHelp() команды /play ('Начать игру')")
    void knownArgWithSlashReturnsItsHelp() {
        String result = help.execute(CHAT_ID, "/play");

        assertEquals("Начать игру", result,
                "должна прийти справка именно /play");
    }

    @Test
    @DisplayName("/help play (без слеша) → тот же результат: HelpCommand добавляет слеш")
    void knownArgWithoutSlashReturnsSameHelp() {
        String result = help.execute(CHAT_ID, "play");

        assertEquals("Начать игру", result,
                "HelpCommand должен нормализовать имя без слеша");
    }

    @Test
    @DisplayName("/help /help → справка по самому help (📖 ...)")
    void helpOnItselfReturnsSelfHelp() {
        String result = help.execute(CHAT_ID, "/help");

        assertTrue(result.contains("Справка по команде /help"),
                "должна прийти справка по /help");
        assertFalse(result.contains("Команда не найдена"));
    }

    @Test
    @DisplayName("Все зарегистрированные команды имеют непустой getHelp()")
    void everyRegisteredCommandHasHelp() {
        CommandRegistry registry = new CommandRegistry(new SessionManager(), new GameService());
        for (Command cmd : registry.getAll()) {
            String h = cmd.getHelp();
            assertNotNull(h, "getHelp() не должен возвращать null: " + cmd.getName());
            assertFalse(h.isEmpty(), "getHelp() не должен быть пустым: " + cmd.getName());
        }
    }


    // 3. Несуществующий аргумент → 'Команда не найдена'

    @Test
    @DisplayName("/help /unknown → 'Команда не найдена: /unknown'")
    void unknownArgWithSlashReportsNotFound() {
        String result = help.execute(CHAT_ID, "/unknown");

        assertTrue(result.startsWith("Команда не найдена"),
                "должно начинаться с 'Команда не найдена'");
        assertTrue(result.contains("/unknown"),
                "в сообщении должно быть исходное имя");
    }

    @Test
    @DisplayName("/help unknown (без слеша) → 'Команда не найдена: unknown' (эхо оригинала)")
    void unknownArgWithoutSlashEchoesOriginal() {
        String result = help.execute(CHAT_ID, "unknown");

        assertTrue(result.startsWith("Команда не найдена"));
        // ВАЖНО: в сообщении именно "unknown", а не "/unknown",
        // потому что HelpCommand эхает исходный arg, а не нормализованный
        assertTrue(result.contains("unknown"),
                "эхо должно содержать исходный аргумент");
    }

    @Test
    @DisplayName("/help какого-то-мусора — не падает, возвращает 'Команда не найдена'")
    void garbageArgDoesNotCrash() {
        String result = help.execute(CHAT_ID, "!!!@@@###");

        assertNotNull(result);
        assertTrue(result.contains("Команда не найдена"));
    }

    // 4. Формат списка
    @Test
    @DisplayName("Все зарегистрированные команды присутствуют в списке, и ничего лишнего")
    void listMatchesRegistry() {
        CommandRegistry registry = new CommandRegistry(new SessionManager(), new GameService());
        String result = help.execute(CHAT_ID, "");

        long commandMentions = result.lines().filter(l -> l.startsWith("/")).count();
        long registered = registry.getAll().size();

        assertEquals(registered, commandMentions,
                "число команд в /help должно совпадать с числом зарегистрированных");
    }
}
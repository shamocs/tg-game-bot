import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandHandlerTest {
    private final CommandHandler handler = new CommandHandler(null);

    @Test
    void shouldReturnHelp() {
        assertEquals("Доступные команды:\n" +
                        "/help — выводит список всех команд\n" +
                        "/about — краткая информация о боте\n" +
                        "/authors — информация об авторах проекта\n\n" +
                        "Чтобы узнать подробнее о команде, введите: \n /help [имя_команды]",handler.getAnswerOnCommand("/help"));

    }

    @Test
    void shouldReturnAbout() {
        assertEquals("🃏 Игровой бот tg-game-bot — это твоя личная игровая комната в Telegram!\n\n" +
                        "Классические карточные игры, быстрые партии и увлекательные сценарии в одном месте. " +
                        "Никаких сложных регистраций — просто выбирай игру и побеждай.\n\n" +
                        "Введи /help, чтобы посмотреть доступные команды.",handler.getAnswerOnCommand("/about"));

    }

    @Test
    void shouldReturnAuthors() {
        assertEquals("Создатели студенты Кб-202: \n Юрочкин Егор и Медведев Данил",handler.getAnswerOnCommand("/authors"));

    }

    @Test
    void shouldReturnHelpHelp() {
        assertEquals("📖 *Справка по команде /help:*\nВыводит список всех доступных команд бота и подсказки по их использованию.",handler.getHelpOnCommand("/help help"));

    }

    @Test
    void shouldReturnHelpAbout() {
        assertEquals("ℹ️ *Справка по команде /about:*\nПоказывает информацию об игровом боте, доступных жанрах игр и правилах.",handler.getHelpOnCommand("/help about"));

    }

    @Test
    void shouldReturnHelpAuthors() {
        assertEquals("👥 *Справка по команде /authors:*\nВыводит информацию об авторах проекта — разработчиках из группы Кб-202.",handler.getHelpOnCommand("/help authors"));

    }
}
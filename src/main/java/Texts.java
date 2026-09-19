public class Texts {
    private String about = "🃏 Игровой бот tg-game-bot — это твоя личная игровая комната в Telegram!\n\n" +
            "Классические карточные игры, быстрые партии и увлекательные сценарии в одном месте. " +
            "Никаких сложных регистраций — просто выбирай игру и побеждай.\n\n" +
            "Введи /help, чтобы посмотреть доступные команды.";

    private String authors = "Создатели студенты Кб-202:\n" +
            "Юрочкин Егор и Медведев Данил";

    private String help = "Доступные команды:\n" +
            "/help — выводит список всех команд\n" +
            "/about — краткая информация о боте\n" +
            "/authors — информация об авторах проекта\n\n" +
            "Чтобы узнать подробнее о команде, введите: \n /help [имя_команды]";

    private final String helpHelp = "📖 Справка по команде /help:\nВыводит список всех доступных команд бота и подсказки по их использованию.";

    private final String helpAbout = "ℹ️ Справка по команде /about:\nПоказывает информацию об игровом боте, доступных жанрах игр и правилах.";

    private final String helpAuthors = "👥 Справка по команде /authors:\nВыводит информацию об авторах проекта — разработчиках из группы Кб-202.";

    public String getAbout() {
        return about;
    }

    public String getAuthors() {
        return authors;
    }

    public String getHelp() {
        return help;
    }

    public String getHelpHelp() {
        return helpHelp;
    }

    public String getHelpAbout() {
        return helpAbout;
    }

    public String getHelpAuthors() {
        return helpAuthors;
    }
}
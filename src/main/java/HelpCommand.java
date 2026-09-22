public class HelpCommand implements Command {
    private final Texts texts = new Texts();

    @Override
    public String execute(String arg) {
        if (arg != null && !arg.isEmpty()) {
            return texts.getHelpOn(arg);
        }
        return texts.getHelp();
    }

    @Override
    public String getName() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "\"Доступные команды:\\n\" +\n" +
                "            \"/help — выводит список всех команд\\n\" +\n" +
                "            \"/about — краткая информация о боте\\n\" +\n" +
                "            \"/authors — информация об авторах проекта\\n\\n\" +\n" +
                "            \"Чтобы узнать подробнее о команде, введите: \\n /help [имя_команды]\"";
    }
}
public class HelpCommand implements Command {
    private final CommandRegistry registry;

    public HelpCommand(CommandRegistry registry) {
        this.registry = registry;
    }

    @Override
    public String execute(long chatId,String arg) {
        if (arg != null && !arg.isEmpty()) {
            Command cmd = registry.getCommand(arg.startsWith("/") ? arg : "/" + arg);
            if (cmd == null) return "Команда не найдена: " + arg;
            return cmd.getHelp();
        }

        String result = "Доступные команды:\n";
        for (Command cmd : registry.getAll()) {
            result += cmd.getName() + "\n";
        }
        return result + "\nПодробнее: /help [имя_команды]";
    }

    @Override
    public String getName() {
        return "/help";
    }

    @Override
    public String getHelp() {
        return "📖 *Справка по команде /help:*\nВыводит список всех доступных команд бота и подсказки по их использованию."; }
    }
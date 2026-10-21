public class CancelCommand implements Command {

    private final SessionManager sessions;

    public CancelCommand(SessionManager sessions) {
        this.sessions = sessions;
    }

    @Override
    public String getName() {
        return "/cancel";
    }

    @Override
    public String getHelp() {
        return "Выйти из игры";
    }

    @Override
    public String execute(long chatId, String arg) {
        sessions.remove(chatId);
        return "Вышел из игры. Меню.";
    }
}
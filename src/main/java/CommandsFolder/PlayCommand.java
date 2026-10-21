public class PlayCommand implements Command {

    private final SessionManager sessions;
    private final GameService gservice;

    public PlayCommand(SessionManager sessions, GameService gameService) {
        this.sessions = sessions;
        this.gservice = gameService;
    }

    @Override
    public String getName() {
        return "/play";
    }

    @Override
    public String getHelp() {
        return "Начать игру";
    }

    @Override
    public String execute(long chatId, String arg) {
        sessions.transition(chatId, BotState.CHOOSING_GAME);
        return "Выбери игру:\n" + gservice.getGamesList();
    }
}
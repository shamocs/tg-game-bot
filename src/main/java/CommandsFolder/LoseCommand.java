
public class LoseCommand implements Command {

    private final SessionManager sessions;
    private final GameBot bot;

    public LoseCommand(SessionManager sessions, GameBot bot) {
        this.sessions = sessions;
        this.bot = bot;
    }

    @Override
    public String getName() {
        return "/lose";
    }

    @Override
    public String getHelp() {
        return "Проиграть (тест)";
    }

    @Override
    public String execute(long chatId, String arg) {
        if (sessions.hasActive(chatId)) {
            sessions.transition(chatId, BotState.GAME_OVER);
            bot.sendText(chatId,"you lost");
        }else{
            bot.sendText(chatId,"you are not in the game");
        }
        return "";
    }


}
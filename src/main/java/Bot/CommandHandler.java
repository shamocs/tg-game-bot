import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

public class CommandHandler {
    private GameBot bot;
    private SessionManager sessions = new SessionManager();
    private GameService gameService = new GameService();
    private CommandRegistry registry = new CommandRegistry(sessions, gameService);

    public CommandHandler(GameBot bot){
        this.bot = bot;
    }

    public boolean isCommand(Update update){
        String text = update.getMessage().getText();
        return text.startsWith("/");
    }

    public void parseCommand(Update update){
        String text = update.getMessage().getText().toLowerCase(Locale.ROOT);
        long chatId = update.getMessage().getChatId();

        String[] parts = text.split(" ",2);
        String commandName = parts[0];
        String arg = "";

        if(parts.length > 1 && !parts[1].isEmpty()){
            arg = parts[1];
        }

        Command cmd = registry.getCommand(commandName);
        if (cmd == null) {
            bot.sendText(chatId, "Неизвестная команда. Напиши /help");
            return;
        }

        bot.sendText(chatId, cmd.execute(chatId, arg));
    }

    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String text = update.getMessage().getText().toLowerCase(Locale.ROOT);
        long chatId = update.getMessage().getChatId();

        System.out.println("[" + chatId + "] получено: " + text);
        if (sessions.hasActive(chatId)) {
            GameSession session = sessions.get(chatId);
            if (text.equals("/cancel")) {
                sessions.remove(chatId);
                Command help = registry.getCommand("/help");
                if (help != null) {
                    bot.sendText(chatId, help.execute(chatId, ""));
                    sessions.transition(chatId,BotState.IDLE);
                }
                return;
            }
            if (session.getState() == BotState.CHOOSING_GAME) {
                String gameId = "";
                switch (text){
                    case "1":
                        gameId = "blackjack";
                        break;
                    case "2":
                        gameId = "other";
                        break;
                    default:
                        bot.sendText(chatId,"Выберите номер игры!");
                        return;
                }
                sessions.transition(chatId, BotState.WAITING_FOR_MOVE);
                bot.sendText(chatId, gameService.startGame(gameId, chatId));
                bot.sendText(chatId,"waiting for your move!");
                return;
            }
            if (text.equals("/lose")){
                LoseCommand lose = new LoseCommand(sessions,bot);
                lose.execute(chatId,"");
                return;
            }
            String reply = gameService.handleMove("blackjack", chatId, text);
            bot.sendText(chatId, reply);
            return;
        }

        if (!isCommand(update)) {
            bot.sendText(chatId, "Введите команду! \nДля списка доступных команд напишите /help");
            return;
        }
        parseCommand(update);
    }
}
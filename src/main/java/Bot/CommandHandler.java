
package Bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import BotServices.SessionManager;
import BotServices.GameService;
import Commands.CommandRegistry;
import BotServices.GameSession;
import BotServices.BotState;
import Commands.Command;
import Commands.LoseCommand;

import java.util.Locale;

import java.util.Locale;

public class CommandHandler {
    private final GameBot bot;
    private final SessionManager sessions = new SessionManager();
    private final GameService gameService = new GameService();
    private final CommandRegistry registry = new CommandRegistry(sessions, gameService);

    public CommandHandler(GameBot bot) {
        this.bot = bot;
    }

    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String text = update.getMessage().getText().toLowerCase(Locale.ROOT);
        long chatId = update.getMessage().getChatId();

        System.out.println("[" + chatId + "] получено: " + text);

        if (sessions.hasActive(chatId)) {
            handleActive(chatId, text);
            return;
        }

        if (!text.startsWith("/")) {
            bot.sendText(chatId, "Введите команду! \nДля списка доступных команд напишите /help");
            return;
        }

        parseCommand(chatId, text);
    }


    private void handleActive(long chatId, String text) {
        GameSession session = sessions.get(chatId);

        if (text.equals("/cancel")) {
            handleCancel(chatId);
            return;
        }
        if (session.getState() == BotState.CHOOSING_GAME) {
            handleChoosing(chatId, text);
            return;
        }
        if (text.equals("/lose")) {
            handleLose(chatId);
            return;
        }
        if (session.getState() == BotState.GAME_OVER) {
            handleGameOver(chatId, text);
            return;
        }
        handleMove(chatId, text);
    }

    private void handleCancel(long chatId) {
        sessions.remove(chatId);
        Command help = registry.getCommand("/help");
        bot.sendText(chatId, help != null ? help.execute(chatId, "") : "Вышел из игры.");
    }

    private void handleChoosing(long chatId, String text) {
        String gameId;
        switch (text) {
            case "1": gameId = "blackjack"; break;
            case "2": gameId = "other"; break;
            default:
                bot.sendText(chatId, "Выберите номер игры!");
                return;
        }
        sessions.transition(chatId, BotState.WAITING_FOR_MOVE);
        bot.sendText(chatId, gameService.startGame(gameId, chatId));
        bot.sendText(chatId, "waiting for your move!");
    }

    private void handleLose(long chatId) {
        LoseCommand lose = new LoseCommand(sessions, bot);
        lose.execute(chatId, "");
    }

    private void handleGameOver(long chatId, String text) {
        if (text.equals("/play")) {
            sessions.transition(chatId, BotState.CHOOSING_GAME);
            bot.sendText(chatId, "Выбери игру:\n" + gameService.getGamesList());
            return;
        }
        bot.sendText(chatId, "Игра окончена.\n/play — новая игра\n/cancel — выход");
    }

    private void handleMove(long chatId, String text) {
        String reply = gameService.handleMove("blackjack", chatId, text);
        bot.sendText(chatId, reply);
    }

    private void parseCommand(long chatId, String text) {
        String[] parts = text.split(" ", 2);
        String commandName = parts[0];
        String arg = parts.length > 1 ? parts[1] : "";

        Command cmd = registry.getCommand(commandName);
        if (cmd == null) {
            bot.sendText(chatId, "Неизвестная команда. Напиши /help");
            return;
        }
        bot.sendText(chatId, cmd.execute(chatId, arg));
    }
}
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.games.Game;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Locale;

public class CommandHandler {
    private GameBot bot;
    private CommandRegistry registry = new CommandRegistry();

    public CommandHandler(GameBot bot){
        this.bot = bot;
    }

    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String text = update.getMessage().getText().toLowerCase(Locale.ROOT);
        long chatId = update.getMessage().getChatId();
        System.out.println("[" + chatId + "] получено: " + text);
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

        bot.sendText(chatId, cmd.execute(arg));



    }
}


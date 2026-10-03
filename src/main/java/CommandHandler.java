import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Locale;

public class CommandHandler {
    private GameBot bot;
    private CommandRegistry registry = new CommandRegistry();

    public CommandHandler(GameBot bot){
        this.bot = bot;
    }

    public boolean isCommand(Update update){
        String text = update.getMessage().getText();
        if (text.startsWith("/")){
            return true;
        }
        return false;
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

        bot.sendText(chatId, cmd.execute(arg));
    }

    public void handle(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }
        String text = update.getMessage().getText().toLowerCase(Locale.ROOT);
        long chatId = update.getMessage().getChatId();

        System.out.println("[" + chatId + "] получено: " + text);

        if (!isCommand(update)) {
            bot.sendText(chatId,"Введите команду! \nДля списка доступных команд напишите /help");
            return;
        }

        parseCommand(update);
    }
}
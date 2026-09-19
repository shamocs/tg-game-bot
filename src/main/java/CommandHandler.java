import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.games.Game;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class CommandHandler {
    private GameBot bot;
    private Texts texts = new Texts();
    public CommandHandler(GameBot bot){
        this.bot = bot;
    }
    public void handle(Update update){
        if(update.hasMessage() && update.getMessage().hasText()){
            String text = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            System.out.println("[" + chatId + "]" + " получено: " + text);
            if(text.startsWith("/help ")){
                String arg = text.substring(6);
                switch(arg){
                    case "about":
                        bot.sendText(chatId,texts.getHelpAbout());
                        break;
                    case "authors":
                        bot.sendText(chatId,texts.getHelpAuthors());
                        break;
                    case "help":
                        bot.sendText(chatId,texts.getHelpHelp());
                        break;
                    default:
                        bot.sendText(chatId,"Команда не найдена!\nИспользуйте /help для просмотра списка доступных команд.");
                }
            }else{
                switch(text){
                    case "/about":
                        bot.sendText(chatId,texts.getAbout());
                        break;
                    case "/authors":
                        bot.sendText(chatId,texts.getAuthors());
                        break;
                    case "/help":
                        bot.sendText(chatId,texts.getHelp());
                        break;
                    default:
                        bot.sendText(chatId, "Неизвестная команда.\nИспользуйте /help для просмотра списка доступных команд.");
                }
            }
        }
    }
}

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GameBot extends TelegramLongPollingBot {
    private CommandHandler handler = new CommandHandler(this);

    @Override
    public String getBotUsername() {
        return "MMgames26_bot";
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        handler.handle(update);
    }
    public void sendText(long chatId, String text){
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(text);
        System.out.println("[" + chatId + "]" + " отправлено: " + message.getText());
        try{
            execute(message);
        } catch(TelegramApiException e){
            System.out.println(e);
        }
    }

}
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class GameBot extends TelegramLongPollingBot {

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
        if (update.hasMessage()) {
            Message message = update.getMessage();
            System.out.println("пришло: " + update.getMessage().getText());

            SendMessage sendMessageRequest = new SendMessage();
            sendMessageRequest.setChatId(message.getChatId().toString());
            sendMessageRequest.setText("пришло: " + message.getText());

            SendMessage sendMessageBack = new SendMessage();
            sendMessageBack.setChatId(message.getChatId().toString());
            System.out.println("отправлено сообщение " + message.getText() + " + тест: ");
            sendMessageBack.setText(message.getText() + " тест");

            try{
                execute(sendMessageBack);
            } catch(TelegramApiException e){
                System.out.println(e);
            }
        }
    }
}
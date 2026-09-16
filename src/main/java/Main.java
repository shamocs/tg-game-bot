import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main{
    public static void main(String[] args) {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            CasinoBot bot = new CasinoBot();
            api.registerBot(bot);
            System.out.println("бот запущен");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
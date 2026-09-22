public class AboutCommand implements Command {
    private final Texts texts = new Texts();
    @Override
    public String execute(String arg){
        return texts.getAbout();
    }
    @Override
    public String getName(){
        return "/about";
    }

    @Override
    public String getDescription(){
        return "🃏 Игровой бот tg-game-bot — это твоя личная игровая комната в Telegram!\n\n" +
                "Классические карточные игры, быстрые партии и увлекательные сценарии в одном месте. " +
                "Никаких сложных регистраций — просто выбирай игру и побеждай.\n\n" +
                "Введи /help, чтобы посмотреть доступные команды.";
    }
}

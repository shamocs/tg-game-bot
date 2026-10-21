public class AboutCommand implements Command {
    private final Texts texts = new Texts();
    @Override
    public String execute(long chatId,String arg){
        return texts.getAbout();
    }
    @Override
    public String getName(){
        return "/about";
    }

    @Override
    public String getHelp(){
        return "ℹ️ *Справка по команде /about:*\nПоказывает информацию об игровом боте, доступных жанрах игр и правилах.";
    }

}

public class AuthorsCommand implements Command {
    private final Texts texts = new Texts();
    @Override
    public String execute(String arg){
        return texts.getAuthors();
    }

    @Override
    public String getName(){
        return "/authors";
    }
    @Override
    public String getHelp(){
        return "👥 *Справка по команде /authors:*\nВыводит информацию об авторах проекта — разработчиках из группы Кб-202.";
    }
}

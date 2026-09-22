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
    public String getDescription(){
        return "Создатели студенты Кб-202: \n Юрочкин Егор и Медведев Данил";
    }
}

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
}

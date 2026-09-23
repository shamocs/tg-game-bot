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

}

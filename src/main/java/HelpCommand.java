public class HelpCommand implements Command {
    private final Texts texts = new Texts();

    @Override
    public String execute(String arg) {
        if (arg != null && !arg.isEmpty()) {
            return texts.getHelpOn(arg);
        }
        return texts.getHelp();
    }

    @Override
    public String getName() {
        return "/help";
    }
}
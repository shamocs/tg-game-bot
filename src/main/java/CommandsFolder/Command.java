public interface Command {
    String execute(long chatId, String arg);
    String getName();
    String getHelp();
}
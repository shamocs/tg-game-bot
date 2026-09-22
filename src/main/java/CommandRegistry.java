import java.util.HashMap;

public class CommandRegistry{
    private HashMap<String,Command> commands = new HashMap<>();

    public CommandRegistry() {
        registerCommand(new HelpCommand());
        registerCommand(new AuthorsCommand());
        registerCommand(new AboutCommand());
    }
    public void registerCommand(Command command){
        commands.put(command.getName(),command);
    }
    public Command getCommand(String name) {
        return commands.get(name);
    }

}


import java.util.Collection;
import java.util.HashMap;

public class CommandRegistry {
    private HashMap<String, Command> commands = new HashMap<>();

    public CommandRegistry(SessionManager sessions, GameService gameService) {
        registerCommand(new HelpCommand(this));
        registerCommand(new AuthorsCommand());
        registerCommand(new AboutCommand());
        registerCommand((new PlayCommand(sessions,gameService)));
    }

    public void registerCommand(Command command) {
        commands.put(command.getName(), command);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public Collection<Command> getAll() {
        return commands.values();
    }
}
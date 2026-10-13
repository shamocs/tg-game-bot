import java.time.Instant;

public class GameSession {
    private final long userId;
    private  BotState state;
    private  Instant lastActivity;
    private  Object gameContext; // заполнит Разработчик B

    public GameSession(long userId) {
        this.userId = userId;
        this.state = BotState.IDLE;
        this.lastActivity = Instant.now();
    }
    public long getUserId()          { return userId; }
    public BotState getState()       { return state; }
    public Instant getLastActivity() { return lastActivity; }
    public Object getGameContext()   { return gameContext; }

    public void setState(BotState state) {
        this.state = state;
        this.lastActivity = Instant.now();
    }
    public void setGameContext(Object ctx) { this.gameContext = ctx; }
    public void touch()                    { this.lastActivity = Instant.now(); }
    public boolean isActive()              { return state != BotState.IDLE; }
}
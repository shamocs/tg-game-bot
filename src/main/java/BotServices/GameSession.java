import java.time.Instant;

public class GameSession {
    private final long userId;
    private  BotState state;
    private  Instant lastActivity;

    public GameSession(long userId) {
        this.userId = userId;
        this.state = BotState.IDLE;
        this.lastActivity = Instant.now();
    }
    public long getUserId()          { return userId; }
    public BotState getState()       { return state; }
    public Instant getLastActivity() { return lastActivity; }

    public void setState(BotState state) {
        this.state = state;
        this.lastActivity = Instant.now();
    }

    public void touch()                    { this.lastActivity = Instant.now(); }
    public boolean isActive()              { return state != BotState.IDLE; }
}
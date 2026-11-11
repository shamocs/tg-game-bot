package BotServices;

import java.time.Instant;

public class GameSession {
    private final long userId;
    private  BotState state;
    private  Instant lastActivity;
    private Object gameContext;
    private String currentGame;

    public GameSession(long userId) {
        this.userId = userId;
        this.state = BotState.IDLE;
        this.lastActivity = Instant.now();
    }
    public long getUserId()          { return userId; }
    public BotState getState()       { return state; }
    public Instant getLastActivity() { return lastActivity; }
    public String getCurrentGame()        { return currentGame; }

    public void setCurrentGame(String g)  { this.currentGame = g; }

    public Object getGameContext()   { return gameContext; }
    public void setGameContext(Object ctx) { this.gameContext = ctx; }

    public void setState(BotState state) {
        this.state = state;
        this.lastActivity = Instant.now();
    }

    public void touch()                    { this.lastActivity = Instant.now(); }
    public boolean isActive()              { return state != BotState.IDLE; }
}
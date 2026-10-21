import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class SessionManager {

    private static final Logger LOG = Logger.getLogger(SessionManager.class.getName());
    private static final Duration TIMEOUT = Duration.ofMinutes(30);

    private final Map<Long, GameSession> sessions = new HashMap<>();

    public GameSession getOrCreate(long userId) {
        GameSession s = sessions.get(userId);
        if (s == null) {
            s = new GameSession(userId);
            sessions.put(userId, s);
            LOG.info("[FSM] create session user=" + userId);
        }
        return s;
    }

    public GameSession get(long userId) {
        GameSession s = sessions.get(userId);
        if (s == null) return null;
        if (isExpired(s)) {
            sessions.remove(userId);
            LOG.info("[FSM] session expired user=" + userId);
            return null;
        }
        return s;
    }

    public boolean hasActive(long userId) {
        GameSession s = get(userId);
        return s != null && s.isActive();
    }

    public void transition(long userId, BotState newState) {
        GameSession s = getOrCreate(userId);
        BotState old = s.getState();
        s.setState(newState);
        LOG.info("[FSM] user=" + userId + " " + old + " -> " + newState);
    }

    public void remove(long userId) {
        GameSession s = sessions.remove(userId);
        if (s != null) LOG.info("[FSM] remove session user=" + userId);
    }

    private boolean isExpired(GameSession s) {
        return s.getLastActivity().plus(TIMEOUT).isBefore(Instant.now());
    }
}
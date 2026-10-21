import java.util.LinkedHashMap;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class GameService {
    private final Map<String, Game> games = new LinkedHashMap<>();

    public GameService() {
        games.put("blackjack", new BlackJack());
        games.put("other", new BlackJack());
    }

    public String getGamesList() {
        StringBuilder sb = new StringBuilder();
        int i = 1;
        for (String gameId : games.keySet()) {
            sb.append(i).append(") ").append(gameId).append("\n");
            i++;
        }
        return sb.toString();
    }

    public String startGame(String gameId, long userId) {
        Game g = games.get(gameId);
        if (g == null) return "Неизвестная игра";
        return g.start(userId);
    }

    public String handleMove(String gameId, long userId, String text) {
        Game g = games.get(gameId);
        if (g == null) return "Неизвестная игра";
        return g.move(userId, text);
    }
}
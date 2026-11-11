package BotServices;

import Games.Game;
import Games.BlackJack;
import java.util.LinkedHashMap;
import java.util.Map;

public class GameService {
    private final Map<String, Game> games = new LinkedHashMap<>();

    public GameService() {
        games.put("blackjack", new BlackJack());
        games.put("other", new BlackJack());
    }

    public String getGamesList() {
        String result = "";
        int i = 1;
        for (String gameId : games.keySet()) {
            result = result + i + ") " + gameId + "\n";
            i += 1;
        }
        return result;
    }

    public String startGame(String gameId, GameSession session) {
        Game g = games.get(gameId);
        if (g == null) return "Неизвестная игра";
        return g.start(session);
    }

    public String handleMove(String gameId, GameSession session, String text) {
        Game g = games.get(gameId);
        if (g == null) return "Неизвестная игра";
        return g.move(session, text);
    }
}
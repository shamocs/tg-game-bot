package Games;

import BotServices.GameSession;

public interface Game {
    String getName();
    String start(GameSession session);
    String move(GameSession session, String playerMove);
}
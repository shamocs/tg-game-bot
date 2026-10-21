public interface Game {
    String getName();
    String start(long userId);
    String move(long userId, String playerMove);

}
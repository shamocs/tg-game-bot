public class BlackJack implements Game {

    @Override
    public String getName() {
        return "blackjack";
    }

    @Override
    public String start(long userId) {
        return "starting blackjack\ntype /cancel to quit the game";
    }

    @Override
    public String move(long userId, String playerMove) {
        System.out.println("player[" + userId + "] moved: " + playerMove);
        return "Ход принят!";
    }
}
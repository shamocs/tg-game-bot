package Games;

import BotServices.GameSession;

public class BlackJack implements Game {

    @Override
    public String getName() {
        return "blackjack";
    }

    @Override
    public String start(GameSession session) {
        BlackJackState state = new BlackJackState();
        state.deck = new Deck();
        state.deck.shuffle();

        state.playerCards.add(state.deck.draw());
        state.dealerCards.add(state.deck.draw());
        state.playerCards.add(state.deck.draw());
        state.dealerCards.add(state.deck.draw());

        session.setGameContext(state);

        return "🃏 Блэкджек начался!\n" +
                "Твои карты: " + state.playerCards + " (Очки: " + calculateScore(state.playerCards) + ")\n" +
                "Карта дилера: " + state.dealerCards.get(0) + "\n" +
                "Напиши 'взять' или 'хватит'.";
    }

    @Override
    public String move(GameSession session, String playerMove) {
        BlackJackState state = (BlackJackState) session.getGameContext();
        if (state == null) return "Ошибка: игра не найдена.";

        if (playerMove.equals("взять")) {
            Card card = state.deck.draw();
            state.playerCards.add(card);
            int score = calculateScore(state.playerCards);

            if (score > 21) {
                return "Ты взял: " + card + "\nТвои карты: " + state.playerCards + " (Очки: " + score + ")\n💀 Перебор! Ты проиграл.";
            }
            return "Ты взял: " + card + "\nТвои карты: " + state.playerCards + " (Очки: " + score + ")\nЕщё 'взять' или 'хватит'?";

        } else if (playerMove.equals("хватит")) {
            while (calculateScore(state.dealerCards) < 17) {
                state.dealerCards.add(state.deck.draw());
            }

            int playerScore = calculateScore(state.playerCards);
            int dealerScore = calculateScore(state.dealerCards);

            String result = "Твои очки: " + playerScore + "\nКарты дилера: " + state.dealerCards + " (Очки: " + dealerScore + ")\n";

            if (dealerScore > 21) return result + "🎉 Дилер перебрал! Ты выиграл!";
            else if (playerScore > dealerScore) return result + "🎉 Ты выиграл!";
            else if (playerScore < dealerScore) return result + "💀 Дилер выиграл.";
            else return result + "🤝 Ничья.";
        }

        return "Напиши 'взять' или 'хватит'.";
    }

    int calculateScore(java.util.List<Card> cards) {
        int score = 0;
        int aces = 0;
        for (Card c : cards) {
            score += c.getPoints();
            if (c.getPoints() == 11) aces++;
        }
        while (score > 21 && aces > 0) {
            score -= 10;
            aces--;
        }
        return score;
    }
}
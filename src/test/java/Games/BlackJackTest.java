package Games;

import BotServices.GameSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlackJackTest {

    private BlackJack game;
    private GameSession session;

    @BeforeEach
    void setUp() {
        game = new BlackJack();
        session = new GameSession(12345L); // тестовый юзер
    }

    // Вспомогательный метод для создания карт
    private Card card(String rank, int points) {
        return new Card(rank, "♠", points);
    }

    @Test
    void testStartGame() {
        String response = game.start(session);

        // Проверяем, что игра началась и карты раздались
        assertTrue(response.contains("Блэкджек начался"));
        assertNotNull(session.getGameContext(), "Состояние игры должно сохраниться в сессию");

        BlackJackState state = (BlackJackState) session.getGameContext();
        assertEquals(2, state.playerCards.size(), "Игроку должно быть раздано 2 карты");
        assertEquals(2, state.dealerCards.size(), "Дилеру должно быть раздано 2 карты");
    }

    @Test
    void testCalculateScore() {
        // Тестируем обычный подсчет
        List<Card> cards = new ArrayList<>();
        cards.add(card("10", 10));
        cards.add(card("7", 7));
        assertEquals(17, game.calculateScore(cards), "10 + 7 = 17");

        // Тестируем тузы (11 + 10 = 21, один туз как 11)
        cards.clear();
        cards.add(card("A", 11));
        cards.add(card("K", 10));
        assertEquals(21, game.calculateScore(cards), "A + K = 21");

        // Тестируем два туза (11 + 11 = 22 -> 1 + 11 = 12)
        cards.clear();
        cards.add(card("A", 11));
        cards.add(card("A", 11));
        assertEquals(12, game.calculateScore(cards), "Два туза должны давать 12");

        // Тестируем перебор (10 + 10 + 5 = 25)
        cards.clear();
        cards.add(card("10", 10));
        cards.add(card("10", 10));
        cards.add(card("5", 5));
        assertEquals(25, game.calculateScore(cards), "Перебор");
    }

    @Test
    void testMoveTakeCard() {
        game.start(session);
        BlackJackState state = (BlackJackState) session.getGameContext();
        int initialSize = state.playerCards.size();

        // Игрок берет карту
        String response = game.move(session, "взять");

        assertTrue(response.contains("Ты взял"));
        assertEquals(initialSize + 1, state.playerCards.size(), "У игрока должно стать на 1 карту больше");
    }

    @Test
    void testMoveInvalidInput() {
        game.start(session);

        // Игрок пишет что-то непонятное
        String response = game.move(session, "абракадабра");

        assertEquals("Напиши 'взять' или 'хватит'.", response);
    }
}
package Games;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RideTheBus: этапы, краевые случаи и некорректный ввод")
class RideTheBusTest {

    private RideTheBus bus;
    private static final long USER_1 = 111L;
    private static final long USER_2 = 222L;

    @BeforeEach
    void setUp() {
        bus = new RideTheBus();
    }

    // ---------- helper для подмены колоды (чтобы тесты были детерминированными) ----------
    @SuppressWarnings("unchecked")
    private void setNextCards(long userId, Card... cards) throws Exception {
        Field gamesField = RideTheBus.class.getDeclaredField("games");
        gamesField.setAccessible(true);
        Map<Long, Object> games = (Map<Long, Object>) gamesField.get(bus);
        Object state = games.get(userId);

        Field deckField = state.getClass().getDeclaredField("deck");
        deckField.setAccessible(true);
        List<Card> deck = (List<Card>) deckField.get(state);

        deck.clear();
        deck.addAll(Arrays.asList(cards));
    }

    // Базовые проверки

    @Test
    @DisplayName("start() инициализирует игру и возвращает правила 1-го этапа")
    void startInitializesGame() {
        String result = bus.start(USER_1);
        assertTrue(result.contains("Этап 1/4"));
        assertTrue(result.contains("red или black"));
    }

    @Test
    @DisplayName("Ход без старта игры возвращает ошибку")
    void moveWithoutStartReturnsError() {
        String result = bus.move(USER_1, "red");
        assertEquals("Игра не начата. Введи /play, чтобы начать.", result);
    }

    @Test
    @DisplayName("Некорректный ввод на этапе 1 не пропускает дальше")
    void invalidInputStage1() {
        bus.start(USER_1);
        String result = bus.move(USER_1, "зеленая");
        assertEquals("Введи red или black.", result);
    }


    // Этап 1: Red / Black

    @Test
    @DisplayName("Угадал цвет: переход на этап 2")
    void correctRedBlackAdvances() throws Exception {
        bus.start(USER_1);
        setNextCards(USER_1, new Card("5", "♥", 5)); // Красная

        String result = bus.move(USER_1, "red");
        assertTrue(result.contains("✅ Верно! Карта: 5♥"));
        assertTrue(result.contains("Этап 2/4"));
    }

    @Test
    @DisplayName("Не угадал цвет: вылет")
    void incorrectRedBlackLoses() throws Exception {
        bus.start(USER_1);
        setNextCards(USER_1, new Card("5", "♥", 5)); // Красная

        String result = bus.move(USER_1, "black");
        assertTrue(result.contains("❌ Вылет на этапе 1/4"));
    }

    // Этап 2: Higher / Lower

    @Test
    @DisplayName("Угадал higher: переход на этап 3")
    void correctHigherAdvances() throws Exception {
        bus.start(USER_1);
        setNextCards(USER_1,
                new Card("5", "♥", 5),   // этап 1
                new Card("10", "♠", 10)  // этап 2
        );
        bus.move(USER_1, "red");

        String result = bus.move(USER_1, "higher");
        assertTrue(result.contains("✅ Верно! Карта: 10♠"));
        assertTrue(result.contains("Этап 3/4"));
    }


    // Этап 3: Inside / Outside


    @Test
    @DisplayName("Угадал outside: переход на этап 4")
    void correctOutsideAdvances() throws Exception {
        bus.start(USER_1);
        setNextCards(USER_1,
                new Card("5", "♥", 5),   // этап 1
                new Card("10", "♠", 10), // этап 2
                new Card("K", "♦", 13)   // этап 3 (снаружи 5 и 10)
        );
        bus.move(USER_1, "red");
        bus.move(USER_1, "higher");

        String result = bus.move(USER_1, "outside");
        assertTrue(result.contains("✅ Верно! Карта: K♦"));
        assertTrue(result.contains("Этап 4/4"));
    }


    // Этап 4: Suit


    @Test
    @DisplayName("Угадал масть: победа")
    void correctSuitWins() throws Exception {
        bus.start(USER_1);
        setNextCards(USER_1,
                new Card("2", "♥", 2),
                new Card("5", "♠", 5),
                new Card("K", "♦", 13),
                new Card("A", "♣", 11) // этап 4
        );
        bus.move(USER_1, "red");
        bus.move(USER_1, "higher");
        bus.move(USER_1, "outside");

        String result = bus.move(USER_1, "clubs");
        assertTrue(result.contains("🎉 Поздравляю, ты проехал на автобусе! Все 4 этапа пройдены."));
    }

    @Test
    @DisplayName("Не угадал масть: вылет")
    void incorrectSuitLoses() throws Exception {
        bus.start(USER_1);
        setNextCards(USER_1,
                new Card("2", "♥", 2),
                new Card("5", "♠", 5),
                new Card("K", "♦", 13),
                new Card("A", "♣", 11) // этап 4 (♣)
        );
        bus.move(USER_1, "red");
        bus.move(USER_1, "higher");
        bus.move(USER_1, "outside");

        String result = bus.move(USER_1, "hearts");
        assertTrue(result.contains("❌ Вылет на этапе 4/4"));
    }


    // Завершение и изоляция


    @Test
    @DisplayName("Ход после окончания игры возвращает сообщение об окончании")
    void moveAfterFinishedReturnsGameOver() throws Exception {
        bus.start(USER_1);
        setNextCards(USER_1, new Card("5", "♥", 5));
        bus.move(USER_1, "black"); // Вылет на этапе 1

        String result = bus.move(USER_1, "red");
        assertEquals("Игра окончена. Введи /cancel, чтобы выйти.", result);
    }

    @Test
    @DisplayName("Изоляция: партии двух пользователей независимы")
    void usersAreIsolated() {
        bus.start(USER_1);
        bus.start(USER_2);

        String res1 = bus.move(USER_1, "black"); // рандомно, может вылететь, может нет
        String res2 = bus.move(USER_2, "зеленый"); // точно ошибка ввода

        assertEquals("Введи red или black.", res2, "user2 должен быть на этапе 1");
    }
}
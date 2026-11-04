package BotServices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("GameService: список игр и обработка неизвестных игр")
class GameServiceTest {

    private GameService service;

    @BeforeEach
    void setUp() {
        service = new GameService();
    }

    // 1. getGamesList — формат и порядок

    @Test
    @DisplayName("getGamesList содержит 'blackjack' и 'other'")
    void gamesListContainsKnownGames() {
        String list = service.getGamesList();

        assertTrue(list.contains("blackjack"));
        assertTrue(list.contains("other"));
    }

    @Test
    @DisplayName("getGamesList нумерует с 1: '1) blackjack', '2) other'")
    void gamesListIsNumbered() {
        String list = service.getGamesList();

        assertTrue(list.contains("1) blackjack"),
                "должно быть '1) blackjack', было:\n" + list);
        assertTrue(list.contains("2) other"),
                "должно быть '2) other', было:\n" + list);
    }

    @Test
    @DisplayName("Порядок игр стабилен (LinkedHashMap): blackjack раньше other")
    void gamesListOrderIsStable() {
        String list = service.getGamesList();

        int idxBlackjack = list.indexOf("blackjack");
        int idxOther     = list.indexOf("other");

        assertTrue(idxBlackjack >= 0 && idxOther >= 0);
        assertTrue(idxBlackjack < idxOther,
                "blackjack должен идти первым (LinkedHashMap)");
    }

    // 2. startGame — корректный / некорректный gameId

    @Test
    @DisplayName("startGame('nonexistent') → 'Неизвестная игра'")
    void startUnknownGameReturnsError() {
        assertEquals("Неизвестная игра", service.startGame("nonexistent", 1L));
    }

    @Test
    @DisplayName("startGame(null) → 'Неизвестная игра' (не падает)")
    void startNullGameReturnsError() {
        assertEquals("Неизвестная игра", service.startGame(null, 1L));
    }

    @Test
    @DisplayName("startGame('') → 'Неизвестная игра'")
    void startEmptyGameReturnsError() {
        assertEquals("Неизвестная игра", service.startGame("", 1L));
    }

    @Test
    @DisplayName("startGame('BLACKJACK') → 'Неизвестная игра' (регистр важен)")
    void startGameIsCaseSensitive() {
        // gameId хранится как "blackjack" (lower case), поэтому "BLACKJACK" не найдётся
        assertEquals("Неизвестная игра", service.startGame("BLACKJACK", 1L));
    }

    // 3. handleMove — корректный / некорректный gameId

    @Test
    @DisplayName("handleMove('nonexistent') → 'Неизвестная игра'")
    void moveUnknownGameReturnsError() {
        assertEquals("Неизвестная игра", service.handleMove("nonexistent", 1L, "hit"));
    }

    @Test
    @DisplayName("handleMove(null) → 'Неизвестная игра' (не падает)")
    void moveNullGameReturnsError() {
        assertEquals("Неизвестная игра", service.handleMove(null, 1L, "hit"));
    }

    @Test
    @DisplayName("handleMove('') → 'Неизвестная игра'")
    void moveEmptyGameReturnsError() {
        assertEquals("Неизвестная игра", service.handleMove("", 1L, "hit"));
    }

    // 4. Делегирование известным играм — не падает

    @Test
    @DisplayName("startGame('blackjack') делегирует без исключений")
    void startKnownGameDelegates() {
        assertDoesNotThrow(() -> service.startGame("blackjack", 1L),
                "startGame не должен бросать исключение для зарегистрированной игры");
    }

    @Test
    @DisplayName("handleMove('blackjack', ...) делегирует без исключений")
    void moveKnownGameDelegates() {
        assertDoesNotThrow(() -> service.handleMove("blackjack", 1L, "hit"));
    }

    @Test
    @DisplayName("startGame('other') тоже работает (не только blackjack)")
    void startOtherGameDelegates() {
        assertDoesNotThrow(() -> service.startGame("other", 1L));
    }
}
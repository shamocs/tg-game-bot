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

    @Test
    @DisplayName("getGamesList содержит 'blackjack' и 'ride the bus'")
    void gamesListContainsKnownGames() {
        String list = service.getGamesList();
        assertTrue(list.contains("blackjack"));
        assertTrue(list.contains("ride the bus"));
    }

    @Test
    @DisplayName("getGamesList нумерует с 1")
    void gamesListIsNumbered() {
        String list = service.getGamesList();
        assertTrue(list.contains("1) blackjack"));
        assertTrue(list.contains("2) ride the bus"));
    }

    @Test
    @DisplayName("Порядок игр стабилен")
    void gamesListOrderIsStable() {
        String list = service.getGamesList();
        assertTrue(list.indexOf("blackjack") < list.indexOf("ride the bus"));
    }

    @Test
    @DisplayName("startGame('nonexistent') → 'Неизвестная игра'")
    void startUnknownGameReturnsError() {
        assertEquals("Неизвестная игра", service.startGame("nonexistent", new GameSession(1L)));
    }

    @Test
    @DisplayName("startGame(null) → 'Неизвестная игра'")
    void startNullGameReturnsError() {
        assertEquals("Неизвестная игра", service.startGame(null, new GameSession(1L)));
    }

    @Test
    @DisplayName("startGame('') → 'Неизвестная игра'")
    void startEmptyGameReturnsError() {
        assertEquals("Неизвестная игра", service.startGame("", new GameSession(1L)));
    }

    @Test
    @DisplayName("startGame('BLACKJACK') → 'Неизвестная игра'")
    void startGameIsCaseSensitive() {
        assertEquals("Неизвестная игра", service.startGame("BLACKJACK", new GameSession(1L)));
    }

    @Test
    @DisplayName("handleMove('nonexistent') → 'Неизвестная игра'")
    void moveUnknownGameReturnsError() {
        assertEquals("Неизвестная игра", service.handleMove("nonexistent", new GameSession(1L), "hit"));
    }

    @Test
    @DisplayName("handleMove(null) → 'Неизвестная игра'")
    void moveNullGameReturnsError() {
        assertEquals("Неизвестная игра", service.handleMove(null, new GameSession(1L), "hit"));
    }

    @Test
    @DisplayName("handleMove('') → 'Неизвестная игра'")
    void moveEmptyGameReturnsError() {
        assertEquals("Неизвестная игра", service.handleMove("", new GameSession(1L), "hit"));
    }

    @Test
    @DisplayName("startGame('blackjack') делегирует без исключений")
    void startKnownGameDelegates() {
        assertDoesNotThrow(() -> service.startGame("blackjack", new GameSession(1L)));
    }

    @Test
    @DisplayName("handleMove('blackjack', ...) делегирует без исключений")
    void moveKnownGameDelegates() {
        assertDoesNotThrow(() -> service.handleMove("blackjack", new GameSession(1L), "hit"));
    }

    @Test
    @DisplayName("startGame('ride the bus') тоже работает")
    void startOtherGameDelegates() {
        assertDoesNotThrow(() -> service.startGame("ride the bus", new GameSession(1L)));
    }
}
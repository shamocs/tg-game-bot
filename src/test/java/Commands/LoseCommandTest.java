package Commands;

import Bot.GameBot;
import BotServices.BotState;
import BotServices.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("LoseCommand: проигрыш только в игре, вне игры — отказ")
class LoseCommandTest {

    private static final long USER_1 = 111L;
    private static final long USER_2 = 222L;

    private GameBot bot;
    private SessionManager sessions;
    private LoseCommand lose;

    @BeforeEach
    void setUp() {
        bot = mock(GameBot.class);
        sessions = new SessionManager();
        lose = new LoseCommand(sessions, bot);
    }

    private List<String> sentTo(long chatId) {
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(bot, atLeast(0)).sendText(eq(chatId), captor.capture());
        return captor.getAllValues();
    }


    // 1. Вне игры → отказ


    @Test
    @DisplayName("Вне игры (нет сессии) → 'you are not in the game', сессия не создаётся")
    void loseOutsideGameRejects() {
        assertNull(sessions.get(USER_1), "до вызова сессии быть не должно");

        lose.execute(USER_1, "");

        assertTrue(sentTo(USER_1).stream()
                        .anyMatch(s -> s.contains("not in the game")),
                "должно прийти 'you are not in the game'");
        assertNull(sessions.get(USER_1), "lose не должен создавать сессию");
    }

    @Test
    @DisplayName("Вне игры (IDLE) → отказ, состояние остаётся IDLE")
    void loseInIdleRejects() {
        sessions.getOrCreate(USER_1);   // создаём, но остаёмся в IDLE
        assertEquals(BotState.IDLE, sessions.get(USER_1).getState());

        lose.execute(USER_1, "");

        assertEquals(BotState.IDLE, sessions.get(USER_1).getState(),
                "вне игры /lose не должен менять состояние");
        assertTrue(sentTo(USER_1).stream()
                .anyMatch(s -> s.contains("not in the game")));
    }


    // 2. В игре → GAME_OVER


    @Test
    @DisplayName("В CHOOSING_GAME → GAME_OVER + 'Игра окончена'")
    void loseDuringChoosingGame() {
        sessions.transition(USER_1, BotState.CHOOSING_GAME);

        lose.execute(USER_1, "");

        assertEquals(BotState.GAME_OVER, sessions.get(USER_1).getState());
        assertTrue(sentTo(USER_1).stream()
                .anyMatch(s -> s.contains("Игра окончена")));
    }

    @Test
    @DisplayName("В WAITING_FOR_MOVE → тоже GAME_OVER")
    void loseDuringWaitingMove() {
        sessions.transition(USER_1, BotState.WAITING_FOR_MOVE);

        lose.execute(USER_1, "");

        assertEquals(BotState.GAME_OVER, sessions.get(USER_1).getState());
    }

    @Test
    @DisplayName("В GAME_OVER → остаётся GAME_OVER (hasActive == true для не-IDLE)")
    void loseInGameOverStaysGameOver() {
        sessions.transition(USER_1, BotState.GAME_OVER);

        lose.execute(USER_1, "");

        assertEquals(BotState.GAME_OVER, sessions.get(USER_1).getState());
    }


    // 3. Контракт execute(): возвращает "" (сообщение шлётся самим ботом)

    @Test
    @DisplayName("execute возвращает пустую строку — сообщение шлёт сам LoseCommand через bot")
    void executeReturnsEmpty() {
        sessions.transition(USER_1, BotState.CHOOSING_GAME);

        String result = lose.execute(USER_1, "");

        assertEquals("", result,
                "LoseCommand сам шлёт боту, поэтому возвращает пустую строку");
    }

    @Test
    @DisplayName("arg игнорируется: execute(chatId, 'anything') работает так же")
    void argIsIgnored() {
        sessions.transition(USER_1, BotState.CHOOSING_GAME);

        lose.execute(USER_1, "сюда_можно_что_угодно");

        assertEquals(BotState.GAME_OVER, sessions.get(USER_1).getState());
    }


    // 4. Изоляция пользователей


    @Test
    @DisplayName("Изоляция: /lose от user1 не трогает игру user2")
    void loseDoesNotAffectOtherUser() {
        sessions.transition(USER_1, BotState.CHOOSING_GAME);
        sessions.transition(USER_2, BotState.WAITING_FOR_MOVE);

        lose.execute(USER_1, "");

        assertEquals(BotState.GAME_OVER,        sessions.get(USER_1).getState());
        assertEquals(BotState.WAITING_FOR_MOVE, sessions.get(USER_2).getState(),
                "user2 всё ещё в игре");
    }

    @Test
    @DisplayName("Изоляция: сообщения идут в чат того, кто вызвал /lose")
    void botRepliesGoToCorrectChat() {
        sessions.transition(USER_1, BotState.CHOOSING_GAME);

        lose.execute(USER_1, "");

        assertTrue(sentTo(USER_1).stream()
                        .anyMatch(s -> s.contains("Игра окончена")),
                "user1 должен получить 'Игра окончена'");
        assertTrue(sentTo(USER_2).isEmpty(),
                "user2 не должен получить ничего");
    }

    // 5. Защита от мусора

    @Test
    @DisplayName("Неизвестный chatId не падает и не создаёт сессию")
    void unknownChatIdDoesNotCrash() {
        assertDoesNotThrow(() -> lose.execute(999L, ""));
        assertNull(sessions.get(999L));
    }
}
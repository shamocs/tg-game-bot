package BotServices;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SessionManager: изоляция пользователей и переходы состояний")
class SessionManagerTest {

    private SessionManager manager;
    private static final long USER_1 = 111L;
    private static final long USER_2 = 222L;

    @BeforeEach
    void setUp() {
        manager = new SessionManager();
    }

    // ---------- базовые операции ----------

    @Test
    @DisplayName("getOrCreate создаёт сессию для нового пользователя")
    void getOrCreateCreatesSessionForNewUser() {
        assertNull(manager.get(USER_1), "до вызова сессии быть не должно");

        var session = manager.getOrCreate(USER_1);

        assertNotNull(session);
        assertNotNull(manager.get(USER_1), "сессия должна существовать после getOrCreate");
    }

    @Test
    @DisplayName("getOrCreate для существующего возвращает тот же объект")
    void getOrCreateReturnsSameSession() {
        var first = manager.getOrCreate(USER_1);
        var second = manager.getOrCreate(USER_1);

        assertSame(first, second, "нельзя пересоздавать сессию");
    }

    @Test
    @DisplayName("get для неизвестного пользователя возвращает null")
    void getForUnknownUserReturnsNull() {
        assertNull(manager.get(USER_1));
    }

    @Test
    @DisplayName("remove удаляет только нужную сессию")
    void removeDeletesOnlyTargetSession() {
        manager.getOrCreate(USER_1);
        manager.getOrCreate(USER_2);

        manager.remove(USER_1);

        assertNull(manager.get(USER_1), "user1 должен быть удалён");
        assertNotNull(manager.get(USER_2), "user2 должен остаться");
    }

    // ---------- изоляция пользователей ----------

    @Test
    @DisplayName("Изоляция: состояние user1 не влияет на user2")
    void sessionsAreIsolatedBetweenUsers() {
        var session1 = manager.getOrCreate(USER_1);
        assertNull(manager.get(USER_2), "user2 не должен появиться сам по себе");

        manager.transition(USER_1, BotState.CHOOSING_GAME);

        assertNull(manager.get(USER_2), "user2 всё ещё не должен существовать");
        assertEquals(BotState.CHOOSING_GAME, session1.getState());
    }

    @Test
    @DisplayName("Изоляция: у двух пользователей независимые состояния")
    void twoUsersHaveIndependentStates() {
        manager.getOrCreate(USER_1);
        manager.getOrCreate(USER_2);

        manager.transition(USER_1, BotState.CHOOSING_GAME);
        manager.transition(USER_2, BotState.WAITING_FOR_MOVE);

        assertEquals(BotState.CHOOSING_GAME, manager.get(USER_1).getState());
        assertEquals(BotState.WAITING_FOR_MOVE, manager.get(USER_2).getState());
    }

    // ---------- переходы состояний ----------

    @Test
    @DisplayName("transition меняет состояние только целевого пользователя")
    void transitionChangesOnlyTargetUser() {
        manager.getOrCreate(USER_1);
        manager.getOrCreate(USER_2);
        BotState initialState = manager.get(USER_2).getState();

        manager.transition(USER_1, BotState.WAITING_FOR_MOVE);

        assertEquals(BotState.WAITING_FOR_MOVE, manager.get(USER_1).getState());
        assertEquals(initialState, manager.get(USER_2).getState());
    }

    @Test
    @DisplayName("transition для неизвестного пользователя создаёт сессию")
    void transitionForUnknownUserCreatesSession() {
        assertNull(manager.get(USER_1));

        manager.transition(USER_1, BotState.CHOOSING_GAME);

        assertNotNull(manager.get(USER_1), "transition должен создать сессию");
        assertEquals(BotState.CHOOSING_GAME, manager.get(USER_1).getState());
    }

    // ---------- hasActive ----------

    @Test
    @DisplayName("hasActive: false для свежей IDLE-сессии, true после перехода")
    void hasActiveReflectsState() {
        manager.getOrCreate(USER_1);

        assertFalse(manager.hasActive(USER_1),
                "свежая IDLE-сессия не должна считаться активной");

        manager.transition(USER_1, BotState.CHOOSING_GAME);

        assertTrue(manager.hasActive(USER_1),
                "после перехода из IDLE сессия активна");
    }

// ---------- helper ----------

    /**
     * Сдвигает lastActivity сессии в прошлое на by
     */
    private static void ageSession(GameSession session, Duration by) throws Exception {
        Field lastActivity = GameSession.class.getDeclaredField("lastActivity");
        lastActivity.setAccessible(true);
        lastActivity.set(session, Instant.now().minus(by));
    }

// ---------- таймаут: граница и истечение ----------

    @Test
    @DisplayName("Сессия НЕ протухает до 30 минут")
    void sessionIsAliveBeforeTimeout() throws Exception {
        var session = manager.getOrCreate(USER_1);
        manager.transition(USER_1, BotState.CHOOSING_GAME);

        ageSession(session, Duration.ofMinutes(29));

        assertSame(session, manager.get(USER_1),
                "на 29-й минуте сессия должна быть жива и та же самая");
        assertTrue(manager.hasActive(USER_1),
                "на 29-й минуте сессия должна быть активна (если состояние не IDLE)");
    }

    @Test
    @DisplayName("Сессия протухает после 30 минут: get()=null, hasActive=false, удалена из мапы")
    void sessionExpiresAfterTimeout() throws Exception {
        var session = manager.getOrCreate(USER_1);
        manager.transition(USER_1, BotState.CHOOSING_GAME); // чтобы isActive()==true

        // Контроль «до»: сессия жива и активна
        assertSame(session, manager.get(USER_1), "до состаривания сессия должна быть доступна");
        assertTrue(manager.hasActive(USER_1), "до состаривания сессия должна быть активна");

        // Состариваем на 31 минуту
        ageSession(session, Duration.ofMinutes(31));

        // 1) get() видит протухание и возвращает null
        assertNull(manager.get(USER_1), "протухшая сессия должна вернуться как null");

        // 2) hasActive тоже false
        assertFalse(manager.hasActive(USER_1), "протухшая сессия не должна быть активной");

        // 3) Сессия реально удалена из мапы: getOrCreate создаёт НОВУЮ
        var fresh = manager.getOrCreate(USER_1);
        assertNotSame(session, fresh, "протухшая сессия должна быть удалена из мапы");
        assertEquals(BotState.IDLE, fresh.getState(), "новая сессия стартует в IDLE");
    }
}
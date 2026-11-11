package Bot;

import BotServices.BotState;
import BotServices.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("CommandHandler: маршрутизация, контекст, изоляция пользователей")
class CommandHandlerTest {

    private GameBot bot;
    private CommandHandler handler;
    private SessionManager sessions;

    private static final long USER_1 = 111L;
    private static final long USER_2 = 222L;

    @BeforeEach
    void setUp() throws Exception {
        bot = mock(GameBot.class);              // Objenesis: конструктор GameBot не выполняется,
        // поле handler внутри бота не создаётся — рекурсии нет
        handler = new CommandHandler(bot);
        sessions = extractSessions(handler);    // sessions создаётся внутри CommandHandler
    }

    /* CommandHandler не даёт доступа к sessions (нет DI) — достаём рефлексией. */
    private static SessionManager extractSessions(CommandHandler handler) throws Exception {
        Field f = CommandHandler.class.getDeclaredField("sessions");
        f.setAccessible(true);
        return (SessionManager) f.get(handler);
    }

    //  helpers

    private Update textUpdate(long chatId, String text) {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(true);
        when(message.getText()).thenReturn(text);
        when(message.getChatId()).thenReturn(chatId);
        return update;
    }

    /* Все тексты, отправленные ботом в указанный чат, в порядке вызовов. */
    private List<String> sentTo(long chatId) {
        ArgumentCaptor<String> texts = ArgumentCaptor.forClass(String.class);
        verify(bot, atLeast(0)).sendText(eq(chatId), texts.capture());
        return texts.getAllValues();
    }

    private static boolean anyContains(List<String> messages, String fragment) {
        return messages.stream().anyMatch(m -> m.contains(fragment));
    }

    // 1. Фильтр входящих данных (некорректные данные)

    @Test
    @DisplayName("Update без сообщения — бот ничего не отправляет")
    void updateWithoutMessageIsIgnored() {
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(false);

        handler.handle(update);

        verifyNoInteractions(bot);
    }

    @Test
    @DisplayName("Сообщение без текста (стикер/фото) — бот ничего не отправляет")
    void messageWithoutTextIsIgnored() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
        when(message.hasText()).thenReturn(false);

        handler.handle(update);

        verifyNoInteractions(bot);
    }

    // 2. IDLE: не-команда, несуществующая команда, регистр

    @Test
    @DisplayName("IDLE + текст без слеша → 'Введите команду!'")
    void plainTextInIdleAsksForCommand() {
        handler.handle(textUpdate(USER_1, "привет"));

        assertTrue(anyContains(sentTo(USER_1), "Введите команду"));
    }

    @Test
    @DisplayName("IDLE + несуществующая команда → 'Неизвестная команда'")
    void unknownCommandInIdle() {
        handler.handle(textUpdate(USER_1, "/unknown"));

        assertTrue(anyContains(sentTo(USER_1), "Неизвестная команда"));
    }

    @Test
    @DisplayName("IDLE + /HELP эквивалентен /help (CommandHandler приводит к lower case)")
    void commandIsCaseInsensitive() {
        handler.handle(textUpdate(USER_1, "/HELP"));

        List<String> sent = sentTo(USER_1);
        assertFalse(anyContains(sent, "Неизвестная команда"),
                "регистр не должен влиять на поиск команды");
        assertTrue(anyContains(sent, "Доступные команды"),
                "должен прийти список команд от HelpCommand");
    }

    // 3. HelpCommand: корректный / некорректный / безслэшный аргумент

    @Test
    @DisplayName("IDLE + /help → 'Доступные команды'")
    void helpWithoutArgListsCommands() {
        handler.handle(textUpdate(USER_1, "/help"));

        assertTrue(anyContains(sentTo(USER_1), "Доступные команды"));
    }

    @Test
    @DisplayName("IDLE + /help /play → getHelp() команды play ('Начать игру')")
    void helpWithKnownArgReturnsHelp() {
        handler.handle(textUpdate(USER_1, "/help /play"));

        assertTrue(anyContains(sentTo(USER_1), "Начать игру"),
                "должна прийти справка именно по /play");
    }

    @Test
    @DisplayName("IDLE + /help play (без слеша) → тот же результат, HelpCommand добавляет слеш")
    void helpArgWithoutSlashWorks() {
        handler.handle(textUpdate(USER_1, "/help play"));

        assertTrue(anyContains(sentTo(USER_1), "Начать игру"),
                "HelpCommand должен уметь принимать имя без слеша");
    }

    @Test
    @DisplayName("IDLE + /help /unknown → 'Команда не найдена'")
    void helpWithUnknownArgReportsNotFound() {
        handler.handle(textUpdate(USER_1, "/help /unknown"));

        assertTrue(anyContains(sentTo(USER_1), "Команда не найдена"));
    }


    // 4. Переход в игру и корректный/некорректный выбор

    @Test
    @DisplayName("/play в IDLE → CHOOSING_GAME + 'Выбери игру'")
    void playInIdleStartsChoosingGame() {
        handler.handle(textUpdate(USER_1, "/play"));

        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_1).getState());
        assertTrue(anyContains(sentTo(USER_1), "Выбери игру"));
    }

    @Test
    @DisplayName("Некорректный выбор игры ('abc') → 'Выберите номер игры', состояние не меняется")
    void invalidGameChoiceKeepsChoosingState() {
        handler.handle(textUpdate(USER_1, "/play"));
        handler.handle(textUpdate(USER_1, "abc"));

        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_1).getState(),
                "некорректный ввод не должен менять состояние");
        assertTrue(anyContains(sentTo(USER_1), "Выберите номер игры"));
    }

    @Test
    @DisplayName("Выбор '1' в CHOOSING_GAME → WAITING_FOR_MOVE + 'waiting for your move'")
    void validGameChoiceTransitionsToWaitingForMove() {
        handler.handle(textUpdate(USER_1, "/play"));
        handler.handle(textUpdate(USER_1, "1"));

        assertEquals(BotState.WAITING_FOR_MOVE, sessions.get(USER_1).getState());
    }

    // 5. КОНТЕКСТ: доступность команд зависит от состояния


    @Test
    @DisplayName("КОНТЕКСТ: /play в CHOOSING_GAME НЕ запускает новую игру, а идёт как 'некорректный выбор'")
    void playCommandIsNotAvailableDuringChoosingGame() {
        handler.handle(textUpdate(USER_1, "/play"));      // → CHOOSING_GAME

        handler.handle(textUpdate(USER_1, "/play"));      // второй /play в игре

        List<String> sent = sentTo(USER_1);
        assertTrue(anyContains(sent, "Выберите номер игры"),
                "в CHOOSING_GAME /play должен попасть в handleChoosing, а не в PlayCommand");
        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_1).getState(),
                "состояние не должно сброситься заново через PlayCommand");
    }

    @Test
    @DisplayName("КОНТЕКСТ: /help в CHOOSING_GAME не вызывает HelpCommand, а идёт как 'некорректный выбор'")
    void helpCommandIsNotAvailableDuringChoosingGame() {
        handler.handle(textUpdate(USER_1, "/play"));
        handler.handle(textUpdate(USER_1, "/help"));

        List<String> sent = sentTo(USER_1);
        assertTrue(anyContains(sent, "Выберите номер игры"),
                "в CHOOSING_GAME /help должен попасть в handleChoosing");
        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_1).getState());
    }

    @Test
    @DisplayName("КОНТЕКСТ: /play в GAME_OVER работает — переход обратно в CHOOSING_GAME")
    void playCommandWorksInGameOver() {
        handler.handle(textUpdate(USER_1, "/play"));                    // → CHOOSING_GAME
        sessions.transition(USER_1, BotState.GAME_OVER);                // принудительно в GAME_OVER

        handler.handle(textUpdate(USER_1, "/play"));                    // в GAME_OVER /play разрешён

        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_1).getState(),
                "в GAME_OVER /play должен запускать новую игру");
        assertTrue(anyContains(sentTo(USER_1), "Выбери игру"));
    }

    @Test
    @DisplayName("КОНТЕКСТ: некорректный ввод в GAME_OVER → 'Игра окончена'")
    void invalidInputInGameOverSaysGameOver() {
        handler.handle(textUpdate(USER_1, "/play"));
        sessions.transition(USER_1, BotState.GAME_OVER);

        handler.handle(textUpdate(USER_1, "привет"));

        assertTrue(anyContains(sentTo(USER_1), "Игра окончена"),
                "в GAME_OVER любой ввод кроме /play и /cancel → 'Игра окончена'");
    }

    // 6. Сквозные команды: /cancel

    @Test
    @DisplayName("/cancel в CHOOSING_GAME → сессия удалена + 'Вышел из игры'")
    void cancelDuringGameRemovesSession() {
        handler.handle(textUpdate(USER_1, "/play"));
        handler.handle(textUpdate(USER_1, "/cancel"));

        assertNull(sessions.get(USER_1), "после /cancel сессии быть не должно");
        assertTrue(anyContains(sentTo(USER_1), "Вышел из игры"));
    }

    @Test
    @DisplayName("/CANCEL (верхний регистр) тоже работает — CommandHandler приводит к lower case")
    void cancelIsCaseInsensitive() {
        handler.handle(textUpdate(USER_1, "/play"));
        handler.handle(textUpdate(USER_1, "/CANCEL"));

        assertNull(sessions.get(USER_1));
        assertTrue(anyContains(sentTo(USER_1), "Вышел из игры"));
    }


    // 7. ИЗОЛЯЦИЯ пользователей

    @Test
    @DisplayName("Изоляция: /help от user2 не влияет на user1 в игре")
    void usersAreIsolated() {
        handler.handle(textUpdate(USER_1, "/play"));
        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_1).getState());

        handler.handle(textUpdate(USER_2, "/help"));

        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_1).getState(),
                "сессия user1 не должна пострадать");
        assertNull(sessions.get(USER_2), "help не должен создавать игровую сессию");
        assertFalse(anyContains(sentTo(USER_2), "Выберите номер игры"),
                "user2 не в игре — игрового ответа быть не должно");
    }

    @Test
    @DisplayName("Изоляция: /cancel одного пользователя не трогает игру другого")
    void cancelDoesNotAffectOtherUser() {
        handler.handle(textUpdate(USER_1, "/play"));
        handler.handle(textUpdate(USER_2, "/play"));
        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_1).getState());
        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_2).getState());

        handler.handle(textUpdate(USER_1, "/cancel"));

        assertNull(sessions.get(USER_1), "user1 вышел");
        assertEquals(BotState.CHOOSING_GAME, sessions.get(USER_2).getState(),
                "user2 всё ещё в CHOOSING_GAME");
    }

    @Test
    @DisplayName("Изоляция: ответы бота идут в чат того, кто писал")
    void botRepliesGoToCorrectChat() {
        handler.handle(textUpdate(USER_1, "/help"));
        handler.handle(textUpdate(USER_2, "/about"));

        // user1 получил help-текст, user2 — about-текст
        assertTrue(anyContains(sentTo(USER_1), "Доступные команды"));
        assertFalse(anyContains(sentTo(USER_2), "Доступные команды"),
                "user2 не должен получать ответ user1");
    }
}
package Games;

import BotServices.GameSession;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RideTheBus implements Game {

    private final Map<Long, State> games = new ConcurrentHashMap<>();
    private final Random random = new Random();

    private static final class State {
        List<Card> deck = new ArrayList<>();
        List<Card> drawn = new ArrayList<>();
        int stage = 0;
        boolean finished = false;
    }

    private int value(Card c) {
        switch (c.getRank()) {
            case "A": return 14;
            case "K": return 13;
            case "Q": return 12;
            case "J": return 11;
            default:  return Integer.parseInt(c.getRank());
        }
    }

    private List<Card> newDeck() {
        List<Card> deck = new ArrayList<>(52);
        String[] suits = {"♠", "♥", "♦", "♣"};
        for (String suit : suits) {
            for (int v = 2; v <= 10; v++) {
                deck.add(new Card(String.valueOf(v), suit, v));
            }
            deck.add(new Card("J", suit, 10));
            deck.add(new Card("Q", suit, 10));
            deck.add(new Card("K", suit, 10));
            deck.add(new Card("A", suit, 11));
        }
        Collections.shuffle(deck, random);
        return deck;
    }

    @Override
    public String getName() {
        return "ride the bus";
    }

    @Override
    public String start(GameSession session) { // <--- ИЗМЕНЕНО: принимает сессию
        State s = new State();
        s.deck.addAll(newDeck());
        games.put(session.getUserId(), s);

        return "🚌 Ride the Bus!\n\n"
                + "4 этапа, каждый неверный ответ — вылет.\n\n"
                + "Этап 1/4: Красная или чёрная карта?\n"
                + "Введи: red или black\n"
                + "/cancel — выйти из игры";
    }

    @Override
    public String move(GameSession session, String playerMove) { // <--- ИЗМЕНЕНО: принимает сессию
        long userId = session.getUserId();
        System.out.println("player[" + userId + "] moved: " + playerMove);

        State s = games.get(userId);
        if (s == null) {
            return "Игра не начата. Введи /play, чтобы начать.";
        }
        if (s.finished) {
            games.remove(userId);
            return "Игра окончена. Введи /cancel, чтобы выйти.";
        }

        String move = playerMove == null ? "" : playerMove.trim().toLowerCase(Locale.ROOT);
        switch (s.stage) {
            case 0: return stageRedBlack(s, move);
            case 1: return stageHigherLower(s, move);
            case 2: return stageInsideOutside(s, move);
            case 3: return stageSuit(s, move);
            default: return "Ошибка состояния, начни заново: /cancel → /play";
        }
    }

    private String stageRedBlack(State s, String move) {
        if (!move.equals("red") && !move.equals("black")) {
            return "Введи red или black.";
        }
        Card card = s.deck.remove(0);
        s.drawn.add(card);
        boolean isRed = card.getSuit().equals("♥") || card.getSuit().equals("♦");
        boolean correct = (move.equals("red") && isRed) || (move.equals("black") && !isRed);

        if (!correct) {
            s.finished = true;
            return "Ты написал " + move + ", а карта: " + card
                    + " (" + (isRed ? "красная" : "чёрная") + ")\n"
                    + "❌ Вылет на этапе 1/4.\n\n/cancel — выйти";
        }
        s.stage = 1;
        return "✅ Верно! Карта: " + card + "\n\n"
                + "Этап 2/4: Следующая карта выше или ниже " + card + "?\n"
                + "Введи: higher или lower\n"
                + "(A — старшая; если карта равна предыдущей — вылет)";
    }

    private String stageHigherLower(State s, String move) {
        if (!move.equals("higher") && !move.equals("lower")) {
            return "Введи higher или lower.";
        }
        Card prev = s.drawn.get(s.drawn.size() - 1);
        Card card;
        do {
            card = s.deck.remove(0);
        } while (value(card) == value(prev));

        s.drawn.add(card);
        int pv = value(prev);
        int cv = value(card);
        boolean correct = (move.equals("higher") && cv > pv)
                || (move.equals("lower") && cv < pv);

        if (!correct) {
            s.finished = true;
            return "Ты написал " + move + ", а карта: " + card + " (была " + prev + ")\n"
                    + "❌ Вылет на этапе 2/4.\n\n/cancel — выйти";
        }
        s.stage = 2;
        return "✅ Верно! Карта: " + card + "\n\n"
                + "Этап 3/4: Третья карта между " + prev + " и " + card + " (inside) или снаружи (outside)?\n"
                + "Введи: inside или outside\n"
                + "(если карты равны — inside невозможен)";
    }

    private String stageInsideOutside(State s, String move) {
        if (!move.equals("inside") && !move.equals("outside")) {
            return "Введи inside или outside.";
        }
        Card c1 = s.drawn.get(s.drawn.size() - 2);
        Card c2 = s.drawn.get(s.drawn.size() - 1);
        int low = Math.min(value(c1), value(c2));
        int high = Math.max(value(c1), value(c2));

        Card card = s.deck.remove(0);
        s.drawn.add(card);
        int v = value(card);
        boolean isInside = v > low && v < high;
        boolean correct = (move.equals("inside") && isInside)
                || (move.equals("outside") && !isInside);

        if (!correct) {
            s.finished = true;
            return "Ты написал " + move + ", а карта: " + card
                    + " (между " + low + " и " + high + ")\n"
                    + "❌ Вылет на этапе 3/4.\n\n/cancel — выйти";
        }
        s.stage = 3;
        return "✅ Верно! Карта: " + card + "\n\n"
                + "Этап 4/4: Угадай масть следующей карты.\n"
                + "Введи: hearts, diamonds, clubs или spades";
    }

    private String stageSuit(State s, String move) {
        String suit;
        switch (move) {
            case "hearts": case "♥": suit = "♥"; break;
            case "diamonds": case "♦": suit = "♦"; break;
            case "clubs": case "♣": suit = "♣"; break;
            case "spades": case "♠": suit = "♠"; break;
            default: return "Введи hearts, diamonds, clubs или spades.";
        }
        Card card = s.deck.remove(0);
        s.drawn.add(card);
        if (!card.getSuit().equals(suit)) {
            s.finished = true;
            return "Ты написал " + move + ", а масть: " + card + "\n"
                    + "❌ Вылет на этапе 4/4.\n\n/cancel — выйти";
        }
        s.finished = true;
        return "✅ Верно! Карта: " + card + "\n\n"
                + "🎉 Поздравляю, ты проехал на автобусе! Все 4 этапа пройдены.\n\n"
                + "/cancel — выйти";
    }
}
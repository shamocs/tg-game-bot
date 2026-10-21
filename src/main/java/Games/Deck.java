import java.util.*;


public class Deck{
    public ArrayList<Card> cards;
    public Deck(){
        Map <String,Integer> points = Map.ofEntries(
                Map.entry("2",2), Map.entry("3",3),
                Map.entry("4",4), Map.entry("5",5),
                Map.entry("6",6), Map.entry("7",7),
                Map.entry("8",8), Map.entry("9",9),
                Map.entry("10",10),
                Map.entry("J", 10), Map.entry("Q",10),
                Map.entry("K",10), Map.entry("A",11)
        );
        String[] suits = {"♠️", "♥️", "♦️", "♣️"};
        String[] ranks = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        cards = new ArrayList<>();
        for(String suit : suits){
            for(String rank : ranks){
                int point = points.get(rank);
                cards.add(new Card(rank,suit,point));
            }
        }
    }

    public Card look(int index){
        return cards.get(index);
    }

    public boolean isEmpty(){
        if(cards.size() > 0){
            return false;
        }
        else{
            return true;
        }
    }

    public Card draw(){
        return cards.remove(0);
    }
    public void shuffle(){
        Collections.shuffle(cards);
    }
    public int deckSize(){
        return cards.size();
    }

    public static void main(String[] args){
        Deck deck = new Deck();
        System.out.println(deck.draw());
        System.out.println(deck.draw());
        deck.shuffle();
        System.out.println(deck.draw());
        System.out.println(deck.draw());
        System.out.println("----------------------------------------");

        for(int i = 0; i < deck.deckSize(); i++){
            System.out.println(deck.look(i) + " " + i);
        }

    }
}

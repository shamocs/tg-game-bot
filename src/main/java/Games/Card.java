public class Card {
    private String rank;
    private String suit;
    private int points;
    public Card(String rank, String suit, int points){
        this.rank = rank;
        this.suit = suit;
        this.points = points;
    }
    @Override
    public String toString(){
        return rank + suit;
    }
}

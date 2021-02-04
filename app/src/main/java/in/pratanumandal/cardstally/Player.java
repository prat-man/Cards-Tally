package in.pratanumandal.cardstally;

public class Player implements Comparable<Player> {

    public String defaultName;
    public String name;
    public int cards;

    public Player(String name, int cards) {
        this.defaultName = this.name = name;
        this.cards = cards;
    }

    @Override
    public int compareTo(Player another) {
        return this.cards - another.cards;
    }

}

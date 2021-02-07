package in.pratanumandal.cardstally;

public class Player implements Comparable<Player> {

    public String defaultName;
    public String name;
    public int cards;

    public Player(String defaultName, int cards) {
        this.defaultName = defaultName;
        this.name = "";
        this.cards = cards;
    }

    @Override
    public int compareTo(Player another) {
        return this.cards - another.cards;
    }

}

package goforthegold.model;

/**
 * Represents a coin in the game.
 */
public class Coin {
    private int x;
    private int y;
    private int value;

    /**
     * Constructs a new Coin.
     * @param x The x-coordinate of the coin
     * @param y The y-coordinate of the coin
     * @param value The value of the coin
     */
    public Coin(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    //Getter methods
    public int getX() { return x; }
    public int getY() { return y; }
    public int getValue() { return value; }
}
package goforthegold.model;

/**
 * Represents the gold in the game.
 */
public class Gold {
    private int x;
    private int y;
    private static final double VALUE = 1000000.0;

    /**
     * Constructs a new Gold.
     * @param x The x-coordinate of the gold
     * @param y The y-coordinate of the gold
     */
    public Gold(int x, int y) {
        this.x = x;
        this.y = y;
    }

    //Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getValue() {
        return VALUE;
    }
}
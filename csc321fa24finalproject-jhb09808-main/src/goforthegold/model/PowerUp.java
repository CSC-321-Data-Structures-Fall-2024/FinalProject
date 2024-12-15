package goforthegold.model;

/**
 * Represents a power-up in the game.
 */
public class PowerUp {
    private int x, y;
    private PowerUpType type;

    /**
     * Constructs a new PowerUp.
     * @param x The x-coordinate of the power-up
     * @param y The y-coordinate of the power-up
     * @param type The type of the power-up
     */
    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    //Getters
    public int getX() { return x; }
    public int getY() { return y; }
    public PowerUpType getType() { return type; }
}
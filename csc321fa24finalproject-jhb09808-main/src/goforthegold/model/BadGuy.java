package goforthegold.model;

/**
 * Abstract class representing a bad guy in the game.
 */
public abstract class BadGuy {
    protected int x;
    protected int y;
    protected BadGuyType type;

    /**
     * Constructs a new BadGuy.
     * @param x The x-coordinate of the bad guy
     * @param y The y-coordinate of the bad guy
     * @param type The type of the bad guy
     */
    public BadGuy(int x, int y, BadGuyType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Moves the bad guy based on the game state.
     * @param world The game world
     * @param robot The robot
     * @param difficulty The game difficulty
     */
    public abstract void move(World world, Robot robot, Difficulty difficulty);

    //Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public BadGuyType getType() {
        return type;
    }

    /**
     * Calculates the Manhattan distance to the robot.
     * @param robot The robot
     * @return The distance to the robot
     */
    public int distanceToRobot(Robot robot) {
        return Math.abs(this.x - robot.getX()) + Math.abs(this.y - robot.getY());
    }

    /**
     * Gets the intelligence factor based on difficulty.
     * @param difficulty The game difficulty
     * @return The intelligence factor
     */
    protected double getDifficultyIntelligence(Difficulty difficulty) {
        switch (difficulty) {
            case EASY: return 0.3;
            case MEDIUM: return 0.5;
            case HARD: return 0.7;
            default: return 0.5;
        }
    }
}
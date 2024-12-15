package goforthegold.model;

import java.util.Stack;

/**
 * Represents the robot player in the game.
 */
public class Robot {
    private int x;
    private int y;
    private double money;
    private static final double MOVE_COST = 100.00;
    private Stack<int[]> moveHistory;
    private PowerUpType activePowerUp;
    private int powerUpDuration;

    /**
     * Constructs a new Robot with initial money.
     * @param initialMoney The starting amount of money for the robot
     */
    public Robot(double initialMoney) {
        this.x = 0;
        this.y = 0;
        this.money = initialMoney;
        this.moveHistory = new Stack<>();
        this.activePowerUp = null;
        this.powerUpDuration = 0;
    }

    /**
     * Moves the robot to a new position and deducts the move cost.
     * @param newX The new x-coordinate
     * @param newY The new y-coordinate
     */
    public void move(int newX, int newY) {
        moveHistory.push(new int[]{this.x, this.y});
        this.x = newX;
        this.y = newY;
        this.money -= MOVE_COST;

        if (powerUpDuration > 0) {
            powerUpDuration--;
            if (powerUpDuration == 0) {
                System.out.println(activePowerUp + " power-up has worn off.");
                activePowerUp = null;
            }
        }
    }
    
    /**
     * Undoes the last move of the robot.
     * @return true if the move was undone successfully, false otherwise
     */
    public boolean undoMove() {
        if (!moveHistory.isEmpty()) {
            int[] lastPosition = moveHistory.pop();
            this.x = lastPosition[0];
            this.y = lastPosition[1];
            this.money += MOVE_COST;
            return true;
        }
        return false;
    }

    /**
     * Checks if the robot has enough money to make a move.
     * @return true if the robot can afford to move, false otherwise
     */
    public boolean canAffordMove() {
        return money >= MOVE_COST;
    }

    /**
     * Adds money to the robot's current amount.
     * @param amount The amount of money to add
     */
    public void addMoney(double amount) {
        this.money += amount;
    }

    /**
     * Applies a power-up to the robot.
     * @param powerUp The power-up to apply
     */
    public void applyPowerUp(PowerUp powerUp) {
        this.activePowerUp = powerUp.getType();
        this.powerUpDuration = 5; // Last for 5 moves
        System.out.println("Robot activated " + activePowerUp + " power-up!");
    }

    //Getters
    public PowerUpType getActivePowerUp() {
        return activePowerUp;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getMoney() {
        return money;
    }
}
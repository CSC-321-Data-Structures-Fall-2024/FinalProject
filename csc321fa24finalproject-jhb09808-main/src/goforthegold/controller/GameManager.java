/**
 * Go for the Gold - Robot Navigation Game
 * 
 * This program implements a game where an AI-controlled robot navigates a grid
 * to find gold while avoiding obstacles and enemies.
 * 
 * Basic Pseudocode:
 * 1. Initialize game world with robot, gold, coins, and bad guys
 * 2. While game is not over:
 *    a. Calculate path to gold using A* algorithm
 *    b. Move robot along path
 *    c. Check for coin collection
 *    d. Move bad guys
 *    e. Check for game over conditions
 * 3. Display final score and game statistics
 * 
 * @author Jerome Bustarga (JHB09808)
 * @version 1.0
 * @since 12/02/2024
 */

package goforthegold.controller;

import goforthegold.model.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.Random;

/**
 * GameManager class controls the main game logic and state.
 * It manages the robot, bad guys, coins, and overall game flow.
 */
public class GameManager {
    private World world;
    private Robot robot;
    private Gold gold;
    private List<BadGuy> badGuys;
    private List<int[]> robotPath;
    private List<Coin> coins;
    private List<int[]> attemptedMoves = new ArrayList<>();
    private static final int COIN_VALUE = 200;
    private Difficulty difficulty;
    private HashTable<String, Boolean> visitedLocations;
    private ScoreKeeper scoreKeeper;
    private int totalEarnings;
    private Stack<int[]> moveHistory;
    private Random random;
    private int score;
    private int coinsCollected;
    private int moveCount;
    private int sessionEarnings;
    private int gridSize;

    
    private static final int MAX_SAFE_MOVE_ATTEMPTS = 10;

    /**
     * Constructs a new GameManager with specified world size and difficulty.
     * 
     * @param worldSize The size of the game world (nxn grid)
     * @param difficulty The game difficulty level
     */
    public GameManager(int gridSize, Difficulty difficulty) {
        this.gridSize = gridSize;
        this.difficulty = difficulty;
        this.scoreKeeper = new ScoreKeeper();
        this.visitedLocations = new HashTable<>();
        this.totalEarnings = 0;
        this.moveHistory = new Stack<>();
        this.random = new Random();
        this.score = 0;
        this.coinsCollected = 0;
        this.moveCount = 0;
        this.sessionEarnings = 0;
        initializeGame();
    }

    /**
     * Initializes the game world with all necessary elements.
     * This includes placing the robot, gold, coins, and bad guys.
     * 
     * @param worldSize The size of the world to initialize
     */
    private void initializeGame() {
        world = new World(gridSize);
        robot = new Robot(getDifficultyBasedMoney());
        int[] goldPosition = world.getRandomUnvisitedCell();
        gold = new Gold(goldPosition[0], goldPosition[1]);
        
        coins = new ArrayList<>();
        for (int i = 0; i < getDifficultyBasedCoins(); i++) {
            int[] position = world.getRandomUnvisitedCell();
            coins.add(new Coin(position[0], position[1], COIN_VALUE));
        }
        
        badGuys = new ArrayList<>();
        for (int i = 0; i < getDifficultyBasedBadGuys(); i++) {
            int[] position = world.getRandomUnvisitedCell();
            BadGuy badGuy;
            switch (random.nextInt(3)) {
                case 0:
                    badGuy = new ChaserBadGuy(position[0], position[1]);
                    break;
                case 1:
                    badGuy = new PatrollerBadGuy(position[0], position[1], generatePatrolPoints());
                    break;
                default:
                    badGuy = new TeleporterBadGuy(position[0], position[1]);
            }
            badGuys.add(badGuy);
        }

        visitedLocations.put(robot.getX() + "," + robot.getY(), true);
        moveHistory.clear();
        robotPath = null; 
    }

    private int[][] generatePatrolPoints() {
        int[][] points = new int[4][2];
        for (int i = 0; i < 4; i++) {
            int[] position = world.getRandomUnvisitedCell();
            points[i] = position; 
        }
        return points; 
    }

    /**
     * Attempts to move the robot based on the current game state.
     * Uses A* pathfinding to determine the next move.
     * 
     * @return true if a move was successfully made, false otherwise
     */
    public boolean makeMove() {
        int attempts = 0; 

        while (attempts < MAX_SAFE_MOVE_ATTEMPTS) {
            if (robotPath == null || robotPath.isEmpty()) {
                robotPath = AStarPathfinder.findPath(world, robot.getX(), robot.getY(), gold.getX(), gold.getY(), coins, badGuys, visitedLocations);
                if (robotPath == null) {
                    System.out.println("No path to gold found!");
                    return false; 
                }
                robotPath.remove(0); 
            }

            if (!robotPath.isEmpty() && robot.canAffordMove()) {
                int[] nextMove = robotPath.remove(0); 
                
                if (world.isValidMove(nextMove[0], nextMove[1]) && isSafeToMove(nextMove[0], nextMove[1])) {
                    moveRobot(nextMove[0], nextMove[1]); 
                    markVisited(nextMove[0], nextMove[1]); 
                    
                    collectCoin(); 
                    checkForPowerUp(); 
                    moveBadGuys(); 
                    checkNearMisses(); 
                    
                    moveCount++; 
                    
                    if (coins.isEmpty() || (robot.getX() == gold.getX() && robot.getY() == gold.getY())) {
                        robotPath = null; 
                    }
                    
                    printGameBoard(); 
                    return true; 
                } else {
                    System.out.println("Robot encountered an obstacle or unsafe move at (" + nextMove[0] + ", " + nextMove[1] + ")");
                    robotPath = null; 
                    attempts++; 
                }
            } else {
                return false; 
            }
        }
        
        System.out.println("Robot unable to find a safe move after " + MAX_SAFE_MOVE_ATTEMPTS + " attempts."); 
        return false; 
    }

    /**
     * Checks if a move to the specified position is safe from bad guys.
     * 
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the move is safe, false otherwise
     */
    private boolean isSafeToMove(int x, int y) {
        for (BadGuy badGuy : badGuys) {
            int distance = Math.abs(badGuy.getX() - x) + Math.abs(badGuy.getY() - y);
            if (distance <= 1) {
                return false; 
            }
            
            if (distance <= 3) {
                if (Math.random() < 0.7) { 
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Marks a cell as visited in the game world.
     * 
     * @param x The x-coordinate of the cell
     * @param y The y-coordinate of the cell
     */
    private void markVisited(int x, int y) {
        visitedLocations.put(x + "," + y, true);
    }

    /**
     * Checks if a cell has been visited by the robot.
     * 
     * @param x The x-coordinate of the cell
     * @param y The y-coordinate of the cell
     * @return true if the cell has been visited, false otherwise
     */
    public boolean isVisited(int x, int y) {
        return visitedLocations.get(x + "," + y) != null;
    }

    /**
     * Checks for and applies any power-up at the robot's current position.
     */
    private void checkForPowerUp() {
        PowerUp powerUp = world.getPowerUpAt(robot.getX(), robot.getY());
        if (powerUp != null) {
            robot.applyPowerUp(powerUp);
            world.removePowerUp(powerUp);
            System.out.println("Robot collected a " + powerUp.getType() + " power-up!");
        }
    }

    /**
     * Moves the robot to a new position and updates the game state accordingly.
     * 
     * @param newX The new x-coordinate for the robot
     * @param newY The new y-coordinate for the robot
     */
    private void moveRobot(int newX, int newY) {
        moveHistory.push(new int[]{robot.getX(), robot.getY()});
        robot.move(newX, newY);
        world.markVisited(newX, newY);
        visitedLocations.put(newX + "," + newY, true);
    }

    /**
     * Undoes the last move made by the robot.
     * 
     * @return true if a move was successfully undone, false if no moves to undo
     */
    public boolean undoMove() {
        if (!moveHistory.isEmpty()) {
            int[] lastMove = moveHistory.pop();
            robot.move(lastMove[0], lastMove[1]);
            robot.addMoney(100); 
            moveCount--;
            return true;
        }
        return false;
    }

    /**
     * Checks for and reports any near misses with bad guys.
     */
    private void checkNearMisses() {
        for (BadGuy badGuy : badGuys) {
            if (Math.abs(badGuy.getX() - robot.getX()) + Math.abs(badGuy.getY() - robot.getY()) == 1) {
                System.out.println("Close call! Bad guy nearly caught the robot at (" + badGuy.getX() + ", " + badGuy.getY() + ")");
            }
        }
    }

    /**
     * Moves all bad guys in the game world.
     */
    private void moveBadGuys() {
        for (BadGuy badGuy : badGuys) {
            badGuy.move(world, robot, difficulty);
        }
    }

    /**
     * Checks if the robot has been caught by any bad guy.
     * 
     * @return true if the robot is caught, false otherwise
     */
    public boolean isRobotCaught() {
        for (BadGuy badGuy : badGuys) {
            if (badGuy.getX() == robot.getX() && badGuy.getY() == robot.getY()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the game is over based on various conditions.
     * 
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return (robot.getX() == gold.getX() && robot.getY() == gold.getY()) 
            || !robot.canAffordMove() 
            || isRobotCaught()
            || isRobotSurrounded();
    }

    /**
     * Checks if the robot is surrounded and unable to move.
     * 
     * @return true if the robot is surrounded, false otherwise
     */
    private boolean isRobotSurrounded() {
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; 
        for (int[] dir : directions) {
            int newX = robot.getX() + dir[0];
            int newY = robot.getY() + dir[1];
            if (world.isValidMove(newX, newY) && visitedLocations.get(newX + "," + newY) == null) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Collects a coin if the robot is on the same position as the coin.
     */
    private void collectCoin() {
        Iterator<Coin> iterator = coins.iterator();
        while (iterator.hasNext()) {
            Coin coin = iterator.next();
            if (coin.getX() == robot.getX() && coin.getY() == robot.getY()) {
                robot.addMoney(coin.getValue());
                iterator.remove();
                coinsCollected++;
                score += 100; 
                System.out.println("Collected coin at: (" + coin.getX() + ", " + coin.getY() + "). Score: " + score);
                break;
            }
        }
    }

    /**
     * Gets the initial money amount based on the game difficulty.
     * 
     * @return The initial money amount
     */
    private int getDifficultyBasedMoney() {
        switch (difficulty) {
            case EASY: return 1500;
            case MEDIUM: return 1000;
            case HARD: return 500;
            default: return 1000;
        }
    }

    /**
     * Gets the number of bad guys based on the game difficulty.
     * 
     * @return The number of bad guys
     */
    private int getDifficultyBasedBadGuys() {
        switch (difficulty) {
            case EASY: return 2;
            case MEDIUM: return 3;
            case HARD: return 4;
            default: return 3;
        }
    }

    /**
     * Gets the number of coins based on the game difficulty.
     * 
     * @return The number of coins
     */
    public int getDifficultyBasedCoins() {
        switch (difficulty) {
            case EASY: return 7;
            case MEDIUM: return 5;
            case HARD: return 3;
            default: return 5;
        }
    }

    /**
     * Calculates the final score based on various game factors.
     * 
     * @return The calculated score
     */
    private int calculateScore() {
        int baseScore = (int) robot.getMoney();
        int coinBonus = coinsCollected * 50;
        int difficultyMultiplier = switch (difficulty) {
            case EASY -> 1;
            case MEDIUM -> 2;
            case HARD -> 3;
        };
        int timeBonus = Math.max(1000 - moveCount * 10, 0);
        return (baseScore + coinBonus + timeBonus + (isGameWon() ? 1000000 : 0)) * difficultyMultiplier; // Add gold value if won
    }

    /**
     * Generates a detailed breakdown of the game score.
     * 
     * @return A string containing the score breakdown
     */
    public String getScoreBreakdown() {
        StringBuilder sb = new StringBuilder();
        sb.append("Score Breakdown:\n");
        sb.append("Base Score (Remaining Money): $").append(robot.getMoney()).append("\n");
        sb.append("Coins Collected: ").append(coinsCollected).append(" (Bonus: $").append(coinsCollected * 50).append(")\n");
        sb.append("Moves Taken: ").append(moveCount).append(" (Time Bonus: $").append(Math.max(1000 - moveCount * 10, 0)).append(")\n");
        sb.append("Difficulty Multiplier: x").append(difficulty.ordinal() + 1).append("\n");
        sb.append("Total Score: $").append(calculateScore());
        return sb.toString();
    }

    /**
     * Ends the game, calculates final score, and updates high scores.
     * 
     * @param playerName The name of the player
     */
    public void endGame(String playerName) {
        
        score = calculateScore();

        
        scoreKeeper.addScore(new Score(playerName, score));

        
        int earnings = (int) (robot.getMoney() - getDifficultyBasedMoney());
        sessionEarnings += earnings;
        totalEarnings += earnings;

        
        System.out.println(getScoreBreakdown());

        
        System.out.println("\nTop Scores:");
        for (Score topScore : scoreKeeper.getTopScores()) {
            System.out.println(topScore);
        }
    }

    /**
     * Checks if the game has been won (robot has reached the gold).
     * 
     * @return true if the game is won, false otherwise
     */
    public boolean isGameWon() {
        return robot.getX() == gold.getX() && robot.getY() == gold.getY();
    }

    /**
     * Resets the game to its initial state.
     */
    public void resetGame() {
        initializeGame();
    }
    
    /**
     * Prints the current state of the game board to the console.
     * Uses different characters to represent game elements.
     */
    public void printGameBoard() {
        for (int y = 0; y < world.getSize(); y++) {
            for (int x = 0; x < world.getSize(); x++) {
                if (robot.getX() == x && robot.getY() == y) {
                    System.out.print("R");
                } else if (gold.getX() == x && gold.getY() == y) {
                    System.out.print("G");
                } else if (isBadGuyAt(x, y)) {
                    System.out.print("B");
                } else if (isCoinAt(x, y)) {
                    System.out.print("C");
                } else if (world.isObstacle(x, y)) {
                    System.out.print("#");
                } else if (isVisited(x, y)) {
                    System.out.print(".");
                } else {
                    System.out.print("·"); 
                }
                System.out.print(" "); 
            }
            System.out.println();
        }
        System.out.println("Score: " + score + " | Moves: " + moveCount + " | Coins: " + coinsCollected);
    }

    private boolean isBadGuyAt(int x, int y) {
        for (BadGuy badGuy : badGuys) {
            if (badGuy.getX() == x && badGuy.getY() == y) {
                return true;
            }
        }
        return false;
    }

    private boolean isCoinAt(int x, int y) {
        for (Coin coin : coins) {
            if (coin.getX() == x && coin.getY() == y) {
                return true;
            }
        }
        return false;
    }
    
    public void addAttemptedMove(int x, int y) {
        attemptedMoves.add(new int[]{x, y});
    }

    public List<int[]> getAndClearAttemptedMoves() {
        List<int[]> moves = new ArrayList<>(attemptedMoves);
        attemptedMoves.clear();
        return moves;
    }
    
    //Getters
    public int getMoveCount() {
        return moveCount;
    }

    public int getScore() {
        return score;
    }

    public World getWorld() { 
    	return world; 
    }
    
    public Robot getRobot() { 
    	return robot; 
    }
    
    public Gold getGold() { 
    	return gold; 
    }
    
    public List<BadGuy> getBadGuys() { 
    	return badGuys; 
    }
    
    public List<Coin> getCoins() { 
    	return coins; 
    }
    
    public Difficulty getDifficulty() { 
    	return difficulty; 
    }
    
    public int getTotalEarnings() { 
    	return totalEarnings; 
    }
    
    public int getSessionEarnings() { 
    	return sessionEarnings; 
    }
    
    public ScoreKeeper getScoreKeeper() { 
    	return scoreKeeper; 
    }
}
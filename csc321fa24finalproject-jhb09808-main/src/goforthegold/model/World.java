package goforthegold.model;

import java.util.Random;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game world.
 */
public class World {
    private int[][] grid;
    private int size;
    private Random random;
    private List<PowerUp> powerUps;
    private boolean[][] obstacles;

    /**
     * Constructs a new World with the specified size.
     * @param size The size of the world grid
     */
    public World(int size) {
        this.size = size;
        this.grid = new int[size][size];
        this.random = new Random();
        this.powerUps = new ArrayList<>();
        this.obstacles = new boolean[size][size];
        initializeGrid();
        generateObstacles();
        generatePowerUps();
    }

    private void initializeGrid() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                grid[i][j] = 0;
            }
        }
    }

    private void generateObstacles() {
        int obstacleCount = size * size / 10;
        for (int i = 0; i < obstacleCount; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            obstacles[x][y] = true;
        }
    }

    private void generatePowerUps() {
        int powerUpCount = size / 2; 
        for (int i = 0; i < powerUpCount; i++) {
            int x = random.nextInt(size);
            int y = random.nextInt(size);
            PowerUpType type = PowerUpType.values()[random.nextInt(PowerUpType.values().length)];
            powerUps.add(new PowerUp(x, y, type));
        }
    }

    /**
     * Checks if a move to the specified coordinates is valid.
     * @param x The x-coordinate to check
     * @param y The y-coordinate to check
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(int x, int y) {
        return x >= 0 && x < size && y >= 0 && y < size && !obstacles[x][y];
    }

    public void markVisited(int x, int y) {
        if (isValidMove(x, y)) {
            grid[x][y] = 1;
        }
    }

    public boolean isVisited(int x, int y) {
        return isValidMove(x, y) && grid[x][y] == 1;
    }

    public int[] getRandomUnvisitedCell() {
        int[] cell = new int[2];
        do {
            cell[0] = random.nextInt(size);
            cell[1] = random.nextInt(size);
        } while (isVisited(cell[0], cell[1]) || obstacles[cell[0]][cell[1]]);
        return cell;
    }

    public PowerUp getPowerUpAt(int x, int y) {
        for (PowerUp powerUp : powerUps) {
            if (powerUp.getX() == x && powerUp.getY() == y) {
                return powerUp;
            }
        }
        return null;
    }

    public void removePowerUp(PowerUp powerUp) {
        powerUps.remove(powerUp);
    }

    public boolean isObstacle(int x, int y) {
        return obstacles[x][y];
    }

    public int getSize() {
        return size;
    }
    
    public void setObstacle(int x, int y) {
        if (isValidMove(x, y)) {
            obstacles[x][y] = true;
        }
    }

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }
}
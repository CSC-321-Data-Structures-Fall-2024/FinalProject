package goforthegold;

import goforthegold.controller.GameManager;
import goforthegold.model.BadGuy;
import goforthegold.model.Coin;
import goforthegold.model.Difficulty;
import goforthegold.view.GameGUI;
import javax.swing.SwingUtilities;

import java.util.Scanner;

/**
 * Main class for the Go for the Gold game.
 * This class handles the game initialization and mode selection.
 */
public class GoForTheGold {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Choose game mode (1: Console, 2: GUI):");
        int modeChoice = scanner.nextInt();

        System.out.println("Enter grid size (5-20):");
        int gridSize = scanner.nextInt();
        gridSize = Math.max(5, Math.min(20, gridSize)); 

        if (modeChoice == 2) {
            launchGUI(gridSize);
        } else {
            playConsoleGame(scanner, gridSize);
        }

        scanner.close();
    }
    
    /**
     * Launches the GUI version of the game.
     *
     * @param gridSize The size of the game grid
     */
    private static void launchGUI(int gridSize) {
        SwingUtilities.invokeLater(() -> new GameGUI(gridSize).setVisible(true));
    }
    
    /**
     * Starts and manages the console version of the game.
     *
     * @param scanner Scanner for user input
     * @param gridSize The size of the game grid
     */
    private static void playConsoleGame(Scanner scanner, int gridSize) {
        System.out.println("Choose difficulty (1: Easy, 2: Medium, 3: Hard):");
        int difficultyChoice = scanner.nextInt();
        Difficulty difficulty = Difficulty.MEDIUM;
        switch (difficultyChoice) {
            case 1: difficulty = Difficulty.EASY; break;
            case 2: difficulty = Difficulty.MEDIUM; break;
            case 3: difficulty = Difficulty.HARD; break;
        }

        GameManager game = new GameManager(gridSize, difficulty);

        printInitialGameState(game);

        int moveCount = 0;
        while (!game.isGameOver()) {
            if (game.makeMove()) {
                moveCount++;
                printGameState(game, moveCount);
            } else {
                System.out.println("Robot couldn't move!");
                break;
            }
        }

        printGameOverState(game, moveCount);
    }

    /**
     * Prints the initial state of the game world.
     *
     * @param game The GameManager instance
     */
    private static void printInitialGameState(GameManager game) {
        System.out.println("World size: " + game.getWorld().getSize() + "x" + game.getWorld().getSize());
        System.out.println("Difficulty: " + game.getDifficulty());
        System.out.println("Robot initial position: (" + game.getRobot().getX() + ", " + game.getRobot().getY() + ")");
        System.out.println("Robot initial money: $" + game.getRobot().getMoney());
        System.out.println("Gold position: (" + game.getGold().getX() + ", " + game.getGold().getY() + ")");
        System.out.println("Number of Bad Guys: " + game.getBadGuys().size());
        System.out.println("Bad Guys initial positions:");
        for (BadGuy badGuy : game.getBadGuys()) {
            System.out.println("(" + badGuy.getX() + ", " + badGuy.getY() + ")");
        }
        System.out.println("Number of Coins: " + game.getCoins().size());
        System.out.println("Coins positions:");
        for (Coin coin : game.getCoins()) {
            System.out.println("(" + coin.getX() + ", " + coin.getY() + ")");
        }
    }

    /**
     * Prints the current state of the game after each move.
     *
     * @param game The GameManager instance
     * @param moveCount The current move count
     */
    private static void printGameState(GameManager game, int moveCount) {
        System.out.println("\nMove " + moveCount + ":");
        game.printGameBoard();
        System.out.println("Robot money: $" + game.getRobot().getMoney());
        System.out.println("Remaining Coins: " + game.getCoins().size());
    }

    /**
     * Prints the final state of the game when it's over.
     *
     * @param game The GameManager instance
     * @param moveCount The total number of moves made
     */
    private static void printGameOverState(GameManager game, int moveCount) {
        if (game.getRobot().getX() == game.getGold().getX() && game.getRobot().getY() == game.getGold().getY()) {
            System.out.println("Gold found! Robot wins!");
            double finalMoney = game.getRobot().getMoney();
            System.out.println("Total moves: " + moveCount);
            System.out.println("Final money: $" + finalMoney);
        } else if (game.isRobotCaught()) {
            System.out.println("Game over. Robot was caught by a Bad Guy!");
            System.out.println("Total moves: " + moveCount);
        } else {
            System.out.println("Game over. Robot ran out of money or got stuck.");
            System.out.println("Total moves: " + moveCount);
        }

        int coinsCollected = game.getDifficultyBasedCoins() - game.getCoins().size();
        System.out.println("Coins collected: " + coinsCollected);
    }
}
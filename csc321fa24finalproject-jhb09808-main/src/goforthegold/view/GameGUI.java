package goforthegold.view;

import goforthegold.controller.GameManager;
import goforthegold.model.*;

import javax.swing.*;
import java.awt.*;

/**
 * Main GUI class for the Go for the Gold game.
 * This class handles the game visualization and user interactions.
 */
public class GameGUI extends JFrame {
    private GameManager game;
    private GameBoard gameBoard;
    private JButton newGameButton;
    private JButton resetButton;
    private JButton undoButton;
    private JComboBox<Difficulty> difficultyComboBox;
    private JLabel statusLabel;
    private JLabel earningsLabel;
    private JLabel scoreLabel;
    private JLabel moveCountLabel;
    private Timer timer;
    private static final long serialVersionUID = 1L;
    private int gridSize;

    /**
     * Constructs a new GameGUI with the specified grid size.
     * 
     * @param gridSize The size of the game grid
     */

    public GameGUI(int gridSize) {
        this.gridSize = gridSize;
        setTitle("Go for the Gold");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gameBoard = new GameBoard();
        add(gameBoard, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        newGameButton = new JButton("New Game");
        resetButton = new JButton("Reset Game");
        undoButton = new JButton("Undo Move");
        difficultyComboBox = new JComboBox<>(Difficulty.values());
        statusLabel = new JLabel("Welcome to Go for the Gold!");
        earningsLabel = new JLabel("Total Earnings: $0");
        scoreLabel = new JLabel("Score: 0");
        moveCountLabel = new JLabel("Moves: 0");
        
        controlPanel.add(scoreLabel);
        controlPanel.add(moveCountLabel);
        controlPanel.add(newGameButton);
        controlPanel.add(resetButton);
        controlPanel.add(undoButton);
        controlPanel.add(difficultyComboBox);
        controlPanel.add(statusLabel);
        controlPanel.add(earningsLabel);
        add(controlPanel, BorderLayout.SOUTH);

        newGameButton.addActionListener(e -> startNewGame());
        resetButton.addActionListener(e -> resetGame());
        undoButton.addActionListener(e -> undoMove());

        timer = new Timer(100, e -> updateGame());

        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Starts a new game with the selected difficulty.
     */
    private void startNewGame() {
        Difficulty difficulty = (Difficulty) difficultyComboBox.getSelectedItem();
        game = new GameManager(gridSize, difficulty);
        gameBoard.setGame(game);
        timer.start();
        statusLabel.setText("Game started. Difficulty: " + difficulty + ", Grid Size: " + gridSize + "x" + gridSize);
        updateEarnings();
        updateStatus();
    }

    /**
     * Resets the current game to its initial state.
     */
    private void resetGame() {
        if (game != null) {
            game.resetGame();
            gameBoard.repaint();
            statusLabel.setText("Game reset. Difficulty: " + game.getDifficulty());
            updateEarnings();
            updateStatus();
        }
    }

    /**
     * Undoes the last move in the game.
     */
    private void undoMove() {
        if (game != null && game.undoMove()) {
            gameBoard.repaint();
            updateStatus();
        }
    }

    /**
     * Updates the game state and GUI.
     */
    private void updateGame() {
        if (game != null && !game.isGameOver()) {
            game.makeMove();
            gameBoard.repaint();
            updateStatus();
        } else if (game != null && game.isGameOver()) {
            timer.stop();
            showGameOverMessage();
        }
    }

    /**
     * Updates the status labels in the GUI.
     */
    private void updateStatus() {
        if (game != null) {
            statusLabel.setText("Robot money: $" + game.getRobot().getMoney() + 
                                " | Coins left: " + game.getCoins().size());
            scoreLabel.setText("Score: " + game.getScore());
            moveCountLabel.setText("Moves: " + game.getMoveCount());
        }
    }

    /**
     * Updates the total earnings display.
     */
    private void updateEarnings() {
        if (game != null) {
            earningsLabel.setText("Total Earnings: $" + game.getTotalEarnings());
        }
    }

    /**
     * Displays the game over message and prompts for the player's name.
     */
    private void showGameOverMessage() {
        String message = game.getScoreBreakdown();
        String playerName = JOptionPane.showInputDialog(this, message + "\nEnter your name:");
        if (playerName != null && !playerName.isEmpty()) {
            game.endGame(playerName);
            showHighScores();
            updateEarnings();
        }
    }

    /**
     * Displays the high scores.
     */
    private void showHighScores() {
        StringBuilder sb = new StringBuilder("Top Scores:\n");
        for (Score score : game.getScoreKeeper().getTopScores()) {
            sb.append(score).append("\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString());
    }

    /**
     * Inner class representing the game board panel.
     */
    private class GameBoard extends JPanel {
        private static final int CELL_SIZE = 50;
        private static final long serialVersionUID = 2L;

        private GameManager game;

        /**
         * Sets the game manager for this game board.
         * 
         * @param game The GameManager to set
         */
        public void setGame(GameManager game) {
            this.game = game;
            setPreferredSize(new Dimension(game.getWorld().getSize() * CELL_SIZE, 
                                           game.getWorld().getSize() * CELL_SIZE));
            revalidate();
        }
        
        /**
         * Highlights an attempted move on the game board.
         * 
         * @param x The x-coordinate of the attempted move
         * @param y The y-coordinate of the attempted move
         */
        @SuppressWarnings("unused")
        public void highlightAttemptedMove(int x, int y) {
            Graphics g = getGraphics();
            g.setColor(Color.ORANGE);
            g.drawRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.dispose();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (game == null) return;

            // Draw grid
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 0; i <= game.getWorld().getSize(); i++) {
                g.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, getHeight());
                g.drawLine(0, i * CELL_SIZE, getWidth(), i * CELL_SIZE);
            }

            //robot
            g.setColor(Color.BLUE);
            g.fillOval(game.getRobot().getX() * CELL_SIZE, game.getRobot().getY() * CELL_SIZE, 
                       CELL_SIZE, CELL_SIZE);

            //gold
            g.setColor(Color.YELLOW);
            g.fillRect(game.getGold().getX() * CELL_SIZE, game.getGold().getY() * CELL_SIZE, 
                       CELL_SIZE, CELL_SIZE);

            //bad guys
            g.setColor(Color.RED);
            for (BadGuy badGuy : game.getBadGuys()) {
                g.fillRect(badGuy.getX() * CELL_SIZE, badGuy.getY() * CELL_SIZE, 
                           CELL_SIZE, CELL_SIZE);
            }

            //coins
            g.setColor(Color.GREEN);
            for (Coin coin : game.getCoins()) {
                g.fillOval(coin.getX() * CELL_SIZE + CELL_SIZE/4, coin.getY() * CELL_SIZE + CELL_SIZE/4, 
                           CELL_SIZE/2, CELL_SIZE/2);
            }

            //visited cells
            g.setColor(new Color(200, 200, 255, 100)); // Light blue with transparency
            for (int x = 0; x < game.getWorld().getSize(); x++) {
                for (int y = 0; y < game.getWorld().getSize(); y++) {
                    if (game.isVisited(x, y)) {
                        g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }

            //obstacles
            g.setColor(Color.DARK_GRAY);
            for (int x = 0; x < game.getWorld().getSize(); x++) {
                for (int y = 0; y < game.getWorld().getSize(); y++) {
                    if (game.getWorld().isObstacle(x, y)) {
                        g.fillRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                    }
                }
            }

            //power-ups
            g.setColor(Color.MAGENTA);
            for (PowerUp powerUp : game.getWorld().getPowerUps()) {
                g.fillOval(powerUp.getX() * CELL_SIZE + CELL_SIZE/4, powerUp.getY() * CELL_SIZE + CELL_SIZE/4, 
                           CELL_SIZE/2, CELL_SIZE/2);
            }
        }
    }

    /**
     * Main method to launch the GUI.
     * 
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameGUI(10).setVisible(true)); // Default size 10
    }
}
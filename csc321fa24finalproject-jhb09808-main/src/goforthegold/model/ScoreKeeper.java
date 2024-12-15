package goforthegold.model;

import java.io.*;
import java.util.*;

/**
 * Manages and persists high scores for the game.
 */
public class ScoreKeeper {
    private PriorityQueue<Score> topScores;
    private static final int MAX_SCORES = 10;
    private static final String SCORE_FILE = "top_scores.txt";

    /**
     * Constructs a new ScoreKeeper and loads existing scores.
     */
    public ScoreKeeper() {
        topScores = new PriorityQueue<>(MAX_SCORES, Collections.reverseOrder());
        loadScores();
    }

    /**
     * Adds a new score to the high scores list.
     * @param score The score to add
     */
    public void addScore(Score score) {
        topScores.offer(score);
        if (topScores.size() > MAX_SCORES) {
            topScores.poll(); 
        }
        saveScores();
    }

    private void loadScores() {
        File file = new File(SCORE_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
                System.out.println("Score file created: " + SCORE_FILE);
            } catch (IOException e) {
                System.err.println("Error creating score file: " + e.getMessage());
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts;
                if (line.contains(",")) {
                    parts = line.split(",");
                } else if (line.contains(":")) {
                    parts = line.split(":");
                } else {
                    System.err.println("Invalid line format: " + line);
                    continue;
                }

                if (parts.length == 2) { 
                    try {
                        String name = parts[0].trim();
                        int score = Integer.parseInt(parts[1].trim());
                        topScores.offer(new Score(name, score));
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid score format: " + line);
                    }
                } else {
                    System.err.println("Invalid line format: " + line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading scores: " + e.getMessage());
        }
    }

    private void saveScores() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SCORE_FILE))) {
            for (Score score : topScores) {
                writer.println(score.getName() + "," + score.getScore());
            }
        } catch (IOException e) {
            System.err.println("Error saving scores: " + e.getMessage());
        }
    }

    /**
     * Retrieves the list of top scores.
     * @return A list of the top scores
     */
    public List<Score> getTopScores() {
        return new ArrayList<>(topScores);
    }
}
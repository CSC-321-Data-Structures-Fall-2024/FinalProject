package goforthegold.model;

/**
 * Represents a score in the game.
 */
public class Score implements Comparable<Score> {
    private String name;
    private int score;

    /**
     * Constructs a new Score.
     * @param name The name of the player
     * @param score The score value
     */
    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public int compareTo(Score other) {
        return Integer.compare(this.score, other.score);
    }

    @Override
    public String toString() {
        return name + ": " + score;
    }
}
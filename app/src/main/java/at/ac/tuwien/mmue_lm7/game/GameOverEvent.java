package at.ac.tuwien.mmue_lm7.game;

public class GameOverEvent {
    /**
     * Level reached
     */
    private int score;
    /**
     * time in frames
     */
    private int time;
    /**
     * true if all levels have been completed
     */
    private boolean gameCompleted;

    public GameOverEvent(int score, int time, boolean gameCompleted) {
        this.score = score;
        this.time = time;
        this.gameCompleted = gameCompleted;
    }

    public int getScore() {
        return score;
    }

    public int getTime() {
        return time;
    }

    public boolean gameCompleted() {
        return gameCompleted;
    }
}

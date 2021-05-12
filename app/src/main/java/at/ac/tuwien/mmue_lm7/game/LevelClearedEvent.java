package at.ac.tuwien.mmue_lm7.game;

/**
 * Fired when all level objectives have been fulfilled
 */
public class LevelClearedEvent {
    private String level;

    public LevelClearedEvent(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }
}

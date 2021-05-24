package at.ac.tuwien.mmue_lm7.game;

/**
 * Used for various level events (e.g. cleared, loaded, ...)
 * @author simon
 */
public class LevelEvent {
    private String level;

    public LevelEvent(String level) {
        this.level = level;
    }

    public String getLevel() {
        return level;
    }
}

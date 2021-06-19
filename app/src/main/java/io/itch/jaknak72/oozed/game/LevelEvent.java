package io.itch.jaknak72.oozed.game;

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

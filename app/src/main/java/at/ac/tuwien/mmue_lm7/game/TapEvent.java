package at.ac.tuwien.mmue_lm7.game;

import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * @author simon
 * Represents a tap on the screen
 */
public class TapEvent {
    private Vec2 position;

    public TapEvent(Vec2 position) {
        this.position = position;
    }

    public Vec2 getPosition() {
        return position;
    }
}

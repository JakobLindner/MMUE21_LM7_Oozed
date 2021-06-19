package io.itch.jaknak72.oozed.game;

import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Represents a tap on the screen
 * @author simon
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

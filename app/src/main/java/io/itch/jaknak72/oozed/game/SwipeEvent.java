package io.itch.jaknak72.oozed.game;

import io.itch.jaknak72.oozed.utils.Vec2;

/**
 * Represents a swipe event, has position and direction
 * @author simon
 */
public class SwipeEvent {
    private Vec2 position;
    private Vec2 direction;

    public SwipeEvent(Vec2 position, Vec2 direction) {
        this.position = position;
        this.direction = direction;
    }

    public Vec2 getPosition() {
        return position;
    }

    public Vec2 getDirection() {
        return direction;
    }
}

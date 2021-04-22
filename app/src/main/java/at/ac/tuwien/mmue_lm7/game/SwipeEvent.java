package at.ac.tuwien.mmue_lm7.game;

import at.ac.tuwien.mmue_lm7.utils.Vec2;

/**
 * @author simon
 * Represents a swipe event, has position and direction
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

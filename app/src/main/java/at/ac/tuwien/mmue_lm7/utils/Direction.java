package at.ac.tuwien.mmue_lm7.utils;

import android.util.Log;

/**
 * Describes cardinal directions
 *
 * @author simon
 */
public enum Direction {
    UP(new Vec2(0, 1)),
    DOWN(new Vec2(0, -1)),
    LEFT(new Vec2(-1, 0)),
    RIGHT(new Vec2(1, 0));

    private static final String TAG = "Direction";

    public final Vec2 dir;

    /**
     * @param rotation
     * @return the closest cardinal direction for given rotation, RIGHT is 0 degrees
     */
    public static Direction fromRotation(float rotation) {
        rotation%=360;
        int multiple = Math.round(rotation/90f);
        if(multiple==0)
            return RIGHT;
        else if(rotation==1)
            return DOWN;
        else if(rotation==2)
            return LEFT;
        else
            return UP;
    }

    public Direction rotateCCW() {
        switch (this) {
            case UP:
                return LEFT;
            case DOWN:
                return RIGHT;
            case LEFT:
                return DOWN;
            case RIGHT:
                return UP;
            default:
                Log.e(TAG, "Unknown Direction in rotateCCW");
                return UP;
        }
    }

    public Direction rotateCW() {
        switch (this) {
            case UP:
                return RIGHT;
            case DOWN:
                return LEFT;
            case LEFT:
                return UP;
            case RIGHT:
                return DOWN;
            default:
                Log.e(TAG, "Unknown Direction in rotateCW");
                return UP;
        }
    }

    /**
     * @return the opposite direction
     */
    public Direction opposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                Log.e(TAG, "Unknown Direction in opposite()");
                return UP;
        }
    }

    public int getRotation() {
        if (this == Direction.RIGHT)
            return 0;
        else if (this == Direction.DOWN)
            return 90;
        else if (this == Direction.LEFT)
            return 180;
        else
            return 270;
    }

    public boolean isHorizontal() {
        return this == LEFT || this == RIGHT;
    }

    public boolean isVertical() {
        return !isHorizontal();
    }

    /**
     * @param to !=null
     * @return cardinal direction that is closest (anglewise) to given vector
     */
    public static Direction getClosest(Vec2 to) {
        //find the direction with the biggest dot product

        float maxDot = to.dot(UP.dir);
        Direction best = UP;

        //check DOWN
        float dot = to.dot(DOWN.dir);
        if (dot > maxDot) {
            maxDot = dot;
            best = DOWN;
        }

        //check LEFT
        dot = to.dot(LEFT.dir);
        if (dot > maxDot) {
            maxDot = dot;
            best = LEFT;
        }

        //check RIGHT
        dot = to.dot(RIGHT.dir);
        if (dot > maxDot) {
            maxDot = dot;
            best = RIGHT;
        }

        return best;
    }

    Direction(Vec2 dir) {
        this.dir = dir;
    }
}

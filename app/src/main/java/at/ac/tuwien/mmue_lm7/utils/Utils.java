package at.ac.tuwien.mmue_lm7.utils;

import at.ac.tuwien.mmue_lm7.game.GameConstants;

/**
 * utility functions that have no other place
 * @author simon
 */
public class Utils {
    public static final float EPSILON = (float) 0.00001;//TODO find good epsilon

    public static float clamp(float value, float min, float max) {
        if (value < min)
            return min;
        if (value > max)
            return max;
        return value;
    }

    public static int signum(float v) {
        if (v > 0)
            return 1;
        if (v < 0)
            return -1;
        return 0;
    }

    /**
     * Transforms given screen coordinates (game resolution, see GameConstants) to world coordinates
     *
     * @param screenCoords !=null changed by this method to have world coordinates
     */
    public static void screenToWorld(Vec2 screenCoords) {
        //scale
        screenCoords.scl(GameConstants.UNITS_PER_PIXEL);
        //invert y
        screenCoords.y = GameConstants.GAME_HEIGHT - screenCoords.y;
    }

    /**
     * Transforms given world coordinates to screen space
     * @param worldCoords !=null, changed by this method to be in screen space
     */
    public static void worldToScreen(Vec2 worldCoords) {
        //invert y
        worldCoords.y = GameConstants.GAME_HEIGHT - worldCoords.y;
        //scale
        worldCoords.scl(GameConstants.PIXELS_PER_UNIT);
    }

    /**
     * Transforms given x world coordinate to screen space
     * @param x in world space
     * @return the transformed x coordinate in screen space
     */
    public static float worldToScreenX(float x) {
        return x * GameConstants.PIXELS_PER_UNIT;
    }

    /**
     * Transforms given y world coordinate to screen space
     * @param y in world space
     * @return the transformed y coordinate in screen space
     */
    public static float worldToScreenY(float y) {
        y = GameConstants.GAME_HEIGHT-y;
        return y * GameConstants.PIXELS_PER_UNIT;
    }

    //prevent instantiation
    private Utils() {
    }
}

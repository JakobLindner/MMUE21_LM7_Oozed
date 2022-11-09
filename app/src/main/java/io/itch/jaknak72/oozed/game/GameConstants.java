package io.itch.jaknak72.oozed.game;

/**
 * @author simon
 */
public class GameConstants {

    public static final int PIXELS_PER_UNIT = 16;
    public static final float UNITS_PER_PIXEL = 1 / (float) PIXELS_PER_UNIT;

    /**
     * Resolution width of the game
     */
    public static final int GAME_RES_WIDTH = 512;
    /**
     * Resolution height of the game
     */
    public static final int GAME_RES_HEIGHT = 288;
    /**
     * in units
     */
    public static final float GAME_WIDTH = GAME_RES_WIDTH / (float) PIXELS_PER_UNIT;//=32
    /**
     * in units
     */
    public static final float GAME_HEIGHT = GAME_RES_HEIGHT / (float) PIXELS_PER_UNIT;//=18

    public static final float HALF_GAME_WIDTH = GAME_WIDTH / 2f;
    public static final float HALF_GAME_HEIGHT = GAME_HEIGHT / 2f;

    public static final int PLAYER_LIVES_PER_LEVEL = 3;

    /**
     * Prevent instantiation
     */
    private GameConstants() {
    }
}

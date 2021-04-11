package at.ac.tuwien.mmue_lm7.game;

public class GameConstants {

    public static final int PIXELS_PER_UNIT = 16;

    /**
     * Resolution width of the game
     */
    public static final int GAME_RES_WIDTH = 480;
    /**
     * Resolution height of the game
     */
    public static final int GAME_RES_HEIGHT = 270;
    /**
     * in units
     */
    public static final float GAME_WIDTH = GAME_RES_WIDTH/(float)PIXELS_PER_UNIT;//30
    /**
     * in units
     */
    public static final float GAME_HEIGHT = GAME_RES_HEIGHT/(float)PIXELS_PER_UNIT;//=16.875

    /**
     * Prevent instantiation
     */
    private GameConstants(){}
}

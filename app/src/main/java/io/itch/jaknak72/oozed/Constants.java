package io.itch.jaknak72.oozed;

/**
 * Constants, which are irrelevant for the game instance
 * @author simon
 */
public class Constants {
    /**
     * Timestep of the game in s
     */
    public static final double FIXED_DELTA = 16/1000.0;
    public static final long FIXED_DELTA_MS = 16;
    /**
     * maximum amount of game updates that can be accumulated to prevent an update spiral of doom (=cannot keep up with target update rate)
     */
    public static final long UPDATE_TIME_ACCUM_MAX_MS = FIXED_DELTA_MS*8;
    /**
     * Prevent instantiation
     */
    private Constants() { }
}

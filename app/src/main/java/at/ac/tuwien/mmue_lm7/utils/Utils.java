package at.ac.tuwien.mmue_lm7.utils;

/**
 * utility functions that have no other place
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
        if(v>0)
            return 1;
        if(v<0)
            return -1;
        return 0;
    }

    //prevent instantiation
    private Utils() {
    }
}

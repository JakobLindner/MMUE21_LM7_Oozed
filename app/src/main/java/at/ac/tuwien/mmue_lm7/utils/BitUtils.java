package at.ac.tuwien.mmue_lm7.utils;

/**
 * Utility class with static methods for bit operations
 */
public class BitUtils {

    /**
     *
     * @param mask, number to check
     * @return the position of the rightmost set bit, 1 for the rightmost bit
     */
    public static int getRightmostSetBit(int mask) {
        int pos = 1;
        int testBit = 1;
        while((testBit & mask)==0) {
            testBit = testBit<<1;
            ++pos;
        }

        return pos;
    }

    //prevent instantiation
    private BitUtils(){}
}

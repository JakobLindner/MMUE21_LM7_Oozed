package at.ac.tuwien.mmue_lm7.utils;

/**
 * Utility class with static methods for bit operations
 * @author simon
 */
public class BitUtils {

    /**
     *
     * @param mask, number to check
     * @return the position of the rightmost set bit, 1 for the rightmost bit, 0 if mask is 0
     */
    public static int getRightmostSetBit(int mask) {
        if(mask==0)
            return 0;

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

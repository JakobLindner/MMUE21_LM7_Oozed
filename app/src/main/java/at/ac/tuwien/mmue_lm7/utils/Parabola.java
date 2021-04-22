package at.ac.tuwien.mmue_lm7.utils;

/**
 * @author simon
 */
public class Parabola {
    /**
     * a component of parabola
     */
    public float a;
    /**
     * b component of parabola
     */
    public float b;

    /**
     * c component of parabola
     */
    public float c;

    public float y(float x) {
        return a * x * x + b * x + c;
    }
}

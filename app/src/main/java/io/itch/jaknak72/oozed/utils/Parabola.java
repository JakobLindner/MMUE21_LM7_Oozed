package io.itch.jaknak72.oozed.utils;

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

package at.ac.tuwien.mmue_lm7.utils;

/**
 * Simple 2d vector utility class
 */
public class Vec2 {
    public float x;
    public float y;

    public Vec2() {
        this(0,0);
    }

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return a copy of this vector
     */
    public Vec2 copy() {
        return new Vec2(x,y);
    }

    /**
     * Adds component of other to this vector
     * @param other, !=null, not changed by method
     * @return this, for chaining
     */
    public Vec2 add(Vec2 other) {
        return add(other.x, other.y);
    }

    /**
     * Adds given values to this vector
     * @param x
     * @param y
     * @return this, for chaining
     */
    public Vec2 add(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /**
     * sets the coordinates of the vector to given values
     * @param x
     * @param y
     * @return this, for chaining
     */
    public Vec2 set(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * sets the coordinates of the vector to the same values as given vector
     * @param vec
     * @return this, for chaining
     */
    public Vec2 set(Vec2 vec) {
        return set(vec.x,vec.y);
    }

    /**
     * inverts this vector
     * @return this, for chaining
     */
    public Vec2 invert() {
        this.x*=-1;
        this.y*=-1;
        return this;
    }

    //TODO methods, length, dot product, rotate ...
}

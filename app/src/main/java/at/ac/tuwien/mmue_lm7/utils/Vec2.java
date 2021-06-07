package at.ac.tuwien.mmue_lm7.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Simple 2d vector utility class
 * @author simon
 */
public class Vec2 implements ObjectPool.Poolable {
    public float x;
    public float y;

    public Vec2() {
        this(0, 0);
    }

    public Vec2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void reset() {
        this.zero();
    }

    /**
     * @return a copy of this vector
     */
    public Vec2 copy() {
        return new Vec2(x, y);
    }

    /**
     * Adds component of other to this vector
     *
     * @param other, !=null, not changed by method
     * @return this, for chaining
     */
    public Vec2 add(Vec2 other) {
        return add(other.x, other.y);
    }

    /**
     * Adds given values to this vector
     *
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
     * Subtracts other from this
     *
     * @param other, !=null, not changed by method
     * @return this, for chaining
     */
    public Vec2 sub(Vec2 other) {
        return sub(other.x, other.y);
    }

    /**
     * Subtracts given values from this vector
     *
     * @param x
     * @param y
     * @return this, for chaining
     */
    public Vec2 sub(float x, float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    public Vec2 scl(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }

    /**
     * Componentwise multiplication
     *
     * @param x
     * @param y
     * @return this, for chaining
     */
    public Vec2 scl(float x, float y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    /**
     * Performs componentwise multiplication
     *
     * @param other, !=null, not changed
     * @return this, for chaining
     */
    public Vec2 scl(Vec2 other) {
        return scl(other.x, other.y);
    }

    public Vec2 div(float scalar) {
        this.x /= scalar;
        this.y /= scalar;
        return this;
    }

    /**
     * Componentwise division
     *
     * @param x
     * @param y
     * @return this, for chaining
     */
    public Vec2 div(float x, float y) {
        this.x /= x;
        this.y /= y;
        return this;
    }

    /**
     * Performs componentwise division
     *
     * @param other, !=null, not changed
     * @return this, for chaining
     */
    public Vec2 div(Vec2 other) {
        return div(other.x, other.y);
    }

    /**
     * sets the coordinates of the vector to given values
     *
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
     *
     * @param vec
     * @return this, for chaining
     */
    public Vec2 set(Vec2 vec) {
        return set(vec.x, vec.y);
    }

    /**
     * inverts this vector
     *
     * @return this, for chaining
     */
    public Vec2 inv() {
        this.x *= -1;
        this.y *= -1;
        return this;
    }

    /**
     * changes the components to the sign of the components, uses Math.signum
     * -1 if negative
     * 1 if positive
     * 0 if 0
     *
     * @return this for chaining.
     */
    public Vec2 sign() {
        this.x = Math.signum(this.x);
        this.y = Math.signum(this.y);
        return this;
    }

    /**
     * Normalizes this vector
     *
     * @return this, for chaining
     */
    public Vec2 norm() {
        float length = len();
        if (length != 0) {
            this.x /= length;
            this.y /= length;
        }
        return this;
    }

    /**
     * @return length of the vector
     */
    public float len() {
        return (float) Math.sqrt(len2());
    }

    /**
     * @return the squared length of the vector
     */
    public float len2() {
        return x * x + y * y;
    }

    /**
     * @return dot product
     */
    public float dot(float x, float y) {
        return this.x*x+this.y*y;
    }

    /**
     * @param vec !=null
     * @return dot product between this and given vector
     */
    public float dot(Vec2 vec) {
        return dot(vec.x,vec.y);
    }

    /**
     * @return cross product of this vector and given values
     */
    public float cross(float x, float y) {
        return this.x*y - this.y*x;
    }

    /**
     * @param vec !=null
     * @return cross product of this and given vector
     */
    public float cross(Vec2 vec) {
        return cross(vec.x,vec.y);
    }

    /**
     * @param to !=null
     * @return true if a counter clockwise rotation is shorter to rotate this vector to to
     */
    public boolean isCCW(Vec2 to) {
        return cross(to)>=0;
    }

    /**
     * Swaps components
     * @return this, for chaining
     */
    public Vec2 swap() {
        float tmp = x;
        this.x = y;
        this.y = tmp;
        return this;
    }

    /**
     * Sets components to 0
     * @return this, for chaining
     */
    public Vec2 zero() {
        this.x = this.y = 0;
        return this;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("(%f;%f)", x,y);
    }

    public boolean approxEquals(Vec2 other) {
        return Math.abs(x-other.x)<Utils.EPSILON && Math.abs(y-other.y)<Utils.EPSILON;
    }
}

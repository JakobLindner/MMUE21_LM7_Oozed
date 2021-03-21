package at.ac.tuwien.mmue_lm7.utils;

import java.util.ArrayList;

/**
 * Utility class to reuse objects, prevent too many instantiations
 * inspired from libgdx object pool
 */
public class ObjectPool<T extends ObjectPool.Poolable> {
    /**
     * Must be implemented by a class to be reuseable by an object pool
     */
    public interface Poolable {
        /**
         * called when the object is returned to the pool
         */
        void reset();
    }

    //TODO maybe change to static array
    private ArrayList<T> objects;

    public ObjectPool() {
        this(16);
    }

    public ObjectPool(int initialSize) {
        objects = new ArrayList<T>(initialSize);
    }

    /**
     * instantiates a new object if there is no free object
     * @return a free object from the object pool
     */
    public T obtain(){
        return null;//TODO
    }

    /**
     * Frees given object, can be obtained later
     * @param t
     */
    public void free(T t) {
        //TODO
    }
}

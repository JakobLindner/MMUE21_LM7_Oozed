package at.ac.tuwien.mmue_lm7.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;

/**
 * @author simon
 * Utility class to reuse objects, prevent too many instantiations
 * inspired from libgdx object pool (https://github.com/libgdx/libgdx/wiki/Memory-management)
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
    private final ArrayDeque<T> freeObjects;
    /**
     * used to generate a new instance of T, if there are no free objects
     */
    private final Supplier<T> supplier;

    public ObjectPool(Supplier<T> supplier) {
        this(supplier,16);
    }

    public ObjectPool(Supplier<T> supplier, int initialSize) {
        freeObjects = new ArrayDeque<T>(initialSize);
        this.supplier = supplier;
    }

    /**
     * instantiates a new object if there is no free object
     * @return a free object from the object pool
     */
    public T obtain(){
        if(freeObjects.isEmpty())
            return supplier.get();
        else
            return freeObjects.removeLast();
    }

    /**
     * Frees given object, calls reset on given object, can be obtained later
     * @param t, !=null
     */
    public void free(T t) {
        freeObjects.addLast(t);
        t.reset();
    }

    /**
     * Frees all given objects, calls reset on every object
     * @param objects , !=null
     */
    public void freeAll(ArrayList<T> objects) {
        freeObjects.addAll(objects);
        for(T t : objects)
            t.reset();
    }
}

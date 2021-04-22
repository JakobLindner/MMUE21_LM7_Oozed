package at.ac.tuwien.mmue_lm7.utils;

/**
 * Like java.util.function.Supplier, which we cannot use, since it needs at least API level24
 * @author simon
 */
@FunctionalInterface
public interface Supplier<T> {
    T get();
}

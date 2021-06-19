package io.itch.jaknak72.oozed.utils;

/**
 * Like java.util.function.Supplier, which we cannot use, since it needs at least API level24
 * @author simon
 */
@FunctionalInterface
public interface Supplier<T> {
    T get();
}

package util;

/**
 * This class represents a tuple - pairs of two objects.
 *
 * @param <T> first object
 * @param <U> second object
 */
public class Tuple<T, U> {
    private T first;
    private U second;

    public Tuple(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public U getSecond() {
        return second;
    }
}

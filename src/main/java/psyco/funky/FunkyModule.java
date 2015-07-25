package psyco.funky;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by lipeng on 15/7/25.
 */
public class FunkyModule {


    public static <T> Predicate<T> any() {
        return e -> true;
    }

    public static <T> Predicate<T> some() {
        return e -> true;
    }

//    public static Predicate pass = e -> true;


    public static <T> Predicate<T> eq(T value) {
        return t -> equal(t, value);
    }

    public static <T extends Comparable> Predicate<T> more(T value) {
        return t -> value != null ? value.compareTo(t) < 0 : t != null;
    }

    public static <T extends Comparable> Predicate<T> noLess(T value) {
        return t -> t == value || t != null && t.compareTo(value) >= 0;
    }

    public static <T extends Comparable> Predicate<T> less(T value) {
        return t -> t == null ? value != null : t.compareTo(value) < 0;
    }

    public static <T extends Comparable> Predicate<T> noMore(T value) {
        return t -> t == value || t != null && t.compareTo(value) <= 0;
    }

    public static <T> Consumer<T> println(T v) {
        return e -> System.out.println(v);
    }

    public static <T> Consumer<T> print(T v) {
        return e -> System.out.print(v);
    }

    private static <T> boolean equal(T v, T t) {
        return v == t || v != null && v.equals(t);
    }
}

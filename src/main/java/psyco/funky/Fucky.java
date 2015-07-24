package psyco.funky;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by lipeng on 15/7/24.
 */
public class Fucky {
    /**
     * match().case().case().get()
     */


    public static <T> Match1<T> match(T t) {
        return new Match1(t);
    }

    public static class Match1<T> {
        T value;

        public Match1(T value) {
            this.value = value;
        }

        public <U extends T> When1<U> when(Predicate<U> predicate) {
            return new When1(value, predicate);
        }
    }

    static class When1<T> {
        T value;
        Predicate<T> predicate;

        public When1(T value, Predicate<T> predicate) {
            this.value = value;
            this.predicate = predicate;
        }

        /**
         * public <R,T> Matcher1R<T, R> get(Function<T, R> fn)
         * the above is not OK !!
         */
        public <R> Matcher1R<T, R> get(Function<T, R> fn) {
            Matcher1R re = new Matcher1R();
            re.value = value;
            When1R when = new When1R(predicate, re);
            when.function = fn;
            re.list.add(when);
            return re;
        }
    }

    static class When1R<T, R> {
        Predicate<T> predicate;
        Function<T, R> function;
        Matcher1R<T, R> parent;

        public When1R(Predicate<T> predicate, Matcher1R<T, R> parent) {
            this.predicate = predicate;
            this.parent = parent;
        }

        public Matcher1R<T, R> get(Function<T, R> fn) {
            this.function = fn;
            parent.list.add(this);
            return parent;
        }
    }


    static class Matcher1R<T, R> {
        T value;
        List<When1R<T, R>> list = new LinkedList<>();

        public Matcher1R() {
        }

        public When1R<T, R> when(Predicate<T> pre) {
            return new When1R(pre, this);
        }

        public R get() {
            Optional<When1R<T, R>> re = list.stream().filter(when -> when.predicate.test(value)).findFirst();
            return re.isPresent() ? re.get().function.apply(value) : null;
        }

        public OptionHolder<T> getMatch() {
            return new OptionHolder(list.stream().filter(when -> when.predicate.test(value)).findFirst());
        }

    }


    public static class OptionHolder<T> {
        Optional<T> value;

        public OptionHolder(Optional<T> value) {
            this.value = value;
        }

        /**
         * if not null return the function result
         * else return null
         */
        public <R> R notNull(Function<T, R> fn) {
            return !value.isPresent() ? null : fn.apply(value.get());
        }

        public static <T> Optional<T> empty() {
            return Optional.empty();
        }

        public static <T> Optional<T> of(T value) {
            return Optional.of(value);
        }

        public static <T> Optional<T> ofNullable(T value) {
            return Optional.ofNullable(value);
        }

        public T get() {
            return value.get();
        }

        public boolean isPresent() {
            return value.isPresent();
        }

        public void ifPresent(Consumer<? super T> consumer) {
            value.ifPresent(consumer);
        }

        public Optional<T> filter(Predicate<? super T> predicate) {
            return value.filter(predicate);
        }

        public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
            return value.map(mapper);
        }

        public <U> Optional<U> flatMap(Function<? super T, Optional<U>> mapper) {
            return value.flatMap(mapper);
        }

        public T orElse(T v) {
            return value.orElse(v);
        }

        public T orElseGet(Supplier<? extends T> other) {
            return value.orElseGet(other);
        }

        public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
            return value.orElseThrow(exceptionSupplier);
        }

        @Override
        public boolean equals(Object obj) {
            return value.equals(obj);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }

        @Override
        public String toString() {
            return value.toString();
        }

    }

    @Test
    public void test() {
        String e = ".shit";
        String re = match(e)
                .when(s -> s.startsWith(".")).get(ss -> ss + "...........")
                .when(s -> s.startsWith("_")).get(ss -> ss + "_____________")
                .get();
        System.out.println(re);
        int result = match(e)
                .when(s -> s.startsWith("never match")).get(ss -> ss + "never match...........")
                .getMatch().notNull(ss -> ss.length());
        System.out.println(result);

    }

}

package psyco.funky;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by lipeng on 15/7/24.
 */
public class Fuckyxxx {
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

        public R doMatch() {
            Optional<When1R<T, R>> re = list.stream().filter(when -> when.predicate.test(value)).findFirst();
            return re.isPresent() ? re.get().function.apply(value) : null;
        }

    }



    @Test
    public void test() {
        String e = ".shit";
        String re = match(e)
                .when(s -> s.startsWith(".")).get(ss -> ss + "...........")
                .when(s -> s.startsWith("_")).get(ss -> ss + "_____________")
                .doMatch();
        System.out.println(re);

    }

}

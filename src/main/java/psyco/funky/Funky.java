package psyco.funky;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by lipeng on 15/7/24.
 * pattern match with Java 8, like:
 * match().when().when().else().get()
 */
public class Funky {


    public static <T> Predicate<T> any() {
        return e -> true;
    }

    public static <T> Predicate<T> some() {
        return e -> true;
    }

    public static Predicate pass = e -> true;


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

    public static <T> Match1R<T> match(T t) {
        return new Match1R(t);
    }

    public static class Match1R<T> {
        protected T value;

        public Match1R(T value) {
            this.value = value;
        }

        public <U extends T> When1<U> when(Predicate<U> predicate) {
            return new When1(value, predicate);
        }

        public <A, B> Match2TAB<T, A, B> map(A a, B b) {
            return new Match2TAB(value, a, b);
        }
    }

    public static class When1<T> {
        protected T value;
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

        public <R> Matcher1R<T, R> get(R v) {
            return get(const2fun(v));
        }

        public Match1R_Consumer<T> then(Consumer<T> consumer) {
            Match1R_Consumer match1R_consumer = new Match1R_Consumer(value);
            When1_Comsumer when1_comsumer = new When1_Comsumer(predicate, match1R_consumer);
            when1_comsumer.consumer = consumer;
            match1R_consumer.conditions.add(when1_comsumer);
            return match1R_consumer;
        }
    }

    public static class When1_Comsumer<T> {
        Consumer<T> consumer;
        Predicate<T> predicate;
        Match1R_Consumer<T> parent;

        public When1_Comsumer(Predicate<T> predicate, Match1R_Consumer<T> parent) {
            this.predicate = predicate;
            this.parent = parent;
        }

        public Match1R_Consumer<T> then(Consumer<T> consumer) {
            this.consumer = consumer;
            parent.conditions.add(this);
            return parent;
        }
    }


    public static class Match1R_Consumer<T> {
        T value;
        List<When1_Comsumer<T>> conditions = new LinkedList<>();


        public Match1R_Consumer(T value) {
            this.value = value;
        }

        public When1_Comsumer<T> when(Predicate<T> predicate) {
            return new When1_Comsumer(predicate, this);
        }

        public Match1R_Consumer<T> orElse(Consumer<T> consumer) {
            When1_Comsumer<T> when = new When1_Comsumer(pass, this);
            when.consumer = consumer;
            this.conditions.add(when);
            return this;
        }

        public void doMatch() {
            Optional<When1_Comsumer<T>> re = conditions.stream().filter(when -> when.predicate.test(value)).findFirst();
            if (re.isPresent() && re.get().consumer != null)
                re.get().consumer.accept(value);
        }
    }


    public static class When1R<T, R> {
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

        public Matcher1R<T, R> get(R v) {
            return get(const2fun(v));
        }
    }


    public static class Matcher1R<T, R> {
        T value;
        List<When1R<T, R>> list = new LinkedList<>();

        public Matcher1R() {
        }

        public When1R<T, R> when(Predicate<T> pre) {
            return new When1R(pre, this);
        }

        public Matcher1R<T, R> orElse(R v) {
            return orElse(e -> v);
        }

        public Matcher1R<T, R> orElse(Function<T, R> f) {
            When1R<T, R> when = new When1R<>(e -> true, this);
            when.function = f;
            list.add(when);
            return this;
        }

        public R get() {
            Optional<When1R<T, R>> re = list.stream().filter(when -> when.predicate.test(value)).findFirst();
            return re.isPresent() ? re.get().function.apply(value) : null;
        }
    }

    public static class Match2TAB<T, A, B> {
        T t;
        A a;
        B b;
        Predicate<A> predicateA;
        Predicate<B> predicateB;

        public Match2TAB(T t, A a, B b) {
            this.t = t;
            this.a = a;
            this.b = b;
        }

        public When2TAB<T, A, B> when(Predicate<A> predicateA, Predicate<B> predicateB) {
            this.predicateA = predicateA;
            this.predicateB = predicateB;
            return new When2TAB(this);
        }
    }


    public static class When2TAB<T, A, B> {
        Match2TAB<T, A, B> matchInitial;

        public When2TAB(Match2TAB<T, A, B> matchInitial) {
            this.matchInitial = matchInitial;
        }

        public <R> Match2TABR<T, A, B, R> get(Function<T, R> fn) {
            When2TABR<T, A, B, R> condition = new When2TABR(matchInitial.predicateA, matchInitial.predicateB);
            condition.function = fn;
            Match2TABR<T, A, B, R> re = new Match2TABR(matchInitial.t, matchInitial.a, matchInitial.b);
            re.conditions.add(condition);
            return re;
        }

        public <R> Match2TABR<T, A, B, R> get(R v) {
            return get(const2fun(v));
        }
    }

    public static class Match2TABR<T, A, B, R> {
        T t;
        A a;
        B b;
        List<When2TABR<T, A, B, R>> conditions = new LinkedList<>();

        public Match2TABR(T t, A a, B b) {
            this.t = t;
            this.a = a;
            this.b = b;
        }

        public When2TABR<T, A, B, R> when(Predicate<A> predicateA, Predicate<B> predicateB) {
            When2TABR<T, A, B, R> re = new When2TABR(predicateA, predicateB);
            re.parent = this;
            conditions.add(re);
            return re;
        }

        public R get() {
            Optional<When2TABR<T, A, B, R>> re = conditions.stream().filter(when -> when.predicateA.test(a) && when.predicateB.test(b)).findFirst();
            return re.isPresent() ? re.get().function.apply(t) : null;
        }

        public Match2TABR<T, A, B, R> orElse(Function<T, R> fn) {
            When2TABR<T, A, B, R> re = new When2TABR(pass, pass);
            re.function = fn;
            conditions.add(re);
            return this;
        }

        public Match2TABR<T, A, B, R> orElse(R v) {
            return orElse(const2fun(v));
        }

    }

    public static class When2TABR<T, A, B, R> {
        Predicate<A> predicateA;
        Predicate<B> predicateB;
        Function<T, R> function;
        Match2TABR<T, A, B, R> parent;

        public When2TABR(Predicate<A> predicateA, Predicate<B> predicateB) {
            this.predicateA = predicateA;
            this.predicateB = predicateB;
        }

        public Match2TABR<T, A, B, R> get(Function<T, R> fn) {
            this.function = fn;
            return parent;
        }

        public Match2TABR<T, A, B, R> get(R v) {
            return get(const2fun(v));
        }
    }


    private static <T, R> Function<T, R> const2fun(R v) {
        return t -> v;
    }

    private static <T> boolean equal(T v, T t) {
        return v == t || v != null && v.equals(t);
    }


}

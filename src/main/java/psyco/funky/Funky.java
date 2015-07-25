package psyco.funky;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import static psyco.funky.FunkyModule.any;

/**
 * Created by lipeng on 15/7/24.
 * pattern match with Java 8, like:
 * match().when().when().else().get()
 */
public class Funky {


    public static <T> Match1R<T> match(T t) {
        return new Match1R(t);
    }


    public static class MatchBuilder<T> {
        Match1R<T> match1R;

        public <U extends T> When1<U> when(Predicate<U> predicate) {
            return match1R.when(predicate);
        }

        public <A, B> Match2TAB<T, A, B> map(A a, B b) {
            return match1R.map(a, b);
        }

        public <R> MatchPair<T, R> pair(T t, R v) {
            return match1R.pair(t, v);
        }
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


        public <R> MatchPair<T, R> pair(T t, R v) {
            MatchPair re = new MatchPair(value);
            re.map.put(t, v);
            return re;
        }
    }

    public static class MatchPair<T, R> {
        T t;
        Map<T, R> map = new HashMap();
        R defaulValue = null;

        public MatchPair(T t) {
            this.t = t;
        }

        public MatchPair<T, R> pair(T t, R v) {
            map.put(t, v);
            return this;
        }

        public R get() {
            R re = map.get(t);
            return re != null ? re : defaulValue;
        }

        public MatchPair<T, R> orElse(R r) {
            this.defaulValue = r;
            return this;
        }

        public R parse(T value) {
            this.t = value;
            return get();
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
            When1_Comsumer<T> when = new When1_Comsumer(any(), this);
            when.consumer = consumer;
            this.conditions.add(when);
            return this;
        }

        public void doMatch() {
            Optional<When1_Comsumer<T>> re = conditions.stream().filter(when -> when.predicate.test(value)).findFirst();
            if (re.isPresent() && re.get().consumer != null)
                re.get().consumer.accept(value);
        }

        public void parse(T value) {
            this.value = value;
            this.doMatch();
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

        public R parse(T value) {
            this.value = value;
            return get();
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
            When2TABR<T, A, B, R> re = new When2TABR(any(), any());
            re.function = fn;
            conditions.add(re);
            return this;
        }

        public Match2TABR<T, A, B, R> orElse(R v) {
            return orElse(const2fun(v));
        }

        public R parse(T value) {
            this.t = value;
            return get();
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


}

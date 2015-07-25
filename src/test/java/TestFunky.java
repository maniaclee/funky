import org.junit.Test;

import static psyco.funky.Funky.*;

/**
 * Created by lipeng on 15/7/25.
 */
public class TestFunky {
    @Test
    public void test() {
        int num = 1;
        String re = match(num)
                .when(eq(4)).get(s -> "equal")
                .when(more(4)).get("more")
                .orElse("little")
                .get();
        System.out.println(re);

        /*lambda返回都为包装类，如果int re=null, 报错！！坑啊*/
        String e = "shit";
        match(e).when(s -> s.startsWith(".")).then(s -> System.out.printf(s + "..........."))
                .when(s -> s.startsWith("_")).then(s -> System.out.printf(s + "_____________"))
                .orElse(s -> System.out.println("nothing match"))
                .doMatch();

        String logo = "ano";
        String what = match(logo).
                map(logo.length(), logo.charAt(0)).
                when(more(10), eq('f')).get("").
                when(less(8), eq('a')).get(s -> "too short:" + s).
                orElse("----no no no -----").
                get();
        System.out.println(what);
    }
}

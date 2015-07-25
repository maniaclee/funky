import org.junit.Test;

import static psyco.funky.Funky.*;
import static psyco.funky.FunkyModule.*;

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

        /*lambda返回都为包装类，如果int re=null, 报错*/
        String e = "shit";
        match(e).when(s -> s.startsWith(".")).then(print(e + "..........."))
                .when(s -> s.startsWith("_")).then(println(e + "_____________"))
                .orElse(println("nothing match"))
                .doMatch();

        String logo = "ano";
        String what = match(logo).
                map(logo.length(), logo.charAt(0)).
                when(more(10), eq('f')).get("").
                when(less(8), any()).get(s -> "too short:" + s).
                orElse("----no no no -----").
                get();
        System.out.println(what);

        int number = 3;
        match(number).
                pair(4, "four").
                pair(6, "six").
                orElse("whatever").
                get();
        System.out.println(number);


        Matcher1R<Integer, String> match = new MatchBuilder<Integer>()
                .when(eq(4)).get(s -> "equal")
                .when(more(4)).get("more")
                .orElse("little");
        String parseResult = match.parse(4);
        System.out.printf(parseResult);
    }
}

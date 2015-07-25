funky
=====

Pattern match for jdk 1.8
## Examples

###1.  match and return
```java
  int num = 1;
        String re = match(num)
                .when(eq(4)).get(s -> "equal")
                .when(more(4)).get("more")
                .orElse("little")
                .get();
        System.out.println(re);
```

###2.  match and then
```java
        String e = "shit";
        match(e).when(s -> s.startsWith(".")).then(print(e + "..........."))
                        .when(s -> s.startsWith("_")).then(println(e + "_____________"))
                        .orElse(println("nothing match"))
                        .doMatch();
```
###3.  map with Tuple
```java
        String logo = "ano";
        String what = match(logo).
                map(logo.length(), logo.charAt(0)).
                when(more(10), eq('f')).get("").
                when(less(8), any()).get(s -> "too short:" + s).
                orElse("----no no no -----").
                get();
        System.out.println(what);
```
###4.   pair match
```java
        int number = 3;
        match(number).
                pair(4, "four").
                pair(6, "six").
                orElse("whatever").
                get();
        System.out.println(number);               
```
###5.  builder for reuse 
```java
        Matcher1R<Integer, String> match = new MatchBuilder<Integer>()
                .when(eq(4)).get(s -> "equal")
                .when(more(4)).get("more")
                .orElse("little");
        String parseResult = match.parse(4);
        System.out.printf(parseResult);
```
## License

    Copyright 2015 Psyco (Peng Li)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
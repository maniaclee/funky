funky
=====

Pattern match for jdk 1.8
## Examples

###1.  match and return
```java
String e = ".shit";
String re = match(e)
        .when(s -> s.startsWith(".")).get(ss -> ss + "...........")
        .when(s -> s.startsWith("_")).get(ss -> ss + "_____________")
        .get();
System.out.println(re);
```

###2.  extend Optional
```java
int result = match(e)
        .when(s -> s.startsWith("never match")).get(ss -> ss + "never match...........")
        .getMatch().notNull(ss -> ss.length());
System.out.println(result);
```
## License

    Copyright 2015 Psyco(PengLi)

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
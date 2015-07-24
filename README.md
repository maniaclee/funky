# funky

Pattern match for jdk 1.8


1.  match and return
        String e = ".shit";
        String re = match(e)
                .when(s -> s.startsWith(".")).get(ss -> ss + "...........")
                .when(s -> s.startsWith("_")).get(ss -> ss + "_____________")
                .doMatch();

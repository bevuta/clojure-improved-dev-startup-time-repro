# Clojure Improved Dev Startup Time Repro

This repository contains a minimal reproduction case for a problem encountered with the [Improving Development Startup Time](https://clojure.org/guides/dev_startup_time) guide. It was originally discovered by depending on [`clj-http`](https://github.com/dakrone/clj-http). We were able to narrow it down to its use of [`potemkin/def-map-type`](https://github.com/clj-commons/potemkin/blob/master/src/potemkin/collections.clj#L206) for the [`HeaderMap`](https://github.com/dakrone/clj-http/blob/3.x/src/clj_http/headers.clj#L105) type. Thus, the reproduction doesn't involve `clj-http` but reproduces the same issue by directly using `potemkin/def-map-type`.

## Steps To Reproduce

Verify that the program works as intended:

    $ clojure -X:dev foo/bar
    {:ok true}

Precompile all files as per the guide with incorporated `user.clj`:

    $ clojure -M:dev -e "(binding [*compile-files* true] (require 'user :reload-all))"

Note that this bug only occurs when `user.clj` is involved.

Now, when running the same command as before:

    $ clojure -X:dev foo/bar
    Exception in thread "main" Syntax error compiling at (user.clj:1:1).
        at clojure.lang.Compiler.load(Compiler.java:7652)
        at clojure.lang.RT.loadResourceScript(RT.java:381)
        at clojure.lang.RT.loadResourceScript(RT.java:368)
        at clojure.lang.RT.maybeLoadResourceScript(RT.java:364)
        at clojure.lang.RT.doInit(RT.java:486)
        at clojure.lang.RT.init(RT.java:467)
        at clojure.main.main(main.java:38)
    Caused by: java.lang.ExceptionInInitializerError
        at java.base/java.lang.Class.forName0(Native Method)
        at java.base/java.lang.Class.forName(Class.java:398)
        at clojure.lang.RT.classForName(RT.java:2212)
        at clojure.lang.RT.classForName(RT.java:2221)
        at clojure.lang.RT.loadClassForName(RT.java:2240)
        at clojure.lang.RT.load(RT.java:449)
        at clojure.lang.RT.load(RT.java:424)
        at clojure.core$load$fn__6856.invoke(core.clj:6115)
        at clojure.core$load.invokeStatic(core.clj:6114)
        at clojure.core$load.doInvoke(core.clj:6098)
        at clojure.lang.RestFn.invoke(RestFn.java:408)
        at clojure.core$load_one.invokeStatic(core.clj:5897)
        at clojure.core$load_one.invoke(core.clj:5892)
        at clojure.core$load_lib$fn__6796.invoke(core.clj:5937)
        at clojure.core$load_lib.invokeStatic(core.clj:5936)
        at clojure.core$load_lib.doInvoke(core.clj:5917)
        at clojure.lang.RestFn.applyTo(RestFn.java:142)
        at clojure.core$apply.invokeStatic(core.clj:669)
        at clojure.core$load_libs.invokeStatic(core.clj:5974)
        at clojure.core$load_libs.doInvoke(core.clj:5958)
        at clojure.lang.RestFn.applyTo(RestFn.java:137)
        at clojure.core$apply.invokeStatic(core.clj:669)
        at clojure.core$require.invokeStatic(core.clj:5996)
        at clojure.core$require.doInvoke(core.clj:5996)
        at clojure.lang.RestFn.invoke(RestFn.java:408)
        at user$eval138$loading__6737__auto____139.invoke(user.clj:1)
        at user$eval138.invokeStatic(user.clj:1)
        at user$eval138.invoke(user.clj:1)
        at clojure.lang.Compiler.eval(Compiler.java:7181)
        at clojure.lang.Compiler.eval(Compiler.java:7170)
        at clojure.lang.Compiler.load(Compiler.java:7640)
        ... 6 more
    Caused by: java.lang.ClassNotFoundException: foo.FooMap
        at java.base/java.net.URLClassLoader.findClass(URLClassLoader.java:471)
        at clojure.lang.DynamicClassLoader.findClass(DynamicClassLoader.java:69)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:589)
        at clojure.lang.DynamicClassLoader.loadClass(DynamicClassLoader.java:77)
        at java.base/java.lang.ClassLoader.loadClass(ClassLoader.java:522)
        at java.base/java.lang.Class.forName0(Native Method)
        at java.base/java.lang.Class.forName(Class.java:398)
        at clojure.lang.RT.classForName(RT.java:2212)
        at clojure.lang.RT.classForName(RT.java:2221)
        at foo__init.__init0(Unknown Source)
        at foo__init.<clinit>(Unknown Source)
        ... 37 more

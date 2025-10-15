error id: 8CC08405ADA46521F477B597CEBB48A2
file://<WORKSPACE>/Main.scala
### java.lang.AssertionError: assertion failed: (List(Stamp(2024-03-05T22:35:58Z,513508,(dev=801,ino=6555836))),0)

occurred in the presentation compiler.



action parameters:
offset: 9
uri: file://<WORKSPACE>/Main.scala
text:
```scala
object Ma@@

```


presentation compiler configuration:
Scala version: 2.13.16
Classpath:
<WORKSPACE>/.bloop/core/bloop-bsp-clients-classes/test-classes-Metals-xGQxN3eVSReXCxSlmLYgWw== [exists ], <HOME>/.cache/bloop/semanticdb/com.sourcegraph.semanticdb-javac.0.11.0/semanticdb-javac-0.11.0.jar [exists ], <WORKSPACE>/.bloop/core/bloop-bsp-clients-classes/classes-Metals-xGQxN3eVSReXCxSlmLYgWw== [exists ], <WORKSPACE>/.bloop/domain/bloop-bsp-clients-classes/classes-Metals-xGQxN3eVSReXCxSlmLYgWw== [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-lang/scala-library/2.13.16/scala-library-2.13.16.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-effect_2.13/3.5.4/cats-effect_2.13-3.5.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-core_2.13/2.12.0/cats-core_2.13-2.12.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scalameta/munit_2.13/1.0.0/munit_2.13-1.0.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-effect-kernel_2.13/3.5.4/cats-effect-kernel_2.13-3.5.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-effect-std_2.13/3.5.4/cats-effect-std_2.13-3.5.4.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/typelevel/cats-kernel_2.13/2.12.0/cats-kernel_2.13-2.12.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scalameta/junit-interface/1.0.0/junit-interface-1.0.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scalameta/munit-diff_2.13/1.0.0/munit-diff_2.13-1.0.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scala-sbt/test-interface/1.0/test-interface-1.0.jar [exists ], <HOME>/.cache/coursier/v1/https/repo1.maven.org/maven2/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar [exists ]
Options:
-deprecation -feature -unchecked -Xlint:unused -Yrangepos -Xplugin-require:semanticdb




#### Error stacktrace:

```
scala.tools.nsc.classpath.FileBasedCache.getOrCreate(ZipAndJarFileLookupFactory.scala:282)
	scala.tools.nsc.classpath.ZipAndJarFileLookupFactory.create(ZipAndJarFileLookupFactory.scala:47)
	scala.tools.nsc.classpath.ZipAndJarFileLookupFactory.create$(ZipAndJarFileLookupFactory.scala:38)
	scala.tools.nsc.classpath.ClassPathFactory$.newClassPath(ClassPathFactory.scala:80)
	scala.tools.nsc.classpath.ClassPathFactory.newClassPath(ClassPathFactory.scala:29)
	scala.tools.nsc.classpath.ClassPathFactory.$anonfun$classesInPathImpl$1(ClassPathFactory.scala:64)
	scala.tools.nsc.classpath.ClassPathFactory.classesInPathImpl(ClassPathFactory.scala:59)
	scala.tools.nsc.classpath.ClassPathFactory.classesInExpandedPath(ClassPathFactory.scala:48)
	scala.tools.util.PathResolver$Calculated$.basis(PathResolver.scala:268)
	scala.tools.util.PathResolver$Calculated$.containers$lzycompute(PathResolver.scala:275)
	scala.tools.util.PathResolver$Calculated$.containers(PathResolver.scala:275)
	scala.tools.util.PathResolver.containers(PathResolver.scala:291)
	scala.tools.util.PathResolver.computeResult(PathResolver.scala:313)
	scala.tools.util.PathResolver.result(PathResolver.scala:296)
	scala.tools.nsc.backend.JavaPlatform.classPath(JavaPlatform.scala:30)
	scala.tools.nsc.backend.JavaPlatform.classPath$(JavaPlatform.scala:29)
	scala.tools.nsc.Global$GlobalPlatform.classPath(Global.scala:133)
	scala.tools.nsc.Global.classPath(Global.scala:158)
	scala.tools.nsc.Global$GlobalMirror.rootLoader(Global.scala:68)
	scala.reflect.internal.Mirrors$Roots$RootClass.<init>(Mirrors.scala:309)
	scala.reflect.internal.Mirrors$Roots.RootClass$lzycompute(Mirrors.scala:323)
	scala.reflect.internal.Mirrors$Roots.RootClass(Mirrors.scala:323)
	scala.reflect.internal.Mirrors$Roots$EmptyPackageClass.<init>(Mirrors.scala:332)
	scala.reflect.internal.Mirrors$Roots.EmptyPackageClass$lzycompute(Mirrors.scala:338)
	scala.reflect.internal.Mirrors$Roots.EmptyPackageClass(Mirrors.scala:338)
	scala.reflect.internal.Mirrors$Roots.EmptyPackageClass(Mirrors.scala:278)
	scala.reflect.internal.Mirrors$RootsBase.init(Mirrors.scala:252)
	scala.tools.nsc.Global.rootMirror$lzycompute(Global.scala:75)
	scala.tools.nsc.Global.rootMirror(Global.scala:73)
	scala.tools.nsc.Global.rootMirror(Global.scala:45)
	scala.reflect.internal.Definitions$DefinitionsClass.ObjectClass$lzycompute(Definitions.scala:295)
	scala.reflect.internal.Definitions$DefinitionsClass.ObjectClass(Definitions.scala:295)
	scala.reflect.internal.Definitions$DefinitionsClass.init(Definitions.scala:1667)
	scala.tools.nsc.Global$Run.<init>(Global.scala:1263)
	scala.tools.nsc.interactive.Global$TyperRun.<init>(Global.scala:1352)
	scala.tools.nsc.interactive.Global.newTyperRun(Global.scala:1375)
	scala.tools.nsc.interactive.Global.<init>(Global.scala:295)
	scala.meta.internal.pc.MetalsGlobal.<init>(MetalsGlobal.scala:49)
	scala.meta.internal.pc.ScalaPresentationCompiler.newCompiler(ScalaPresentationCompiler.scala:627)
	scala.meta.internal.pc.ScalaPresentationCompiler.$anonfun$compilerAccess$1(ScalaPresentationCompiler.scala:147)
	scala.meta.internal.pc.CompilerAccess.loadCompiler(CompilerAccess.scala:40)
	scala.meta.internal.pc.CompilerAccess.retryWithCleanCompiler(CompilerAccess.scala:182)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withSharedCompiler$1(CompilerAccess.scala:155)
	scala.Option.map(Option.scala:242)
	scala.meta.internal.pc.CompilerAccess.withSharedCompiler(CompilerAccess.scala:154)
	scala.meta.internal.pc.CompilerAccess.$anonfun$withNonInterruptableCompiler$1(CompilerAccess.scala:132)
	scala.meta.internal.pc.CompilerAccess.$anonfun$onCompilerJobQueue$1(CompilerAccess.scala:209)
	scala.meta.internal.pc.CompilerJobQueue$Job.run(CompilerJobQueue.scala:152)
	java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1144)
	java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:642)
	java.base/java.lang.Thread.run(Thread.java:1583)
```
#### Short summary: 

java.lang.AssertionError: assertion failed: (List(Stamp(2024-03-05T22:35:58Z,513508,(dev=801,ino=6555836))),0)
import ReleaseTransformations._

name := "pure-log"
scalaVersion := "2.12.7"
organization := "org.novelfs"

val catsEffectVersion = "1.0.0"
val kafkaSerializationV = "0.3.2"
val scalatestVersion = "3.0.4"
val typesafeConfigVersion = "1.3.1"

libraryDependencies ++= Seq(
  "ch.qos.logback"  % "logback-classic"               % "1.2.3"
  , "com.github.mpilquist" %% "simulacrum" % "0.14.0"
  , "com.typesafe" % "config" % typesafeConfigVersion
  , "org.log4s" %% "log4s" % "1.6.1"
  , "org.scalactic" %% "scalactic" % scalatestVersion
  , "org.scalatest" %% "scalatest" % scalatestVersion % Test
  , "org.scalamock" %% "scalamock" % "4.1.0" % Test
  , "org.scalacheck" %% "scalacheck" % "1.13.4" % Test
  , "org.typelevel" %% "cats-effect" % catsEffectVersion
  , "org.typelevel" %% "cats-mtl-core" % "0.4.0"
  , "net.manub" %% "scalatest-embedded-kafka" % "2.0.0" % Test
)

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.4")
addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

scalacOptions ++= Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
)

scalacOptions in (Compile, console) --= Seq("-Ywarn-unused:imports", "-Xfatal-warnings")

releaseVersionBump := sbtrelease.Version.Bump.Bugfix

credentials ++= (for {
  username <- sys.env.get("SONATYPE_USERNAME")
  password <- sys.env.get("SONATYPE_PASSWORD")
} yield Credentials("Sonatype Nexus Repository Manager", "oss.sonatype.org", username, password)).toSeq

pomIncludeRepository := { _ => false }
publishMavenStyle := true

licenses := Seq("Apache-2.0" -> url("https://opensource.org/licenses/Apache-2.0"))

homepage := Some(url("https://github.com/TheInnerLight/pure-log"))

scmInfo := Some(
  ScmInfo(
    url("https://github.com/TheInnerLight/pure-log"),
    "scm:git@github.com:TheInnerLight/pure-log.git"
  )
)

releaseCrossBuild := true
crossScalaVersions := Seq("2.11.12", "2.12.6")

useGpg := false
pgpSecretRing := file("local.secring.gpg")
pgpPublicRing := file("local.pubring.gpg")
pgpPassphrase := sys.env.get("PGP_PASS").map(_.toArray)

developers := List(
  Developer(
    id    = "TheInnerLight",
    name  = "Phil Curzon",
    email = "phil@novelfs.org",
    url   = url("https://github.com/TheInnerLight")
  )
)

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases"  at nexus + "service/local/staging/deploy/maven2")
}

parallelExecution in Test := false

releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  releaseStepCommand("publishSigned"),
  setNextVersion,
  commitNextVersion,
  releaseStepCommand("sonatypeRelease"),
  pushChanges
)

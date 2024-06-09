ThisBuild / organization := "org.zio.guide"

ThisBuild / description := "Scala ZIO Guide"

ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / scalacOptions ++= Seq(
  "-encoding", "utf8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xfatal-warnings"
)

ThisBuild / fork := true
ThisBuild / run / connectInput := true

val zioVersion = "2.0.6"
val http4sVersion = "0.23.20"
val http4sBlazeVersion = "0.23.15"
val zioInteropCatsVersion = "23.0.03"
val logbackClasscVersion = "1.4.8"
val jansiVersion = "2.4.0"

ThisBuild / libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-test" % zioVersion % Test,
  "dev.zio" %% "zio-test-sbt" % zioVersion % Test
)

lazy val root = project
  .in(file("."))
  .settings(name := "zio-guide")
  .aggregate(
    essentials,
    parallelismAndConcurrency,
    concurrentStructures,
    resourceHandling,
    dependencyInjection
  )

lazy val essentials = project
  .in(file("essentials"))
  .settings(
    libraryDependencies += "org.http4s" %% "http4s-dsl" % http4sVersion,
    libraryDependencies += "org.http4s" %% "http4s-blaze-server" % http4sBlazeVersion,
    libraryDependencies += "dev.zio" %% "zio-interop-cats" % zioInteropCatsVersion,
    libraryDependencies += "ch.qos.logback" % "logback-classic" % logbackClasscVersion,
    libraryDependencies += "org.fusesource.jansi" % "jansi" % jansiVersion
  )

lazy val parallelismAndConcurrency = project.in(file("parallelism-and-concurrency"))
lazy val concurrentStructures = project.in(file("concurrent-structures"))
lazy val resourceHandling = project.in(file("resource-handling"))
lazy val dependencyInjection = project.in(file("dependency-injection"))

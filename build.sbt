ThisBuild / organization := "org.zio.guide"

ThisBuild / description := "Scala ZIO Guide"

ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "2.13.10"

ThisBuild / scalacOptions ++= Seq(
  "-encoding", "utf8",
  "-deprecation",
  "-feature",
  "-unchecked"
)

ThisBuild / fork := true

val zioVersion = "2.0.6"

ThisBuild / libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion
)

lazy val root = project.in(file("."))
  .settings(name := "zio-guide")
  .aggregate(
    essentials
  )

lazy val essentials = project.in(file("essentials"))


ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "capital.gains"
  )

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-json" % "0.7.43",
  "org.scalatest" %% "scalatest" % "3.2.19" % Test
)
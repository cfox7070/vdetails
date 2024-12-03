enablePlugins(ScalaJSPlugin)

ThisBuild / scalaVersion := "2.13.13"
ThisBuild / organization := "com.cfx70"
ThisBuild / version      := "0.1.0"
 
import scala.sys.process._
import sbt.Keys.streams
val toHtml = taskKey[Unit]("copy to html//")

lazy val commonSettings = Seq(
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) },
    libraryDependencies +="org.scalactic" %%% "scalactic" % "3.2.14",
//    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.7" % "test",
	libraryDependencies += "com.lihaoyi" %%% "utest" % "0.8.1" % "test",
    libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.7.0",
    testFrameworks += new TestFramework("utest.runner.Framework"),
    scalacOptions ++= Seq(
          "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
          "-encoding", "utf-8",                // Specify character encoding used by source files.
          "-explaintypes",                     // Explain type errors in more detail.
          "-feature"),
    toHtml := {
       val log = streams.value.log
       log.info("replacing server files")
      "./copy_det.sh" !
    }
)

lazy val threejsfacade = (project in file("threejsfacade"))
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "threejsfacade",
    commonSettings
   )

lazy val core = (project in file("core"))
  .dependsOn(threejsfacade)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "core",
   commonSettings
   )

lazy val render = (project in file("render"))
  .dependsOn(core)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    name := "render",
   commonSettings
   )

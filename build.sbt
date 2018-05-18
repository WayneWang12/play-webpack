
import play.sbt.PlayImport.PlayKeys.playRunHooks

import scala.sys.process.Process

name := """ShuyunAnalyzer"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  guice,
  ws,
 "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
)

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "com.example.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "com.example.binders._"
// Starts: Webpack build task

lazy val isWin = System.getProperty("os.name").toUpperCase().contains("WIN")
val appPath = if (isWin) "app\\assets" else "./app/assets"
val webpackBuild = taskKey[Unit]("Webpack build task.")

webpackBuild := {
  if (isWin) Process("cmd /c npm run build", file(appPath)).run
  else Process("npm run build", file(appPath)).run
}

(packageBin in Universal) := ((packageBin in Universal) dependsOn webpackBuild).value
// Ends.

// Starts: Webpack server process when running locally and build actions for production bundle
lazy val frontendDirectory = baseDirectory {
  _ / appPath
}
playRunHooks += frontendDirectory.map(WebpackServer(_)).value

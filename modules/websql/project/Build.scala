import sbt._
import Keys._
import PlayKeys._

object WebSQLModule extends Build {


  val appName = "websql-module"
  val appVersion = "2.0-SNAPSHOT"

  val appDependencies = Seq(
    "play" %% "play" % "2.1-RC1",
    "com.typesafe" % "slick_2.10.0-RC1" % "0.11.2",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.h2database" % "h2" % "1.3.166"
  )

  lazy val module = play.Project(appName,appVersion,appDependencies).settings( 
    templatesImport += "views.helpers.Helpers._",
    scalacOptions ++= Seq(
      "-feature", 
      "-language:postfixOps,implicitConversions"
    ),
    organization := "lu.intech",
    scalaVersion := "2.10.0-RC5"
  )

}
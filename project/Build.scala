import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName = "WebSql"
  val appVersion = "2.0-SNAPSHOT"

  val appDependencies = Seq(
    "com.typesafe" % "slick_2.10" % "1.0.0-RC1",
    "org.slf4j" % "slf4j-nop" % "1.6.4",
    "com.h2database" % "h2" % "1.3.166",
  jdbc
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalacOptions ++= Seq("-feature", "-language:postfixOps,implicitConversions"),
    templatesImport += "views.helpers.Helpers._"
  )

}

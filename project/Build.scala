import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "WebSql"
  val appVersion      = "2.0-SNAPSHOT"

  val appDependencies = Seq(
  	anorm,
  	jdbc
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    scalacOptions ++= Seq("-feature","-language:postfixOps,implicitConversions"),
    templatesImport += "views.helpers.Helpers._"
  )

}

import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "passwordSafeServer"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "com.typesafe" % "slick_2.10.0-M7" % "0.11.1",
    "org.scalatest" % "scalatest_2.10" % "1.9.1" % "test",
    "org.scala-lang" % "scala-actors" % "2.10.0"    % "test"
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    testOptions in Test := Nil
    // Add your own project settings here      
  )

}

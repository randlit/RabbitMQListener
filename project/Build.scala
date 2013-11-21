import sbt._
import sbt.Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "akkaWithRabbit"
  val appVersion      = "1.0-SNAPSHOT"



  val appDependencies = Seq(
    // Add your project dependencies here,
    "com.rabbitmq" % "amqp-client" % "2.8.1",
    "io.searchbox" % "jest" % "0.0.5",
    "com.github.tototoshi" %% "play-json4s-native" % "0.2.0",
    "com.github.tototoshi" %% "play-json4s-test-native" % "0.2.0" % "test",
    jdbc,
    anorm
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here
    defaultScalaSettings:_*
  ).settings(
    resolvers += "sonatype" at "https://oss.sonatype.org/content/groups/public/"
  ).settings(
    resolvers +="Codahale" at "http://repo.codahale.com"

  )

}



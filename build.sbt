import Dependencies._

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization := "com.kixeye",
      scalaVersion := "2.12.3",
      version := "1.0.0"
    )),
    name := "testapp",
    libraryDependencies ++= Seq(
      scalaTest % Test,
      "com.typesafe.akka" %% "akka-http" % "10.0.10",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.17",
      "ch.qos.logback" % "logback-classic" % "1.2.1",
      "net.logstash.logback" % "logstash-logback-encoder" % "4.8",
      "com.github.etaty" %% "rediscala" % "1.8.0",
      "io.circe" %% "circe-core" % "0.7.0",
      "io.circe" %% "circe-generic" % "0.7.0",
      "io.circe" %% "circe-parser" % "0.7.0",
      "de.heikoseeberger" %% "akka-http-circe" % "1.13.0"
    ),
    fork in run := true,
    assemblyJarName in assembly := name.value + ".jar",
    mainClass in assembly := Some("com.kixeye.faction.Main")
  )
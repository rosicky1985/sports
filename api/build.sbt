name := "sports.api"

organization := "com.nbb"

version := "1.0"

scalaVersion := "2.10.5"

libraryDependencies ++= Seq(
  "io.spray" % "spray-can" % "1.1-M8",
  "io.spray" % "spray-http" % "1.1-M8",
  "io.spray" % "spray-routing" % "1.1-M8",
  "net.liftweb" %%
    "lift-json" % "2.5.1",
  "net.liftweb" % "lift-json-ext_2.10" % "2.5.1",
  "com.typesafe.slick" %% "slick" % "1.0.1",
  "mysql" % "mysql-connector-java" % "5.1.25",
  "com.typesafe.akka" %% "akka-actor" % "2.1.4",
  "com.typesafe.akka" %% "akka-slf4j" % "2.1.4",
  "ch.qos.logback" % "logback-classic" % "1.0.13",
  "net.databinder.dispatch" % "dispatch-core_2.10" % "0.11.3",
  "com.github.philcali" %% "cronish" % "0.1.3",
  "org.jsoup" % "jsoup" % "1.8.3",
  "org.scalaj" % "scalaj-time_2.10.2" % "0.7"
)

resolvers ++= Seq(
  "Spray repository" at "http://repo.spray.io",
  "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"
)

assemblySettings

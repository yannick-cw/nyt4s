name := "nyt4s"
version := "0.1-SNAPSHOT"
scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.scalatest" % "scalatest_2.11" % "3.0.0-M15" % Test,
  "com.typesafe.akka" % "akka-http-experimental_2.11" % akkaV,
  "com.typesafe.akka" % "akka-stream_2.11" % akkaV,
  "com.typesafe.akka" % "akka-testkit_2.11" % akkaV,
  "com.typesafe.akka" % "akka-http-spray-json-experimental_2.11" % akkaV,
  "joda-time" % "joda-time" % "2.9.4"
)

lazy val akkaV: String = "2.4.8"

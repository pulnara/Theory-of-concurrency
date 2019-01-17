name := "ScalaProducersConsumers"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.17",
  "com.typesafe.akka" %% "akka-testkit" % "2.5.17" % "test",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test")
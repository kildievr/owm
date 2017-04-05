name := "owm"

version := "1.0"

scalaVersion := "2.11.9"

libraryDependencies ++= {
  val akkaV = "2.4.17"
  val sprayV = "1.3.4"
  val json4sV = "3.5.1"
  Seq(
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "io.spray" %% "spray-routing" % sprayV,
    "io.spray" %% "spray-can" % sprayV,
    "org.json4s" %% "json4s-jackson" % json4sV,
    "org.locationtech.geotrellis" %% "geotrellis-proj4" % "1.0.0",
    "io.spray" %% "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-slf4j" % akkaV,
    "org.specs2" %% "specs2-core" % "2.5" % "test",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
}
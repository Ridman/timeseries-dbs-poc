import sbt._

object Dependencies {
  lazy val typesafeConfig = "com.typesafe" % "config" % "1.4.0"
  lazy val postgres = "org.postgresql" % "postgresql" % "42.2.12"
  lazy val flyway = "org.flywaydb" % "flyway-core" % "6.3.3"
  lazy val apacheHttpClient = "org.apache.httpcomponents" % "httpclient" % "4.5.12"
  lazy val sttpMonixClient = List(
    "com.softwaremill.sttp.client" %% "async-http-client-backend-monix" % "2.0.7",
    "com.softwaremill.sttp.client" %% "circe" % "2.0.7",
    "io.circe" %% "circe-generic" % "0.12.1"
  )
  lazy val logging = List(
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
    "ch.qos.logback" % "logback-classic" % "1.2.3"
  )
}

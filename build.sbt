import Dependencies._

name := "timeseries-dbs-poc"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= List(
  postgres,
  flyway,
  typesafeConfig,
  apacheHttpClient
)

libraryDependencies ++= logging
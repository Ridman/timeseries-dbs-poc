import Dependencies._

name := "timeseries-dbs-benchmark"

version := "0.1"

scalaVersion := "2.13.1"

libraryDependencies ++= List(
  postgres,
  flyway,
  typesafeConfig
)

libraryDependencies ++= sttpMonixClient

libraryDependencies ++= logging
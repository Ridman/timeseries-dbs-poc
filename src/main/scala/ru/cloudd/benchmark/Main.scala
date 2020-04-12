package ru.cloudd.benchmark

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.typesafe.scalalogging.Logger
import org.slf4j.LoggerFactory
import ru.cloudd.benchmark.generator.MetricSetGen
import ru.cloudd.benchmark.influx.InfluxDB
import ru.cloudd.benchmark.timescaledb.TimescaleDB

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}

object Main extends App {

  import ru.cloudd.benchmark.generator.Metrics._

  val logger = Logger(LoggerFactory.getLogger("Main"))
  logger.info("Starting app...")

  val farmIds = (1 to 50).map(_ => UUID.randomUUID()).zipWithIndex
  val farms = farmIds.map {
    case (uuid, id) =>
      val metrics = id % 3 match {
        case 0 => metricNames1
        case 1 => metricNames2
        case 2 => metricNames3
      }
      Farm(id, new MetricSetGen(metrics, uuid))
  }

  val pointsPerFarm = 1_500_000 // Примерно 3 года

  val timescale = new TimescaleDB
  val influx = new InfluxDB

  val tasks = farms.map(farm => {
    Future {
      logger.info(s"Generating for farm ${farm.id}...")
      for (i <- 1 to pointsPerFarm) {
        val point = farm.gen.next
        timescale.persist(point)
        influx.persist(point)
      }
      logger.info(s"Done generating for farm ${farm.id}")
    }(ExecutionContext.global)
  })

  implicit val ec = ExecutionContext.global

  val globalResult = Future.traverse(farms)(farm => {
    Future {
      logger.info(s"Generating for farm ${farm.id}...")
      for (i <- 1 to pointsPerFarm) {
        val point = farm.gen.next
        timescale.persist(point)
        influx.persist(point)
      }
      logger.info(s"Done generating for farm ${farm.id}")
    }
  })
  Await.result(globalResult, Duration(10, TimeUnit.MINUTES))
}

case class Farm(id: Long, gen: MetricSetGen)

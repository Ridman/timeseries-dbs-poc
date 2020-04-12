package ru.cloudd.benchmark.timescaledb

import java.sql.Timestamp
import java.time.Instant
import java.util.concurrent.TimeUnit

import ru.cloudd.benchmark.DBClient
import ru.cloudd.benchmark.generator.{MetricSet, Metrics}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.{Duration, FiniteDuration}

class TimescaleDB extends DBClient with TimescaleSupport {

  private val buffer: mutable.Buffer[String] = ListBuffer()

  override def persist(metrics: MetricSet): Unit = {
    val metricValues = Metrics.allMetricNames.map(metrics.vals.getOrElse(_, "null")).mkString(",")
    val farmId = metrics.tags("farm_id")
    val timestamp = sqlTimestamp(metrics.timestamp)
    val record = s"VALUES($timestamp,'$farmId',$metricValues)"
    val result = this.synchronized {
      buffer += record
      if (buffer.size >= TimescaleDB.bufferThreshold) {
        val insert = TimescaleDB.queryPrefix + buffer.mkString(",")
        Future {
          writeToTimescaleDb(insert)
        }(ExecutionContext.global)
      } else {
        Future.successful()
      }
    }
    Await.result(result, TimescaleDB.queryTimeout)
  }

  private def sqlTimestamp(seconds: Long): java.sql.Timestamp = {
    val instant = Instant.ofEpochSecond(seconds)
    Timestamp.from(instant)
  }
}

object TimescaleDB {
  private[this] val table = "metrics"
  private[this] val queryColumns = "time,farm_id," + Metrics.allMetricNames.mkString(",")

  private val queryPrefix = s"INSERT INTO $table ($queryColumns) "
  private val bufferThreshold = 100000
  private val queryTimeout: FiniteDuration = Duration(2, TimeUnit.SECONDS)
}
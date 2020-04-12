package ru.cloudd.benchmark.influx

import java.util.concurrent.TimeUnit

import ru.cloudd.benchmark.DBClient
import ru.cloudd.benchmark.generator.MetricSet

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, ExecutionContext, Future}


class InfluxDB extends DBClient with InfluxSupport {

  private val buffer: mutable.Buffer[String] = ListBuffer()

  override def persist(metrics: MetricSet): Unit = {
    addToBufferAndWrite(metrics)
  }

  private def addToBufferAndWrite(metrics: MetricSet): Unit = {
    val record = metricsToString(metrics)
    val result = this.synchronized {
      buffer += record
      if (buffer.size >= InfluxDB.bufferThreshold) {
        val request = buffer.mkString(",")
        buffer.clear()
        Future {
          writeToInflux(request)
        }(ExecutionContext.global)
      } else {
        Future.successful()
      }
    }
    Await.result(result, InfluxDB.queryTimeout)
  }

  def metricsToString(metrics: MetricSet): String = {
    val tags = metrics.tags
      .map {
        case (tag, value) => s"$tag=$value"
      }
      .mkString(",")
    val values = metrics.vals
      .map {
        case (metric, value) => s"$metric=$value"
      }
      .mkString("\n")
    s"state,$tags $values ${metrics.timestamp}"
  }
}

object InfluxDB {
  val bufferThreshold = 100000
  val queryTimeout: FiniteDuration = Duration(2, TimeUnit.SECONDS)
}

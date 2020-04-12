package ru.cloudd.benchmark.generator

import java.time.{OffsetDateTime, ZoneOffset}
import java.util.UUID

import scala.math.BigDecimal.RoundingMode
import scala.util.Random

case class MetricGen(name: String) {
  private val random = Random

  def next: BigDecimal = BigDecimal(random.between(-10, 100000) + random.nextDouble()).setScale(2, RoundingMode.HALF_UP)
}

class MetricSetGen(metricNames: Seq[String], farmId: UUID) {
  private val gens = metricNames.map(MetricGen(_))
  private var timestamp = MetricGen.genBeginningTimestamp

  def next: MetricSet = {
    val vals = gens.map(gen => (gen.name, gen.next)).toMap

    val tags = Map("farm_id" -> farmId.toString)

    val metricSet = MetricSet(timestamp, vals, tags)
    timestamp += MetricGen.step
    metricSet
  }
}

case class MetricSet(timestamp: Long, vals: Map[String, BigDecimal], tags: Map[String, String])

object MetricGen {
  val step = 60_000L // 1 minute
  private val minYear = OffsetDateTime.of(2010, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toEpochSecond // 2010
  private val maxYear = OffsetDateTime.of(2013, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC).toEpochSecond // 2013

  def genBeginningTimestamp: Long = {
    val random = Random
    random.between(MetricGen.minYear, MetricGen.maxYear)
  }
}
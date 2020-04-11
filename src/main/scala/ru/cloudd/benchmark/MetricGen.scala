package ru.cloudd.benchmark

import java.util.UUID

import scala.math.BigDecimal.RoundingMode
import scala.util.Random

case class MetricGen(name: String) {
  private val random = Random
  def next: BigDecimal = BigDecimal(random.nextInt() + random.nextDouble()).setScale(2, RoundingMode.HALF_UP)
}

class MetricSetGen(metricNames: Seq[String], farmId: UUID, cycleIds: Seq[UUID]) {
  private val gens = metricNames.map(MetricGen(_))
  private var timestamp = MetricGen.genBeginningTimestamp
  private val random = Random

  def next: MetricSet = {
    val vals = gens.map(gen => (gen.name, gen.next)).toMap

    val cycleId = cycleIds(random.nextInt(cycleIds.length))
    val tags = Map("farm_id" -> farmId.toString, "cycle_id" -> cycleId.toString)

    val metricSet = MetricSet(timestamp, vals, tags)
    timestamp += MetricGen.step
    metricSet
  }
}

case class MetricSet(timestamp: Long, vals: Map[String, BigDecimal], tags: Map[String, String])

object MetricGen {
  val step = 60_000L // 1 minute
  private val minYear = 1262304000_000L // 2010
  private val maxYear = 1356998400_000L // 2013

  def genBeginningTimestamp: Long = {
    val random = Random
    random.between(MetricGen.minYear, MetricGen.maxYear)
  }
}

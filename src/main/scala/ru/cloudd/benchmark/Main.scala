package ru.cloudd.benchmark

import java.util.UUID

object Main extends App {
  val metricNames1 = (10 to 30).map("metric_" + _)
  val metricNames2 = (1 to 30 by 2).map("metric_" + _)
  val metricNames3 = (1 to 50 by 3).map("metric_" + _)
  val farmIds = (1 to 200).map(_ => UUID.randomUUID()).zipWithIndex
  val cycleIds = (1 to 1000).map(_ => UUID.randomUUID())
  val farms = farmIds.map {
    case (uuid, id) =>
      val metrics = id % 3 match {
        case 0 => metricNames1
        case 1 => metricNames2
        case 2 => metricNames3
      }
      val gens = Seq(new MetricSetGen(metrics, uuid, cycleIds))
      Farm(id, gens)
  }
  val gen = farms.head.metricSetGens.head
  for (
    i <- 1 to 1000000
  ) println(gen.next)
}

case class Farm(id: Long, metricSetGens: Seq[MetricSetGen])

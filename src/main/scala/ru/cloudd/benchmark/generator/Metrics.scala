package ru.cloudd.benchmark.generator

object Metrics {
  val allMetricNames = (1 to 50).map("metric_" + _)
  val metricNames1 = (10 to 30).map("metric_" + _)
  val metricNames2 = (1 to 30 by 2).map("metric_" + _)
  val metricNames3 = (1 to 50 by 3).map("metric_" + _)
}

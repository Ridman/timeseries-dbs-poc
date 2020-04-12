package ru.cloudd.benchmark

import ru.cloudd.benchmark.generator.MetricSet

trait DBClient {
  def persist(metricSet: MetricSet)
}

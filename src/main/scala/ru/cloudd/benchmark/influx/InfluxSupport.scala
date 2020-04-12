package ru.cloudd.benchmark.influx

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import sttp.client.{HttpURLConnectionBackend, _}
import sttp.model.StatusCode

trait InfluxSupport {

  protected lazy val logger: Logger = Logger(classOf[InfluxSupport])

  private val url = {
    val config = ConfigFactory.load().getConfig("influx")
    val url = config.getString("write.url")
    val precision = config.getString("write.precision")
    val db = config.getString("db")
    uri"$url?db=$db&precision=$precision"
  }

  private implicit val backend = HttpURLConnectionBackend()

  protected def writeToInflux(body: String): Unit = {
    logger.info("Writing data to InfluxDB...")
    val request = basicRequest
      .body(body)
      .contentType("application/json")
      .post(url)
    val response = request.send()
    if (response.code != StatusCode.NoContent) {
      throw new RuntimeException("Cannot write data to Influxdb. " +
        s"Status: ${response.code}. Response: ${response.body}")
    }
    logger.info("Written")
  }

}

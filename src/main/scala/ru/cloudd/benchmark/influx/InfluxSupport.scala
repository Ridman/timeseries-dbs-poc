package ru.cloudd.benchmark.influx

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.apache.http.client.entity.GzipCompressingEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.{ContentType, StringEntity}
import org.apache.http.impl.client.HttpClients
import ru.cloudd.benchmark.CanDump

import scala.util.{Failure, Try}

trait InfluxSupport extends CanDump {

  protected lazy val logger: Logger = Logger(classOf[InfluxSupport])

  private val url = {
    val config = ConfigFactory.load().getConfig("influx")
    val url = config.getString("write.url")
    val precision = config.getString("write.precision")
    val db = config.getString("db")
    s"$url?db=$db&precision=$precision"
  }

  private val httpClient = HttpClients.createDefault

  protected def writeToInflux(body: String): Unit = {
    Try {
      logger.info("Writing data to InfluxDB...")
      val request = new HttpPost(url)
      request.addHeader("Host", "localhost")

      val entity = new StringEntity(body, ContentType.APPLICATION_JSON)
//      val gzip = new GzipCompressingEntity(entity)
      request.setEntity(entity)

      val response = httpClient.execute(request)
      response.close()

      val code = response.getStatusLine.getStatusCode
      val msg = response.getStatusLine.getReasonPhrase
      if (code != 204) {
        throw new RuntimeException("Cannot write data to Influxdb. " +
          s"$code $msg")
      }
      logger.info("Written")
    } match {
      case Failure(ex) =>
        dump(body, "influx_error_request_dump.txt")
        throw ex
      case _ =>
    }
  }

}

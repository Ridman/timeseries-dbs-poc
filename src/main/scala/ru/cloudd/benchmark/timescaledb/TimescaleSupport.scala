package ru.cloudd.benchmark.timescaledb

import java.sql.DriverManager

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.Logger
import org.flywaydb.core.Flyway

trait TimescaleSupport {

  protected lazy val logger: Logger = Logger(classOf[TimescaleSupport])

  private val (connectionUrl, username, password) = {
    val config = ConfigFactory.load().getConfig("timescale")
    val host = config.getString("host")
    val port = config.getInt("port")
    val username = config.getString("username")
    val password = config.getString("password")
    val db = config.getString("db")

    val url = s"jdbc:postgresql://$host:$port/$db?createDatabaseIfNotExist=true"

    val flyway = Flyway
      .configure()
      .dataSource(url, username, password)
      .load()
    flyway.migrate()
    (url, username, password)
  }

  protected def writeToTimescaleDb(sql: String) = {
    logger.info("Inserting into TimescaleDB...")
    val connection = DriverManager.getConnection(connectionUrl, username, password)
    val statement = connection.createStatement()
    statement.execute(sql)
    statement.close()
    connection.close()
    logger.info("Inserted")
  }

}

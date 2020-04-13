package ru.cloudd.benchmark

trait CanDump {
  def dump(data: String, path: String): Unit = {
    import java.io._
    val pw = new PrintWriter(new File(path))
    pw.write(data)
    pw.close
  }
}

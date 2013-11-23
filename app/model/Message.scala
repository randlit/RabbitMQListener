package model

import play.api.libs.json._
import java.util.{Calendar, Date}
import controllers.rabbit.ElasticSearchRestIndexer
import play.libs.WS
import controllers.config.Config
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit


case class Response(msg_process: Long, time_running: String, avg_msg_process: Double, totalDocs: Long)

case class Item(description: String, checkedOut: Boolean)

case class Message(id: String, event: String, body: Option[Item])

trait ToJson {
  implicit val itemReads = Json.reads[Item]
  implicit val msgReads = Json.reads[Message]
}

object DataResponse {
  implicit val responseWrites = Json.writes[Response]
  var msg_process: Long = 0
  val init_time: Long = System.currentTimeMillis()
  var time_running: Long = 0

  def setExecutionTime() = {
    time_running = init_time-System.currentTimeMillis()
  }

  var avg_msg_process: Double = 0D
  val client = ElasticSearchRestIndexer.client

  def totalDocs: Long = {
    val c = WS.url(Config.ELASTICSEARCH_HOST + "/randl/item/_count").get().get(1000).asJson()
    c.get("count").asLong()
  }

  def toJson: JsValue = {
    val formatter = new SimpleDateFormat("HH:mm:ss:SSS");
    val c = Calendar.getInstance()
    c.setTimeInMillis(time_running)
    val dateFormatted = formatter.format(c.getTime);
    val minTimeUnit.MILLISECONDS.toMinutes(millis)
    val TimeUnit.MILLISECONDS.toHours(millis)
    val res = Response(msg_process, dateFormatted, avg_msg_process, totalDocs)

    Json.toJson(res)
  }
}
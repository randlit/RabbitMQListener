package controllers.actors

import controllers.rabbit.{ElasticSearchRestIndexer, RabbitMQConnection}
import controllers.config.Config
import play.libs.{Akka}
import akka.actor.{Actor, Props}
import scala.concurrent.duration._
import com.rabbitmq.client.{QueueingConsumer, Channel}
import scala.concurrent.ExecutionContext
import ExecutionContext.Implicits.global
import play.api.Logger
import io.searchbox.core.{Index, Delete, Get}
import play.api.libs.json.JsValue
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes._
import anorm.{NotAssigned, Id, Pk}
import model.{ToJson, Item, Message}
import com.github.tototoshi.play2.json4s.native.Json4s

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 11/20/13
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */
object Sender {

  def startSending = {
    // create the connection

    val connection = RabbitMQConnection.getConnection();
    // create the channel we use to send
    val sendingChannel = connection.createChannel();
    // make sure the queue exists we want to send to
    sendingChannel.queueDeclare(Config.RABBITMQ_QUEUE, false, false, false, null);

    Akka.system.scheduler.schedule(2 seconds, 1 seconds
      , Akka.system.actorOf(Props(
        new SendingActor(channel = sendingChannel,
          queue = Config.RABBITMQ_QUEUE)))
      , "MSG to Queue");
  }

  def startListener = {
    Logger.info("++1+++++HOST+++++++++++" + Config.RABBITMQ_HOST);
    val connection = RabbitMQConnection.getConnection();
    // create the channel we use to send
    val sendingChannel = connection.createChannel();
    // make sure the queue exists we want to send to

    Logger.info(connection.toString)
    Akka.system.actorOf(Props(
      new ListeningActor(channel = sendingChannel,
        queue = Config.RABBITMQ_QUEUE))) ! ""

  }
}

class SendingActor(channel: Channel, queue: String) extends Actor {

  def receive = {
    case some: String => {
      val msg = (some + " : " + System.currentTimeMillis());
      channel.basicPublish("", queue, null, msg.getBytes());
      Logger.info(msg);
    }
    case _ => {}
  }
}



class ListeningActor(channel: Channel, queue: String) extends Actor  with ToJson{

  // called on the initial run
  def receive = {
    case _ => startReceving
  }

  def startReceving = {

    val consumer = new QueueingConsumer(channel);
    channel.basicConsume("randl", true, consumer);
    println("iteration")
    val client = ElasticSearchRestIndexer.client
    while (true) {
      // wait for the message
      val delivery = consumer.nextDelivery();
      val msg = new String(delivery.getBody());

      indexer(msg)
      // send the message to the provided callback function
      // and execute this in a subactor
      def indexer(entry: String) = {
        val json: JsValue = Json.parse(entry);
        val message = Json.fromJson[Message](json).get
//        val message = Json.parse[Message](entry)
        val body = message.event match {
          case "update" => message.body
          case "checkout" => {
            val search = new Get.Builder("randl", message.id).`type`("item").build();
            val result = client.execute(search)
            val item: Item = result.getSourceAsObject(classOf[Item])

            item.copy(checkedOut = true)
          }
          case "checkin" => {
            val search = new Get.Builder("randl", message.id).`type`("item").build();
            val result = client.execute(search)
            val item: Item = result.getSourceAsObject(classOf[Item])

            item.copy(checkedOut = false)
          }
          case "remove" => {
            val search = new Delete.Builder("randl", message.id, "item").build();
            val result = client.execute(search)
            val item: Item = result.getSourceAsObject(classOf[Item])

            item.copy(checkedOut = true)
          }
          case _ => null
        }
        println(" [x] Message '" + Json.stringify(json) + "'");
        val index = new Index.Builder(body).id(message.id).index("randl").`type`("item").build()
        val writeRequest = client.execute(index)
        println("request -> ", writeRequest.getErrorMessage)
      }
    }
  }
}
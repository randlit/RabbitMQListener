package controllers.config

import com.typesafe.config.ConfigFactory

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 11/20/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
object Config {
  val RABBITMQ_HOST = ConfigFactory.load().getString("rabbitmq.host");
  val RABBITMQ_QUEUE = ConfigFactory.load().getString("rabbitmq.queue");
  val RABBITMQ_EXCHANGEE = ConfigFactory.load().getString("rabbitmq.exchange");
  val ELASTICSEARCH_HOST = ConfigFactory.load().getString("elasticsearch.host");
}

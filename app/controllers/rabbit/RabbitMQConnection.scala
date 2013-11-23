package controllers.rabbit

import com.rabbitmq.client.{ConnectionFactory, Connection}
import controllers.config.Config
import play.api.Logger
import io.searchbox.client.config.ClientConfig
import io.searchbox.client.{JestClient, JestClientFactory}

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 11/20/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
object RabbitMQConnection {

  private var connection: Option[Connection] = None;

  /**
   * Return a connection if one doesn't exist. Else create
   * a new one
   */
  def getConnection(): Connection = {
    connection match {
      case None => {
        val factory = new ConnectionFactory();
        factory.setUri(Config.RABBITMQ_HOST);
        Logger.info("+++++++FACTORY+++++++++++++");
        val con = factory.newConnection();
        Logger.info("+++++++FACTORY+++++++++++++");
        con
      }
      case _ => connection.get
    }
  }
}

object ElasticSearchRestIndexer {

  private var connection: Option[JestClient] = None;

  def client: JestClient = {
    connection match {
      case None => {
        val clientConfig: ClientConfig =
          new ClientConfig
          .Builder(Config.ELASTICSEARCH_HOST)
            .multiThreaded(true)
            .build()
        val factoryES: JestClientFactory = new JestClientFactory();
        factoryES.setClientConfig(clientConfig)
        factoryES.getObject()

      }
      case _ => connection.get
    }
  }
}
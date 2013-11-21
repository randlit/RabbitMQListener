package model

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 11/21/13
 * Time: 7:41 PM
 * To change this template use File | Settings | File Templates.
 */



case class Item(description: String, checkedOut: Boolean)

case class Message(id: String, event: String, body: Option[Item])

trait ToJson {
  implicit val itemReads = Json.reads[Item]
  implicit val msgReads = Json.reads[Message]
}
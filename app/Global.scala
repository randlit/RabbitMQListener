import controllers.actors.Sender
import play.api.{Logger, Application, GlobalSettings}

/**
 * Created with IntelliJ IDEA.
 * User: cgonzalez
 * Date: 11/20/13
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Sender.startListener
  }
}

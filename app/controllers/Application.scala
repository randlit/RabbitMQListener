package controllers

import play.api._
import play.api.mvc._
import model.DataResponse

object Application extends Controller {
  
  def index = Action {
    Ok(DataResponse.toJson)
//    Ok(views.html.index("Your new application is ready."))
  }
  
}
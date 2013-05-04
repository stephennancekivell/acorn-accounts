package auth

import play.api._
import play.api.mvc._
import play.api.libs.json._

class UserCtrl extends Controller {
  
  def all = Action { implicit request =>
    Ok(Json.toJson(SUser.getAll)
  }

}
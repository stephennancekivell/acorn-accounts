package info.stephenn.passwordsafe.password

import play.api._
import play.api.mvc._
import play.api.libs.json._
import info.stephenn.passwordsafe.auth._

object PermissionCtrl extends Controller {
  
  def list = Action { implicit request =>
    Ok(Json.toJson(Permission.list))
  }
}
package password

import play.api._
import play.api.mvc._

import play.api.libs.json._

import anorm._

object PasswordCtrl extends Controller {
  
  def list = Action { implicit request =>
    Ok(Json.toJson(Password.list))
  }
  
  def create = Action(parse.json) { request =>
    val x= Json.fromJson[Password](request.body)(Password.passwordReads)
    x.asOpt.map{ p =>  
      Ok(Password.insert(p).toString)
    }.getOrElse{
      BadRequest("Missing parameter [password]")
    }
  }
}
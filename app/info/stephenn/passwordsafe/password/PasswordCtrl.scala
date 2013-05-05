package info.stephenn.passwordsafe.password

import play.api._
import play.api.mvc._
import play.api.libs.json._

object PasswordCtrl extends Controller {
  
  def list = Action { implicit request =>
    Ok(Json.toJson(Password.list))
  }
  
  def get(id: String) = Action { implicit request =>
    val p = Password.getOne(id.toLong)
    Ok(Json.toJson(p))
  }
  
  def delete(id: String) = Action { implicit request =>
    Password.getOne(id.toLong).delete
    Accepted
  }
  
  def update(id: String) = Action(parse.json) { implicit request =>
    val in = Json.fromJson[Password](request.body)
    in.asOpt.map { in =>
      Password.update(in)
      Accepted
    }.getOrElse {
      BadRequest("couldnt parse request")
    }
  }
  
  def create = Action(parse.json) { implicit request =>
    val in= Json.fromJson[Password](request.body)
    in.asOpt.map{ p =>
      val pp = Password.create(p)
      Ok(Json.toJson(pp))
    }.getOrElse{
      BadRequest("Missing parameter [password]")
    }
  }
}
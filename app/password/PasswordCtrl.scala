package password

import play.api._
import play.api.mvc._
import play.api.libs.json._
import anorm._

object PasswordCtrl extends Controller {
  
  def list = Action { implicit request =>
    Ok(Json.toJson(Password.list))
  }
  
  def get(id: String) = Action { implicit request =>
    Password.getOne(id) match {
      case Some(p) => Ok(Json.toJson(p))
      case _ => BadRequest
    }
  }
  
  def update(id: String) = Action(parse.json) { implicit request =>
    val in = Json.fromJson[Password](request.body)
    in.asOpt.map { in =>
      Password.update(in) match {
        case Some(p) => Accepted
        case None => NotFound("")
      }
    }.getOrElse {
      BadRequest("couldnt parse request")
    }
  }
  
  def create = Action(parse.json) { implicit request =>
    val in= Json.fromJson[Password](request.body)
    in.asOpt.map{ p =>
      Password.create(p) match {
        case Some(pp) => Ok(Json.toJson(pp))
        case None => BadRequest("Error inserting")
      }
    }.getOrElse{
      BadRequest("Missing parameter [password]")
    }
  }
}
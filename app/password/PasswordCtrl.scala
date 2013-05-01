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
  
  def post = Action(parse.json) { implicit request =>
    val x= Json.fromJson[Password](request.body)
    x.asOpt match {
      case Some(p) =>
        p.id.get match {
          case -1 => {
            Ok(Json.toJson(Password.create(p)))
          }
          case _ => {
            // TODO: should update
            BadRequest
          }
        }
      case None => {
        BadRequest
      }
    }
  }
  
  def create = Action(parse.json) { request =>
    val x= Json.fromJson[Password](request.body)
    x.asOpt.map{ p =>
      Password.create(p) match {
        case Some(pp) => Ok(Json.toJson(pp))
        case None => BadRequest("Error inserting")
      }
    }.getOrElse{
      BadRequest("Missing parameter [password]")
    }
  }
}
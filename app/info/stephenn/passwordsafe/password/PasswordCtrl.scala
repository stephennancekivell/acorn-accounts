package info.stephenn.passwordsafe.password

import play.api._
import play.api.mvc._
import play.api.libs.json._
import info.stephenn.passwordsafe.auth._

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
    in.asOpt.map{ passwordIn =>
      val password = Password.create(passwordIn)
      
      getIndividual match {
        case None => NotFound("couldnt find user")
        case Some(individual) => {
          Permission.create(Permission(password, individual, true, true))
        }
      }
      
      Ok(Json.toJson(password))
    }.getOrElse {
      BadRequest("Missing parameter [password]")
    }
  }
  
  def addPermission(passwordID: Long) = Action(parse.json) { implicit request =>
    Logger.error("adding Permission")
    val password = Password.getOne(passwordID)
    val in= Json.fromJson[Permission](request.body)
    Logger.info("got in"+in)
    in.asOpt match {
      case None => BadRequest("permission expected.")
      case Some(permission) => {
        getIndividual(request) match {
          case None => BadRequest("couldnt find individual")
          case Some(individual) => {
            password.canRead(individual) match {
              case false => Forbidden
              case true => {
                Permission.create(permission)
                Accepted
              }
            }
          }
        }
      }
    }
  }
  
  def getIndividual(implicit request: Request[_]):Option[Party] = {
    request.headers.get("x-remote-user").headOption match {
      case None => {
        Logger.warn("could not find x-remote-user")
        None
      }
      case Some(username) => User.get(username).headOption match {
        case None => {
          Logger.warn("could not find user: "+username)
          None
        }
        case Some(user) => Party.getIndividual(user).headOption
      }
    }
  }
  
  def getUserName(implicit r: Request[Any]) = {
    r.headers.get("x-remote-user")
  }
}
package info.stephenn.passwordsafe.password

import play.api._
import play.api.mvc._
import play.api.libs.json._
import info.stephenn.passwordsafe.auth._

object PasswordCtrl extends Controller {
  
  def list = Action { implicit request =>
    Ok(Json.toJson(Password.list))
  }
  
  def get(id: Long) = Action { implicit request =>
    val p = Password.getOne(id)
    Ok(Json.toJson(p))
  }
  
  def delete(id: Long) = Action { implicit request =>
    Password.getOne(id).delete
    Accepted
  }
  
  def update(id: Long) = Action(parse.json) { implicit request =>
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
    Json.fromJson[Permission](request.body).asOpt match {
      case None => BadRequest("permission expected.")
      case Some(permission) => {
        val password = Password.getOne(permission.passwordID)
        getIndividual(request) match {
          case None => BadRequest("couldnt find individual")
          case Some(individual) => {
            password.canWrite(individual) match {
              case false => Forbidden
              case true => {
                password.getPartyPermissions.find(_.partyID == permission.partyID) match {
                  case None => Permission.create(permission)
                  case Some(existingPerm) => {
                    existingPerm.canRead = permission.canRead
                    existingPerm.canWrite = permission.canWrite
                    Permission.create(existingPerm)
                  }
                }
                Accepted
              }
            }
          }
        }
      }
    }
  }
  
  def removePermission(passwordID: Long, partyID:Long) = Action { implicit request =>  
    val password = Password.getOne(passwordID)
    getIndividual(request) match {
      case None => Forbidden
      case Some(individual) => {
        password.canWrite(individual) match {
          case false => Forbidden
          case true => {
            password.getPartyPermissions.find(_.partyID == partyID) match {
              case None => {
                Logger.error("could not find perm "+ password)
                NotFound
              }
              case Some(permission) => {
                Permission.remove(permission)
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
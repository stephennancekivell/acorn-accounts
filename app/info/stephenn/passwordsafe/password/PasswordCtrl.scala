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
      
      //create permission to user read and write
      getUserName match {
        case None => BadRequest("couldnt find username")
        case Some(userName) => {
          User.get(userName) match {
            case None => NotFound("couldnt find user")
            case Some(user) => {
              Party.getIndividual(user) match {
                case None => FailedDependency("couldnt find individual")
                case Some(party) => {
                  val perm = Permission.create(Permission(password, party, true, true))
                  Logger.warn("created perm "+perm.partyID.toString+ " "+perm.passwordID)
                }
              }
            } 
          }
        }
      }
      
      Ok(Json.toJson(password))
    }.getOrElse{
      BadRequest("Missing parameter [password]")
    }
  }
  
  def getUserName(implicit r: Request[Any]) = {
    r.headers.get("x-remote-user")
  }
}
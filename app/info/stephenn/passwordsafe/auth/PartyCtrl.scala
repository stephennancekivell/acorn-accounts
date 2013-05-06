package info.stephenn.passwordsafe.auth

import play.api.mvc._
import play.api.libs.json._

import play.api.data.Form
import play.api.data.Forms.{mapping, text, optional}

object PartyCtrl extends Controller {
  
  def list = Action { implicit request =>
    Ok(Json.toJson(Party.list))
  }
  
  def getGroups = Action { implicit request =>
    Ok(Json.toJson(Party.getGroups))
  }
  
  def get(id: Long) = Action { implicit request =>
    val p = Party.getOne(id)
    Ok(Json.toJson(p))
  }
  
  def delete(id: Long) = Action { implicit request =>
    Party.getOne(id).delete
    Accepted
  }
  
  def update(id: Long) = Action(parse.json) { implicit request =>
    val in = Json.fromJson[Party](request.body)
    in.asOpt.map { in =>
      Party.update(in)
      Accepted
    }.getOrElse {
      BadRequest("couldnt parse request")
    }
  }
  
  def updateUsers(partyID: Long) = Action(parse.json) { implicit request =>
    val in = Json.fromJson[List[User]](request.body)
    in.asOpt.map {jsonUsers =>
      val party = Party.getOne(partyID)
      
      //sync the lists
      val users = jsonUsers.map(u => User.getOne(u.id))
      party.setUsers(users.toSet[User])
            
      Accepted
    }.getOrElse {
      BadRequest("couldnt parse request")
    }
  }
  
  def create = Action(parse.json) { implicit request =>
    //TODO not all users should have the permission to do this.
    //Need a admin group
    val in= Json.fromJson[Party](request.body)
    in.asOpt.map{ p =>
      val pp = Party.create(p)
      Ok(Json.toJson(pp))
    }.getOrElse{
      BadRequest("Missing parameter")
    }
  }
}
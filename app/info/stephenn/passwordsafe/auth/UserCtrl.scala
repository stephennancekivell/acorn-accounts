package info.stephenn.passwordsafe.auth

import play.api.mvc._
import play.api.mvc.Results._
import play.api.libs.json._
import play.api.data.Form
import play.api.data.Forms.{mapping, text, optional}
import org.squeryl.PrimitiveTypeMode._
import info.stephenn.passwordsafe.AuthenticatedAction._

object UserCtrl extends Controller {
  
  def list = Authenticated { implicit request =>
    Ok(Json.toJson(User.list))
  }
  
  def get(id: Long) = Authenticated { implicit request =>
    val u = User.getOne(id)
    Ok(Json.toJson(u))
  }
  
  def delete(id: Long) = Authenticated { implicit request =>
    User.getOne(id).delete
    Accepted
  }
  
  def getMe = Authenticated { implicit request =>
    Ok(Json.toJson(request.user))
  }
  
  def update(id: Long) = Authenticated(parse.json) { implicit request =>
    val in = Json.fromJson[User](request.body)
    in.asOpt.map { in =>
      User.update(in)
      Accepted
    }.getOrElse {
      BadRequest("couldnt parse request")
    }
  }
  
  def create = Authenticated(parse.json) { implicit request =>
    //TODO not all users should have the permission to do this.
    //Need a admin group
    val in= Json.fromJson[User](request.body)
    in.asOpt.map{ u =>
      val uu = User.create(u)
      Ok(Json.toJson(uu))
    }.getOrElse{
      BadRequest("Missing parameter [User]")
    }
  }
}
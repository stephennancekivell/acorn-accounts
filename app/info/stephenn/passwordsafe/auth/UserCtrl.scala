package info.stephenn.passwordsafe.auth

import play.api.mvc._
import play.api.libs.json._

import play.api.data.Form
import play.api.data.Forms.{mapping, text, optional}

import org.squeryl.PrimitiveTypeMode._
//import models.{AppDB, Bar}


object UserCtrl extends Controller {
  
  def list = Action { implicit request =>
    Ok(Json.toJson(User.list))
  }
}
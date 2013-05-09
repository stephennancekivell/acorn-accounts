package info.stephenn.passwordsafe.password

import play.api._
import play.api.mvc._
import play.api.libs.json._
import info.stephenn.passwordsafe.auth._
import info.stephenn.passwordsafe.password.Permission.PermissionFormat

object AccountCtrl extends Controller {
  
  def list = Action { implicit request =>
    getUser match {
      case None => Forbidden
      case Some(user) => {
        Ok(Json.toJson(Permission.getUsersPasswords(user)))
      }
    }
  }
  
  def get(id: Long) = Action { implicit request =>
    val account = Account.getOne(id)
    Ok(Json.toJson(account))
  }
  
  def getPassword(id: Long) = Action { implicit request =>
    val account = Account.getOne(id)
    Ok(account.password)
  }
  
  def delete(id: Long) = Action { implicit request =>
    Account.getOne(id).delete
    Accepted
  }
  
  def update(id: Long) = Action(parse.json) { implicit request =>
    Json.fromJson[Account](request.body).asOpt match {
      case None => BadRequest("couldnt parse request")
      case Some(account) => {
        Account.update(account)
        Accepted
      }
    }
  }

  def create = Action(parse.json) { implicit request =>
    Json.fromJson[Account](request.body).asOpt match {
      case None => BadRequest("Missing parameter")
      case Some(inAccount) => {
        //TODO shouldnt create account until i know who individual is
        val accountCreated = Account.create(inAccount)

        getIndividual match {
          case None => NotFound("couldnt find user")
          case Some(individual) => {
            Permission.create(Permission(accountCreated, individual, true, true))
          }
        }

        Ok(Json.toJson(accountCreated))
      }
    }
  }
  
  def addPermission(passwordID: Long) = Action(parse.json) { implicit request =>
    Json.fromJson[Permission](request.body).asOpt match {
      case None => BadRequest("permission expected.")
      case Some(permission) => {
        val account = Account.getOne(permission.accountID)
        getIndividual(request) match {
          case None => BadRequest("couldnt find individual")
          case Some(individual) => {
            account.canWrite(individual) match {
              case false => Forbidden
              case true => {
                account.getPartyPermissions.find(_.partyID == permission.partyID) match {
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
    val password = Account.getOne(passwordID)
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
  
  def getUser(implicit request: Request[_]):Option[User] = {
    request.headers.get("x-remote-user").headOption match {
      case None => {
        Logger.warn("could not find x-remote-user")
        None
      }
      case Some(username) => User.get(username)
    }
  }
  
  def getIndividual(implicit request: Request[_]):Option[Party] = {
    getUser(request) match {
      case None => None
      case Some(user) => Party.getIndividual(user).headOption
    }
  }
  
  def getUserName(implicit r: Request[Any]) = {
    r.headers.get("x-remote-user")
  }
}
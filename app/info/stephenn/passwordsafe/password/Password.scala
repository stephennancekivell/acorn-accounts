package info.stephenn.passwordsafe.password

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.AppDB
import info.stephenn.passwordsafe.auth._

class Password(
    val id: Long = 0 ,
    var password: String,
    var title: String,
    var description: String) extends KeyedEntity[Long] {
  lazy val partyPermissions = AppDB.passwordPasswordPermission.left(this)
  
  def delete = inTransaction {
    AppDB.passwordTable.delete(id)
  }
  
  def getPartyPermissions = inTransaction {
    partyPermissions.toList
  }
  
  def canRead(party: Party) = inTransaction {
    this.partyPermissions.find(perm => perm.partyID == party.id & perm.canRead).isDefined
  }
  
  def canWrite(party: Party) = inTransaction {
    this.partyPermissions.find(perm => perm.partyID == party.id & perm.canWrite).isDefined
  }
}

object Password {
  
  def list = inTransaction {
	AppDB.passwordTable.iterator.toList
  }
  
  def getOne(id: Long) = inTransaction {
    AppDB.passwordTable.get[Long](id)
  }
  
  def update(pw: Password) = inTransaction {
    AppDB.passwordTable.update(pw)
  }
  
  def create(pw: Password) = inTransaction {
    AppDB.passwordTable.insert(pw)
  }
  
  implicit object PasswordFormat extends Format[Password] {
    def writes(p: Password): JsValue = inTransaction {
      Json.obj("id" -> p.id,
          "password" -> p.password,
          "title" -> p.title,
          "description" -> p.description,
          "permissions" -> p.partyPermissions.toList)
    }

    def reads(js: JsValue) = {
      val id = (js \ "id").as[Long]
      val pw = (js \ "password").as[String]
      
      JsSuccess(new Password(
          id = id,
          password = pw,
          title = (js \ "title").as[String],
          description = (js \ "description").as[String]))
    }
  }
}
package info.stephenn.passwordsafe.password

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.AppDB
import info.stephenn.passwordsafe.auth._

class Account(
    val id: Long = 0,
    var password: String = "",
    var name: String,
    var username: String,
    var description: String,
    var hidden: Boolean = false) extends KeyedEntity[Long] {
  lazy val partyPermissions = AppDB.accountsPermissions.left(Account.this)
  
  def delete = inTransaction {
    AppDB.accounts.delete(id)
  }
  
  def getPartyPermissions = inTransaction {
    partyPermissions.toList
  }
  
  def canRead(party: Party) = inTransaction {
    Account.this.partyPermissions.find(perm => perm.partyID == party.id & perm.canRead).isDefined
  }
  
  def canWrite(party: Party) = inTransaction {
    Account.this.partyPermissions.find(perm => perm.partyID == party.id & perm.canWrite).isDefined
  }
}

object Account {
  
  def list = inTransaction {
	AppDB.accounts.iterator.toList
  }
  
  def getOne(id: Long) = inTransaction {
    AppDB.accounts.get[Long](id)
  }
  
  def update(account: Account) = inTransaction {
    AppDB.accounts.update(account)
  }
  
  def create(account: Account) = inTransaction {
    AppDB.accounts.insert(account)
  }
  
  implicit object AccountFormat extends Format[Account] {
    def writes(account: Account): JsValue = inTransaction {
      Json.obj("id" -> account.id,
          "username" -> account.username,
          "name" -> account.name,
          "description" -> account.description,
          "permissions" -> account.partyPermissions.toList)
    }

    def reads(js: JsValue) = {
      JsSuccess(new Account(
          id = (js \ "id").as[Long],
          username = (js \ "username").as[String],
          name = (js \ "name").as[String],
          description = (js \ "description").as[String]))
    }
  }
}
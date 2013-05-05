package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._

import play.api.libs.json._
import play.api.libs.json.Json._

import info.stephenn.passwordsafe.AppDB

case class Party(var id: Long, var name:String) extends KeyedEntity[Long] {
    lazy val users = AppDB.userParty.left(this)
}

object Party {
  
  def getOne(id: Long) = inTransaction {
    AppDB.partyTable.get[Long](id)
  }
  
  def list = inTransaction {
    AppDB.partyTable.iterator.toList
  }
  
  def update(p: Party) = inTransaction {
    AppDB.partyTable.update(p)
  }
  
  def create(party: Party) = inTransaction {
    AppDB.partyTable.insert(party)
  }
  
  implicit object PartyFormat extends Format[Party] {
    def writes(u: Party): JsValue = Json.obj(
        "id" -> u.id,
        "name" -> u.name,
        "users" -> u.users)

    def reads(js: JsValue) = {
      val id = (js \ "id").as[Long]
      val n = (js \ "name").as[String]
      
      JsSuccess(Party(name = n, id = id))
    }
  }
  
}

case class UserParty(val userID: Long, val partyID: Long) extends KeyedEntity[CompositeKey2[Long, Long]] {
  def id = compositeKey(userID, partyID)
} 

object UserParty {
  def apply(user: User, party:Party) = {
    new UserParty(user.id, party.id)
  }
  
  def deletePartiesUsers(p:Party) = inTransaction {
    AppDB.userPartyTable.deleteWhere(up => (up.partyID === p.id))
  }
  
  def create(userParty: UserParty) = inTransaction {
    AppDB.userPartyTable.insert(userParty)
  }
}
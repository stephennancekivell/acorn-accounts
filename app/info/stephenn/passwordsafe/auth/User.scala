package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.AppDB

import org.squeryl.PrimitiveTypeMode._

import org.squeryl.dsl._

case class User(var name: String, val id: Long) extends KeyedEntity[Long] {
  lazy val parties = AppDB.userParty.right(this)
  
  def delete = inTransaction {
    AppDB.users.delete(id)
  }
}

object User {
  import info.stephenn.passwordsafe.AppDB._
  
  def list = inTransaction {
    AppDB.users.iterator.toList
  }
  
  def getOne(id: Long) = inTransaction {
    AppDB.users.get[Long](id)
  }
  
  def get(name: String) = inTransaction {
    from(AppDB.users)(u => where(u.name === name) select(u)).headOption
  }
  
  def update(user: User) = inTransaction {
    val u = AppDB.users.update(user)
    var party = Party.getIndividual(user).get
    party.name = user.name
    Party.update(party)
    
    u
  }
  
  def create(user: User) = inTransaction {
    val u = AppDB.users.insert(user)
    var party = Party.create(Party(id = -1, name=user.name, isIndividual=true))
    party.setUsers(Set(user))
    
    u
  }
  
  implicit object UserFormat extends Format[User] {
    def writes(u: User): JsValue = Json.obj("id" -> u.id, "name" -> u.name)

    def reads(js: JsValue) = {
      val id = (js \ "id").as[Long]
      val n = (js \ "name").as[String]
      
      JsSuccess(User(name = n, id = id))
    }
  }
}
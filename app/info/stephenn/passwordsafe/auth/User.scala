package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.PrimitiveTypeMode.inTransaction
import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.AppDB

import org.squeryl.PrimitiveTypeMode._

import org.squeryl.dsl._

case class User(var name: String, var id: Long) extends KeyedEntity[Long] {
  lazy val parties = AppDB.userParty.right(this)
  
  def delete = inTransaction {
    AppDB.userTable.delete(id)
  }
}

object User {
  import info.stephenn.passwordsafe.AppDB._
  
  def list = inTransaction {
    AppDB.userTable.iterator.toList
  }
  
  def getOne(id: Long) = inTransaction {
    AppDB.userTable.get[Long](id)
  }
  
  def update(user: User) = inTransaction {
    AppDB.userTable.update(user)
  }
  
  def create(user: User) = inTransaction {
    AppDB.userTable.insert(user)
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
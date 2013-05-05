package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.PrimitiveTypeMode.inTransaction
import play.api.libs.json._
import play.api.libs.json.Json.toJsFieldJsValueWrapper
import info.stephenn.passwordsafe.AppDB

import org.squeryl.PrimitiveTypeMode._

import org.squeryl.dsl._


case class User(name: Option[String]) extends KeyedEntity[Long] {
  val id: Long = 0
}

object User {
  import info.stephenn.passwordsafe.AppDB._
  
  def list = inTransaction {
      from(AppDB.userTable)(u =>
        	select(u)
        ).seq
  }
  
  
  
  implicit object UserFormat extends Format[User] {
    def writes(u: User): JsValue = Json.obj("id" -> u.id, "name" -> u.name)

    def reads(js: JsValue) = {
      val id = (js \ "id").as[Long]
      val n = (js \ "name").as[String]
      val u = User(Option(n))
      
      JsSuccess(User(name = Option(n)))
    }
  }
}
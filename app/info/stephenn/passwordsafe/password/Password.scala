package info.stephenn.passwordsafe.password

import org.squeryl._
import org.squeryl.PrimitiveTypeMode.inTransaction
import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.AppDB

import org.squeryl.PrimitiveTypeMode._

import org.squeryl.dsl._

case class Password(password: String, title: String, description: String) extends KeyedEntity[Long] {
  val id: Long = 0
}

object Password {
  
  def list = inTransaction {
	  from(AppDB.userTable)(u =>
	    	select(u)
	    ).seq
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
    def writes(p: Password): JsValue = Json.obj("id" -> p.id, "password" -> p.password, "title" -> p.title, "description" -> p.description)

    def reads(js: JsValue) = {
      val id = (js \ "id").as[Long]
      val pw = (js \ "password").as[String]
      
      JsSuccess(Password(
          password = pw,
          title = (js \ "title").as[String],
          description = (js \ "description").as[String]))
    }
  }
}
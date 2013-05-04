package auth

import org.squeryl.KeyedEntity
import play.api.libs.json._

import org.squeryl.PrimitiveTypeMode.inTransaction

case class User(name: Option[String]) extends KeyedEntity[Long] {
  val id: Long = 0
}

object User {
  
  def list = {
    inTransaction {
      AppDB.userTable
    }
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
package password

import play.api._
import play.api.db._
import play.api.Play.current

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.util._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._

import anorm._
import anorm.SqlParser._

case class Password(id: Pk[Long] = NotAssigned, password: String)

object Password {

  def list = {
    DB.withConnection { implicit connection =>
      SQL(
        """
          select * from password
        """)
        .as(Password.parser.*)
    }
  }
  
  def getOne(id: String) = {
    DB.withConnection { implicit connection =>
      SQL(
          """
          select * from password
          where id = {id}
          """
      ).on("id" -> id)
      .as(Password.parser.singleOpt)
    }
  }

  def insert(password: Password) = {
    DB.withConnection { implicit connection =>
      SQL(
        """
            insert into Password (password) values  (
    		  {password}
            )
        """).on('password -> password.password)
        .executeUpdate()
    }
  }

  val parser = {
    get[Pk[Long]]("password.id") ~
      get[String]("password.password") map {
        case id ~ password => Password(id, password)
      }
  }

  implicit object PasswordFormat extends Format[Password] {
    def writes(p: Password): JsValue = Json.obj("id" -> p.id.get, "password" -> p.password)

    def reads(js: JsValue) = {
      val id = (js \ "id").as[Long]
      val pw = (js \ "password").as[String]
      JsSuccess(Password(Id(id), pw))
    }
  }
}
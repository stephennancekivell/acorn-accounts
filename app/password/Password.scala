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

case class Password(id: Pk[Long] = NotAssigned, password: String, title: String, description: String)

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
  
  def update(password: Password) = {
    val count = DB.withConnection { implicit connection =>
      SQL(
          """
          update password
          set password = {pw},
    		  title = {title},
    		  description = {description}
          where id = {id}
          """
          ).on('id -> password.id,
              'pw -> password.password,
              'title -> password.title,
              'description -> password.description
              ).executeUpdate()
    }
    count match {
      case 0 => None
      case 1 => Some(password)
      case x:Int => throw new Exception("updated more passwords than expected: "+x)
    }
  }

  def create(password: Password) = {
    val newId = DB.withConnection { implicit connection =>
      SQL(
        """
            insert into password (password, title, description) values  (
    		  {password}, {title}, {description}
            )
        """).on('password -> password.password,
        		'title -> password.title,
        		'description -> password.description
            ).executeInsert()
    }
    newId match{
      case Some(p) => Some(password.copy(id=Id(p)))
      case None => None
    }
  }

  val parser = {
    get[Pk[Long]]("password.id") ~
      get[String]("password.password") ~
      get[String]("password.title") ~
      get[String]("password.description") map {
        case id ~ password ~ title ~ description => Password(id = id, password = password, title = title, description = description)
      }
  }

  implicit object PasswordFormat extends Format[Password] {
    def writes(p: Password): JsValue = Json.obj("id" -> p.id.get, "password" -> p.password, "title" -> p.title, "description" -> p.description)

    def reads(js: JsValue) = {
      val id = (js \ "id").as[Long]
      val pw = (js \ "password").as[String]
      JsSuccess(Password(id = Id(id),
          password = pw,
          title = (js \ "title").as[String],
          description = (js \ "description").as[String]))
    }
  }
}
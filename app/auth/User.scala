package auth

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class User(id: Pk[Long] = NotAssigned, name: String)

object User {
  
  def getAll = DB.withConnection { implicit connection =>
    SQL("""
        select * from user
        """).as(parser.*)
  }
  
  def create(name: String) = DB.withConnection { implicit connection =>
    
  }

  def getOne(name: String) = DB.withConnection { implicit connection =>
    SQL("""
        select * from user
        where name = {name}
        """).on('name -> name)
      .as(User.parser.singleOpt)
  }

  val parser = {
    get[Pk[Long]]("user.id") ~
      get[String]("user.name") map {
        case id ~ name => User(id, name)
      }
  }
}
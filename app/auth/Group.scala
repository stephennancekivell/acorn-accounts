package auth

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Group(id: Pk[Long] = NotAssigned, name: String)

object Group {
  
  def getAll = DB.withConnection { implicit connection =>
    SQL("""
        select * from group
        """).as(parser.*)
  }
  
  def getOne(id: Id[Long]) = DB.withConnection { implicit connection =>
    SQL("""
        select * from group
        where id = {id}
        """).on('id -> id).as(parser.singleOpt)
  }
  
  
  val parser = {
    get[Pk[Long]]("group.id") ~
      get[String]("group.name") map {
        case id ~ name => Group(id, name)
      }
  }
}
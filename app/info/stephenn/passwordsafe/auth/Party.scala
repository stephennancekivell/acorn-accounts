package info.stephenn.passwordsafe.auth

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Party(id: Pk[Long] = NotAssigned)

object Party {
  
  def getAll = DB.withConnection { implicit connection =>
    SQL("""
        select * from party
        """).as(parser.*)
  }
  
  def getOne(id: Id[Long]) = DB.withConnection { implicit connection =>
    SQL("""
        select * from party
        where id = {id}
        """).on('id -> id).as(parser.singleOpt)
  }
  
  val parser = {
    get[Pk[Long]]("party.id")  map {
        case id => Party(id)
      }
  }
}
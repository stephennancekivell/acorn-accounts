package password

import play.api.db._
import play.api.Play.current

import anorm._
import anorm.SqlParser._

case class Permission(id: Pk[Long] = NotAssigned, read: Boolean, write: Boolean, partyId: Long)

object Permission {
  
  def getOne(id: Id[Long]) = DB.withConnection { implicit connection =>
    SQL("""
        select * from permission
        where id = {id}
        """).on('id -> id).as(parser.singleOpt)
  }
  
  val parser = {
    get[Pk[Long]]("permission.id") ~
     get[Boolean]("permission.read") ~
     get[Boolean]("permission.read") ~
     get[Long]("permission.partyId") map {
        case id ~ read ~ write ~ partyId => Permission(id, read, write, partyId)
      }
  }
}
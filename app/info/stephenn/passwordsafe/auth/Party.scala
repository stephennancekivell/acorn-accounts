package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.AppDB

import org.squeryl.dsl._

case class Party(var id: Long, var name:String) extends KeyedEntity[Long] {
    lazy val users = AppDB.userParty.left(this)
}

case class UserParty(val userID: Long, val partyID: Long) extends KeyedEntity[CompositeKey2[Long, Long]] {
  def id = compositeKey(userID, partyID)
} 

object UserParty {
  def apply(u: User, p:Party) = {
    new UserParty(u.id, p.id)
  }
}

object Party {
  
  def list = inTransaction {
    AppDB.partyTable.iterator.toList
  }
  
  
  
//  def getUsers(user: User) = inTransaction {
//    from(AppDB.partyTable) (p => where(p.userId === user.id) select(p)).toList
//  }
    
  def update(p: Party) = inTransaction {
    AppDB.partyTable.update(p)
  }
  
  def create(party: Party) = inTransaction {
    AppDB.partyTable.insert(party)
  }
  
}
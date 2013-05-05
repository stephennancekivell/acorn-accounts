package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.AppDB
import play.Logger

case class Party(var id: Long, var name: String, isIndividual: Boolean) extends KeyedEntity[Long] {
  lazy val users = AppDB.userParty.left(this)
  lazy val passwordPermissions = AppDB.partyPasswordPermission.left(this)

  def setUsers(usersToSet: Set[User]) = inTransaction {
    if (isIndividual && usersToSet.size > 1)
      throw new UnsupportedOperationException("A party that isIndividual can only have one user.")
    
    val currentUsers = users.toSet[User]
    
    val newUsers = usersToSet.filter(!currentUsers.contains(_))
    val removedUsers = currentUsers.filter(!usersToSet.contains(_))
        
    removedUsers.foreach(u => UserParty.remove(UserParty(u, this)))
    newUsers.foreach(u => UserParty.create(UserParty(u, this)))
  }
  
  def delete = inTransaction {
    //TODO delete the userParties
    AppDB.partyTable.delete(id)
  }
}

object Party {
  def apply(id: Long, name:String) = {
    new Party(id, name, false)
  }

  def getOne(id: Long) = inTransaction {
    AppDB.partyTable.get[Long](id)
  }
  
  def list = inTransaction {
    AppDB.partyTable.iterator.toList
  }
  
  def groups = inTransaction {
    from(AppDB.partyTable)(p =>
	      where(p.isIndividual === false)
	      select(p)
      )
  }
  
  def getIndividual(user: User) = inTransaction {
    from(AppDB.partyTable, AppDB.userPartyTable)((p, up) =>
	        where(up.userID === user.id and up.partyID === p.id and p.isIndividual === true)
	        select(p)
        ).headOption
  }

  def update(p: Party) = inTransaction {
    AppDB.partyTable.update(p)
  }

  def create(party: Party) = inTransaction {
    AppDB.partyTable.insert(party)
  }

  implicit object PartyFormat extends Format[Party] {
    def writes(u: Party): JsValue = inTransaction { Json.obj(
      "id" -> u.id,
      "name" -> u.name,
      "users" -> u.users)
    }

    def reads(js: JsValue) = {
      val id = (js \ "id").as[Long]
      val n = (js \ "name").as[String]

      JsSuccess(Party(name = n, id = id))
    }
  }
}

case class UserParty(val userID: Long, val partyID: Long) extends KeyedEntity[CompositeKey2[Long, Long]] {
  def id = compositeKey(userID, partyID)
}

object UserParty {
  def apply(user: User, party: Party) = {
    new UserParty(user.id, party.id)
  }

  def remove(userParty: UserParty) = inTransaction {
    AppDB.userPartyTable.deleteWhere(up => (up.partyID === userParty.partyID) and (up.userID === userParty.userID))
  }

  def create(userParty: UserParty) = inTransaction {
    AppDB.userPartyTable.insert(userParty)
  }
}
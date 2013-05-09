package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.AppDB
import play.Logger

case class Party(val id: Long, var name: String, val isIndividual: Boolean) extends KeyedEntity[Long] {
  lazy val users = AppDB.userParty.left(this)
  lazy val passwordPermissions = AppDB.partiesPermissions.left(this)
  
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
    AppDB.parties.delete(id)
  }  
  
}

object Party {
  def apply(id: Long, name:String) = {
    new Party(id, name, false)
  }

  def getOne(id: Long) = inTransaction {
    AppDB.parties.get[Long](id)
  }
  
  def list = inTransaction {
    AppDB.parties.iterator.toList
  }
  
  def getGroups = inTransaction {
    from(AppDB.parties)(p =>
	      where(p.isIndividual === false)
	      select(p)
      ).toList
  }
  
  def getIndividual(user: User) = inTransaction {
    from(AppDB.parties, AppDB.usersParties)((p, up) =>
	        where(up.userID === user.id and up.partyID === p.id and p.isIndividual === true)
	        select(p)
        ).headOption
  }

  def update(p: Party) = inTransaction {
    AppDB.parties.update(p)
  }

  def create(party: Party) = inTransaction {
    AppDB.parties.insert(party)
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
    AppDB.usersParties.deleteWhere(up => (up.partyID === userParty.partyID) and (up.userID === userParty.userID))
  }

  def create(userParty: UserParty) = inTransaction {
    AppDB.usersParties.insert(userParty)
  }
}
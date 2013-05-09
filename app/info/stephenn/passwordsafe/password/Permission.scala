package info.stephenn.passwordsafe.password

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import info.stephenn.passwordsafe.AppDB
import info.stephenn.passwordsafe.auth._

import play.api.libs.json._
import play.api.libs.json.Json._

case class Permission(
		val partyID: Long,
		val accountID: Long,
		var canRead: Boolean,
		var canWrite: Boolean) extends KeyedEntity[CompositeKey2[Long, Long]] {
  def id = compositeKey(accountID, partyID)
}

object Permission {
  def apply(account: Account, party: Party, canRead: Boolean, canWrite: Boolean) = {
    new Permission(partyID = party.id, accountID = account.id, canRead = canRead, canWrite = canWrite)
  }
  
  def list = inTransaction {
	AppDB.passwordPermissionTable.iterator.toList
  }
  
  def remove(permission: Permission) = inTransaction {
    AppDB.passwordPermissionTable.deleteWhere(pp => (pp.partyID === permission.partyID) and (pp.accountID === permission.accountID))
  }

  def create(permission: Permission) = inTransaction {
    AppDB.passwordPermissionTable.insertOrUpdate(permission)
  }
  
  def getUsersPasswords(user: User) = inTransaction {
    // this should probably have a sub select instead of being a 3 table join
    from(AppDB.accountTable, AppDB.passwordPermissionTable, AppDB.userParty)((account, perm, userParty) =>
      where(userParty.userID === user.id and userParty.partyID === perm.partyID and perm.accountID === account.id)
      select(account)
      ).toList
  }
  
  implicit object PermissionFormat extends Format[Permission] {
    def writes(p: Permission): JsValue = inTransaction {
      Json.obj("party" -> Party.getOne(p.partyID),
          "partyID" -> p.partyID,
          "accountID" -> p.accountID,
          "canRead" -> p.canRead,
          "canWrite" -> p.canWrite)
    }

    def reads(js: JsValue) = {
      JsSuccess(new Permission(
          partyID = (js \ "partyID").as[Long],
          accountID = (js \ "accountID").as[Long],
          canRead = (js \ "canRead").as[Boolean],
          canWrite = (js \ "canWrite").as[Boolean]))
    }
  }
}
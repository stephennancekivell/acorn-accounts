package info.stephenn.passwordsafe.password

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import info.stephenn.passwordsafe.AppDB
import info.stephenn.passwordsafe.auth._

case class Permission(
		val partyID: Long,
		val passwordID: Long,
		val canRead: Boolean,
		val canWrite: Boolean) extends KeyedEntity[CompositeKey2[Long, Long]] {
  def id = compositeKey(passwordID, partyID)
}

object Permission {
  def apply(password: Password, party: Party, canRead: Boolean, canWrite: Boolean) = {
    new Permission(password.id, party.id, canRead, canWrite)
  }
  
  def remove(permission: Permission) = inTransaction {
    AppDB.passwordPermissionTable.deleteWhere(pp => (pp.partyID === permission.partyID) and (pp.passwordID === permission.passwordID))
  }

  def create(permission: Permission) = inTransaction {
    AppDB.passwordPermissionTable.insert(permission)
  }
}
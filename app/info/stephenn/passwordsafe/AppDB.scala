package info.stephenn.passwordsafe

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._

import info.stephenn.passwordsafe.password._
import info.stephenn.passwordsafe.auth._

object AppDB extends Schema {
  val userTable = table[User]("user")
  val accountTable = table[Account]("account")
  val partyTable = table[Party]("party")
  val userPartyTable = table[UserParty]("userParty")
  val userParty = manyToManyRelation(partyTable, userTable).
  	via[UserParty]((p,u, up) =>
  	  (up.partyID === p.id, up.userID === u.id))
  
  val passwordPermissionTable = table[password.Permission]("passwordPermission")
  val partyPasswordPermission = oneToManyRelation(partyTable, passwordPermissionTable)
  	.via((party, perm) => party.id === perm.partyID)
  val passwordPasswordPermission = oneToManyRelation(accountTable, passwordPermissionTable)
  	.via((account, perm) => account.id === perm.accountID)
}

package info.stephenn.passwordsafe

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._

import info.stephenn.passwordsafe.password._
import info.stephenn.passwordsafe.auth._

object AppDB extends Schema {
  val users = table[User]("user")
  val parties = table[Party]("party")
  val usersParties = table[UserParty]("userParty")
  val userParty = manyToManyRelation(parties, users).
  	via[UserParty]((p,u, up) =>
  	  (up.partyID === p.id, up.userID === u.id))
  	  
  val accounts = table[Account]("account")
  
  val permissions = table[Permission]("permission")
  val partiesPermissions = oneToManyRelation(parties, permissions)
  	.via((party, perm) => party.id === perm.partyID)
  val accountsPermissions = oneToManyRelation(accounts, permissions)
  	.via((account, perm) => account.id === perm.accountID)
}

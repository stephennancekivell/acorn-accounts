package info.stephenn.passwordsafe

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._

import info.stephenn.passwordsafe.password._
import info.stephenn.passwordsafe.auth._

object AppDB extends Schema {
  val userTable = table[User]("user")
  val passwordTable = table[Password]("password")
  val partyTable = table[Party]("party")
  val userPartyTable = table[UserParty]("userParty")
  
  var userParty = manyToManyRelation(partyTable, userTable).
  	via[UserParty]((p,u, up) =>
  	  (up.partyID === p.id, up.userID === u.id))  
}

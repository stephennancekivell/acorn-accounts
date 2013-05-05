package info.stephenn.passwordsafe

import org.squeryl._
import org.squeryl.dsl._
import info.stephenn.passwordsafe.password._
import info.stephenn.passwordsafe.auth._

object AppDB extends Schema {
  val userTable = table[User]("user")
  val passwordTable = table[Password]("password")
  val partyTable = table[Party]("party")
}
package info.stephenn.passwordsafe

import org.squeryl.Schema
import info.stephenn.passwordsafe.password._
import info.stephenn.passwordsafe.auth._

object AppDB extends Schema {
  val userTable = table[User]("user")
  val passwordTable = table[Password]("password")
}
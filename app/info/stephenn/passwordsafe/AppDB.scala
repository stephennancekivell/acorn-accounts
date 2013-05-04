package info.stephenn.passwordsafe

import org.squeryl.Schema
import auth._

object AppDB extends Schema {
  val userTable = table[User]("user")
}
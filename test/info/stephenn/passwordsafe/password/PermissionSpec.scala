package info.stephenn.passwordsafe.password

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import play.api.test._
import play.api.test.Helpers._
import info.stephenn.passwordsafe.AppDB
import info.stephenn.passwordsafe.auth._

class PermissionSpec extends FlatSpec with ShouldMatchers {
  "Permission" should "have a party and a permission" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val pass = Password.create(new Password(0, "", "", ""))
        val party = Party.create(Party(0, "Blue team"))

        val perm = Permission.create(Permission(pass, party, true, true))

        perm.id.a1 should not equal (0)
        perm.id.a2 should not equal (0)
      }
    }
  }

  "A Password" should "have many parties through their permissions" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val pass = Password.create(new Password(0, "", "", ""))
        val blue = Party.create(Party(0, "Blue team"))
        val red = Party.create(Party(0, "Red team"))

        Permission.create(Permission(pass, blue, true, true))
        Permission.create(Permission(pass, red, true, true))

        pass.partyPermissions.toList.length should equal(2)
      }
    }
  }
}
package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import play.api.test._
import play.api.test.Helpers._
import info.stephenn.passwordsafe.AppDB

class PartySpec extends FlatSpec with ShouldMatchers {
  "A Party" should "be many users" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val u1 = User.create(new User("1", 0))
        val u2 = User.create(new User("2", 0))

        var p1 = Party.create(new Party(0, ""))
        p1.id should not equal (0)

        AppDB.userPartyTable.insert(Seq(UserParty(u1, p1), UserParty(u2, p1)))

        p1.users.toList.length should equal(2)
      }
    }
  }

  "A Party" should "be able to remove users" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val u1 = User.create(new User("1", 0))
        val u2 = User.create(new User("2", 0))

        var p1 = Party.create(new Party(0, ""))
        p1.id should not equal (0)

        AppDB.userPartyTable.insert(Seq(UserParty(u1, p1), UserParty(u2, p1)))

        AppDB.userPartyTable.deleteWhere(up => (up.userID === u1.id) and (up.partyID === p1.id))

        p1.users.toList.length should equal(1)
        p1.users.head.name should equal("2")
      }
    }
  }
}
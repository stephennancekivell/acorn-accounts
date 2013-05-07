package info.stephenn.passwordsafe.auth

import org.squeryl._
import org.squeryl.dsl._
import org.squeryl.PrimitiveTypeMode._
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import play.api.test._
import play.api.test.Helpers._
import info.stephenn.passwordsafe.AppDB
import play.api.libs.json._

class PartySpec extends FlatSpec with ShouldMatchers {

  def makePartyTwoUsers = {
    val u1 = User.create(new User("1", 0))
    val u2 = User.create(new User("2", 0))

    var p1 = Party.create(Party(0, "Blue Team"))
    p1.id should not equal (0)

    AppDB.userPartyTable.insert(Seq(UserParty(u1, p1), UserParty(u2, p1)))
    p1
  }

  "A Party" should "be many users" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        makePartyTwoUsers
        
        val p = Party.list.filter(_.name == "Blue Team").head
        p.users.toList.length should equal(2)
      }
    }
  }

  "A Party" should "be able to remove users" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val p = makePartyTwoUsers

        val user = User.list.head

        AppDB.userPartyTable.deleteWhere(up => (up.userID === user.id) and (up.partyID === p.id))

        p.users.toList.length should equal(1)
        p.users.head.name should equal("2")
      }
    }
  }

  "Party" should "set users" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val p = makePartyTwoUsers
        
        val u3 = User.create(new User("3", 0))
        val u4 = User.create(new User("4", 0))
        
        p.setUsers(Set(u3,u4))
        
        p.users.toList.length should equal(2)
        val names = p.users.map(_.name)
        names should contain("3")
        names should contain("4")
      }
    }
  }

  "Party" should "make json" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val p = makePartyTwoUsers

        Json.toJson(p).toString should equal("""{"id":3,"name":"Blue Team","users":[{"id":1,"name":"1"},{"id":2,"name":"2"}]}""")
      }
    }
  }

  "Json" should "make Party" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val party = Party.PartyFormat.reads(Json.parse("""{"id":1,"name":"n","users":[{"id":1,"name":"1"},{"id":2,"name":"2"}]}""")).get

        party.name should equal("n")
        party.users.toList.length should equal(0)
      }
    }
  }
}
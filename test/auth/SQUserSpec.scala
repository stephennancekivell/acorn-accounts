
import auth._

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.squeryl.PrimitiveTypeMode.inTransaction

import play.api.test._
import play.api.test.Helpers._

class SQUserSpec extends FlatSpec with ShouldMatchers {

  "A User" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val u = AppDB.userTable insert SQUser(Some("foo"))
        u.id should not equal(0)
      }
    }
  }

}
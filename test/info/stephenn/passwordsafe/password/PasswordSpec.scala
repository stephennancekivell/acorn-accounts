package info.stephenn.passwordsafe.password

import org.squeryl.PrimitiveTypeMode.inTransaction
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import play.api.test._
import play.api.test.Helpers._
import info.stephenn.passwordsafe.AppDB

class PasswordSpec extends FlatSpec with ShouldMatchers {
  "A Password" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val u= Password.create(new Password("", "", ""))
        //val u = AppDB.passwordTable insert new Password("", "", "")
        u.id should not equal(0)
      }
    }
  }
  
  "List Passwords" should "return all the passwords" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        def p = new Password("", "", "")
        val u = AppDB.passwordTable insert Seq(p, p, p)
        
        Password.list.toList.length should equal(3)
      }
    }
  }
  
  "getOne" should "return the one" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val u = AppDB.passwordTable insert new Password("Password10", "", "")
        
        val ret = Password.getOne(u.id)
        ret.password should equal("Password10")
      }
    }
  }
  
  "update" should "save new values" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val p = AppDB.passwordTable insert new Password("Password10", "", "")
        p.password = "Password11"
          
        Password.update(p)
        
        val ret = Password.getOne(p.id)
        ret.password should equal("Password11")
      }
    }
  }
  
  "update" should "save new values" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
      }
    }
  }
  
  "update" should "save new values" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
      }
    }
  }
}
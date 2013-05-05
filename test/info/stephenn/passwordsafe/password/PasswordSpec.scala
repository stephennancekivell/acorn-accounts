package info.stephenn.passwordsafe.password

import org.squeryl.PrimitiveTypeMode.inTransaction
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import play.api.test._
import play.api.test.Helpers._
import info.stephenn.passwordsafe.AppDB
import play.api.libs.json._

class PasswordSpec extends FlatSpec with ShouldMatchers {
  "A Password" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val p = Password.create(new Password(0, "", "", ""))
        p.id should not equal(0)
      }
    }
  }
  
  "List Passwords" should "return all the passwords" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        def p = new Password(0, "", "", "")
        val u = AppDB.passwordTable insert Seq(p, p, p)
        
        Password.list.length should equal(3)
      }
    }
  }
  
  "getOne" should "return the one" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val p = Password.create(new Password(0, "Password10", "", ""))
        
        val ret = Password.getOne(p.id)
        ret.password should equal("Password10")
      }
    }
  }
  
  "update" should "save new values" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val p = Password.create(new Password(0, "Password10", "", ""))
        p.password = "Password11"
          
        Password.update(p)
        
        val ret = Password.getOne(p.id)
        ret.password should equal("Password11")
      }
    }
  }
  
  "json pasrser" should "make the json" in {
    val p = new Password(0, "Password10", "title", "desc")
      
    val j = Password.PasswordFormat.writes(p)
    j.toString should equal("""{"id":0,"password":"Password10","title":"title","description":"desc"}""")
  }
  
  "json parser" should "make the object" in {
      val pw = Password.PasswordFormat.reads(Json.parse("""{"id":1,"password":"Password10", "title":"title", "description":"desc"}""")).get
      
      pw.password should equal ("Password10")
      pw.title should equal("title")
      pw.description should equal ("desc")
  }
}
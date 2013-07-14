package info.stephenn.passwordsafe.password

import org.squeryl.PrimitiveTypeMode.inTransaction
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import play.api.test._
import play.api.test.Helpers._
import info.stephenn.passwordsafe.AppDB
import play.api.libs.json._

class PasswordSpec extends FlatSpec with ShouldMatchers {
  
  def defaultAccount = new Account(0, "", "" ,"" ,"")
  
  "A Password" should "be creatable" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        val p = Account.create(defaultAccount)
        p.id should not equal(0)
      }
    }
  }
  
  "List Passwords" should "return all the passwords" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        def p = defaultAccount
        val u = AppDB.accounts insert Seq(p, p, p)
        
        Account.list.length should equal(3)
      }
    }
  }
  
  "getOne" should "return the one" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        var a = defaultAccount
        a.password = "Password10"
        
        val aa = Account.create(a)
        
        val ret = Account.getOne(aa.id)
        ret.password should equal("Password10")
      }
    }
  }
  
  "update" should "save new values" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
        var a = defaultAccount
        a.password = "Password10"
        val p = Account.create(a)
        p.password = "Password11"
          
        Account.update(p)
        
        val ret = Account.getOne(p.id)
        ret.password should equal("Password11")
      }
    }
  }
  
  "json pasrser" should "make the json" in {
    running(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      inTransaction {
	    val p = new Account(0, "Password10", "title", "uname", "desc")
	    
	    val j = Account.AccountFormat.writes(p)
	    j.toString should equal("""{"id":0,"username":"uname","name":"title","description":"desc","permissions":[]}""")
      }
    }
  }
  
  "json parser" should "make the object" in {
      val pw = Account.AccountFormat.reads(Json.parse("""{"id":1,"username":"uname", "name":"title", "description":"desc"}""")).get
      
      pw.username should equal ("uname")
      pw.name should equal("title")
      pw.description should equal ("desc")
  }
}
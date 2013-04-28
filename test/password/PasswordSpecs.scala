package password

import org.specs2.mutable._
import play.api.test._
import play.api.test.Helpers._
import anorm.Id

import org.specs2.runner.JUnitRunner
import org.junit.runner.RunWith

import play.api.libs.json._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
@RunWith(classOf[JUnitRunner])
class PasswordSpec extends Specification {
  "json parsing" should {
    "Object to json String" in {
      val p = Password(Id(2), "Password10")
      
      val j = Password.PasswordFormat.writes(p)
      j.toString must equalTo("""{"id":2,"password":"Password10"}""")
    }
    
    "json string to Object" in {
      val pw = Password.PasswordFormat.reads(Json.parse("""{"id":2,"password":"Password10"}""")).get
      
      pw.id must equalTo(Id(2))
      pw.password must equalTo("Password10")
    }
  }
}
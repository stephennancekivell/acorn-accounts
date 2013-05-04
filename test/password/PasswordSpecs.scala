package password


import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

import org.squeryl.PrimitiveTypeMode.inTransaction

import play.api.test._
import play.api.test.Helpers._

import anorm.Id

import play.api.libs.json._

class PasswordSpec extends FlatSpec with ShouldMatchers {
  
  "json pasrser" should "make the json" in {
      val p = Password(Id(2), "Password10", "title", "desc")
      
    val j = Password.PasswordFormat.writes(p)
      j.toString should equal("""{"id":2,"password":"Password10","title":"title","description":"desc"}""")
  }
  
  "json parser" should "make the object" in {
      val pw = Password.PasswordFormat.reads(Json.parse("""{"id":2,"password":"Password10", "title":"title", "description":"desc"}""")).get
      
      pw.id should equal (Id(2))
      pw.password should equal ("Password10")
      pw.title should equal("title")
      pw.description should equal ("desc")
  }
}
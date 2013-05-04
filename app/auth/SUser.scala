package auth

import scala.slick.driver.H2Driver.simple._

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.util._
import play.api.libs.json.Reads._
import play.api.libs.json.Writes._

case class SUser(id: Option[Int] = None, name:String)

object SUser extends Table[SUser]("suser"){
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
 
  // This is the primary key column
  def name = column[String]("name")
 
  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ name <>(SUser.apply _, SUser.unapply _)
  
  implicit object SUserFormat extends Format[SUser] {
    def writes(p: SUser): JsValue = Json.obj("id" -> p.id.get, "name" -> p.name)

    def reads(js: JsValue) = {
      val id = (js \ "id").as[Int]
      val pw = (js \ "name").as[String]
      JsSuccess(SUser(Option(id), pw))
    }
  }
}
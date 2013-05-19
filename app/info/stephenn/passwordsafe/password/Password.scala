package info.stephenn.passwordsafe.password

import play.api.libs.json._
import play.api.libs.json.Json._

class Password(var password:String) {

}

object Password {
  implicit object PasswordFormat extends Format[Password] {
    def writes(password: Password): JsValue = {
      Json.obj("password" -> password.password)
    }

    def reads(js: JsValue) = {
      JsSuccess(new Password(
          password = (js \ "password").as[String]))
    }
  }
}
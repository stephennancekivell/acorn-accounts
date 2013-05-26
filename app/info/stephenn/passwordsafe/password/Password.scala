package info.stephenn.passwordsafe.password

import play.api.libs.json._
import play.api.libs.json.Json._
import info.stephenn.passwordsafe.EncryptionUtils

class Password(var password:String) {

}

object Password {
  implicit object PasswordFormat extends Format[Password] {
    def writes(password: Password): JsValue = {
      Json.obj("password" -> EncryptionUtils.decode(password.password))
    }

    def reads(js: JsValue) = {
      JsSuccess(new Password(
          password = EncryptionUtils.encrypt((js \ "password").as[String])))
    }
  }
}
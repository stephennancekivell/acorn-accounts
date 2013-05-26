package info.stephenn.passwordsafe

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers
import play.api.test._
import play.api.test.Helpers._

class EncryptionUtilsSpec extends FlatSpec with ShouldMatchers {
  
  "encryptor" should "hide the string" in {
    
    val x= EncryptionUtils.encrypt("Password10")
    x should not equal("Password10")
    x should equal("5JGZXs2xv6zx9BktxbcYow==")
    
    EncryptionUtils.decode(x) should equal("Password10")
  }
}
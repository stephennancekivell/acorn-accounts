package info.stephenn.passwordsafe

import javax.crypto.Cipher
import javax.crypto._
import java.security._
import javax.crypto.spec._
import org.apache.commons.codec.binary.Base64

object EncryptionUtils {
  val passphrase = play.api.Play.current.configuration.getString("app.encryption.passphrase") match {
    case None => throw new Exception("could not find property app.encryption.passphrase")
    case Some(p) => p
  }

  val digest = MessageDigest.getInstance("SHA")
  digest.update(passphrase.getBytes())
  val key = new SecretKeySpec(digest.digest(), 0, 16, "AES")

  def encrypt(in: String) = {
    val bytes = aes(Cipher.ENCRYPT_MODE).doFinal(in.getBytes())
    new String(Base64.encodeBase64(bytes))
  }

  def decode(in: String) = {
    val bytes = Base64.decodeBase64(in.getBytes())
    new String(aes(Cipher.DECRYPT_MODE).doFinal(bytes))
  }

  private def aes(mode: Int) = {
    val aes = Cipher.getInstance("AES/ECB/PKCS5Padding")
    aes.init(mode, key)
    aes
  }
}
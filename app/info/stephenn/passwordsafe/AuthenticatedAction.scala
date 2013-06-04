package info.stephenn.passwordsafe

import play.api.mvc._
import play.api.mvc.BodyParsers._
import play.api.mvc.Results.Unauthorized
import org.squeryl.PrimitiveTypeMode._
import info.stephenn.passwordsafe.auth.User
import scala.annotation.implicitNotFound

object AuthenticatedAction {

  case class AuthenticatedRequest[A](
    user: User, private val request: Request[A]) extends WrappedRequest(request)

  def Authenticated[A](p: BodyParser[A])(f: AuthenticatedRequest[A] => Result) = {
    Action(p) { request =>
      request.headers.get("x-remote-user") match {
        case None => Unauthorized
        case Some(username) => {
          // We could be handling concurrent requests.
          // If those require creating the user a unique constraint violation is likely.
          // If that happens retry, and select the new one.
          val user = getOrCreate(username, 3)
          
          f(AuthenticatedRequest(user, request))
        }
      }
    }
  }
  
  def getOrCreate(username: String, retry: Int): User = {
    User.get(username) match {
      case Some(u) => u
      case None => {
        try {
          User.create(new User(username, -1))
        } catch {
          case e: Exception => {
            if (retry > 0) {
            	getOrCreate(username, retry -1)
            } else {
              throw e
            }
          }
        }
      }
    }
  }
 
  def Authenticated(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent] = {
    Authenticated(parse.anyContent)(f)
  }
}
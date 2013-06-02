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
          val user = User.get(username) match {
            case Some(u) => u
            case None => {
              User.create(new User(username, -1))
            }
          }
          f(AuthenticatedRequest(user, request))
        }
      }
    }
  }
 
  def Authenticated(f: AuthenticatedRequest[AnyContent] => Result): Action[AnyContent] = {
    Authenticated(parse.anyContent)(f)
  }
}
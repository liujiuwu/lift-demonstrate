package learn.service

import net.liftweb.common.{ Box, Empty, Full, Failure }
import net.liftweb.http.{ SessionVar, S, LiftResponse, RedirectResponse }
import net.liftweb.sitemap.Loc.{ If, Unless, TestAccess }

import learn.model.Account

object SessionManager extends net.liftweb.common.Loggable {

  object theAccount extends SessionVar[Box[Account]](Empty)

  def accountAccess = TestAccess { () =>
    theAccount.is match {
      case Full(theAccount) => Empty
      case _ =>
        Account(S.findCookie(Account.cookieName)) match {
          case account @ Full(_) =>
            theAccount(account)
            Empty
          case Failure(msg, _, _) =>
            logger.error("用户自动登陆失败: " + msg)
            S.deleteCookie(Account.cookieName)
            Full(RedirectResponse("/session/login"))
          case Empty =>
            S.error("用户自动登陆: 系统错误")
            Full(RedirectResponse("/session/login"))
        }
    }
  }

  def accountLogout = TestAccess { () =>
    if (theAccount.is.isDefined)
      theAccount(Empty)
    Full(RedirectResponse("/session/login"))
  }
}

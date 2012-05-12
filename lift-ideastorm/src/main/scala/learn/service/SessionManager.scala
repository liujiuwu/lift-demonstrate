package learn.service

import net.liftweb.common.{ Box, Empty, Full, Failure }
import net.liftweb.http._
import net.liftweb.sitemap.Loc.{ If, Unless, TestAccess }

import learn.model.Account

object SessionManager extends net.liftweb.common.Loggable {

  object theAccountId extends SessionVar[Box[String]](Empty) {

    override def remove() {
      is.foreach(ContextSystem.main ! AccountLogout(_)) // Box
      super.remove()
    }
  }

  def accountAccess = TestAccess { () =>
    theAccountId.is match {
      case Full(theAccount) => Empty
      case _ =>
        Account(S.findCookie(Account.cookieName)) match {
          case Full(account) =>
            saveSessionAndCookie(account.id)
            Empty
          case Failure(msg, _, _) =>
            logger.error("用户自动登陆失败: " + msg)
            S.deleteCookie(Account.cookieName)
            Full(RedirectResponse("/session/login"))
          case Empty =>
            Full(RedirectResponse("/session/login"))
        }
    }

  }

  def accountLogout = TestAccess { () =>
    if (theAccountId.is.isDefined) {
      //theAccount(Empty)
      theAccountId.remove()
    }
    S.deleteCookie(Account.cookieName)
    Full(RedirectResponse("/session/login"))
  }

  def saveSessionAndCookie(accountId: String, beReset: Boolean = false) {
    S.session.foreach(_.addSessionCleanup(_ => {
      ContextSystem.main ! AccountLogin(accountId)
    }))

    theAccountId(Full(accountId))

    ContextSystem.main ! AccountLogin(accountId)

    if (beReset) {
      S.addCookie(Account.httpCookie(accountId).open_!) // 此处可安全打开
    } else {
      S.deleteCookie(Account.cookieName)
    }

  }
}

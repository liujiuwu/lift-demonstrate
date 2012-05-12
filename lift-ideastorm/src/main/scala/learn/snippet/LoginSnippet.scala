package learn.snippet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.{ SHtml, SessionVar, RequestVar, S }
import net.liftweb.http.provider.HTTPCookie
import net.liftweb.util.Helpers._

import learn.model.Account
import learn.service.SessionManager._

object LoginSnippet {
  private object username extends RequestVar[String]("")
  private object password extends RequestVar[String]("")
  private object remember extends RequestVar[Boolean](false)

  def render(nodeSeq: NodeSeq): NodeSeq = {
    if (theAccountId.is.isDefined) {
      S.warning("用户: %s 您已经登陆" format Account.find(theAccountId.is.open_!).open_!.username)
      S.redirectTo("/account/index")
    }

    val cssSel =
      "@username" #> SHtml.text(username.is, username(_)) &
        "@password" #> SHtml.password(password.is, password(_)) &
        "@remember" #> SHtml.checkbox(remember.is, remember(_)) &
        "@login" #> SHtml.submit("登陆", () => {
          Account(username.is, password.is) match {
            case Full(account) =>
              saveSessionAndCookie(account.id, remember.is)
              S.notice("用户登陆: %s, 欢迎您" format account.username)
              S.redirectTo("/index")
            case Failure(msg, _, _) =>
              S.error("用户登陆失败：" + msg)
            case Empty =>
              S.error("用户登陆: 系统错误")
          }
        })

    cssSel(nodeSeq)
  }
}

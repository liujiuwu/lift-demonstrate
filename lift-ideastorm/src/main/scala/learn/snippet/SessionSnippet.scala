package learn.snippet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common._
import net.liftweb.util.CssSel
import net.liftweb.util.Helpers._
import net.liftweb.http._
import net.liftweb.http.js._
import net.liftweb.http.js.jquery.{ JqJsCmds }
import net.liftweb.json.JsonDSL._

import learn.web.Y
import learn.service._
import learn.model.Account

import SessionManager.theAccountId

class SessionSnippet {
  def navCollapse: NodeSeq = theAccountId.is match {
    case Full(accountId) =>
      val account = Account.find(accountId).open_!

      val cssSel =
        "@username" #> account.username &
          ".dropdown-menu" #> <div>
                                <li>#其它功能#</li>
                                <li class="divider"></li>
                                <li><a href="/session/logout">退出</a></li>
                              </div>
      cssSel(_navCollapse) ++ Y.template("navbar-comet").open_!

    case _ =>
      val cssSel = "@username" #> "登陆/注册" &
        ".dropdown-menu" #> <div>
                              <li><a href="/session/login">登陆</a></li>
                              <li><a href="/session/register">注册</a></li>
                            </div> &
        ".dropdown-toggle [class]" #> "btn btn-info dropdown-toggle"
      cssSel(_navCollapse)

  }

  private val _navCollapse = Y.template("navbar-session").open_!
}

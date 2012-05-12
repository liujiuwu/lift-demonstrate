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
  def navCollapse: NodeSeq = {
    val cssSel = theAccountId.is match {
      case Full(accountId) =>
	val account = Account.find(accountId).open_!
        "@username" #> account.username &
          ".dropdown-menu" #> <div>
                                <li><a href="/session/logout">退出</a></li>
                              </div>
      case _ =>
        "@username" #> "登陆/注册" &
          ".dropdown-menu" #> <div>
                                <li><a href="/session/login">登陆</a></li>
                                <li><a href="/session/register">注册</a></li>
                              </div>
    }
    cssSel(_navCollapse)
  }

  private val _navCollapse =
    <div class="nav-collapse">
      <div class="btn-group pull-right">
        <a class="btn btn-primary dropdown-toggle" data-toggle="dropdown" href="#">
          <span name="username">#用户名#</span>
          <span class="caret"></span>
        </a>
        <ul class="dropdown-menu">
        </ul>
      </div>
    </div>

  //<a class="brand">#子系统名称#</a>

}

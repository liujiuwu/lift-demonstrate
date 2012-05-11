package learn.service

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.util.Helpers._
import net.liftweb.http.{ SHtml, RequestVar, SessionVar }
import net.liftweb.http.js.{ JsCmds, JE }
import net.liftweb.http.js.jquery.JqJsCmds
import JqJsCmds._

import learn.web.Y
import learn.model.Account

object IMService {
  object Reqs {
    object reqMsg extends RequestVar[String]("")
    object reqCurMsgAccountId extends RequestVar[List[String]](Nil)
  }

  import Reqs._

  def accountList: NodeSeq = {
    <ul class="nav nav-tabs nav-stacked">
      {
        Account.findAll.map { account =>
          <li>{
            Y.ajaxA(account.username, Full("/infoshare?accountId=" + account.id), () => {
              // TODO 向#account_tabs添加li项，并重设#msg_window_frame
              if (reqCurMsgAccountId.is.contains(account.id)) {
                JsCmds.Run("$('#account_tab_%s').addClass('active')" format account.id)
              } else {

                val htmlId = "msg_window_" + account.id

                reqCurMsgAccountId(account.id :: reqCurMsgAccountId.is)
                AppendHtml("account_tabs", accountTabItem(account.id)) &
                  JsCmds.SetHtml("msg_window_frame", msgWindow(account.id))
              }
            })
          }</li>
        }
      }
    </ul>
  }

  def msgWindow(accountId: String): NodeSeq = {
    <div>
      # 此处是聊天窗口啊!!!!!! #
    </div>
  }

  def accountTabItem(accountId: String): NodeSeq = Account.find(accountId).dmap(NodeSeq.Empty) { account =>
    val cssSel =
        "#account_tab_ [id]" #> "account_tab_%s".format(account.id) &
        "@nickname" #> account.username

    cssSel(dropdownNodeSeq)
  }

  val dropdownNodeSeq =
    <li class="dropdown" id="account_tab_">
      <a class="dropdown-toggle" data-toggle="dropdown" href="#"><span name="nickname"/><b class="caret"></b></a>
      <ul class="dropdown-menu">
        <li><a href="#" data-yj="openMsg">发送即时消息</a></li>
        <li><a href="#" data-yj="openEmail">发送电子邮件</a></li>
        <li class="divider"></li>
        <li><a href="#" data-yj="closeMsg">关闭</a></li>
      </ul>
    </li>

  val navTabsNodeSeq = <ul class="nav nav-tabs">
                         <li class="dropdown">
                           <a class="dropdown-toggle" data-toggle="dropdown" href="#">Dropdown <b class="caret"></b></a>
                           <ul class="dropdown-menu">
                             <li><a href="#">Action</a></li>
                             <li><a href="#">Another action</a></li>
                             <li><a href="#">Something else here</a></li>
                             <li class="divider"></li>
                             <li><a href="#">Separated link</a></li>
                           </ul>
                         </li>
                         <li class="dropdown active">
                           <a class="dropdown-toggle" data-toggle="dropdown" href="#">Dropdown <b class="caret"></b></a>
                           <ul class="dropdown-menu">
                             <li><a href="#">Action</a></li>
                             <li><a href="#">Another action</a></li>
                             <li><a href="#">Something else here</a></li>
                             <li class="divider"></li>
                             <li><a href="#">Separated link</a></li>
                           </ul>
                         </li>
                         <li class="dropdown">
                           <a class="dropdown-toggle" data-toggle="dropdown" href="#">Dropdown <b class="caret"></b></a>
                           <ul class="dropdown-menu">
                             <li><a href="#">Action</a></li>
                             <li><a href="#">Another action</a></li>
                             <li><a href="#">Something else here</a></li>
                             <li class="divider"></li>
                             <li><a href="#">Separated link</a></li>
                           </ul>
                         </li>
                       </ul>

}

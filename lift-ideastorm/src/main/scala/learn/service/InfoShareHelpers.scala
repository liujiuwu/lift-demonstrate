package learn.service

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.util.Helpers._
import net.liftweb.http.{ SHtml, RequestVar, SessionVar, CometActor }
import net.liftweb.http.js.{ JsCmds, JE }
import JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds
import JqJsCmds._

import learn.web.Y
import learn.model.Account
import learn.service._

class InfoShareHelpers(liftComet: CometActor) {
  object Reqs {
    object reqMsg extends RequestVar[String]("")
  }

  import Reqs._

  def accountList(): NodeSeq = {
    val onlineAccountIds = ContextSystem.onlineAccountIds.toList
    <ul class="nav nav-tabs nav-stacked">
      {
        Account.findAll.map { account =>
          val state = if (onlineAccountIds.contains(account.id)) "在线" else "离线"
          <li>{
            Y.ajaxA(account.username + " " + state, Full("/infoshare?accountId=" + account.id), () => {
              if (onlineAccountIds.contains(account.id)) {
                JsCmds.Run("$('#account_tab_%s').click();")
              } else {

                val htmlId = "msg_window_" + account.id

                AppendHtml("account_tabs", accountTabItem(account.immutable)) &
                  AppendHtml("tab_content", msgWindow(account.immutable))
              }
            })
          }</li>
        }
      }
    </ul>
  }

  def msgWindow(account: Account): NodeSeq = {
    val sendArea =
      <div>{ SHtml.textarea("", reqMsg(_), "style" -> "height:128px;", "class" -> "span8") }</div>
      <div>{
        SHtml.button("发送", () => (), "id" -> "smg_send_button") ++
          SHtml.hidden(() => if (reqMsg.is != "") {
            val c = MessageLine(liftComet, account.id, Text(reqMsg.is), timeNow)
            IMSystem.main ! c
            // appendHtml(c)
            Noop
          } else
            Noop)
      }</div>

    //
    <div class="tab-pane" id={ "msg_window_frame_" + account.id }>
      <div>
        <ul id={ "msg_window_" + account.id }>
        </ul>
      </div>
      <div>
        {
          SHtml.ajaxForm(sendArea)
        }
        <span>{ account.username }</span>
      </div>
    </div>
  }

  def accountTabItem(account: Account): NodeSeq = {
    val cssSel =
      "#account_tab_ [id]" #> "account_tab_%s".format(account.id) &
        "@nickname" #> account.username

    cssSel(accountTabItemNode(account))
  }

  private def accountTabItemNode(account: Account) =
    <li><a href={ "#msg_window_frame_" + account.id } data-toggle="tab">{ account.username }</a></li>

}

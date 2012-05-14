package learn.comet

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
import SessionManager.theAccountId

class InfoShareHelpers(liftComet: CometActor) {
  object Reqs {
    object reqMsg extends RequestVar[String]("")
  }

  import Reqs._

  private val openTabs = scala.collection.mutable.HashSet[String]()

  def accountList(onlineAccountIds: Seq[String]): NodeSeq = theAccountId.dmap(NodeSeq.Empty) { sessionAccountId =>
    <span>ID: { sessionAccountId }</span>
    <ul class="nav nav-tabs nav-stacked">
      {
        Account.findAll.filterNot(_.id == sessionAccountId).map { account =>
          val state = if (onlineAccountIds.contains(account.id)) "在线" else "离线"
          <li>{
            Y.ajaxA(account.username + " " + state, Full("/infoshare?accountId=" + account.id), () => {
              if (openTabs.contains(account.id) || account.id == sessionAccountId) {
                JsCmds.Run("$('#account_tab_%s').click();" format account.id)
              } else {

                openTabs add account.id

                AppendHtml("account_tabs", accountTabItem(account.immutable)) &
                  AppendHtml("tab_content", msgWindow(account.immutable)) &
                  JsCmds.Run("$('#account_tab_%s').click();" format account.id)
              }
            })
          }</li>
        }
      }
    </ul>
  }

  def msgWindow(toAccount: Account): NodeSeq = {
    val sendArea =
      <div>{ SHtml.textarea("", reqMsg(_), "style" -> "height:128px;", "class" -> "span8") }</div>
      <div>{
        SHtml.button("发送", () => (), "id" -> "smg_send_button") ++
          SHtml.hidden(() => if (reqMsg.is != "") {
            val msg = MessageLine(liftComet, theAccountId.open_!, toAccount.id, Text(reqMsg.is), timeNow)

            IMSystem.main ! msg
            AppendHtml("msg_window_" + msg.toId, line(msg))
          } else {
            Noop
          })
      }</div>

    //
    <div class="tab-pane" id={ "msg_window_frame_" + toAccount.id }>
      <div>
        <ul id={ "msg_window_" + toAccount.id }>
        </ul>
      </div>
      <div>
        {
          SHtml.ajaxForm(sendArea)
        }
        <span>{ toAccount.username + " " + toAccount.id }</span>
      </div>
    </div>
  }

  def accountTabItem(account: Account): NodeSeq = {
    val cssSel =
      "#account_tab_ [id]" #> "account_tab_%s".format(account.id) &
        "@nickname" #> account.username

    cssSel(accountTabItemNode(account))
  }

  def line(line: MessageLine): NodeSeq = {
    val username = Account.find(line.fromId).dmap("系统管理员")(_.username)
    val cssSel =
      "li" #> (
        "name=who" #> username &
        "name=when" #> hourFormat(line.when) &
        "name=body" #> line.msg)

    cssSel(lineTemplate openOr defaultTemplate)
  }

  private def accountTabItemNode(account: Account) =
    <li><a href={ "#msg_window_frame_" + account.id } data-toggle="tab">{ account.username }</a></li>

  private val defaultTemplate: NodeSeq =
    <li>
      <div>
        <span name="who"/>
        <span name="when"/>
      </div>
      <div>
        <i><span name="body"/></i>
      </div>
    </li>

  private lazy val lineTemplate: Box[NodeSeq] = Y.resource("/test/_im_line")
}

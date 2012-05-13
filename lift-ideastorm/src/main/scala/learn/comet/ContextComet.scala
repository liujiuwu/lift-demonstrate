package learn.comet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.{ SHtml, S, CometActor, RequestVar }
import net.liftweb.actor.{ LiftActor }
import net.liftweb.util.Helpers._
import net.liftweb.util.Schedule
import net.liftweb.http.js.{ JE, JsCmds }
import JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds.{ AppendHtml }

import learn.web.Y
import learn.service._
import SessionManager.theAccountId
import learn.model.Account

class ContextComet extends CometActor { contextComet =>

  override def render = {
    "#%s *".format(MSG) #> "0" &
      "#%s *".format(BACKLOG) #> "0" &
      "#%s *".format(IMPORTANT) #> "0"
  }

  override def lowPriority = {
    case OnlineAccountIds(onlineAccountIds) if theAccountId.isDefined =>
      // TODO 测试用，需替换 

      for (accountId <- theAccountId if !onlineAccountIds.contains(accountId)) {
        ContextSystem.s.context ! CometStatus(contextComet, Full(accountId))
      }

      partialUpdate(SetHtml(MSG, Text(onlineAccountIds.size.toString)))

    case _ =>
  }

  override def localSetup {
    println("\n\n ContextComet setup %s\n\n" format this)
    ContextSystem.s.context ! LiftActorRegisterListener(contextComet, theAccountId.is)
  }

  override def localShutdown {
    println("\n\n ContextComet shutdown %s\n\n" format this)
    ContextSystem.s.context ! LiftActorRemoveListener(contextComet, theAccountId.is)
  }

  private def summary(account: Account): NodeSeq = {
    val content = "未读消息: " + account.unreadInfomationIds.size

    Text(content)
  }

  private def dropdownMenu(account: Account): NodeSeq = {
    <li><a href="#">查看消息</a></li>
  }

  private val MSG = "context_msg" // 未读消息
  private val BACKLOG = "context_backlog" // 待办事项			       
  private val IMPORTANT = "context_important" // 催办事项
  private val DROPDOWN_MENU = "context_dropdown-menu"
}

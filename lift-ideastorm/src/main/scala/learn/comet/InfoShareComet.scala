package learn.comet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.{ SHtml, S, CometActor, RequestVar }
import net.liftweb.actor.{ LiftActor }
import net.liftweb.util.Helpers._
import net.liftweb.http.js.{ JE, JsCmds }
import JsCmds.{ Noop, SetHtml }
import net.liftweb.http.js.jquery.JqJsCmds.{ AppendHtml }

import learn.web.Y
import learn.service._
import SessionManager.theAccountId
import learn.model.Account

/**
 * SiteMap中已进行了Session判断，这里当可安全的open_! theAccount
 */
class InfoShareComet extends CometActor { self =>

  private val accountId = theAccountId.is.open_!
  private val helper = new InfoShareHelpers(self)

  private object reqMsg extends RequestVar[String]("")

  override def render = {
    "#account_list *" #> helper.accountList(ContextSystem.onlineAccountIds.toSeq)
  }

  override def lowPriority = {
    case a @ MessageLines(imActor, lines) =>
      partialUpdate(appendHtml(lines: _*))

    case OnlineStatus(onlineIds) =>
      partialUpdate(SetHtml("account_list", helper.accountList(onlineIds.toSeq)))
  }

  override def localSetup {
    IMSystem.main ! MessageRegisterListener(this, accountId)
  }

  override def localShutdown {
    IMSystem.main ! MessageRemoveListener(this, accountId)
  }

  private def appendHtml(lines: MessageLine*) = {
    lines.map(line => AppendHtml("msg_window_" + line.fromId, helper.line(line))).foldLeft(Noop)(_ & _)
  }
}

package learn.comet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.{ SHtml, S, CometActor, RequestVar }
import net.liftweb.actor.{ LiftActor }
import net.liftweb.util.Helpers._
import net.liftweb.http.js.{ JE, JsCmds }
import JsCmds.Noop
import net.liftweb.http.js.jquery.JqJsCmds.{ AppendHtml }

import learn.web.Y
import learn.service.{ IMSystem, MessageLine, MessageLines, MessageRegisterListener, MessageRemoveListener, SessionManager }
import SessionManager.theAccountId
import learn.model.Account

/**
 * SiteMap中已进行了Session判断，这里当可安全的open_! theAccount
 */
class InfoShareComet extends CometActor { liftComet =>

  private val accountId = theAccountId.is.open_!
  private val helper = new InfoShareHelpers(liftComet)

  private object reqMsg extends RequestVar[String]("")

  override def render = {
    "@accountList" #> helper.accountList
  }

  override def lowPriority = {
    case a @ MessageLines(imActor, lines) =>
      partialUpdate(appendHtml(lines: _*))
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

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
import learn.service.{ IMSystem, InfoShareHelpers, MessageLine, MessageLines, MessageRegisterListener, MessageRemoveListener, SessionManager }
import SessionManager.theAccount
import learn.model.Account

/**
 * SiteMap中已进行了Session判断，这里当可安全的open_! theAccount
 */
class InfoShareComet extends CometActor { liftComet =>

  private val account = theAccount.is.open_!
  private val helper = new InfoShareHelpers(liftComet)

  private object reqMsg extends RequestVar[String]("")

  override def render = {
    "@accountList" #> helper.accountList
  }

  override def lowPriority = {
    case MessageLines(imActor, lines) =>
      partialUpdate(appendHtml(lines: _*))
  }

  override def localSetup {
    IMSystem.main ! MessageRegisterListener(this, account)
  }

  override def localShutdown {
    IMSystem.main ! MessageRemoveListener(this, account)
  }

  private def appendHtml(lines: MessageLine*) = {
    lines.map(line => AppendHtml("msg_window_" + line.account.id, _line(line))).foldLeft(Noop)(_ & _)
  }

  private def _line(line: MessageLine) = {
    val cssSel =
      "li" #> (
        "name=who" #> line.account.username &
        "name=when" #> hourFormat(line.when) &
        "name=body" #> line.msg)

    cssSel(lineTemplate openOr defaultTemplate)
  }

  private lazy val lineTemplate: Box[NodeSeq] = Y.resource("/test/_im_line")

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

}

package learn.comet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.{ SHtml, S, CometActor }
import net.liftweb.actor.{ LiftActor }
import net.liftweb.util.Helpers._
import net.liftweb.http.js.{ JE, JsCmds }
import JsCmds.Noop
import net.liftweb.http.js.jquery.JqJsCmds.{ AppendHtml }

import learn.web.Y
import learn.service.{ IMSystem, MessageLine, MessageLines, MessageRegisterListener, MessageRemoveListener, SessionManager }
import SessionManager.theAccount

/**
 * SiteMap中已进行了Session判断，这里当可安全的open_! theAccount
 */
class IMComet extends CometActor { liftComet =>

  override def render = {
    "@username" #> theAccount.open_!.username &
      "@sendMsg" #> SHtml.ajaxTextarea("", msg => {
        val c = MessageLine(liftComet, theAccount.is.open_!.username, Text(msg), timeNow)
        IMSystem.main ! c
//        appendHtml(c)
	Noop
      })
  }

  override def lowPriority = {
    case MessageLines(imActor, lines) =>
      partialUpdate(appendHtml(lines: _*))
  }

  override def localSetup {
    IMSystem.main ! MessageRegisterListener(this, theAccount.is.open_!.username)
  }

  override def localShutdown {
    IMSystem.main ! MessageRemoveListener(this)
  }

  private def appendHtml(lines: MessageLine*) = {
    AppendHtml("main_message_window", line(lines: _*))
  }

  private def line(line: MessageLine*) = {
    val cssSel =
      "li" #> line.map { c =>
        "name=who" #> c.user &
          "name=when" #> hourFormat(c.when) &
          "name=body" #> c.msg
      }
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

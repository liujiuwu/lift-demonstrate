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
import SessionManager.theAccount

/**
 * SiteMap中已进行了Session判断，这里当可安全的open_! theAccount
 */
class IMComet extends CometActor { liftComet =>
  private object reqMsg extends RequestVar[String]("")

  override def render = {
    "@username" #> theAccount.open_!.username &
      "#send" #> SHtml.ajaxForm(
        SHtml.textarea("", reqMsg(_)) ++
          <button>发送</button> ++
          SHtml.hidden(() => if (reqMsg.is != "") {
            val c = MessageLine(liftComet, theAccount.is.open_!.username, Text(reqMsg.is), timeNow)
            IMSystem.main ! c
            // appendHtml(c)
            Noop
          } else
            Noop))
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

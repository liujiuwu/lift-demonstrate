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
class ChatComet extends CometActor { liftComet =>
  private val accountId = theAccountId.is.open_!
  private object reqMsg extends RequestVar[String]("")

  override def render = {
    val account = Account.find(accountId).open_!

    "@username" #> account.username &
      "#send" #> SHtml.ajaxForm(
        SHtml.textarea("", reqMsg(_)) ++
          <button>发送</button> ++
          SHtml.hidden(() => if (reqMsg.is != "") {
            val c = MessageLine(liftComet, account.id, Text(reqMsg.is), timeNow)
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
    IMSystem.main ! MessageRegisterListener(this, accountId)
  }

  override def localShutdown {
    IMSystem.main ! MessageRemoveListener(this, accountId)
  }

  private def appendHtml(lines: MessageLine*) = {
    AppendHtml("main_message_window", line(lines: _*))
  }

  private def line(line: MessageLine*) = {
    val cssSel =
      "li" #> line.map { c =>
        val account = Account.find(c.accountId).open_! // 此处可安全打开

        "name=who" #> account.username &
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

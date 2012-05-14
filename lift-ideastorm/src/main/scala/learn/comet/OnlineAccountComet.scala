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

class OnlineAccountComet extends CometActor { onlineComet =>
  val accountId = theAccountId.open_!

  override def render = {
    "#account_list *" #> accountNodeSeq(ContextSystem.onlineAccountIds.toList: _*)
  }

  override def lowPriority = {
    case OnlineAccountIds(accountIds) =>
      partialUpdate(SetHtml("account_list", accountNodeSeq(accountIds.toSeq: _*)))
  }

  private def accountNodeSeq(accountId: String*) = {
    accountId.map(id => <li>{ id + " " + Account.find(id).open_!.username } </li>).foldLeft(NodeSeq.Empty)(_ ++ _)
  }

  override def localSetup {
    ContextSystem.s.context ! LiftActorRegisterListener(onlineComet, Empty)
  }

  override def localShutdown {
    ContextSystem.s.context ! LiftActorRemoveListener(onlineComet, Empty)
  }
}

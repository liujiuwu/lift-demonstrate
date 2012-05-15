package learn.comet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.{ SHtml, S, CometActor, RequestVar }
import net.liftweb.actor.{ LiftActor }
import net.liftweb.util.Helpers._
import net.liftweb.http.js.{ JE, JsCmds }
import JsCmds.{ Noop, SetHtml }
import net.liftweb.http.js.jquery.JqJsCmds.{ AppendHtml }

import me.yangbajing.util.Utils._

import learn.web.Y
import learn.service._
import SessionManager.theAccountId
import learn.model.Account

class AccountCenterComet extends CometActor { self =>

  private val accountId = theAccountId.open_! // 此处安全

  override def render = {
    "#current_datetime *" #> dateIsoWeak.format(new java.util.Date)
  }

  override def lowPriority = {
    case _ =>
  }

  override def localSetup {

  }

  override def localShutdown {

  }

}

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
import learn.service.{ IMSystem, InfoShareHelpers, MessageLine, MessageLines, MessageRegisterListener, MessageRemoveListener, SessionManager }
import SessionManager.theAccountId
import learn.model.Account

case object Tick

class SessionComet extends CometActor { liftComet =>
  Schedule.schedule(this, Tick, 30 seconds)

  override def render = {
    "#session_status" #> defTemp
  }

  override def lowPriority = {
    case Tick =>
      partialUpdate(
        SetHtml(SUMMARY, summary) &
          SetHtml(DROPDOWN_MENU, dropdownMenu))
      Schedule.schedule(this, Tick, 30 seconds)
  }

  override def localSetup {

  }

  override def localShutdown {

  }

  def sessionStatus = {
    SUMMARY #> summary &
      DROPDOWN_MENU #> dropdownMenu
  }

  private def summary = {
    NodeSeq.Empty
  }

  private def dropdownMenu = {
    NodeSeq.Empty
  }

  private val SUMMARY = "session_summary"
  private val DROPDOWN_MENU = "session_dropdown-menu"

  private val defTemp = <div class="nav-collapse">
                          <div class="btn-group pull-right">
                            <a class="btn btn-warning dropdown-toggle" data-toggle="dropdown" href="#">
                              <span id={ SUMMARY }>#未登陆#</span>
                              <span class="caret"></span>
                            </a>
                            <ul class="dropdown-menu" id={ DROPDOWN_MENU }>
                            </ul>
                          </div>
                        </div>
}

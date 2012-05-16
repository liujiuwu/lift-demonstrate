package learn.comet

import scala.xml.{ NodeSeq, Text }

import net.liftweb.common.{ Box, Full, Empty, Failure }
import net.liftweb.http.{ SHtml, S, CometActor, RequestVar }
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

  private val helper = new AccountCenterHelper(self)

  override def render = {
    S.appendJs(ready)

    "#yj-sidebar *" #> helper.sidebar
  }

  override def lowPriority = {
    case _ =>
  }

  override def localSetup {

  }

  override def localShutdown {

  }

  private def ready = JsCmds.Run("""
var hrefHash = window.location.hash.split('=')[1];
$('#yj-a-' + hrefHash).click();""")
}

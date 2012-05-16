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

class AccountCenterHelper(comet: CometActor) {

  def accountId: String = theAccountId.open_!

  def message: NodeSeq = {
    Y.processTempate("/c/_message") { nodeSeq =>
      nodeSeq
    }
  }

  def profile: NodeSeq = {
    Y.processTempate("/c/_profile") { nodeSeq =>
      nodeSeq
    }
  }

  def password: NodeSeq = {
    Y.processTempate("/c/_password") { nodeSeq =>
      nodeSeq
    }
  }

  def privacy: NodeSeq = {
    Y.processTempate("/c/_privacy") { nodeSeq =>
      nodeSeq
    }
  }

  def subscribe: NodeSeq = {
    Y.processTempate("/c/_subscribe") { nodeSeq =>
      nodeSeq
    }
  }

  def settings: NodeSeq = {
    Y.processTempate("/c/_settings") { nodeSeq =>
      nodeSeq
    }
  }

  def sidebar: NodeSeq = {
    def sidebarLi(id: String, text: String, locationHash: String, template: NodeSeq) =
      <li>{
        Y.ajaxA(text, Full("#" + locationHash), () => {
          JsCmds.SetHtml("yj-container-main", template) &
            JE.JsRaw("window.location.hash='%s'" format locationHash).cmd
        })
      }</li>

    Y.processTempate("/c/_sidebar") { nodeSeq =>
      val cssSel =
        "#yj-a-message" #> sidebarLi("yj-a-message", "消息管理", "href=message", message) &
          "#yj-a-profile" #> sidebarLi("yj-a-profile", "个人信息", "href=profile", profile) &
          "#yj-a-password" #> sidebarLi("yj-a-password", "修改密码", "href=password", password) &
          "#yj-a-privacy" #> sidebarLi("yj-a-privacy", "隐私设置", "href=privacy", privacy) &
          "#yj-a-subscribe" #> sidebarLi("yj-a-subscribe", "邮件提醒/订阅设置", "href=subscribe", subscribe) &
          "#yj-a-settings" #> sidebarLi("yj-a-settings", "参数设置", "href=settings", settings)

      cssSel(nodeSeq)
    }
  }
}

package bootstrap.liftweb

import net.liftweb.http.{ XHtmlInHtml5OutProperties, LiftRules, Req }

class Boot {
  def boot {
    LiftRules.addToPackages("learn")

    LiftRules.htmlProperties.default.set((r: Req) => new XHtmlInHtml5OutProperties(r.userAgent))
  }
}

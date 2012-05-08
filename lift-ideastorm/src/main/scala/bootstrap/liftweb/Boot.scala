package bootstrap.liftweb

import net.liftweb.common.{ Full }
import net.liftweb.http.{ XHtmlInHtml5OutProperties, LiftRules, Req }
import net.liftweb.mongodb.{ MongoDB, DefaultMongoIdentifier, MongoAddress, MongoHost }
import net.liftweb.sitemap.{ SiteMap, Menu, ** }

class Boot {
  def boot {
    LiftRules.addToPackages("learn")

    LiftRules.htmlProperties.default.set((r: Req) => new XHtmlInHtml5OutProperties(r.userAgent))

    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("loading").cmd)
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("loading").cmd)

    configureSiteMap()
    configureMongoDB()
  }

  private def configureMongoDB() {
    MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("localhost", 27017), "learn"))
  }

  private def configureSiteMap() {
    import learn.service.SessionManager._

    val siteMap = SiteMap(
      Menu("index", "首页") / "index" >> accountAccess,
      Menu("account", "账户") / "account" >> accountAccess,
      Menu("model", "模型") / "model" / ** >> accountAccess,
      Menu("session", "会话") / "session" / "index" submenus (
        Menu("session-login", "登陆") / "session" / "login",
        Menu("session-logout", "退出") / "session" / "logout" >> accountLogout))

    LiftRules.setSiteMap(siteMap)
  }
}

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

    new Setup().setup
    configureSiteMap()
    configureMongoDB()
    configureUnloadHook()
  }

  private def configureMongoDB() {
    MongoDB.defineDb(DefaultMongoIdentifier, MongoAddress(MongoHost("localhost", 27017), "learn"))
  }

  private def configureSiteMap() {
    import learn.service.SessionManager._

    val siteMap = SiteMap(
      Menu("index", "首页") / "index" >> accountAccess,
      Menu("single", "单页") / "single" >> accountAccess,
      Menu("test", "Test") / "test" / ** >> accountAccess,
      Menu("infoshare", "Info Share") / "infoshare" >> accountAccess,
      Menu("center", "Account Center") / "c" / ** >> accountAccess,
      Menu("help", "Helper") / "help" / **, // TODO 帮助需要开放权限吗?
      Menu("session", "会话") / "session" / "inTryUsingResourcesdex" submenus (
        Menu("session-login", "登陆") / "session" / "login",
        Menu("session-register", "注册") / "session" / "register",
        Menu("session-register2", "注册2") / "session" / "register2",
        Menu("session-logout", "退出") / "session" / "logout" >> accountLogout))

    LiftRules.setSiteMap(siteMap)
  }

  private def configureUnloadHook() {
    LiftRules.unloadHooks.append(() => learn.service.System.system.shutdown())
  }
}

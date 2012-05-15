package bootstrap.liftweb

import net.liftweb.http._

import me.yangbajing.log.{ Logger, LoggableLazy }

import learn.service.System
import learn.YjProps

class Setup extends LoggableLazy {
  def setup() {
    setupLog
  }

  private def setupLog() {
    Logger.rules.enableDebug = true
    Logger.start()

    if (YjProps.enableLogMongodb) {
      Logger.plugins.mongodbStart("localhost", 27017, "learn")
    }

    if (YjProps.enableLogStdio) {
      Logger.plugins.stdioStart(java.lang.System.out)
    }

    LiftRules.unloadHooks.append(() => Logger.stop())
  }

  // Not used
  private def test {
    LiftSession.afterSessionCreate = List(
      (session, req) => {
        logger.debug("session aftersessionCreate: %s\nreq: %s" format (session, req))
        session.httpSession.foreach(s => {
          logger.debug(s.sessionId + " --- " + s.maxInactiveInterval)
        })
      })

    LiftSession.onSessionActivate = List(
      session => {
        logger.debug("session onSessionActivate: %s" format session)
      })

    LiftSession.onSessionPassivate = List(
      session => {
        logger.debug("session onSessionPassiavte: %s" format session)
      })

    LiftSession.onShutdownSession = List(
      session => {
        logger.debug("session onShutdownSession: %s" format (session))
        session.httpSession.foreach(s => logger.debug(s.sessionId + " --- " + s.maxInactiveInterval))
      })

    S.session.foreach(_.addSessionCleanup { session =>
      logger.debug("nsession: " + session)
    })
  }
}

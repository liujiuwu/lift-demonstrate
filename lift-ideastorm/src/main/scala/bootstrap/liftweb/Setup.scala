package bootstrap.liftweb

import net.liftweb.http._

import me.yangbajing.log.{ Logger, Loggable }

import learn.service.System

class Setup extends Loggable {
  def setup() {
    Logger.rules.enableDebug = true
    Logger.start()
    Logger.plugins.mongodbStart()

    LiftRules.unloadHooks.append(() => Logger.stop())
  }

  // Not used
  def test {
    LiftSession.afterSessionCreate = List(
      (session, req) => {
        println("session aftersessionCreate: %s\nreq: %s\n\n" format (session, req))
        session.httpSession.foreach(s => {
          println(s.sessionId + " --- " + s.maxInactiveInterval)
        })
      })

    LiftSession.onSessionActivate = List(
      session => {
        println("session onSessionActivate: %s\n\n" format session)
      })

    LiftSession.onSessionPassivate = List(
      session => {
        println("session onSessionPassiavte: %s\n\n" format session)
      })

    LiftSession.onShutdownSession = List(
      session => {
        println("session onShutdownSession: %s\n\n" format (session))
        session.httpSession.foreach(s => println(s.sessionId + " --- " + s.maxInactiveInterval))
      })

    S.session.foreach(_.addSessionCleanup { session =>
      println("\n\nsession: " + session)
    })
  }
}

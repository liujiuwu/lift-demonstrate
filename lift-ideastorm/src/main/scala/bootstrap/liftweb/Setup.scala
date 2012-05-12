package bootstrap.liftweb

import net.liftweb.http._

import learn.service.System

class Setup extends net.liftweb.common.Loggable {
  def setup() {

    /*
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
*/

  }
}

package learn.service

import akka.actor.ActorSystem

object System {
  val systemName = "system"
  val system = ActorSystem(systemName)
}

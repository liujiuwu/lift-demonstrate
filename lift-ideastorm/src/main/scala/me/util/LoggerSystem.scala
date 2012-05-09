package me.yangbajing
package util

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

object LoggerSystem {
  val systemName = "logger-system"
  private lazy val system = ActorSystem(systemName)

  private lazy val _main = system.actorOf(Props[LoggerDispatcher], "logger-dispatcher")

  def main = _main

  class LoggerDispatcher extends Actor {

    def receive = {
      case log: Log =>
        context.children.foreach(_ ! log)
    }
  }

}

case class LoggerAddListener(listener: Actor)
case class LoggerRemoveListener(listener: Actor)


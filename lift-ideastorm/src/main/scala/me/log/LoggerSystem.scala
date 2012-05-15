package me.yangbajing
package log

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

object LoggerSystem {
  val systemName = "logger-system"
  private lazy val system = ActorSystem(systemName)

  private lazy val _main = system.actorOf(Props[LoggerSystem], "logger-system")

  def main = _main

}

private class LoggerSystem extends Actor {
  private var subscribes: Set[ActorRef] = Set()

  def receive = {
    case log: Log =>
      context.children.foreach(_ ! log)

    case LoggerSubscribe(sub) =>
      subscribes += sub

    case LoggerUnsbuscribe(sub) =>
      subscribes -= sub
  }

  override def preStart() = {
  }

  override def postStop() = {
    subscribes.foreach(_ ! LoggerSystemStop)

    
  }
}

case class LoggerSubscribe(sub: ActorRef)
case class LoggerUnsbuscribe(sub: ActorRef)
case object LoggerSystemStop


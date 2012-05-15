package me.yangbajing
package log

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

case class LoggerSubscribe(sub: ActorRef)
case class LoggerUnsbuscribe(sub: ActorRef)
case object LoggerSystemStop
case object LoggerStop

object LoggerSystem {
  val systemName = "logger-system"
  private[log] lazy val system = ActorSystem(systemName)

  private lazy val _main = system.actorOf(Props[LoggerSystem], "logger-system")

  def is = _main
}

private class LoggerSystem extends Actor {
  private var subscribes: Set[ActorRef] = Set()

  def receive = {
    case log: Log =>
      subscribes.foreach(_ ! log)

    case LoggerSubscribe(sub) =>
      subscribes += sub

    case LoggerUnsbuscribe(sub) =>
      subscribes -= sub

  }

  override def preStart() = {
    println(self + " start...")
  }

  override def postStop() = {
    println(self + " stop...")
    subscribes.foreach(_ ! LoggerSystemStop) // TODO need?
  }
}

package me.yangbajing
package log

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

case class LoggerSubscribe(sub: ActorRef, levels: List[Level] = Nil) // levels: Nil 订阅所有日志级别
case class LoggerUnsbuscribe(sub: ActorRef, levels: List[Level] = Nil) // levels: Nil 取消订阅所有日志级别
case object LoggerSystemStop
case object LoggerStop

object LoggerSystem {
  val systemName = "logger-system"
  private[log] lazy val system = ActorSystem(systemName)

  private lazy val _main = system.actorOf(Props[LoggerSystem], "logger-system")

  def is = _main
}

private class LoggerSystem extends Actor {
  private var errors: Set[ActorRef] = Set()
  private var warnings: Set[ActorRef] = Set()
  private var successes: Set[ActorRef] = Set()
  private var infos: Set[ActorRef] = Set()
  private var debugs: Set[ActorRef] = Set()
  private var traces: Set[ActorRef] = Set()

  def receive = {
    case log: Log =>
      routerLog(log)

    case LoggerSubscribe(sub, Nil) =>
      allLevel.foreach(subscribe(sub, _))

    case LoggerUnsbuscribe(sub, Nil) =>
      allLevel.foreach(unsubscribe(sub, _))

    case LoggerSubscribe(sub, levels) =>
      levels.foreach(subscribe(sub, _))

    case LoggerUnsbuscribe(sub, levels) =>
      levels.foreach(unsubscribe(sub, _))

  }

  import me.yangbajing.log.Level._
  private val allLevel = ERROR :: WARN :: SUCCESS :: INFO :: DEBUG :: TRACE :: Nil

  private def routerLog(log: Log) = log.l match {
    case ERROR => errors.foreach(_ ! log)
    case WARN => warnings.foreach(_ ! log)
    case SUCCESS => successes.foreach(_ ! log)
    case INFO => infos.foreach(_ ! log)
    case DEBUG => debugs.foreach(_ ! log)
    case TRACE => traces.foreach(_ ! log)
    case _ =>
  }

  private def subscribe(sub: ActorRef, level: Level) = level match {
    case ERROR => errors += sub
    case WARN => warnings += sub
    case SUCCESS => successes += sub
    case INFO => infos += sub
    case DEBUG => debugs += sub
    case TRACE => traces += sub
    case _ =>
  }

  private def unsubscribe(sub: ActorRef, level: Level) = level match {
    case ERROR => errors -= sub
    case WARN => warnings -= sub
    case SUCCESS => successes -= sub
    case INFO => infos -= sub
    case DEBUG => debugs -= sub
    case TRACE => traces -= sub
    case _ =>
  }

  override def preStart() = {
    println(self + " start...")
  }

  override def postStop() = {
    println(self + " stop...")

    // TODO need?
    errors.foreach(_ ! LoggerSystemStop)
    warnings.foreach(_ ! LoggerSystemStop)
    successes.foreach(_ ! LoggerSystemStop)
    infos.foreach(_ ! LoggerSystemStop)
    debugs.foreach(_ ! LoggerSystemStop)
    traces.foreach(_ ! LoggerSystemStop)
  }
}

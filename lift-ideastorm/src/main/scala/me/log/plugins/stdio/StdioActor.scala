package me.yangbajing
package log
package plugins.stdio

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

private[log] class StdioActor extends Actor {
  var out: java.io.PrintStream = null

  def receive = {
    case log: Log if out ne null =>
      out.println("[%s] %s" format (self, log))

    case LoggerStop =>
      context stop self

    case o: java.io.OutputStream =>
      out = new java.io.PrintStream(o)
  }

  override def preStart() {
    LoggerSystem.is ! LoggerSubscribe(self)
    println("%s start" format self)
  }

  override def postStop() {
    println("%s stop" format self)
    LoggerSystem.is ! LoggerUnsbuscribe(self)
  }
}


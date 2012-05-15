package me.yangbajing
package log
package plugins.mongodb

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

class MongoActor extends Actor {
  def receive = {
    case log: Log =>
      // write database
      // or 继续分发
      println("[MongoActor] " + log)

    case LoggerStop =>
      context stop self
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


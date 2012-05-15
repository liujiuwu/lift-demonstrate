package me.yangbajing
package log
package plugins.mongodb

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

class MongoActor extends Actor {
  println(this + " kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkk ")

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
  }

  override def postStop() {
    LoggerSystem.is ! LoggerUnsbuscribe(self)
  }
}


package me.yangbajing
package log
package plugins.mongodb

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

private[log] class MongoActor extends Actor {
  var mongo: MongoLog = null

  def receive = {
    case log: Log =>
      mongo += log

    case LoggerStop =>
      context stop self

    case MongoConnUri(host, port, db, collection, username, password) =>
      mongo = new MongoLog(host, port, db, collection, username, password)
      
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


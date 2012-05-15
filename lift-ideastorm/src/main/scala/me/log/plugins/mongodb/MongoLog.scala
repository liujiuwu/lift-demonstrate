package me.yangbajing
package log
package plugins.mongodb

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

class MongoActor extends Actor {
  def receive = {
    case log: Log =>
      
  }
}


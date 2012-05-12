package learn.service

import java.util.Date

import scala.xml.{ NodeSeq, Text }

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

import net.liftweb.actor.LiftActor
import net.liftweb.util.Helpers

import learn.model.Account
import me.yangbajing.util.Utils._

object System {
  val systemName = "system"
  lazy val system = ActorSystem(systemName)

}

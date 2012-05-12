package learn.service

import java.util.Date

import scala.xml.{ NodeSeq, Text }

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }
import akka.actor.Terminated

import net.liftweb.actor.LiftActor
import net.liftweb.util.Helpers

import learn.model.Account
import me.yangbajing.util.Utils._

object ContextSystem {
  private lazy val _main = System.system.actorOf(Props[ContextDispatcher], "context-dispatcher")

  var onlineAccountIds: Set[String] = Set()

  def main = _main

  def shutdown() {
    _main ! Terminated
  }

  class ContextDispatcher extends Actor {
    def receive = {
      case AccountLogin(accountId) =>
	onlineAccountIds += accountId
      case AccountLogout(accountId) =>
	onlineAccountIds -= accountId
    }
  }
}

case class AccountLogin(accountId: String)
case class AccountLogout(accountId: String)

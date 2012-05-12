package learn.service

import java.util.Date

import scala.xml.{ NodeSeq, Text }

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }
import akka.actor.Terminated

import net.liftweb.actor.LiftActor
import net.liftweb.util.Helpers

import learn.model.Account
import me.yangbajing.util.Utils._

object IMSystem {
  private lazy val _main = System.system.actorOf(Props[IMDispatcher], "im-dispatcher")

  def shutdown() {
    _main ! Terminated
  }

  def main = _main

  var registerAccountIds: Set[String] = Set()

  class IMDispatcher extends Actor {
    private var listeners: List[LiftActor] = Nil
    private var lines: List[MessageLine] = Nil

    def receive = {
      case line @ MessageLine(liftActor, account, msg, when) =>
        lines = line :: lines.take(49)
        listeners foreach (_ ! MessageLines(self, lines.head :: Nil))

      case MessageRegisterListener(liftActor, accountId) =>
        listeners ::= liftActor
        registerAccountIds += accountId
        liftActor ! MessageLines(self, MessageLine(liftActor, accountId, Text("欢迎来到IM系统"), Helpers.timeNow) :: Nil)
        liftActor ! MessageLines(self, lines take 15)
	println("MessageRegisterListener liftaccount: %s, account: %s\n\n" format (liftActor, accountId))

      case MessageRemoveListener(liftActor, accountId) =>
        listeners = listeners.filterNot(_ eq liftActor)
        registerAccountIds -= accountId
	println("MessageRemoveListener liftaccount: %s, account: %s\n\n" format (liftActor, accountId))

    }
  }

}

// 从lift comet 过来的消息
case class MessageLine(liftActor: LiftActor, accountId: String, msg: NodeSeq, when: Date)

case class MessageRegisterListener(liftActor: LiftActor, accountId: String)

case class MessageRemoveListener(liftActor: LiftActor, accountId: String)

case class MessageLines(imActor: ActorRef, lines: List[MessageLine]) // 发送给lift comet 的消息

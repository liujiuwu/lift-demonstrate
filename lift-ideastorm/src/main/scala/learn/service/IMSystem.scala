package learn.service

import java.util.Date

import scala.xml.{ NodeSeq, Text }

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

import net.liftweb.actor.LiftActor
import net.liftweb.util.Helpers

import learn.model.Account
import me.yangbajing.util.Utils._

object IMSystem {
  val systemName = "im-system"
  private lazy val system = ActorSystem(systemName)

  private lazy val _main = system.actorOf(Props[IMDispatcher], "im-dispatcher")

  def shutdown() {
    system.shutdown()
  }

  def main = _main

  var registerAccounts: Set[Account] = Set()

  class IMDispatcher extends Actor {
    private var listeners: List[LiftActor] = Nil
    private var lines: List[MessageLine] = Nil

    def receive = {
      case line @ MessageLine(liftActor, account, msg, when) =>
        lines = line :: lines.take(49)
        listeners foreach (_ ! MessageLines(self, lines.head :: Nil))

      case MessageRegisterListener(liftActor, account) =>
        listeners ::= liftActor
        registerAccounts += account
        liftActor ! MessageLines(self, MessageLine(liftActor, account, Text("欢迎来到IM系统"), Helpers.timeNow) :: Nil)
        liftActor ! MessageLines(self, lines take 15)

      case MessageRemoveListener(liftActor, account) =>
        listeners = listeners.filterNot(_ eq liftActor)
        registerAccounts -= account

    }
  }

}

// 从lift comet 过来的消息
case class MessageLine(liftActor: LiftActor, account: Account, msg: NodeSeq, when: Date)

case class MessageRegisterListener(liftActor: LiftActor, account: Account)

case class MessageRemoveListener(liftActor: LiftActor, account: Account)

case class MessageLines(imActor: ActorRef, lines: List[MessageLine]) // 发送给lift comet 的消息

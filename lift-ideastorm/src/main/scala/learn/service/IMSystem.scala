package learn.service

import java.util.Date

import scala.xml.{ NodeSeq, Text }

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

import net.liftweb.actor.LiftActor
import net.liftweb.util.Helpers

import me.yangbajing.util.Utils._

object IMSystem {
  val systemName = "im-system"
  private lazy val system = ActorSystem(systemName)

  private lazy val _main = system.actorOf(Props[IMDispatcher], "im-dispatcher")

  def shutdown() {
    system.shutdown()
  }

  def main = _main

  class IMDispatcher extends Actor {
    private var listeners: List[LiftActor] = Nil
    private var lines: List[MessageLine] = Nil

    def receive = {
      case line @ MessageLine(liftActor, user, msg, when) =>
        lines = line :: lines.take(49)
        listeners foreach (_ ! MessageLines(self, lines.head :: Nil))

      case MessageRegisterListener(liftActor, user) =>
        listeners ::= liftActor
        liftActor ! MessageLines(self, MessageLine(liftActor, user, Text("欢迎来到IM系统"), Helpers.timeNow) :: Nil)
        liftActor ! MessageLines(self, lines take 15)

      case MessageRemoveListener(liftActor) =>
        listeners = listeners.filterNot(_ eq liftActor)

    }
  }

}

// 从lift comet 过来的消息
case class MessageLine(liftActor: LiftActor, user: String, msg: NodeSeq, when: Date)

case class MessageRegisterListener(liftActor: LiftActor, user: String)

case class MessageRemoveListener(liftActor: LiftActor)

case class MessageLines(imActor: ActorRef, lines: List[MessageLine]) // 发送给lift comet 的消息

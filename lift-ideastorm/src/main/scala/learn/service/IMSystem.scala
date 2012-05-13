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

  class IMDispatcher extends Actor {
    private var lines: List[MessageLine] = Nil
    private var listenerMap: Map[String, LiftActor] = Map[String, LiftActor]()

    def receive = {
      case line @ MessageLine(fromLiftActor, fromId, toId, msg, when) =>
        lines = line :: lines.take(200)

        for (toComet <- listenerMap.get(toId)) {
          toComet ! MessageLines(self, line :: Nil)
          println("\nMessageLine: %s\n" format line)
        }

      case MessageRegisterListener(liftActor, accountId) =>
        listenerMap += accountId -> liftActor

        liftActor ! MessageLines(self, MessageLine(liftActor, "systemId", accountId, Text("欢迎来到IM系统"), Helpers.timeNow) :: Nil)
        liftActor ! MessageLines(self, lines.filter(_.toId == accountId).take(15))

        println("MessageRegisterListener liftaccount: %s, account: %s\n\n" format (liftActor, accountId))

      case MessageRemoveListener(liftActor, accountId) =>
        listenerMap -= accountId

        println("MessageRemoveListener liftaccount: %s, account: %s\n\n" format (liftActor, accountId))

    }
  }

}

// 从lift comet 过来的消息
case class MessageLine(liftActor: LiftActor, fromId: String, toId: String, msg: NodeSeq, when: Date)

case class MessageRegisterListener(liftActor: LiftActor, accountId: String)

case class MessageRemoveListener(liftActor: LiftActor, accountId: String)

case class MessageLines(imActor: ActorRef, lines: List[MessageLine]) // 发送给lift comet 的消息

package learn.service

import java.util.Date

import scala.xml.{ NodeSeq, Text }

import akka.actor.{ Actor, ActorSystem, Props, ActorRef, Cancellable }
import akka.actor.Terminated
import akka.util.duration._

import net.liftweb.common.{ Box, Empty, Full, Failure }
import net.liftweb.actor.LiftActor

import learn.model.Account
import me.yangbajing.util.Utils._

case object RefreshOnlineStatus

case class OnlineAccountIds(accountIds: List[String])

case class AccountLogin(accountId: String)
case class AccountLogout(accountId: String)

case class LiftActorRegisterListener(liftActor: LiftActor, accountId: Box[String])
case class LiftActorRemoveListener(liftActor: LiftActor, accountId: Box[String])
case class CometStatus(liftActor: LiftActor, accountId: Box[String])

object ContextSystem {
  @volatile
  var onlineAccountIds: Set[String] = Set()

  

  object s {
    lazy val context = {
      System.system.actorOf(Props[ContextDispatcher], "context-dispatcher")
    }
  }

  def shutdown() {
    s.context ! Terminated
  }

  class ContextDispatcher extends Actor {
    @volatile private var listeners = Set[LiftActor]()

    def receive = {
      case RefreshOnlineStatus =>
        listeners = for (listener <- listeners if listener ne null) yield {
          listener ! OnlineAccountIds(onlineAccountIds.toList)
          listener
        }

      case a @ AccountLogin(accountId) =>
        onlineAccountIds += accountId
        self ! RefreshOnlineStatus

        listeners = for (listener <- listeners if listener ne null) yield {
          listener ! a
          listener
        }

      case a @ AccountLogout(accountId) =>
        onlineAccountIds -= accountId
        self ! RefreshOnlineStatus

        listeners = for (listener <- listeners if listener ne null) yield {
          listener ! a
          listener
        }

      case a @ LiftActorRegisterListener(liftActor, accountIdBox) =>
        listeners += liftActor
        for (accountId <- accountIdBox if !onlineAccountIds.contains(accountId)) {
          onlineAccountIds += accountId
          self ! RefreshOnlineStatus
        }

      case a @ LiftActorRemoveListener(liftActor, accountIdBox) =>
        listeners -= liftActor
        for (accountId <- accountIdBox if onlineAccountIds.contains(accountId)) {
          onlineAccountIds -= accountId
          self ! RefreshOnlineStatus
        }

      case CometStatus(liftActor, Full(accountId)) =>
        onlineAccountIds += accountId
        self ! RefreshOnlineStatus

    }

    var schedule: Option[Cancellable] = None
    override def preStart() {
      schedule = Full(System.system.scheduler.schedule(15 seconds, 15 seconds) {
        self ! RefreshOnlineStatus
      })
    }

    override def postStop() {
      for (s <- schedule if !s.isCancelled) s.cancel()
      schedule = None
    }
  }

}

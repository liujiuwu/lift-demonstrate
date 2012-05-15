package me.yangbajing
package log

import java.util.Date

import me.yangbajing.util.Utils

/*sealed*/ abstract class Level(val code: Int)
object Level {
  case object ERROR extends Level(100)
  case object WARN extends Level(200)
  case object SUCCESS extends Level(300)
  case object INFO extends Level(400)
  case object DEBUG extends Level(500)
  case object TRACE extends Level(600)
}

case class Log(
  val l: Level, // 日志级别
  val c: String, // 产生日志的类
  val t: String, // 产生日志时所在线程
  val m: String, // 日志内容
  val e: Option[Throwable], // 日志产生时的异常
  val k: Option[String], // 日志关键字
  val d: Date = new Date) {

  override def toString = {
    "%s level:[%s] class:[%s] thread:[%s], except:[%s], key:[%s], msg:\n%s" format (Utils.dateIso.format(d), l, c, t, e, k, m)
  }
}

import plugins.mongodb.MongoActor

object Logger {
  def apply(clazz: Class[_]): Logger = apply(clazz.getName)
  def apply(className: String): Logger = new DefaultLogger(className)

  object rules extends LoggerRules

  def start() {
    LoggerSystem.is ! "" // 启动LoggerSystem
  }

  def stop() {
    LoggerSystem.system.shutdown()
  }

  object plugins {
    import akka.actor.{ Props, ActorRef }

    private var mongoActor: ActorRef = null
    def mongodbStart() {
      mongoActor = LoggerSystem.system.actorOf(Props[MongoActor], "logger-plugins-mongodb")
    }

    def mongodbStop() {
      if (mongoActor ne null)
        mongoActor ! LoggerStop
    }
  }
}

trait Logger {
  def className: String

  def logging(level: Level, clazz: String, key: String, msg: => AnyRef, t: => Throwable)

  def error(msg: => AnyRef, t: => Throwable)(implicit key: String) {
    logging(Level.ERROR, className, key, msg, t)
  }
  def error(msg: => AnyRef)(implicit key: String) {
    logging(Level.ERROR, className, key, msg, null)
  }

  def warn(msg: => AnyRef, t: => Throwable)(implicit key: String) {
    logging(Level.WARN, className, key, msg, t)
  }
  def warn(msg: => AnyRef)(implicit key: String) {
    logging(Level.WARN, className, key, msg, null)
  }

  def info(msg: => AnyRef, t: => Throwable)(implicit key: String) {
    logging(Level.INFO, className, key, msg, t)
  }
  def info(msg: => AnyRef)(implicit key: String) {
    logging(Level.INFO, className, key, msg, null)
  }

  def debug(msg: => AnyRef, t: => Throwable)(implicit key: String) {
    logging(Level.DEBUG, className, key, msg, t)
  }
  def debug(msg: => AnyRef)(implicit key: String) {
    logging(Level.DEBUG, className, key, msg, null)
  }

  def trace(msg: => AnyRef, t: => Throwable)(implicit key: String) {
    logging(Level.TRACE, className, key, msg, t)
  }
  def trace(msg: => AnyRef)(implicit key: String) {
    logging(Level.TRACE, className, key, msg, null)
  }
}

class DefaultLogger(val className: String) extends Logger {
  import me.yangbajing.util.Utils._
  import Logger.rules._

  def logging(level: Level, clazz: String, key: String, msg: => AnyRef, e: => Throwable) {
    def sendLog() {
      LoggerSystem.is ! Log(
        l = level,
        c = if (clazz eq null) "" else clazz,
        t = Thread.currentThread.getName,
        m = tryout(msg.toString).getOrElse(""),
        e = optionNull(e),
        k = optionNull(key))
    }

    level match {
      case Level.ERROR if enableError => sendLog
      case Level.WARN if enableWarn => sendLog
      case Level.SUCCESS if enableSuccess => sendLog
      case Level.INFO if enableInfo => sendLog
      case Level.DEBUG if enableDebug => sendLog
      case Level.TRACE if enableTrace => sendLog
    }
  }
}

trait LoggerRules {
  @volatile var enableError = true
  @volatile var enableWarn = true
  @volatile var enableSuccess = true
  @volatile var enableInfo = true
  @volatile var enableDebug = false
  @volatile var enableTrace = false
}

trait Loggable {
  @transient protected lazy val logger = Logger(this.getClass)
  @transient protected implicit val _defaultLoggerKey: String = this.getClass.getSimpleName
}

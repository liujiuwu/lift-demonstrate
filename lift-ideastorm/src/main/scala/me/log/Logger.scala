package me.yangbajing
package log

import java.util.Date

sealed abstract class Level(val code: Int)
object Level {
  case object ERROR extends Level(100)
  case object WARN extends Level(200)
  case object INFO extends Level(300)
  case object DEBUG extends Level(400)
  case object TRACE extends Level(500)
}

case class Log(
  val l: Level, // 日志级别
  val c: String, // 产生日志的类
  val t: String, // 产生日志时所在线程
  val m: String, // 日志内容
  val e: Option[Throwable], // 日志产生时的异常
  val k: Option[String], // 日志关键字
  val d: Date = new Date)

object Logger {
  def apply(clazz: Class[_]): Logger = apply(clazz.getName)
  def apply(className: String): Logger = new DefaultLogger(className)

  object rules extends LoggerRules
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
  import Level._
  import Logger.rules._

  def logging(level: Level, clazz: String, key: String, msg: => AnyRef, e: => Throwable) {
    def sendLog() {
      LoggerSystem.main ! Log(
        l = level,
        c = if (clazz eq null) "" else clazz,
        t = Thread.currentThread.getName,
        m = tryout(msg.toString).getOrElse(""),
        e = optionNull(e),
        k = optionNull(key))
    }

    level match {
      case ERROR if enableError => sendLog
      case WARN if enableWarn => sendLog
      case INFO if enableInfo => sendLog
      case DEBUG if enableDebug => sendLog
      case TRACE if enableTrace => sendLog
    }
  }
}

trait LoggerRules {
  var enableError = true
  var enableWarn = true
  var enableInfo = true
  var enableDebug = false
  var enableTrace = false
}

trait Loggable {
  @transient protected lazy val logger = Logger(this.getClass)
  @transient protected implicit val _defaultLoggerKey: String = this.getClass.getSimpleName
}

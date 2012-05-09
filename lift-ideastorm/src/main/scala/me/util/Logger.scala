package me.yangbajing
package util

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
  val m: String, // 日志内容
  val t: Option[Throwable], // 日志产生时的异常
  val k: Option[String], // 日志关键字
  val d: Date = new Date
  )

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
  import Utils._
  import Level._
  import Logger.rules._

  def logging(level: Level, clazz: String, key: String, msg: => AnyRef, t: => Throwable) {
    def sendLog() {
      LoggerSystem.main ! Log(level,
        if (clazz eq null) "" else clazz,
        tryout(msg.toString).getOrElse(""),
        optionNull(t),
        optionNull(key))
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
  @transient protected implicit val implictKey: String = this.getClass.getSimpleName
}

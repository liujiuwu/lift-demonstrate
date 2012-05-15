package me.yangbajing
package log

import java.util.Date

import me.yangbajing.util.Utils

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

  def toMap: Map[String, AnyRef] = {
    val map = scala.collection.mutable.Map[String, AnyRef]("l" -> l.toString, "c" -> c, "t" -> t, "m" -> m, "d" -> d)
    e.foreach(map.put("e", _))
    k.foreach(map.put("k", _))
    map.toMap
  }
}

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
    import me.yangbajing.log.plugins.mongodb.{ MongoActor, MongoConnUri, MongoLog }
    import me.yangbajing.log.plugins.stdio.StdioActor

    private var stdioActor: ActorRef = null
    def stdioStart(out: java.io.OutputStream) {
      stdioActor = LoggerSystem.system.actorOf(Props[StdioActor], "logger-plugin-stdio")
      stdioActor ! out
    }
    def stdioStop() {
      if (stdioActor ne null)
        stdioActor ! LoggerStop
    }

    private var mongoActor: ActorRef = null
    def mongodbStart(host: String, port: Int = 27017, db: String = "app_log", collection: String = "app_log", username: Option[String] = None, password: Option[String] = None) {
      mongoActor = LoggerSystem.system.actorOf(Props[MongoActor], "logger-plugin-mongodb")
      mongoActor ! MongoConnUri(host, port, db, collection, username, password)
    }
    def mongodbStop() {
      if (mongoActor ne null) {
        mongoActor ! LoggerStop
        mongoActor match {
          case m: MongoActor if m.mongo ne null => m.mongo.conn.close
          case _ =>
        }
      }
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
      case Level.ERROR /*if enableError*/ => sendLog
      case Level.WARN /*if enableWarn*/ => sendLog
      case Level.SUCCESS if enableSuccess => sendLog
      case Level.INFO if enableInfo => sendLog
      case Level.DEBUG if enableDebug => sendLog
      case Level.TRACE if enableTrace => sendLog
      case _ =>
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
  @transient protected val logger = Logger(this.getClass)
  @transient protected implicit val _defaultLoggerKey: String = ""
}

trait LoggableLazy {
  @transient protected lazy val logger = Logger(this.getClass)
  @transient protected implicit val _defaultLoggerKey: String = ""
}


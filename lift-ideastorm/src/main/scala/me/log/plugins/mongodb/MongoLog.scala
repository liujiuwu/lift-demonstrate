package me.yangbajing
package log
package plugins.mongodb

import akka.actor.{ Actor, ActorSystem, Props, ActorRef }

import com.mongodb.casbah.commons.conversions.scala._
import com.mongodb.casbah.query._
import com.mongodb.casbah._

private[log] case class MongoConnUri(host: String, port: Int, db: String, collection: String, username: Option[String] = None, password: Option[String] = None)

private[log] class MongoLog(
  val mongoHost: String,
  val mongoPort: Int,
  val mongoDb: String,
  val mongoCollection: String,
  val mongoUsername: Option[String] = None,
  val mongoPassword: Option[String] = None) {

  val conn = {
    val uri = for {
      username <- mongoUsername
      password <- mongoPassword
    } yield "mongodb://%s:%s@%s:%d/" format (username, password, mongoHost, mongoPort)

    MongoConnection(MongoURI(uri getOrElse "mongodb://%s:%d".format(mongoHost, mongoPort)))
  }

  val collection = conn(mongoDb)(mongoCollection)

  def +=(log: Log) {
    collection += log.toMap
  }
}


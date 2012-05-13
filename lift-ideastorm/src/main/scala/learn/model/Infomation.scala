package learn.model

import java.util.{ Date, GregorianCalendar }

import net.liftweb.common.Box

import me.yangbajing.KV

object Infomation {

  def apply() = new InfomationImpl(InfomationRecord.createRecord)

}

class InfomationImpl(record: InfomationRecord) {
  def id: String = record._id.is.toString

  def setAuthors(accountId: String*) = {
    record.authors(accountId.mkString(","))
  }
  def authors: List[String] = record.authors.is.split(',').toList

  def createdAt: Date = record.createdAt.is.getTime
  def updatedAt: Date = record.updatedAt.is.getTime

  def setMain(name: String, kv: KV[String, String]*) = {
    record.main(JsonDataLine(name, kv.toList))
    this
  }
  def main: JsonDataLine = record.main.is

  val groups = scala.collection.mutable.ListBuffer[JsonDataGroup]()
  def addGroup(name: String, line: JsonDataLine*) = {
    groups append JsonDataGroup(name, line.toList)
    this
  }
  def body: JsonDataBody = record.body.is

  def save() = { // 调用后对象数据才完整
    record.body(JsonDataBody(groups.toList))
    record.save
    this
  }

  def immutable = {
    Infomation(id, authors, createdAt, updatedAt, main, body.groups)
  }
}

case class Infomation(id: String, authors: List[String], createdAt: Date, updatedAt: Date, main: JsonDataLine, body: List[JsonDataGroup])

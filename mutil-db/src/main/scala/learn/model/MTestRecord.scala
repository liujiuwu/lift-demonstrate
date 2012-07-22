package learn.model

import net.liftweb.record.field._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.mongodb._

import bootstrap.liftweb.IdentifierFactory

object MTestRecord {
  def apply(name: String) = instances(name).Record

  var instances =
    Map("xxxxx" -> new MTestRecordWrapper(IdentifierFactory("xxxxx")),
      "nnnnn" -> new MTestRecordWrapper(IdentifierFactory("nnnnn")))
}

class MTestRecordWrapper(_identifier: MongoIdentifier) {
  object Record extends Record with MongoMetaRecord[Record] {
    override val collectionName = "m_test"
    override def mongoIdentifier = _identifier
    override def instantiateRecord = new Record
  }

  class Record extends MongoRecord[Record] with MongoId[Record] {
    def meta = Record

    object createdAt extends DateTimeField(this) // 创建时间
    object updatedAt extends DateTimeField(this) // 最后更新时间
  }
}

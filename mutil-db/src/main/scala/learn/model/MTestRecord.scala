package learn.model

import net.liftweb.record.field._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field._
import net.liftweb.mongodb._

object MTestRecord {
  def apply() = new MTestRecord(DefaultMongoIdentifier)

  def apply(identifier: MongoIdentifier) = new MTestRecord(identifier)
}

class MTestRecord(identifier: MongoIdentifier)
  extends MongoRecord[MTestRecord]
  with MongoId[MTestRecord]
  with MongoMetaRecord[MTestRecord] {
  
  def meta = this

  override val collectionName = "m_test"
  override def mongoIdentifier = identifier

  object createdAt extends DateTimeField(this) // 创建时间
  object updatedAt extends DateTimeField(this) // 最后更新时间
}


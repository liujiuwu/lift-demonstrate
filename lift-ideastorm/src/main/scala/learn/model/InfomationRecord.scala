package learn.model

import net.liftweb.record.field._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field.{ JsonObjectField, ObjectIdField }

object InfomationRecord extends InfomationRecord with MongoMetaRecord[InfomationRecord] {
  override val collectionName = "information"
}

class InfomationRecord extends MongoRecord[InfomationRecord] with MongoId[InfomationRecord] {
  def meta = InfomationRecord

  object main extends JsonObjectField[InfomationRecord, JsonDataLine](this, JsonDataLine) {
    def defaultValue = new JsonDataLine("main", Nil)
  }

  object body extends JsonObjectField[InfomationRecord, JsonDataBody](this, JsonDataBody) {
    def defaultValue = new JsonDataBody(Nil)
  }

  object authors extends StringField(this, "") // 每个作者ID以','分隔(不包括两边的单引号)
  object readers extends StringField(this, "") // 每个读者ID以','分隔(不包括两边的单引号)

  // 是否需要reader确认
  object usedFlow extends BooleanField(this, false)

  object createdAt extends DateTimeField(this)
  object updatedAt extends DateTimeField(this)

  override def save = {
    updatedAt(new java.util.GregorianCalendar)
    super.save
  }
}

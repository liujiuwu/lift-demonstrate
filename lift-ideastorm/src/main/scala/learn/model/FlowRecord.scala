package learn.model

import net.liftweb.record.field._
import net.liftweb.mongodb.record._
import net.liftweb.mongodb.record.field.{ JsonObjectField, ObjectIdField }

object FlowRecord extends FlowRecord with MongoMetaRecord[FlowRecord] {
  override val collectionName = "flow"
}

class FlowRecord extends MongoRecord[FlowRecord] with MongoId[FlowRecord] {
  def meta = FlowRecord

  object infomationId extends ObjectIdField(this) // InfomationRecord.id

  object process extends JsonObjectField[FlowRecord, JsonDataGroup](this, JsonDataGroup) {
    def defaultValue = JsonDataGroup("", Nil)
  }
}

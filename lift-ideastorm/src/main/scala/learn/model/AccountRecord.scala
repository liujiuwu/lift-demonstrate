package learn.model

import net.liftweb.record.field._
import net.liftweb.mongodb.record.field.{ MongoPasswordField, JsonObjectField, ObjectIdField }
import net.liftweb.mongodb.record.{
  MongoRecord,
  MongoMetaRecord,
  MongoId
}
import java.util.Locale
import java.util.TimeZone

object AccountRecord extends AccountRecord with MongoMetaRecord[AccountRecord] {
  override val collectionName = "account"
}

class AccountRecord private () extends MongoRecord[AccountRecord]
  with MongoId[AccountRecord] {
  def meta = AccountRecord

  object username extends StringField(this, "")
  object email extends EmailField(this, 64)
  object usertype extends IntField(this, -1) // -1 代码当前账户未启用
  object password extends MongoPasswordField(this)

  object token extends StringField(this, "")

  object age extends IntField(this)
  object locale extends LocaleField(this /*, Locale.SIMPLIFIED_CHINESE*/ )
  object timeZone extends TimeZoneField(this)

  object createdAt extends DateTimeField(this)
  object updatedAt extends DateTimeField(this)

  object unreadInfomationIds extends JsonObjectField[AccountRecord, JsonDataList](this, JsonDataList) {
    def defaultValue = JsonDataList(Nil)
  }

  override def save = {
    updatedAt(java.util.Calendar.getInstance(timeZone.isAsTimeZone))
    super.save
  }
}

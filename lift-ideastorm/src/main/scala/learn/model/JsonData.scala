package learn.model

import net.liftweb.mongodb.{ JsonObject, JsonObjectMeta }

import me.yangbajing._
import me.yangbajing.util.Utils._

case class JsonDataList(list: List[String]) extends JsonObject[JsonDataList] {
  def meta = JsonDataList
}
object JsonDataList extends JsonObjectMeta[JsonDataList]

case class JsonDataBody(groups: List[JsonDataGroup]) extends JsonObject[JsonDataBody] {
  def meta = JsonDataBody
}
object JsonDataBody extends JsonObjectMeta[JsonDataBody]

/**
 * 因确保每个文档内部的JsonDataGroup.name 不会重复。
 */
case class JsonDataGroup(name: String, rows: List[JsonDataLine]) extends JsonObject[JsonDataGroup] {
  def meta = JsonDataGroup
}
object JsonDataGroup extends JsonObjectMeta[JsonDataGroup]

case class JsonDataLine(name: String, columns: List[KV[String, String]]) extends JsonObject[JsonDataLine] {
  def meta = JsonDataLine
}
object JsonDataLine extends JsonObjectMeta[JsonDataLine] {
  def apply(name: String, kv: KV[String, String]*): JsonDataLine = JsonDataLine(name, kv.toList)
}

case class JsonDataFlow(list: List[KV[String, Int]]) extends JsonObject[JsonDataFlow] {
  def meta = JsonDataFlow
}
object JsonDataFlow extends JsonObjectMeta[JsonDataFlow]

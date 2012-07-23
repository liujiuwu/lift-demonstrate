package learn.model

import net.liftweb.mongodb.{ JsonObject, JsonObjectMeta }

/**
 * Collection元数据，设置各model属性
 */
object JsonDataMetadata extends JsonObjectMeta[JsonDataMetadata] 
case class JsonDataMetadata(
  val name: String,
  val data: Map[String, String] = Map(),
  val sets: Map[String, List[String]] = Map(),
  var metadata: Option[JsonDataMetadata] = None) extends JsonObject[JsonDataMetadata] {

  def meta = JsonDataMetadata
}

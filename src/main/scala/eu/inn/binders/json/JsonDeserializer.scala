package eu.inn.binders.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import eu.inn.binders.core.Deserializer
import eu.inn.binders.naming.Converter

class JsonException(message: String) extends RuntimeException(message)

class JsonDeserializer[C <: Converter] protected (jsonNode: JsonNode) extends Deserializer[C] {

  def this (jsonParser: JsonParser) = this(JsonDeserializer.createJsonNodeFromParser(jsonParser))

  override def hasField(fieldName: String): Boolean = jsonNode.isObject && jsonNode.has(fieldName)

  override def iterator(): Iterator[Deserializer[C]] = {
    import scala.collection.JavaConversions._

    if (jsonNode.isArray)
      jsonNode.iterator().map { e => new JsonDeserializer[C](e)}
    else
      throw new JsonException("Couldn't iterate nonarray field")
  }

  def getString(name: String): String = getObjectField(name).asText()

  def getInt(name: String): Int = getObjectField(name).asInt()

  def getAsString: String = jsonNode.asText()

  def getAsInt: Int = jsonNode.asInt()

  def getFieldDeserializer(name: String): JsonDeserializer[C] = new JsonDeserializer[C](jsonNode.get(name))

  def getFieldOptionDeserializer(name: String): Option[JsonDeserializer[C]] =
    if (jsonNode.hasNonNull(name))
      Some(new JsonDeserializer[C](jsonNode.get(name)))
    else
      None

  protected def getObjectField(name: String) =
    if (jsonNode.isObject && jsonNode.get(name) != null)
      jsonNode.get(name)
    else
      throw new JsonException(s"'$jsonNode' isn't an object (trying to get field '$name'}")
}

object JsonDeserializer {
  protected def createJsonNodeFromParser(jsonParser: JsonParser): JsonNode = {
    val mapper = new ObjectMapper()
    mapper.readTree(jsonParser)
  }
}
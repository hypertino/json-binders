package eu.inn.binders.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import eu.inn.binders.core.Deserializer
import eu.inn.binders.naming.Converter

class JsonException(message: String) extends RuntimeException(message)

class JsonDeserializer[C <: Converter](val jsonParser: JsonParser) extends Deserializer[C] {
  private val jsonNode: JsonNode = {
    val mapper = new ObjectMapper()
    mapper.readTree(jsonParser)
  }
  override def hasField(fieldName: String): Boolean = jsonNode.isObject && jsonNode.has(fieldName)

  override def iterator(): Iterator[Deserializer[C]] = {
    import scala.collection.JavaConversions._

    if (jsonNode.isArray)
      jsonNode.iterator().map { e => new JsonDeserializer[C](e.traverse())} // todo: fix, this is very ineficient
    else
      throw new JsonException("Couldn't iterate nonarray field")
  }

  def getString(name: String): String = jsonNode.get(name).asText()

  def getInt(name: String): Int = jsonNode.get(name).asInt()

  def getAsString: String = jsonNode.asText()

  def getAsInt: Int = jsonNode.asInt()
}

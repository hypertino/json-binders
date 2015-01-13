package eu.inn.binders.json

import java.util.Date

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import eu.inn.binders.core.{FieldNotFoundException, Deserializer}
import eu.inn.binders.json.internal.{JsonMacro}
import eu.inn.binders.naming.Converter
import scala.reflect.runtime.universe._
import scala.language.experimental.macros

class JsonDeserializeException(message: String) extends RuntimeException(message)

class JsonDeserializerBase[C <: Converter, I <: Deserializer[C]] protected (jsonNode: JsonNode, val fieldName: Option[String])
  extends Deserializer[C] {

  def iterator(): Iterator[I] = {
    import scala.collection.JavaConversions._

    if (jsonNode.isArray)
      jsonNode.iterator().map { e => createFieldDeserializer(e, None)}
    else
    if (jsonNode.isObject)
      jsonNode.fields().map {  e =>
      //  println("iterating: " + e)
        createFieldDeserializer(e.getValue, Some(e.getKey))
      }
    else
      throw new JsonDeserializeException("Couldn't iterate nonarray/nonobject field")
  }

  protected def createFieldDeserializer(jsonNode: JsonNode, fieldName: Option[String]): I = ??? //new JsonDeserializer[C](jsonNode, fieldName)

  def isNull: Boolean = jsonNode.isNull()
  def readString(): String = jsonNode.asText()
  def readInt(): Int = jsonNode.asInt()
  def readLong(): Long = jsonNode.asLong()
  def readFloat(): Float = jsonNode.asDouble().toFloat
  def readDouble(): Double = jsonNode.asDouble()
  def readBoolean(): Boolean = jsonNode.asBoolean()
  def readBigDecimal(): BigDecimal = JsonDeserializer.stringToBigDecimal(jsonNode.asText())
  def readDate(): Date = new Date(jsonNode.asLong())
  def readMap[T](): Map[String,T] = macro JsonMacro.readMap[I, T]
}

class JsonDeserializer[C <: Converter] protected (jsonNode: JsonNode, override val fieldName: Option[String])
  extends JsonDeserializerBase[C, JsonDeserializer[C]](jsonNode, fieldName) {
  def this (jsonParser: JsonParser) = this(JsonDeserializer.createJsonNodeFromParser(jsonParser), None)

  protected override def createFieldDeserializer(jsonNode: JsonNode, fieldName: Option[String]): JsonDeserializer[C] = new JsonDeserializer[C](jsonNode, fieldName)
}

object JsonDeserializer {
  def createJsonNodeFromParser(jsonParser: JsonParser): JsonNode = {
    val mapper = new ObjectMapper()
    mapper.readTree(jsonParser)
  }

  def stringToBigDecimal(s: String): BigDecimal ={
    val precision = 150//s.size
    BigDecimal(s, new java.math.MathContext(precision))
  }

//  def apply[C <: Converter : TypeTag](jsonParser: JsonParser) = new JsonDeserializer[C](jsonParser)
}
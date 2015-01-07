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

class JsonDeserializer[C <: Converter] protected (jsonNode: JsonNode, val fieldName: Option[String])
  extends Deserializer[C] {

  def this (jsonParser: JsonParser) = this(JsonDeserializer.createJsonNodeFromParser(jsonParser), None)

  def iterator(): Iterator[JsonDeserializer[C]] = {
    import scala.collection.JavaConversions._

    if (jsonNode.isArray)
      jsonNode.iterator().map { e => new JsonDeserializer[C](e, None)}
    else
    if (jsonNode.isObject)
      jsonNode.fields().map {  e =>
      //  println("iterating: " + e)
        new JsonDeserializer[C](e.getValue, Some(e.getKey))
      }
    else
      throw new JsonDeserializeException("Couldn't iterate nonarray/nonobject field")
  }

  def readString(): String = jsonNode.asText()
  def readStringOption(): Option[String] = getNullable(readString())

  def readInt(): Int = jsonNode.asInt()
  def readIntOption() = getNullable(readInt())

  def readLong(): Long = jsonNode.asLong()
  def readLongOption() = getNullable(readLong())

  def readFloat(): Float = jsonNode.asDouble().toFloat
  def readFloatOption() = getNullable(readFloat())

  def readDouble(): Double = jsonNode.asDouble()
  def readDoubleOption() = getNullable(readDouble())

  def readBoolean(): Boolean = jsonNode.asBoolean()
  def readBooleanOption() = getNullable(readBoolean())

  def readBigDecimal(): BigDecimal = JsonDeserializer.stringToBigDecimal(jsonNode.asText())
  def readBigDecimalOption() = getNullable(readBigDecimal())

  def readDate(): Date = new Date(jsonNode.asLong())
  def readDateOption(): Option[Date] = getNullable(readDate())

  def readMap[T](): Map[String,T] = macro JsonMacro.readMap[JsonDeserializer[C], T]

  protected def getNullable[T](f: => T):Option[T] = {
    if (jsonNode.isNull)
      None
    else
      Some(f)
  }
}

object JsonDeserializer {
  protected def createJsonNodeFromParser(jsonParser: JsonParser): JsonNode = {
    val mapper = new ObjectMapper()
    mapper.readTree(jsonParser)
  }

  protected def stringToBigDecimal(s: String): BigDecimal ={
    val precision = 150//s.size
    BigDecimal(s, new java.math.MathContext(precision))
  }

  def apply[C <: Converter : TypeTag](jsonParser: JsonParser) = new JsonDeserializer[C](jsonParser)
}
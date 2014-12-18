package eu.inn.binders.json

import java.util.Date

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.{JsonNode, ObjectMapper}
import eu.inn.binders.core.{FieldNotFoundException, Deserializer}
import eu.inn.binders.json.internal.{JsonMacro, ISO8601}
import eu.inn.binders.naming.Converter
import scala.reflect.runtime.universe._
import scala.language.experimental.macros

class JsonDeserializeException(message: String) extends RuntimeException(message)

class JsonDeserializer[C <: Converter] protected (jsonNode: JsonNode, fieldName: String) extends Deserializer[C] {

  def this (jsonParser: JsonParser) = this(JsonDeserializer.createJsonNodeFromParser(jsonParser), "<root>")

  override def hasField(fieldName: String): Boolean = jsonNode.isObject && jsonNode.has(fieldName)

  def iterator(): Iterator[JsonDeserializer[C]] = {
    import scala.collection.JavaConversions._

    if (jsonNode.isArray)
      jsonNode.iterator().map { e => new JsonDeserializer[C](e, fieldName + "[]")}
    else
      throw new JsonDeserializeException("Couldn't iterate nonarray field")
  }

  def fieldsIterator(): Iterator[(String, JsonDeserializer[C])] = {
    import scala.collection.JavaConversions._

    if (jsonNode.isObject)
      jsonNode.fields().map {  e =>
        println("iterating: " + e)
        (e.getKey, new JsonDeserializer[C](e.getValue, e.getKey))
      }
    else
      throw new JsonDeserializeException("Couldn't iterate nonobject fields")
  }

  def getString(name: String): String = getObjectField(name).asText()
  def getAsString: String = jsonNode.asText()
  def getAsStringOption: Option[String] = getNullable(getAsString)

  def getInt(name: String): Int = getObjectField(name).asInt()
  def getAsInt: Int = jsonNode.asInt()
  def getAsIntOption = getNullable(getAsInt)

  def getLong(name: String): Long = getObjectField(name).asLong()
  def getAsLong: Long = jsonNode.asLong()
  def getAsLongOption = getNullable(getAsLong)

  def getFloat(name: String): Float = getObjectField(name).asDouble().toFloat
  def getAsFloat: Float = jsonNode.asDouble().toFloat
  def getAsFloatOption = getNullable(getAsFloat)

  def getDouble(name: String): Double = getObjectField(name).asDouble()
  def getAsDouble: Double = jsonNode.asDouble()
  def getAsDoubleOption = getNullable(getAsDouble)

  def getBoolean(name: String): Boolean = getObjectField(name).asBoolean()
  def getAsBoolean: Boolean = jsonNode.asBoolean()
  def getAsBooleanOption = getNullable(getAsBoolean)

  def getBigDecimal(name: String): BigDecimal = JsonDeserializer.stringToBigDecimal(getObjectField(name).asText())
  def getAsBigDecimal: BigDecimal = JsonDeserializer.stringToBigDecimal(jsonNode.asText())
  def getAsBigDecimalOption = getNullable(getAsBigDecimal)

  def getDate(name: String): Date = ISO8601.fromString(getObjectField(name).asText())
  def getAsDate: Date = ISO8601.fromString(jsonNode.asText())
  def getAsDateOption: Option[Date] = getNullable(ISO8601.fromString(jsonNode.asText()))

  // def getFieldDeserializer(name: String): JsonDeserializer[C] = new JsonDeserializer[C](jsonNode.get(name), name)

  def getMap[T](name: String): Map[String, T] = macro JsonMacro.getMap[T]
  def getAsMap[T]: Map[String, T] = macro JsonMacro.getAsMap[T]
  // def getAsMap[T]: Map[String, T] = macro JsonMacro.getMap[T]

  def getFieldOptionDeserializer(name: String): Option[JsonDeserializer[C]] =
    if (jsonNode.hasNonNull(name))
      Some(new JsonDeserializer[C](jsonNode.get(name), name))
    else
      None

  protected def getObjectField(name: String) =
    if (jsonNode.isObject)
      if (jsonNode.get(name) != null)
        jsonNode.get(name)
      else
        throw new FieldNotFoundException(s"'$jsonNode' has no field '$name'}")
    else
      throw new JsonDeserializeException(s"'$jsonNode' isn't an object (trying to get field '$name'}")

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
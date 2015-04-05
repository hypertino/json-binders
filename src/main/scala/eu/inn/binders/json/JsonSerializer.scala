package eu.inn.binders.json

import java.util.Date

import com.fasterxml.jackson.core.JsonGenerator
import eu.inn.binders.core.Serializer
import eu.inn.binders.json.internal.JsonMacro
import eu.inn.binders.naming.Converter
import scala.language.experimental.macros

class JsonSerializeException(message: String) extends RuntimeException(message)

class JsonSerializerBase[C <: Converter, F <: Serializer[C]] protected (val jsonGenerator: JsonGenerator) extends Serializer[C]{

  def getFieldSerializer(fieldName: String): Option[F] = {
    jsonGenerator.writeFieldName(fieldName)
    Some(createFieldSerializer())
  }

  protected def createFieldSerializer(): F = ???

  def writeNull(): Unit = jsonGenerator.writeNull()
  def writeInteger(value: Int): Unit = jsonGenerator.writeNumber(value)
  def writeLong(value: Long): Unit = jsonGenerator.writeNumber(value)
  def writeString(value: String): Unit = jsonGenerator.writeString(value)
  def writeFloat(value: Float): Unit = jsonGenerator.writeNumber(value)
  def writeDouble(value: Double): Unit = jsonGenerator.writeNumber(value)
  def writeBoolean(value: Boolean): Unit = jsonGenerator.writeBoolean(value)
  def writeBigDecimal(value: BigDecimal): Unit = jsonGenerator.writeNumber(value.bigDecimal)
  def writeDate(value: Date): Unit = jsonGenerator.writeNumber(value.getTime)
  def writeMap[T](value: Map[String,T]) = macro JsonMacro.writeMap[F, T]

  def beginObject(): Unit = {
    jsonGenerator.writeStartObject()
  }

  def endObject(): Unit = {
    jsonGenerator.writeEndObject()
  }

  def beginArray(): Unit = {
    jsonGenerator.writeStartArray()
  }
  def endArray(): Unit = {
    jsonGenerator.writeEndArray()
  }

  def writeAny(value: Any): Unit = {
    value match {
      case null => writeNull()
      case i: Integer => writeInteger(i)
      case l: Long => writeLong(l)
      case s: String =>writeString(s)
      case f: Float => writeFloat(f)
      case d: Double => writeDouble(d)
      case b: Boolean => writeBoolean(b)
      case dc: BigDecimal => writeBigDecimal(dc)
      case dt: Date => writeDate(dt)
      //case m: Map[String, Any] => writeMap[Any](m)
      case _ => throw new JsonSerializeException("Can't serialize field:" + value)
    }
  }
}

class JsonSerializer[C <: Converter](override val jsonGenerator: JsonGenerator) extends JsonSerializerBase[C, JsonSerializer[C]](jsonGenerator){
  protected override def createFieldSerializer(): JsonSerializer[C] = new JsonSerializer[C](jsonGenerator)
}

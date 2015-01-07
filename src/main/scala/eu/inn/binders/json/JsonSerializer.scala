package eu.inn.binders.json

import java.util.Date

import com.fasterxml.jackson.core.JsonGenerator
import eu.inn.binders.core.Serializer
import eu.inn.binders.json.internal.JsonMacro
import eu.inn.binders.naming.{Converter, PlainConverter}
import scala.language.experimental.macros
import scala.reflect.runtime.universe._

class JsonSerializer[C <: Converter : TypeTag](val jsonGenerator: JsonGenerator) extends Serializer[C]{
  def getFieldSerializer(fieldName: String): Option[JsonSerializer[C]] = {
    jsonGenerator.writeFieldName(fieldName)
    Some(new JsonSerializer[C](jsonGenerator))
  }

  def writeInteger(value: Int): Unit = jsonGenerator.writeNumber(value)
  def writeIntegerOption(value: Option[Int]): Unit = writeNullableField(value, writeInteger)

  def writeLong(value: Long): Unit = jsonGenerator.writeNumber(value)
  def writeLongOption(value: Option[Long]): Unit = writeNullableField(value, writeLong)

  def writeString(value: String): Unit = jsonGenerator.writeString(value)
  def writeStringOption(value: Option[String]): Unit = writeNullableField(value, writeString)

  def writeFloat(value: Float): Unit = jsonGenerator.writeNumber(value)
  def writeFloatOption(value: Option[Float]): Unit = writeNullableField(value, writeFloat)

  def writeDouble(value: Double): Unit = jsonGenerator.writeNumber(value)
  def writeDoubleOption(value: Option[Double]): Unit = writeNullableField(value, writeDouble)

  def writeBoolean(value: Boolean): Unit = jsonGenerator.writeBoolean(value)
  def writeBooleanOption(value: Option[Boolean]): Unit = writeNullableField(value, writeBoolean)

  def writeBigDecimal(value: BigDecimal): Unit = jsonGenerator.writeNumber(value.bigDecimal)
  def writeBigDecimalOption(value: Option[BigDecimal]): Unit = writeNullableField(value, writeBigDecimal)

  def writeDate(value: Date): Unit = jsonGenerator.writeNumber(value.getTime)
  def writeDateOption(value: Option[Date]): Unit = writeNullableField(value, writeDate)

  def writeMap[T](value: Map[String,T]) = macro JsonMacro.writeMap[JsonSerializer[C], T]
  //def writeMapOption[T](value: Map[String,T]): Unit = jsonGenerator.writeNumber(value.getTime)

  protected def writeNullableField[T](value: Option[T], f: (T) ⇒ Unit) = {
    value.map {
      v ⇒
        f(v)
    } getOrElse {
      jsonGenerator.writeNull()
    }
  }

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
}

object JsonSerializer {
  def apply[C <: Converter : TypeTag](jsonGenerator: JsonGenerator) = new JsonSerializer[C](jsonGenerator)
}
package eu.inn.binders.json

import java.util.Date

import com.fasterxml.jackson.core.JsonGenerator
import eu.inn.binders.core.Serializer
import eu.inn.binders.value._
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

  def writeValue(value: Value): Unit = {
    if (value == null)
      writeNull()
    else
      value ~~ new ValueVisitor[Unit] {
        override def visitNumber(d: Number) = writeBigDecimal(d.v)
        override def visitBool(d: Bool) = writeBoolean(d.v)
        override def visitObj(d: Obj) = {
          beginObject()
          d.v.foreach(kv => {
            getFieldSerializer(kv._1).get.asInstanceOf[JsonSerializerBase[_,_]].writeValue(kv._2)
          })
          endObject()
        }
        override def visitText(d: Text) = writeString(d.v)
        override def visitLst(d: Lst) = {
          beginArray()
          d.v.foreach(writeValue)
          endArray()
        }
        override def visitNull() = writeNull()
      }
  }
}

class JsonSerializer[C <: Converter](override val jsonGenerator: JsonGenerator) extends JsonSerializerBase[C, JsonSerializer[C]](jsonGenerator){
  protected override def createFieldSerializer(): JsonSerializer[C] = new JsonSerializer[C](jsonGenerator)
}

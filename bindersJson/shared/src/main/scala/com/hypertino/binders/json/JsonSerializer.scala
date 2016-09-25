package com.hypertino.binders.json

import com.hypertino.binders.core.Serializer
import com.hypertino.binders.json.api.JsonGeneratorApi
import com.hypertino.binders.value.{Bool, Lst, Number, Obj, Text, Value, ValueVisitor}
import com.hypertino.inflector.naming.Converter

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.language.experimental.macros

class JsonSerializeException(message: String) extends RuntimeException(message)

abstract class JsonSerializerBase[C <: Converter, F <: Serializer[C]] protected (val jsonGenerator: JsonGeneratorApi) extends Serializer[C]{

  def getFieldSerializer(fieldName: String): Option[F] = {
    jsonGenerator.writeFieldName(fieldName)
    Some(createFieldSerializer())
  }

  protected def createFieldSerializer(): F

  def writeNull(): Unit = jsonGenerator.writeNull()
  def writeInt(value: Int): Unit = jsonGenerator.writeInt(value)
  def writeLong(value: Long): Unit = jsonGenerator.writeLong(value)
  def writeString(value: String): Unit = jsonGenerator.writeString(value)
  def writeFloat(value: Float): Unit = jsonGenerator.writeFloat(value)
  def writeDouble(value: Double): Unit = jsonGenerator.writeDouble(value)
  def writeBoolean(value: Boolean): Unit = jsonGenerator.writeBoolean(value)
  def writeBigDecimal(value: BigDecimal): Unit = jsonGenerator.writeBigDecimal(value.bigDecimal)
  def writeFiniteDuration(value: FiniteDuration): Unit = jsonGenerator.writeLong(value.toMillis)
  def writeDuration(value: Duration): Unit = {
    val s = value.toString
    val s2 = if (s.startsWith("Duration.")) {
      s.substring(9)
    }
    else {
      s
    }
    jsonGenerator.writeString(s2)
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

class JsonSerializer[C <: Converter](override val jsonGenerator: JsonGeneratorApi) extends JsonSerializerBase[C, JsonSerializer[C]](jsonGenerator){
  protected override def createFieldSerializer(): JsonSerializer[C] = new JsonSerializer[C](jsonGenerator)
}

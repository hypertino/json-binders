package eu.inn.binders.json

import java.util.Date

import com.fasterxml.jackson.core.JsonGenerator
import eu.inn.binders.core.Serializer
import eu.inn.binders.json.internal.{ISO8601, JsonMacro}
import eu.inn.binders.naming.{Converter, PlainConverter}
import scala.language.experimental.macros
import scala.reflect.runtime.universe._

class JsonSerializer[C <: Converter : TypeTag](val jsonGenerator: JsonGenerator, val skipNullFields: Boolean) extends Serializer[C]{

  def hasField(fieldName: String) = true

  def setInteger(name: String, value: Int): Unit = jsonGenerator.writeNumberField(name, value)
  def setIntegerOption(name: String, value: Option[Int]): Unit = _setNullableField(name, value, setInteger)
  def addInteger(value: Int): Unit = jsonGenerator.writeNumber(value)
  def addIntegerOption(value: Option[Int]): Unit = _addNullableField(value, addInteger)

  def setLong(name: String, value: Long): Unit = jsonGenerator.writeNumberField(name, value)
  def setLongOption(name: String, value: Option[Long]): Unit = _setNullableField(name, value, setLong)
  def addLong(value: Long): Unit = jsonGenerator.writeNumber(value)
  def addLongOption(value: Option[Long]): Unit = _addNullableField(value, addLong)

  def setString(name: String, value: String): Unit = jsonGenerator.writeStringField(name, value)
  def setStringOption(name: String, value: Option[String]): Unit = _setNullableField(name, value, setString)
  def addString(value: String): Unit = jsonGenerator.writeString(value)
  def addStringOption(value: Option[String]): Unit = _addNullableField(value, addString)

  def setFloat(name: String, value: Float): Unit = jsonGenerator.writeNumberField(name, value)
  def setFloatOption(name: String, value: Option[Float]): Unit = _setNullableField(name, value, setFloat)
  def addFloat(value: Float): Unit = jsonGenerator.writeNumber(value)
  def addFloatOption(value: Option[Float]): Unit = _addNullableField(value, addFloat)

  def setDouble(name: String, value: Double): Unit = jsonGenerator.writeNumberField(name, value)
  def setDoubleOption(name: String, value: Option[Double]): Unit = _setNullableField(name, value, setDouble)
  def addDouble(value: Double): Unit = jsonGenerator.writeNumber(value)
  def addDoubleOption(value: Option[Double]): Unit = _addNullableField(value, addDouble)

  def setBoolean(name: String, value: Boolean): Unit = jsonGenerator.writeBooleanField(name, value)
  def setBooleanOption(name: String, value: Option[Boolean]): Unit = _setNullableField(name, value, setBoolean)
  def addBoolean(value: Boolean): Unit = jsonGenerator.writeBoolean(value)
  def addBooleanOption(value: Option[Boolean]): Unit = _addNullableField(value, addBoolean)

  def setBigDecimal(name: String, value: BigDecimal): Unit = jsonGenerator.writeNumberField(name, value.bigDecimal)
  def setBigDecimalOption(name: String, value: Option[BigDecimal]): Unit = _setNullableField(name, value, setBigDecimal)
  def addBigDecimal(value: BigDecimal): Unit = jsonGenerator.writeNumber(value.bigDecimal)
  def addBigDecimalOption(value: Option[BigDecimal]): Unit = _addNullableField(value, addBigDecimal)

  def setDate(name: String, value: Date): Unit = jsonGenerator.writeStringField(name, formatDate(value))
  def setDateOption(name: String, value: Option[Date]): Unit = _setNullableField(name, value, setDate)
  def addDate(value: Date): Unit = jsonGenerator.writeString(formatDate(value))
  def addDateOption(value: Option[Date]): Unit = _addNullableField(value, addDate)
  
  def setProduct[T <: Product](name: String, value: T) = macro JsonMacro.setProduct[T]
  def addProduct[T <: Product](value: T) = macro JsonMacro.addProduct[T]

  def setSequence[T](name: String, value: Seq[T]) = macro JsonMacro.setSequence[T]
  // todo: def addSequence[T](value: Seq[T]) = macro JsonMacro.addSequence[T]

  def setNull(name: String): Unit = if (!skipNullFields) jsonGenerator.writeNullField(name)
  def addNull() = jsonGenerator.writeNull()

  protected def formatDate(date: Date): String = {
    ISO8601.fromDate(date)
  }

  protected def _setNullableField[T](name: String, value: Option[T], f: (String,T) ⇒ Unit) = {
    value.map {
      v ⇒
        f(name,v)
    } getOrElse {
      setNull(name)
    }
  }

  protected def _addNullableField[T](value: Option[T], f: (T) ⇒ Unit) = {
    value.map {
      v ⇒
        f(v)
    } getOrElse {
      jsonGenerator.writeNull()
    }
  }

  def beginObject(name: Option[String] = None) = {
    name.map {
      n ⇒ jsonGenerator.writeObjectFieldStart(n)
    } getOrElse {
      jsonGenerator.writeStartObject()
    }
  }

  def endObject() = {
    jsonGenerator.writeEndObject()
  }

  def beginArray(name: Option[String] = None) = {
    name.map {
      n ⇒ jsonGenerator.writeArrayFieldStart(n)
    } getOrElse {
      jsonGenerator.writeStartArray()
    }
  }

  def endArray() = {
    jsonGenerator.writeEndArray()
  }
}

object JsonSerializer {
  def apply[C <: Converter : TypeTag](jsonGenerator: JsonGenerator, skipNullFields: Boolean = true) = new JsonSerializer[C](jsonGenerator, skipNullFields)
}
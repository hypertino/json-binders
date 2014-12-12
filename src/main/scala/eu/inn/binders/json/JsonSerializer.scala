package eu.inn.binders.json

import com.fasterxml.jackson.core.JsonGenerator
import eu.inn.binders.core.Serializer
import eu.inn.binders.json.internal.JsonMacro
import eu.inn.binders.naming.{Converter, PlainConverter}
import scala.language.experimental.macros
import scala.reflect.runtime.universe._

class JsonSerializer[C <: Converter : TypeTag](val jsonGenerator: JsonGenerator, val skipNullFields: Boolean) extends Serializer[C]{

  def hasField(fieldName: String) = true

  def setInteger(name: String, value: Int): Unit = jsonGenerator.writeNumberField(name, value)

  def setIntegerOption(name: String, value: Option[Int]): Unit = _setNullableField(name, value, setInteger)

  def addInteger(value: Int): Unit = jsonGenerator.writeNumber(value)

  def addIntegerOption(value: Option[Int]): Unit = _addNullableField(value, addInteger)

  def setString(name: String, value: String): Unit = jsonGenerator.writeStringField(name, value)

  def addString(value: String): Unit = jsonGenerator.writeString(value)

  def setFloat(name: String, value: Float): Unit = jsonGenerator.writeNumberField(name, value)

  def setDouble(name: String, value: Double): Unit = jsonGenerator.writeNumberField(name, value)

  def setProduct[T <: Product](name: String, value: T) = macro JsonMacro.setProduct[T]

  def setSequence[T](name: String, value: Seq[T]) = macro JsonMacro.setSequence[T]

  def setNull(name: String): Unit = if (!skipNullFields) jsonGenerator.writeNullField(name)

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

  def beginObject(name: String) = {
    if (name == null)
      jsonGenerator.writeStartObject()
    else
      jsonGenerator.writeObjectFieldStart(name)
  }

  def endObject() = {
    jsonGenerator.writeEndObject()
  }

  def beginArray(name: String) = {
    if (name == null)
      jsonGenerator.writeStartArray()
    else
      jsonGenerator.writeArrayFieldStart(name)
  }

  def endArray() = {
    jsonGenerator.writeEndArray()
  }
}

object JsonSerializer {
  def apply[C <: Converter : TypeTag](jsonGenerator: JsonGenerator, skipNullFields: Boolean = true) = new JsonSerializer[C](jsonGenerator, skipNullFields)
}
package eu.inn.binders.json

import com.fasterxml.jackson.core.JsonGenerator
import eu.inn.binders.core.Serializer
import eu.inn.binders.json.internal.JsonMacro
import eu.inn.binders.naming.Converter
import scala.language.experimental.macros
import scala.reflect.runtime.universe._

class JsonSerializer[C <: Converter](val jsonGenerator: JsonGenerator) extends Serializer[C]{

  def hasField(fieldName: String) = true

  def setString(name: String, value: String): Unit = jsonGenerator.writeStringField(name, value)

  def setString(index: Int, value: String): Unit = jsonGenerator.writeString(value)

  def setInteger(name: String, value: Int): Unit = jsonGenerator.writeNumberField(name, value)

  def setInteger(index: Int, value: Int): Unit = jsonGenerator.writeNumber(value)

  def setProduct[T <: Product](name: String, value: T) = macro JsonMacro.setProduct[T]

  //def setInteger(index: Int, value: String) = setInteger(null, value)

  def setSequence[T](name: String, value: Seq[T]) = macro JsonMacro.setSequence[T]

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

  /*{
    jsonGenerator.writeObjectFieldStart(name)
    import eu.inn.binders._
    this.bind(value)
    jsonGenerator.writeEndObject()
  }*/
}
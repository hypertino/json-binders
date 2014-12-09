package eu.inn.binders.json

import com.fasterxml.jackson.core.JsonGenerator
import eu.inn.binders.core.Serializer
import eu.inn.binders.json.internal.JsonMacro
import eu.inn.binders.naming.Converter
import scala.language.experimental.macros
import scala.reflect.runtime.universe._

class JsonSerializer[C <: Converter](val jsonGenerator: JsonGenerator) extends Serializer[C]{

  def hasField(fieldName: String) = true

  def setString(name: String, value: String) : Unit = jsonGenerator.writeStringField(name, value)

  def setProduct[T <: Product](name: String, value: T) = macro JsonMacro.setProduct[T]

  /*{
    jsonGenerator.writeObjectFieldStart(name)
    import eu.inn.binders._
    this.bind(value)
    jsonGenerator.writeEndObject()
  }*/
}
package com.hypertino.binders.json

import com.hypertino.binders.json.api.{JsonGeneratorApi, JsonParserApi}
import com.hypertino.inflector.naming.Converter

class DefaultSerializerFactory[C <: Converter](override val prettyPrint: Boolean)
  extends SerializerFactory[C, JsonSerializer[C], JsonDeserializer[C]]{
  def this() = this(false)
  override def createSerializer(jsonGenerator: JsonGeneratorApi): JsonSerializer[C] = new JsonSerializer[C](jsonGenerator)
  override def createDeserializer(jsonParser: JsonParserApi): JsonDeserializer[C] = new JsonDeserializer[C](jsonParser)
}

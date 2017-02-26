package com.hypertino.binders.json

import com.hypertino.binders.core.BindOptions
import com.hypertino.binders.json.api.{JsonGeneratorApi, JsonParserApi}
import com.hypertino.inflector.naming.Converter

class DefaultJsonBindersFactory[C <: Converter](override val prettyPrint: Boolean)
  extends JsonBindersFactory[C, JsonSerializer[C], JsonDeserializer[C]]{
  def this() = this(false)
  override def createSerializer(jsonGenerator: JsonGeneratorApi)
                               (implicit bindOptions: BindOptions): JsonSerializer[C] = new JsonSerializer[C](jsonGenerator)
  override def createDeserializer(jsonParser: JsonParserApi)
                                 (implicit bindOptions: BindOptions): JsonDeserializer[C] = new JsonDeserializer[C](jsonParser)
}

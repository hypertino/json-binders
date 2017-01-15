package com.hypertino.binders.json

import java.io.{Reader, Writer}

import com.hypertino.binders.core.{Deserializer, Serializer}
import com.hypertino.binders.json.api.{JsonBindersFactoryApi, JsonGeneratorApi, JsonParserApi}
import com.hypertino.inflector.naming.{Converter, PlainConverter}

import scala.scalajs.js
import scala.scalajs.js.JSON

trait JsonBindersFactory[C <: Converter, S <: Serializer[C], D <: Deserializer[C]]
  extends JsonBindersFactoryApi[C, S, D] {

  override def withStringParser[T](jsonString: String)(codeBlock: D ⇒ T): T = {
    val adapter = new JsParserAdapter(JSON.parse(jsonString))
    val jds = createDeserializer(adapter)
    codeBlock(jds)
  }

  override def withReader[T](reader: Reader)(codeBlock: (D) ⇒ T): T = {
    val stringBuilder = new StringBuilder
    val len = 256
    val buffer = new Array[Char](len)
    var readed = len
    while (readed == len) {
      readed = reader.read(buffer, 0, len)
      stringBuilder.append(buffer, 0, readed)
    }
    withStringParser(stringBuilder.toString())(codeBlock)
  }

  def withJsonObjectParser[T](jsonObject: js.Dynamic)(codeBlock: D ⇒ T): T = {
    val adapter = new JsParserAdapter(jsonObject)
    val jds = createDeserializer(adapter)
    codeBlock(jds)
  }

  override def withWriter(writer: Writer)(codeBlock: S ⇒ Unit): Unit = {
    codeBlock(createSerializer(new JsGeneratorAdapter(writer)))
  }

  def createSerializer(jsonGenerator: JsonGeneratorApi): S
  def createDeserializer(jsonParser: JsonParserApi): D
  def prettyPrint: Boolean = false
}

object JsonBindersFactory {
  implicit val defaultJsonBindersFactory = new DefaultJsonBindersFactory[PlainConverter.type]
  def findFactory[C <: Converter, S <: Serializer[C], D <: Deserializer[C]]()
    (implicit factory: JsonBindersFactory[C, S, D]): JsonBindersFactory[C, S, D] = factory
}

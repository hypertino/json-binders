package com.hypertino.binders.json

import java.io._

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonGenerator, JsonParser}
import com.hypertino.binders.core.{Deserializer, Serializer}
import com.hypertino.binders.json.api.{JsonBindersFactoryApi, JsonGeneratorApi, JsonParserApi}
import com.hypertino.inflector.naming.{Converter, PlainConverter}

trait JsonBindersFactory[C <: Converter, S <: Serializer[C], D <: Deserializer[C]]
  extends JsonBindersFactoryApi[C, S, D] {

  val jf = new JsonFactory()

  override def withStringParser[T](jsonString: String)(codeBlock: D ⇒ T): T  = {
    val jp = jf.createParser(jsonString)
    try {
      withJsonParser[T](jp)(codeBlock)
    }
    finally {
      jp.close()
    }
  }

  def withReader[T](reader: Reader)(codeBlock: D ⇒ T): T = {
    val jp = jf.createParser(reader)
    try {
      withJsonParser[T](jp)(codeBlock)
    }
    finally {
      jp.close()
    }
  }

  def withJsonParser[T](jsonParser: JsonParser)(codeBlock: D ⇒ T): T = {
    val jds = createDeserializer(new JacksonParserAdapter(jsonParser))
    codeBlock(jds)
  }

  def withWriter(writer: Writer)(codeBlock: S ⇒ Unit): Unit = {
    val jg = jf.createGenerator(writer)
    try {
      if (prettyPrint)
        jg.useDefaultPrettyPrinter()
      withJsonGenerator(jg)(codeBlock)
    }
    finally {
      jg.close()
    }
  }

  def withJsonGenerator(outputGenerator: JsonGenerator)(codeBlock: S ⇒ Unit): Unit = {
    val js = createSerializer(new JacksonGeneratorAdapter(outputGenerator))
    codeBlock(js)
  }

  def encoding = JsonEncoding.UTF8
  def createSerializer(jsonGenerator: JsonGeneratorApi): S
  def createDeserializer(jsonParser: JsonParserApi): D
  def prettyPrint: Boolean = false
}

object JsonBindersFactory {
  implicit val defaultJsonBindersFactory = new DefaultJsonBindersFactory[PlainConverter.type]
  def findFactory[C <: Converter, S <: Serializer[C], D <: Deserializer[C]]()
    (implicit factory: JsonBindersFactory[C, S, D]): JsonBindersFactory[C, S, D] = factory
}

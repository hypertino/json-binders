package com.hypertino.binders.json

import java.io.{ByteArrayOutputStream, InputStream, OutputStream}

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonGenerator, JsonParser}
import com.hypertino.binders.core.{Deserializer, Serializer}
import com.hypertino.binders.json.api.{JsonBindersFactoryApi, JsonGeneratorApi, JsonParserApi}
import com.hypertino.inflector.naming.{Converter, PlainConverter}

trait JsonBindersFactory[C <: Converter, S <: Serializer[C], D <: Deserializer[C]]
  extends JsonBindersFactoryApi[C, S, D] {

  val jf = new JsonFactory()

  def withStringParser[T](jsonString: String)(codeBlock: D ⇒ T): T = {
    val jp = jf.createParser(jsonString)
    try {
      val jds = createDeserializer(new JacksonParserAdapter(jp))
      codeBlock(jds)
    } finally {
      jp.close()
    }
  }

  def withJsonParser[T](jsonParser: JsonParser)(codeBlock: D ⇒ T): T = {
    val jds = createDeserializer(new JacksonParserAdapter(jsonParser))
    codeBlock(jds)
  }

  def withStreamParser[T](inputStream: InputStream)(codeBlock: D ⇒ T): T = {
    val jp = jf.createParser(inputStream)
    try {
      val jds = createDeserializer(new JacksonParserAdapter(jp))
      codeBlock(jds)
    } finally {
      jp.close()
    }
  }

  def withStringGenerator(codeBlock: S ⇒ Unit): String = {
    val ba = new ByteArrayOutputStream()
    try {
      val jg = jf.createGenerator(ba, encoding)
      if (prettyPrint)
        jg.useDefaultPrettyPrinter()
      try {
        val js = createSerializer(new JacksonGeneratorAdapter(jg))
        codeBlock(js)
      }
      finally {
        jg.close()
      }
    }
    finally {
      ba.close()
    }
    ba.toString(encoding.getJavaName)
  }

  def withStreamGenerator(outputStream: OutputStream)(codeBlock: S ⇒ Unit): Unit = {
    val jg = jf.createGenerator(outputStream, encoding)
    try {
      val js = createSerializer(new JacksonGeneratorAdapter(jg))
      codeBlock(js)
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

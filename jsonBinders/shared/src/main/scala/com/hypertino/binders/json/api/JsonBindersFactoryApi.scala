package com.hypertino.binders.json.api

import java.io.{Reader, StringReader, StringWriter, Writer}

import com.hypertino.binders.core.{Deserializer, Serializer}
import com.hypertino.inflector.naming.Converter

trait JsonBindersFactoryApi[C <: Converter, S <: Serializer[C], D <: Deserializer[C]] {
  def createSerializer(jsonGenerator: JsonGeneratorApi): S
  def createDeserializer(jsonParser: JsonParserApi): D
  def prettyPrint: Boolean = false

  def withStringParser[T](jsonString: String)(codeBlock: D ⇒ T): T  = {
    val reader = new StringReader(jsonString)
    try {
      withReader[T](reader)(codeBlock)
    } finally {
      reader.close()
    }
  }

  def withStringGenerator(codeBlock: S ⇒ Unit): String = {
    val writer = new StringWriter()
    try {
      withWriter(writer)(codeBlock)
    } finally {
      writer.close()
    }
    writer.toString
  }

  def withJsonParserApi[T](jsonParserApi: JsonParserApi)(codeBlock: D ⇒ T): T = {
    val jds = createDeserializer(jsonParserApi)
    codeBlock(jds)
  }

  def withJsonGeneratorApi(jsonGeneratorApi: JsonGeneratorApi)(codeBlock: S ⇒ Unit): Unit = {
    val js = createSerializer(jsonGeneratorApi)
    codeBlock(js)
  }

  def withReader[T](reader: Reader)(codeBlock: D ⇒ T): T

  def withWriter(writer: Writer)(codeBlock: S ⇒ Unit): Unit
}

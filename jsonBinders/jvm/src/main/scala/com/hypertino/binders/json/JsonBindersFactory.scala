package com.hypertino.binders.json

import java.io._

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonGenerator, JsonParser}
import com.hypertino.binders.core.{BindOptions, Deserializer, Serializer}
import com.hypertino.binders.json.api.JsonBindersFactoryApi
import com.hypertino.inflector.naming.{Converter, PlainConverter}

trait JsonBindersFactory[C <: Converter, S <: Serializer[C], D <: Deserializer[C]]
  extends JsonBindersFactoryApi[C, S, D] {

  val jacksonFactory = new JsonFactory()
  // don't close sources, we don't own!
  jacksonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE)

  override def withStringParser[T](jsonString: String)
                                  (codeBlock: D ⇒ T)
                                  (implicit bindOptions: BindOptions): T  = {
    val jp = jacksonFactory.createParser(jsonString)
    try {
      withJacksonParser[T](jp)(codeBlock)
    }
    finally {
      jp.close()
    }
  }

  def withReader[T](reader: Reader)
                   (codeBlock: D ⇒ T)
                   (implicit bindOptions: BindOptions): T = {
    val jp = jacksonFactory.createParser(reader)
    try {
      withJacksonParser[T](jp)(codeBlock)
    }
    finally {
      jp.close()
    }
  }

  def withJacksonParser[T](jsonParser: JsonParser)
                          (codeBlock: D ⇒ T)
                          (implicit bindOptions: BindOptions): T = {
    withJsonParserApi(new JacksonParserAdapter(jsonParser))(codeBlock)
  }

  def withWriter(writer: Writer)
                (codeBlock: S ⇒ Unit)
                (implicit bindOptions: BindOptions): Unit = {
    val jg = jacksonFactory.createGenerator(writer)
    try {
      if (prettyPrint)
        jg.useDefaultPrettyPrinter()
      withJacksonGenerator(jg)(codeBlock)
    }
    finally {
      jg.close()
    }
  }

  def withJacksonGenerator(jsonGenerator: JsonGenerator)
                          (codeBlock: S ⇒ Unit)
                          (implicit bindOptions: BindOptions): Unit = {
    withJsonGeneratorApi(new JacksonGeneratorAdapter(jsonGenerator))(codeBlock)
  }

  def encoding = JsonEncoding.UTF8
}

object JsonBindersFactory {
  implicit val defaultJsonBindersFactory = new DefaultJsonBindersFactory[PlainConverter.type]
  def findFactory[C <: Converter, S <: Serializer[C], D <: Deserializer[C]]()
    (implicit factory: JsonBindersFactory[C, S, D]): JsonBindersFactory[C, S, D] = factory
}

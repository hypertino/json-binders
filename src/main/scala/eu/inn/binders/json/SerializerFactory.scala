package eu.inn.binders.json

import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory, JsonParser, JsonGenerator}
import eu.inn.binders.core.{Deserializer, Serializer}
import eu.inn.binders.naming.{PlainConverter, Converter}

trait SerializerFactory[C <: Converter, S <: Serializer[C], D <: Deserializer[C]] {
  def withParser[T](jsonString: String, codeBlock: D ⇒ T): T = {
    val jf = new JsonFactory()
    val jp = jf.createParser(jsonString)
    try {
      val jds = createDeserializer(jp)
      codeBlock(jds)
    } finally {
      jp.close()
    }
  }

  def withGenerator(codeBlock: S ⇒ Unit): String = {
    val jf = new JsonFactory()
    val ba = new ByteArrayOutputStream()
    try {
      val jg = jf.createGenerator(ba, encoding)
      try {
        val js = createSerializer(jg)
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

  def encoding = JsonEncoding.UTF8
  def createSerializer(jsonGenerator: JsonGenerator): S
  def createDeserializer(jsonParser: JsonParser): D
}

class DefaultSerializerFactory[C <: Converter] extends SerializerFactory[C, JsonSerializer[C], JsonDeserializer[C]] {
  override def createSerializer(jsonGenerator: JsonGenerator): JsonSerializer[C] = new JsonSerializer[C](jsonGenerator)
  override def createDeserializer(jsonParser: JsonParser): JsonDeserializer[C] = new JsonDeserializer[C](jsonParser)
}

object SerializerFactory {
  implicit val defaultSerializerFactory = new DefaultSerializerFactory[PlainConverter]
  def findFactory[C <: Converter, S <: Serializer[C], D <: Deserializer[C]]()
  (implicit factory: SerializerFactory[C, S, D]): SerializerFactory[C, S, D] = factory
}
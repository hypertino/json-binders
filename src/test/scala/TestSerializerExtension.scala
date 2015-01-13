
import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import com.fasterxml.jackson.databind.{ObjectMapper, JsonNode}
import eu.inn.binders.json.{SerializerFactory, JsonSerializer, JsonDeserializer}
import eu.inn.binders.naming.{PlainConverter, Converter}
import org.scalatest.{FlatSpec, Matchers}

class ExtraDataType(val v: String)

class JsonSerializerEx[C <: Converter](jsonGenerator: JsonGenerator) extends JsonSerializer[C](jsonGenerator) {
  override def createFieldSerializer() = new JsonSerializerEx[C](jsonGenerator)

  def writeExtraDataType(value: ExtraDataType): Unit = jsonGenerator.writeString("-" + value.v + "-")
}

class JsonDeserializerEx[C <: Converter]protected (jsonNode: JsonNode, override val fieldName: Option[String]) extends JsonDeserializer[C](jsonNode, fieldName) {
  def this (jsonParser: JsonParser) = this({val mapper = new ObjectMapper(); mapper.readTree(jsonParser)}, None)
  override def createFieldDeserializer(jsonNode: JsonNode, fieldName: Option[String]) = new JsonDeserializerEx[C](jsonNode, fieldName)

  def readExtraDataType() : ExtraDataType = new ExtraDataType(jsonNode.asText())
}

class SerializerFactoryEx[C <: Converter] extends SerializerFactory[C, JsonSerializerEx[C], JsonDeserializerEx[C]] {
  def createSerializer(jsonGenerator: JsonGenerator): JsonSerializerEx[C] = new JsonSerializerEx[C](jsonGenerator)
  def createDeserializer(jsonParser: JsonParser): JsonDeserializerEx[C] = new JsonDeserializerEx[C](jsonParser)
}

class TestSerializerExtension extends FlatSpec with Matchers {

  import eu.inn.binders.json._

  "Json " should " serialize extra data" in {
    implicit val serializerFactory = new SerializerFactoryEx[PlainConverter]
    val t = new ExtraDataType("ha")
    val str = t.toJson
    assert (str === "\"-ha-\"")
  }

  "Json " should " deserialize extra data" in {
    implicit val serializerFactory = new SerializerFactoryEx[PlainConverter]
    val o = """"ha"""".parseJson[ExtraDataType]
    val t = new ExtraDataType("ha")
    assert (o.v === t.v)
  }
}


import com.fasterxml.jackson.core.{JsonGenerator, JsonParser}
import eu.inn.binders.json._
import eu.inn.binders.naming.{Converter, PlainConverter}
import org.scalatest.{FlatSpec, Matchers}

class ExtraDataType(val v: String)
case class InnerWithExtraData(extra: ExtraDataType)

class JsonSerializerEx[C <: Converter](jsonGenerator: JsonGenerator) extends JsonSerializerBase[C, JsonSerializerEx[C]](jsonGenerator) {
  protected override def createFieldSerializer() = new JsonSerializerEx[C](jsonGenerator)

  def writeExtraDataType(value: ExtraDataType): Unit = jsonGenerator.writeString("-" + value.v + "-")
}

class JsonDeserializerEx[C <: Converter] (jsonParser: JsonParser, override val moveToNextToken: Boolean = true, override val fieldName: Option[String] = None) extends JsonDeserializerBase[C, JsonDeserializerEx[C]](jsonParser, moveToNextToken, fieldName) {

  protected override def createFieldDeserializer(jsonParser: JsonParser, moveToNextToken: Boolean, fieldName: Option[String]) = new JsonDeserializerEx[C](jsonParser, moveToNextToken, fieldName)

  def readExtraDataType() : ExtraDataType = new ExtraDataType(jsonParser.getValueAsString)
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

  "Json " should " serialize extra data inside class" in {
    implicit val serializerFactory = new SerializerFactoryEx[PlainConverter]
    val t = InnerWithExtraData(new ExtraDataType("ha"))
    val str = t.toJson
    assert (str === "{\"extra\":\"-ha-\"}")
  }

  "Json " should " deserialize extra data to class" in {
    implicit val serializerFactory = new SerializerFactoryEx[PlainConverter]
    val o = "{\"extra\":\"ha\"}".parseJson[InnerWithExtraData]
    val t = InnerWithExtraData(new ExtraDataType("ha"))
    assert (o.extra.v === t.extra.v)
  }
}

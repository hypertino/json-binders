import com.hypertino.binders.json.api.{JsonGeneratorApi, JsonParserApi}
import com.hypertino.binders.json.{JsonBinders, JsonBindersFactory, JsonDeserializerBase, JsonSerializerBase}
import com.hypertino.inflector.naming.{Converter, PlainConverter}
import org.scalatest.{FlatSpec, Matchers}

class ExtraDataType(val v: String)
case class InnerWithExtraData(extra: ExtraDataType)

class JsonSerializerEx[C <: Converter](jsonGenerator: JsonGeneratorApi) extends JsonSerializerBase[C, JsonSerializerEx[C]](jsonGenerator) {
  protected override def createFieldSerializer() = new JsonSerializerEx[C](jsonGenerator)

  def writeExtraDataType(value: ExtraDataType): Unit = jsonGenerator.writeString("-" + value.v + "-")
}

class JsonDeserializerEx[C <: Converter] (jsonParser: JsonParserApi, override val moveToNextToken: Boolean = true, override val fieldName: Option[String] = None) extends JsonDeserializerBase[C, JsonDeserializerEx[C]](jsonParser, moveToNextToken, fieldName) {

  protected override def createFieldDeserializer(jsonParser: JsonParserApi, moveToNextToken: Boolean, fieldName: Option[String]) = new JsonDeserializerEx[C](jsonParser, moveToNextToken, fieldName)

  def readExtraDataType() : ExtraDataType = new ExtraDataType(jsonParser.stringValue)
}

class JsonBindersFactoryEx[C <: Converter] extends JsonBindersFactory[C, JsonSerializerEx[C], JsonDeserializerEx[C]] {
  def createSerializer(jsonGenerator: JsonGeneratorApi): JsonSerializerEx[C] = new JsonSerializerEx[C](jsonGenerator)
  def createDeserializer(jsonParser: JsonParserApi): JsonDeserializerEx[C] = new JsonDeserializerEx[C](jsonParser)
}

class TestJsonBindersFactoryExtension extends FlatSpec with Matchers {
  import JsonBinders._

  "Json " should " serialize extra data" in {
    implicit val serializerFactory = new JsonBindersFactoryEx[PlainConverter.type]
    val t = new ExtraDataType("ha")
    val str = t.toJson
    assert (str === "\"-ha-\"")
  }

  "Json " should " deserialize extra data" in {
    implicit val serializerFactory = new JsonBindersFactoryEx[PlainConverter.type]
    val o = """"ha"""".parseJson[ExtraDataType]
    val t = new ExtraDataType("ha")
    assert (o.v === t.v)
  }

  "Json " should " serialize extra data inside class" in {
    implicit val serializerFactory = new JsonBindersFactoryEx[PlainConverter.type]
    val t = InnerWithExtraData(new ExtraDataType("ha"))
    val str = t.toJson
    assert (str === "{\"extra\":\"-ha-\"}")
  }

  "Json " should " deserialize extra data to class" in {
    implicit val serializerFactory = new JsonBindersFactoryEx[PlainConverter.type]
    val o = "{\"extra\":\"ha\"}".parseJson[InnerWithExtraData]
    val t = InnerWithExtraData(new ExtraDataType("ha"))
    assert (o.extra.v === t.extra.v)
  }
}


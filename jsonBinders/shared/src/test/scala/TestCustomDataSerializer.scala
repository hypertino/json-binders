import com.hypertino.binders.core.{ImplicitDeserializer, ImplicitSerializer}
import com.hypertino.binders.json.{JsonBinders, JsonDeserializer, JsonSerializer}
import org.scalatest.{FlatSpec, Matchers}

class CustomDataTypeSerializer extends ImplicitSerializer[ExtraDataType, JsonSerializer[_]] {
  override def write(serializer: JsonSerializer[_], value: ExtraDataType): Unit = serializer.writeString("-" + value.v + "-")
}

class CustomDataTypeDeserializer extends ImplicitDeserializer[ExtraDataType, JsonDeserializer[_]] {
  override def read(deserializer: JsonDeserializer[_]): ExtraDataType = new ExtraDataType(deserializer.readString())
}

object CustomDataJsonBinders {
  implicit val serializer = new CustomDataTypeSerializer
  implicit val deserializer = new CustomDataTypeDeserializer
}

class TestSerializerTypeExtension extends FlatSpec with Matchers {

  import JsonBinders._
  import CustomDataJsonBinders._

  it should " serialize extra data type" in {
    val t = new ExtraDataType("ha")
    val str = t.toJson
    str shouldBe "\"-ha-\""
  }

  it should " deserialize extra data type" in {
    val o = """"ha"""".parseJson[ExtraDataType]
    val t = new ExtraDataType("ha")
    o.v shouldBe t.v
  }

  it should " serialize extra data type inside class" in {
    val t = InnerWithExtraData(new ExtraDataType("ha"))
    val str = t.toJson
    str shouldBe "{\"extra\":\"-ha-\"}"
  }

  it should " deserialize extra data type to class" in {
    val o = "{\"extra\":\"ha\"}".parseJson[InnerWithExtraData]
    val t = InnerWithExtraData(new ExtraDataType("ha"))
    o.extra.v shouldBe t.extra.v
  }
}

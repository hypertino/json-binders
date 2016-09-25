import com.hypertino.binders.core.{ImplicitDeserializer, ImplicitSerializer}
import com.hypertino.binders.json.{JsonBinders, JsonDeserializer, JsonSerializer}
import org.scalatest.{FlatSpec, Matchers}

class ExtraDataTypeSerializer extends ImplicitSerializer[ExtraDataType, JsonSerializer[_]] {
  override def write(serializer: JsonSerializer[_], value: ExtraDataType): Unit = serializer.writeString("-" + value.v + "-")
}

class ExtraDataTypeDeserializer extends ImplicitDeserializer[ExtraDataType, JsonDeserializer[_]] {
  override def read(deserializer: JsonDeserializer[_]): ExtraDataType = new ExtraDataType(deserializer.readString())
}

class TestSerializerTypeExtension extends FlatSpec with Matchers {

  import JsonBinders._

  implicit val serializer = new ExtraDataTypeSerializer
  implicit val deserializer = new ExtraDataTypeDeserializer

  "Json " should " serialize extra data type" in {
    val t = new ExtraDataType("ha")
    val str = t.toJson
    assert (str === "\"-ha-\"")
  }

  "Json " should " deserialize extra data type" in {
    val o = """"ha"""".parseJson[ExtraDataType]
    val t = new ExtraDataType("ha")
    assert (o.v === t.v)
  }

  "Json " should " serialize extra data type inside class" in {
    val t = InnerWithExtraData(new ExtraDataType("ha"))
    val str = t.toJson
    assert (str === "{\"extra\":\"-ha-\"}")
  }

  "Json " should " deserialize extra data type to class" in {
    val o = "{\"extra\":\"ha\"}".parseJson[InnerWithExtraData]
    val t = InnerWithExtraData(new ExtraDataType("ha"))
    assert (o.extra.v === t.extra.v)
  }
}

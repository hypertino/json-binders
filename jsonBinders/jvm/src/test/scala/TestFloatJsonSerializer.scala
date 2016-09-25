import com.hypertino.binders.json.JsonBinders
import org.scalatest.{FlatSpec, Matchers}

case class TestFloat(floatVal: Float)
case class TestFloatN(floatValN1: Option[Float], floatValN2: Option[Float])
case class TestFloatArray(floatArray: Seq[Float])
case class TestFloatArrayN(floatArrayN: Seq[Option[Float]])

class TestFloatJsonSerializer extends FlatSpec with Matchers {

  import JsonBinders._

  "Json " should " serialize class with Float" in {
    val t = TestFloat(1234.567f)
    val str = t.toJson
    assert (str === """{"floatVal":1234.567}""")
  }

  "Json " should " deserialize class with Float" in {
    val o = """{"floatVal":1234.567}""".parseJson[TestFloat]
    val t = TestFloat(1234.567f)
    assert (o === t)
  }

  "Json " should " serialize class with array of Float" in {
    val t = TestFloatArray(List(1.5f,2,3))
    val str = t.toJson
    assert (str === """{"floatArray":[1.5,2.0,3.0]}""")
  }

  "Json " should " deserialize class with array of Float" in {
    val o = """{"floatArray":[1.5,2.0,3.0]}""".parseJson[TestFloatArray]
    val t = TestFloatArray(List(1.5f,2,3))
    assert (o === t)
  }

  "Json " should " serialize class with array of Option[Float]" in {
    val t = TestFloatArrayN(List(Some(1.5f),None,Some(3)))
    val str = t.toJson
    assert (str === """{"floatArrayN":[1.5,null,3.0]}""")
  }

  "Json " should " deserialize class with array of Option[Float]" in {
    val o = """{"floatArrayN":[1.5,null,3.0]}""".parseJson[TestFloatArrayN]
    val t = TestFloatArrayN(List(Some(1.5f),None,Some(3)))
    assert (o === t)
  }

  "Json " should " serialize class with Nullable Float" in {
    val t = TestFloatN(Some(1234.567f), Some(456))
    val str = t.toJson
    assert (str === """{"floatValN1":1234.567,"floatValN2":456.0}""")

    val t2 = TestFloatN(Some(1234),None)
    val str2 = t2.toJson
    assert (str2 === """{"floatValN1":1234.0,"floatValN2":null}""")
  }
}


import com.hypertino.binders.core.FieldNotFoundException
import com.hypertino.binders.json.JsonBinders
import org.scalatest.{FlatSpec, Matchers}

case class TestString(stringVal: String)
case class TestStringN(stringValN1: Option[String], stringValN2: Option[String])
case class TestStringArray(stringArray: Seq[String])
case class TestStringArrayN(stringArrayN: Seq[Option[String]])

class TestStringJsonSerializer extends FlatSpec with Matchers {

  import JsonBinders._

  "Json " should " serialize class with String" in {
    val t = TestString("abc")
    val str = t.toJson
    assert (str === """{"stringVal":"abc"}""")
  }

  "Json " should " deserialize class with String" in {
    val o = """{"stringVal":"abc"}""".parseJson[TestString]
    val t = TestString("abc")
    assert (t === o)
  }

  "Json " should " serialize class with array of String" in {
    val t = TestStringArray(List("a","b"))
    val str = t.toJson
    assert (str === """{"stringArray":["a","b"]}""")
  }

  "Json " should " deserialize class with array of String" in {
    val o = """{"stringArray":["a","b"]}""".parseJson[TestStringArray]
    val t = TestStringArray(List("a","b"))
    assert (t === o)
  }

  "Json " should " serialize class with array of Option[String]" in {
    val t = TestStringArrayN(List(Some("a"),None,Some("c")))
    val str = t.toJson
    assert (str === """{"stringArrayN":["a",null,"c"]}""")
  }

  "Json " should " deserialize class with array of Option[String]" in {
    val o = """{"stringArrayN":["a",null,"b"]}""".parseJson[TestStringArrayN]
    val t = TestStringArrayN(List(Some("a"),None,Some("b")))
    assert (t === o)
  }

  "Json " should " serialize class with Nullable String" in {
    val t = TestStringN(Some("a"), Some("b"))
    val str = t.toJson
    assert (str === """{"stringValN1":"a","stringValN2":"b"}""")

    val t2 = TestStringN(Some("a"),None)
    val str2 = t2.toJson
    assert (str2 === """{"stringValN1":"a","stringValN2":null}""")
  }

  "Json " should " deserialize class with Nullable String" in {
    val o = """{"stringValN1":"a","stringValN2":"b"}""".parseJson[TestStringN]
    val t = TestStringN(Some("a"), Some("b"))
    assert (o === t)

    val o2 = """{"stringValN1":"a"}""".parseJson[TestStringN]
    val t2 = TestStringN(Some("a"),None)
    assert (o2 === t2)
  }

  "Json " should " deserialize empty array if no field is found" in {
    val o = """{}""".parseJson[TestStringArray]
    val t = TestStringArray(Seq.empty)
    assert (o === t)
  }

  "Json " should " throw exception if fieldname doesn't match" in {
    intercept[FieldNotFoundException] {
      """{"wrongFieldName":"abc"}""".parseJson[TestString]
    }
  }
}


import org.scalatest.{FlatSpec, Matchers}

case class TestString(stringVal: String)
case class TestStringN(stringValN1: Option[String], stringValN2: Option[String])
case class TestStringArray(stringArray: Seq[String])
case class TestStringArrayN(stringArrayN: Seq[Option[String]])

class TestStringJsonSerializer extends FlatSpec with Matchers {

  import eu.inn.binders.json._

  "Json " should " serialize class with String" in {
    val t = TestString("abc")
    val str = t.toJson
    assert (str === """{"stringVal":"abc"}""")
  }

  "Json " should " deserialize class with String" in {
    val o = """{"stringVal":"abc"}""".parseJson[TestString]
    val t = TestString("abc")
    val str = t.toJson
    assert (t === 0)
  }

  "Json " should " serialize class with array of String" in {
    val t = TestStringArray(List("a","b"))
    val str = t.toJson
    assert (str === """{"stringArray":["a","b"]}""")
  }

  "Json " should " serialize class with array of Option[String]" in {
    val t = TestStringArrayN(List(Some("a"),None,Some("c")))
    val str = t.toJson
    assert (str === """{"stringArrayN":["a",null,"c"]}""")
  }

  "Json " should " serialize class with Nullable String" in {
    val t = TestStringN(Some("a"), Some("b"))
    val str = t.toJson
    assert (str === """{"stringValN1":"a","stringValN2":"b"}""")

    val t2 = TestStringN(Some("a"),None)
    val str2 = t2.toJson
    assert (str2 === """{"stringValN1":"a"}""")
  }
}
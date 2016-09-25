
import com.hypertino.binders.json.JsonBinders
import org.scalatest.{FlatSpec, Matchers}

case class TestLong(longVal: Long)
case class TestLongN(longValN1: Option[Long], longValN2: Option[Long])
case class TestLongArray(longArray: Seq[Long])
case class TestLongArrayN(longArrayN: Seq[Option[Long]])

class TestLongJsonSerializer extends FlatSpec with Matchers {

  import JsonBinders._

  "Json " should " serialize class with Long" in {
    val t = TestLong(1234)
    val str = t.toJson
    assert (str === """{"longVal":1234}""")
  }

  "Json " should " deserialize class with Long" in {
    val o = """{"longVal":1234}""".parseJson[TestLong]
    val t = TestLong(1234)
    assert (o === t)
  }

  "Json " should " serialize class with array of Long" in {
    val t = TestLongArray(List(1,2,3))
    val str = t.toJson
    assert (str === """{"longArray":[1,2,3]}""")
  }

  "Json " should " deserialize class with array of Long" in {
    val o = """{"longArray":[1,2,3]}""".parseJson[TestLongArray]
    val t = TestLongArray(List(1,2,3))
    assert (o === t)
  }

  "Json " should " serialize class with array of Option[Long]" in {
    val t = TestLongArrayN(List(Some(1),None,Some(3)))
    val str = t.toJson
    assert (str === """{"longArrayN":[1,null,3]}""")
  }

  "Json " should " deserialize class with array of Option[Long]" in {
    val o = """{"longArrayN":[1,null,3]}""".parseJson[TestLongArrayN]
    val t = TestLongArrayN(List(Some(1),None,Some(3)))
    assert (o === t)
  }

  "Json " should " serialize class with Nullable Long" in {
    val t = TestLongN(Some(1234), Some(456))
    val str = t.toJson
    assert (str === """{"longValN1":1234,"longValN2":456}""")

    val t2 = TestLongN(Some(1234),None)
    val str2 = t2.toJson
    assert (str2 === """{"longValN1":1234,"longValN2":null}""")
  }
}

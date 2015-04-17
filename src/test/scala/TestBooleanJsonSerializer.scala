
import org.scalatest.{FlatSpec, Matchers}

case class TestBoolean(booleanVal: Boolean)
case class TestBooleanN(booleanValN1: Option[Boolean], booleanValN2: Option[Boolean])
case class TestBooleanArray(booleanArray: Seq[Boolean])
case class TestBooleanArrayN(booleanArrayN: Seq[Option[Boolean]])

class TestBooleanJsonSerializer extends FlatSpec with Matchers {

  import eu.inn.binders.json._

  "Json " should " serialize class with Boolean" in {
    val t = TestBoolean(booleanVal = true)
    val str = t.toJson
    assert (str === """{"booleanVal":true}""")
  }

  "Json " should " deserialize class with Boolean" in {
    val o = """{"booleanVal":true}""".parseJson[TestBoolean]
    val t = TestBoolean(booleanVal = true)
    assert (o === t)
  }

  "Json " should " serialize class with array of Boolean" in {
    val t = TestBooleanArray(List(true,false,true))
    val str = t.toJson
    assert (str === """{"booleanArray":[true,false,true]}""")
  }

  "Json " should " deserialize class with array of Boolean" in {
    val o = """{"booleanArray":[true,false,true]}""".parseJson[TestBooleanArray]
    val t = TestBooleanArray(List(true,false,true))
    assert (o === t)
  }

  "Json " should " serialize class with array of Option[Boolean]" in {
    val t = TestBooleanArrayN(List(Some(true),None,Some(false)))
    val str = t.toJson
    assert (str === """{"booleanArrayN":[true,null,false]}""")
  }

  "Json " should " deserialize class with array of Option[Boolean]" in {
    val o = """{"booleanArrayN":[true,null,false]}""".parseJson[TestBooleanArrayN]
    val t = TestBooleanArrayN(List(Some(true),None,Some(false)))
    assert (o === t)
  }

  "Json " should " serialize class with Nullable Boolean" in {
    val t = TestBooleanN(Some(true), Some(false))
    val str = t.toJson
    assert (str === """{"booleanValN1":true,"booleanValN2":false}""")

    val t2 = TestBooleanN(Some(true),None)
    val str2 = t2.toJson
    assert (str2 === """{"booleanValN1":true}""")
  }
}
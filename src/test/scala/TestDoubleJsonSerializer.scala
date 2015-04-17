
import org.scalatest.{FlatSpec, Matchers}
case class TestDouble(doubleVal: Double)
case class TestDoubleN(doubleValN1: Option[Double], doubleValN2: Option[Double])
case class TestDoubleArray(doubleArray: Seq[Double])
case class TestDoubleArrayN(doubleArrayN: Seq[Option[Double]])

class TestDoubleJsonSerializer extends FlatSpec with Matchers {

  import eu.inn.binders.json._

  "Json " should " serialize class with Double" in {
    val t = TestDouble(1234.567)
    val str = t.toJson
    assert (str === """{"doubleVal":1234.567}""")
  }

  "Json " should " deserialize class with Double" in {
    val o = """{"doubleVal":1234.567}""".parseJson[TestDouble]
    val t = TestDouble(1234.567)
    assert (o === t)
  }

  "Json " should " serialize class with array of Double" in {
    val t = TestDoubleArray(List(1.5,2,3))
    val str = t.toJson
    assert (str === """{"doubleArray":[1.5,2.0,3.0]}""")
  }

  "Json " should " deserialize class with array of Double" in {
    val o = """{"doubleArray":[1.5,2.0,3.0]}""".parseJson[TestDoubleArray]
    val t = TestDoubleArray(List(1.5,2,3))
    assert (o === t)
  }

  "Json " should " serialize class with array of Option[Double]" in {
    val t = TestDoubleArrayN(List(Some(1.5),None,Some(3)))
    val str = t.toJson
    assert (str === """{"doubleArrayN":[1.5,null,3.0]}""")
  }

  "Json " should " deserialize class with array of Option[Double]" in {
    val o = """{"doubleArrayN":[1.5,null,3.0]}""".parseJson[TestDoubleArrayN]
    val t = TestDoubleArrayN(List(Some(1.5),None,Some(3)))
    assert (o === t)
  }

  "Json " should " serialize class with Nullable Double" in {
    val t = TestDoubleN(Some(1234.567), Some(456))
    val str = t.toJson
    assert (str === """{"doubleValN1":1234.567,"doubleValN2":456.0}""")

    val t2 = TestDoubleN(Some(1234),None)
    val str2 = t2.toJson
    assert (str2 === """{"doubleValN1":1234.0}""")
  }
}
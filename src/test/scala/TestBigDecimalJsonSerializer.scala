
import org.scalatest.{FlatSpec, Matchers}

case class TestBigDecimal(bigdecimalVal: BigDecimal)
case class TestBigDecimalN(bigdecimalValN1: Option[BigDecimal], bigdecimalValN2: Option[BigDecimal])
case class TestBigDecimalArray(bigdecimalArray: Seq[BigDecimal])
case class TestBigDecimalArrayN(bigdecimalArrayN: Seq[Option[BigDecimal]])

class TestBigDecimalJsonSerializer extends FlatSpec with Matchers {

  import eu.inn.binders.json._

  "Json " should " serialize class with BigDecimal" in {
    val t = TestBigDecimal(
      BigDecimal("123411111111111111119999999999999999999999999999898989898989899898989898988.454546",
        new java.math.MathContext(120)
      )
    )
    val str = t.toJson
    assert (str === """{"bigdecimalVal":123411111111111111119999999999999999999999999999898989898989899898989898988.454546}""")
  }

  "Json " should " serialize class with array of BigDecimal" in {
    val t = TestBigDecimalArray(List(1,2,3))
    val str = t.toJson
    assert (str === """{"bigdecimalArray":[1,2,3]}""")
  }

  "Json " should " serialize class with array of Option[BigDecimal]" in {
    val t = TestBigDecimalArrayN(List(Some(1),None,Some(3)))
    val str = t.toJson
    assert (str === """{"bigdecimalArrayN":[1,null,3]}""")
  }

  "Json " should " serialize class with Nullable BigDecimal" in {
    val t = TestBigDecimalN(Some(1234), Some(456))
    val str = t.toJson
    assert (str === """{"bigdecimalValN1":1234,"bigdecimalValN2":456}""")

    val t2 = TestBigDecimalN(Some(1234),None)
    val str2 = t2.toJson
    assert (str2 === """{"bigdecimalValN1":1234}""")
  }
}
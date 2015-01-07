
import eu.inn.binders.naming.CamelCaseToSnakeCaseConverter
import org.scalatest.{FlatSpec, Matchers}

case class TestComplex(
  intVal: Int,
  intValN: Option[Int],
  seq: Seq[TestInt],
  seqString: Seq[String],
  inner1: TestFloat,
  inner2N: Option[TestDouble]
  )

class TestComplexJsonSerializer extends FlatSpec with Matchers {
/* todo:
  import eu.inn.binders.json._

  "Json " should " serialize complex class" in {
    val t = TestComplex(
      123,
      Some(456),
      List(TestInt(1), TestInt(2)),
      List("a","b"),
      TestFloat(1.3f),
      Some(TestDouble(0.5))
    )
    val str = t.toJson
    assert (str === """{"intVal":123,"intValN":456,"seq":[{"intVal":1},{"intVal":2}],"seqString":["a","b"],"inner1":{"floatVal":1.3},"inner2N":{"doubleVal":0.5}}""")
  }

  "Json " should " serialize complex class with empty fields" in {
    val t = TestComplex(
      123,
      None,
      List(),
      List(),
      TestFloat(1.3f),
      None
    )
    val str = t.toJson
    assert (str === """{"intVal":123,"seq":[],"seqString":[],"inner1":{"floatVal":1.3}}""")
  }*/
}
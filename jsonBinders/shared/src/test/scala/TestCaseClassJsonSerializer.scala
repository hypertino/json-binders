
import com.hypertino.binders.core.FieldNotFoundException
import com.hypertino.binders.json.{DefaultJsonBindersFactory, JsonBinders}
import com.hypertino.inflector.naming.CamelCaseToSnakeCaseConverter
import org.scalatest.{FlatSpec, Matchers}

case class TestCaseClass(
  intVal: Int,
  intValN: Option[Int],
  seq: Seq[TestInt],
  seqString: Seq[String],
  inner1: TestDouble,
  inner2N: Option[TestDouble]
  )

case class TestCaseClass2(userId: String)

class TestCaseClassJsonSerializer extends FlatSpec with Matchers {

  import JsonBinders._

  "Json " should " serialize complex class" in {
    val t = TestCaseClass(
      123,
      Some(456),
      List(TestInt(1), TestInt(2)),
      List("a","b"),
      TestDouble(4.1),
      Some(TestDouble(0.5))
    )
    val str = t.toJson
    assert (str === """{"intVal":123,"intValN":456,"seq":[{"intVal":1},{"intVal":2}],"seqString":["a","b"],"inner1":{"doubleVal":4.1},"inner2N":{"doubleVal":0.5}}""")
  }

  "Json " should " deserialize complex class" in {
    val t = TestCaseClass(
      123,
      Some(456),
      List(TestInt(1), TestInt(2)),
      List("a","b"),
      TestDouble(4.1),
      Some(TestDouble(0.5))
    )
    val d = """{"intVal":123,"intValN":456,"seq":[{"intVal":1},{"intVal":2}],"seqString":["a","b"],"inner1":{"doubleVal":4.1},"inner2N":{"doubleVal":0.5}}""".parseJson[TestCaseClass]
    assert (t === d)
  }

  "Json " should " deserialize class with extra fields" in {
    val s =
      """
        {
          "test":{"x":"y"},
          "userId":"101396227229647"
        }
      """

    s.parseJson[TestCaseClass2]
  }

  "Json " should "not deserialize class if it's inside inner field" in {
    val s =
      """
        {
          "data":{
            "test":{"x":"y"},
            "userId":"101396227229647"
          }
        }
      """

    intercept[FieldNotFoundException] {
      s.parseJson[TestCaseClass2]
    }
  }
}


import org.scalatest.{FlatSpec, Matchers}

/*case class TestInt(intVal: Int)
case class TestIntN(intValN1: Option[Int], intValN2: Option[Int])
case class TestIntArray(intArray: Seq[Int])
case class TestIntArrayN(intArrayN: Seq[Option[Int]])
*/

class TestAnyJsonSerializer extends FlatSpec with Matchers {

  import eu.inn.binders.json._

  "Json " should " serialize class with Int" in {
    val t = TestInt(1234)
    val str = t.toJson
    assert (str === """{"intVal":1234}""")
  }

  "Json " should " deserialize class with Int" in {
    val o = """{"intVal":1234}""".parseJson[TestInt]
    val t = TestInt(1234)
    assert (o === t)
  }

}
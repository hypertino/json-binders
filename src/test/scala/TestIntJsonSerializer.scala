
import eu.inn.binders.core.BindOptions
import org.scalatest.{FlatSpec, Matchers}

case class TestInt(intVal: Int)
case class TestIntN(intValN1: Option[Int], intValN2: Option[Int])
case class TestIntArray(intArray: Seq[Int])
case class TestIntArrayN(intArrayN: Seq[Option[Int]])

class TestIntJsonSerializer extends FlatSpec with Matchers {

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

  "Json " should " serialize class with array of Int" in {
    val t = TestIntArray(List(1,2,3))
    val str = t.toJson
    assert (str === """{"intArray":[1,2,3]}""")
  }

  "Json " should " deserialize class with array of Int" in {
      val o = """{"intArray":[1,2,3]}""".parseJson[TestIntArray]
      val t = TestIntArray(List(1,2,3))
      assert (o === t)
    }

    "Json " should " serialize class with array of Option[Int]" in {
      val t = TestIntArrayN(List(Some(1),None,Some(3)))
      val str = t.toJson
      assert (str === """{"intArrayN":[1,null,3]}""")
    }

    "Json " should " deserialize class with array of Option[Int]" in {
      val o = """{"intArrayN":[1,null,3]}""".parseJson[TestIntArrayN]
      val t = TestIntArrayN(List(Some(1),None,Some(3)))
      assert (o === t)
    }

  "Json " should " serialize class with Nullable Int" in {
    val t = TestIntN(Some(1234), Some(456))
    val str = t.toJson
    assert(str === """{"intValN1":1234,"intValN2":456}""")

    val t2 = TestIntN(Some(1234), None)
    val str2 = t2.toJson
    assert(str2 === """{"intValN1":1234,"intValN2":null}""")
  }

  "Json " should " skip field when BindOptions(skipOptionalFields=true)" in {
    implicit val op3: BindOptions = new BindOptions(true)
    val t3 = TestIntN(Some(1234), None)
    val str3 = t3.toJson
    assert(str3 === """{"intValN1":1234}""")
  }
}
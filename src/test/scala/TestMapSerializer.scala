
import eu.inn.binders.naming.CamelCaseToSnakeCaseConverter
import org.scalatest.{FlatSpec, Matchers}

case class TestStringMap1(map: Map[String, String])
case class TestStringMap2(map: Map[String, TestInt])

class TestMapSerializer extends FlatSpec with Matchers {

   import eu.inn.binders.json._

   "Json " should " serialize map[String,String]" in {
     val t = TestStringMap1(Map("a" -> "1", "b" ->"2"))
     val str = t.toJson
     assert (str === """{"map":{"a":"1","b":"2"}}""")
   }

  "Json " should " deserialize map[String,String]" in {
    val o = """{"map":{"a":"1","b":"2"}}""".parseJson[TestStringMap1]
    val t = TestStringMap1(Map("a" -> "1", "b" ->"2"))
    assert (o === t)
  }

  "Json " should " serialize map[String,TestInt]" in {
    val t = TestStringMap2(Map("a" -> TestInt(1), "b" ->TestInt(2)))
    val str = t.toJson
    assert (str === """{"map":{"a":{"intVal":1},"b":{"intVal":2}}}""")
  }

  "Json " should " deserialize map[String,TestInt]" in {
    val o = """{"map":{"a":{"intVal":1},"b":{"intVal":2}}}""".parseJson[TestStringMap2]
    val t = TestStringMap2(Map("a" -> TestInt(1), "b" ->TestInt(2)))
    assert (o === t)
  }
 }
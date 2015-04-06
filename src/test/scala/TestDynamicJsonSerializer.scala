
import eu.inn.binders.dynamic._
import org.scalatest.{FlatSpec, Matchers}

class TestDynamicJsonSerializer extends FlatSpec with Matchers {

  import eu.inn.binders.json._

  "Json " should " serialize dynamic null" in {
    val t: DynamicValue = null
    val str = t.toJson
    assert (str === "null")
  }

  "Json " should " deserialize dynamic null" in {
    val o = "null".parseJson[DynamicValue]
    val t = null
    assert (o === t)
  }

  "Json " should " serialize dynamic Number" in {
    val t = Number(1234)
    val str = t.toJson
    assert (str === "1234")
  }

  "Json " should " deserialize dynamic Number" in {
    val o = "1234".parseJson[DynamicValue]
    val t = Number(1234)
    assert (o === t)
  }

  "Json " should " serialize dynamic Text" in {
    val t = Text("ha")
    val str = t.toJson
    assert (str === "\"ha\"")
  }

  "Json " should " deserialize dynamic Text" in {
    val o = "\"ha\"".parseJson[DynamicValue]
    val t = Text("ha")
    assert (o === t)
  }

  "Json " should " serialize dynamic Bool(true)" in {
    val t = Bool(true)
    val str = t.toJson
    assert (str === "true")
  }

  "Json " should " deserialize dynamic Bool" in {
    val o = "false".parseJson[DynamicValue]
    val t = Bool(false)
    assert (o === t)
  }

  "Json " should " serialize dynamic Lst" in {
    val t = Lst(Seq(Number(1),Number(2),Text("ha"),null))
    val str = t.toJson
    assert (str === "[1,2,\"ha\",null]")
  }

  "Json " should " deserialize dynamic Lst" in {
    val o = "[1,2,\"ha\",null]".parseJson[DynamicValue]
    val t = Lst(Seq(Number(1),Number(2),Text("ha"),null))
    assert (o === t)
  }

  "Json " should " serialize dynamic Obj" in {
    val t = Obj(Map("a" -> Number(1),"b"->Text("ha"),"c"->null))
    val str = t.toJson
    assert (str === """{"a":1,"b":"ha","c":null}""")
  }

  "Json " should " deserialize dynamic Obj" in {
    val o = """{"a":1,"b":"ha","c":null}""".parseJson[DynamicValue]
    val t = Obj(Map("a" -> Number(1),"b"->Text("ha"),"c"->null))
    assert (o === t)
  }
}

import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory}
import eu.inn.binders.json.{JsonDeserializer, JsonSerializer}
import eu.inn.binders.naming.PlainConverter
import org.scalatest.{FlatSpec, Matchers}
import eu.inn.binders._
import sun.org.mozilla.javascript.internal.json.JsonParser

case class Test1(innerStrVal: String, x: Int)
case class Test2(strVal: String, t: Test1, tn: Option[Test1])
case class Test3(strVal: String, tx: Test2)
case class Test4(a: Seq[Int])

case class TestInt(intVal: Int)

class TestJsonSerializer extends FlatSpec with Matchers {

  import eu.inn.binders.json._

  "Json " should " serialize class with Int" in {
    implicit val defaultConverter = new PlainConverter
    val t = TestInt(1234)
    val str = t.toJson
    assert (str === """{"intVal":1234}""")
  }

  "Test " should " should be able to serialize" in {

    val jf = new JsonFactory()

    val ba = new ByteArrayOutputStream()
    val jg = jf.createGenerator(ba, JsonEncoding.UTF8)

    val slz = new JsonSerializer[PlainConverter](jg)

    jg.writeStartObject()
    slz.bind(Test3("haha", Test2("aa", Test1("bebe", 67), None)))
    jg.writeEndObject()

    jg.close()
    ba.close()
    val s = ba.toString("UTF-8")

    //jf.create
    //val jg = new JsonFactory.

    assert(s == """{"strVal":"haha","tx":{"strVal":"aa","t":{"innerStrVal":"bebe","x":67},"tn":null}}""")
  }

  "Test " should " should be able to serialize 1" in {

    val jf = new JsonFactory()
    val ba = new ByteArrayOutputStream()
    val jg = jf.createGenerator(ba, JsonEncoding.UTF8)
    val slz = new JsonSerializer[PlainConverter](jg)

    jg.writeStartObject()
    slz.bind(Test4(Array(1,2,3)))
    jg.writeEndObject()
    jg.close()
    ba.close()
    val s = ba.toString("UTF-8")

    //jf.create
    //val jg = new JsonFactory.

    assert(s == """{"a":[1,2,3]}""")
  }

  "Test " should " should be able to deserialize" in {

    val jf = new JsonFactory()
    val jp = jf.createParser("""{"innerStrVal":"bebe","x":67}""")

    val ds = new JsonDeserializer[PlainConverter](jp)

    val t = ds.unbind[Test1]
    //jf.create
    //val jg = new JsonFactory.

    assert(t == Test1("bebe", 67))
  }

  "Test " should " should be able to deserialize 1" in {

    val jf = new JsonFactory()
    val jp = jf.createParser("""123""")

    val ds = new JsonDeserializer[PlainConverter](jp)

    val t = ds.unbind[String]
    //jf.create
    //val jg = new JsonFactory.

    assert(t === "123")
  }

  "Test " should " should be able to deserialize 2" in {

    val jf = new JsonFactory()
    val jp = jf.createParser("""{"strVal":"haha","tx":{"strVal":"aa","t":{"innerStrVal":"bebe","x":67},"tn":null}}""")

    val ds = new JsonDeserializer[PlainConverter](jp)

    val t = ds.unbind[Test3]
    //jf.create
    //val jg = new JsonFactory.

    assert(t == Test3("haha", Test2("aa", Test1("bebe", 67), None)))
  }

}
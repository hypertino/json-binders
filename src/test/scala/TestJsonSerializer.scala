
import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory}
import eu.inn.binders.json.JsonSerializer
import eu.inn.binders.naming.PlainConverter
import org.scalatest.{FlatSpec, Matchers}
import eu.inn.binders._

case class Test1(innerStrVal: String, x: Int)
case class Test2(strVal: String, t: Test1)
case class Test3(strVal: String, tx: Test2)
case class Test4(a: Seq[Int])

class TestJsonSerializer extends FlatSpec with Matchers {

  "Test " should " should be able to serialize" in {

    val jf = new JsonFactory()

    val ba = new ByteArrayOutputStream()
    val jg = jf.createGenerator(ba, JsonEncoding.UTF8)

    val slz = new JsonSerializer[PlainConverter](jg)

    jg.writeStartObject()
    slz.bind(Test3("haha", Test2("aa", Test1("bebe", 67))))
    jg.writeEndObject()

    jg.close()
    ba.close()
    val s = ba.toString("UTF-8")

    //jf.create
    //val jg = new JsonFactory.

    assert(s == """{"strVal":"haha","tx":{"strVal":"aa","t":{"innerStrVal":"bebe","x":67}}}""")
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
}
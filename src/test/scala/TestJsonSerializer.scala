
import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory}
import eu.inn.binders.json.JsonSerializer
import eu.inn.binders.naming.PlainConverter
import org.scalatest.{FlatSpec, Matchers}
import eu.inn.binders._

case class Test1(innerStrVal: String)
case class Test2(strVal: String, t: Test1)
case class Test3(strVal: String, tx: Test2)

class TestJsonSerializer extends FlatSpec with Matchers {

  "Test " should " should be able to serialize" in {

    val jf = new JsonFactory()

    val ba = new ByteArrayOutputStream()
    val jg = jf.createGenerator(ba, JsonEncoding.UTF8)

    val slz = new JsonSerializer[PlainConverter](jg)

    jg.writeStartObject()
    slz.bind(Test3("haha", Test2("aa", Test1("bebe"))))
    jg.writeEndObject()

    jg.close()
    ba.close()
    val s = ba.toString("UTF-8")

    //jf.create
    //val jg = new JsonFactory.

    assert(s == """{"strVal":"haha","tx":{"strVal":"aa","t":{"innerStrVal":"bebe"}}}""")
  }
}
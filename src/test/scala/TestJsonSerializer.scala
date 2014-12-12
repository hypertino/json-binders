
import java.io.ByteArrayOutputStream

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory}
import eu.inn.binders.json.{JsonDeserializer, JsonSerializer}
import eu.inn.binders.naming.{CamelCaseToSnakeCaseConverter, PlainConverter}
import org.scalatest.{FlatSpec, Matchers}
import eu.inn.binders._
import sun.org.mozilla.javascript.internal.json.JsonParser

/*
case class TestInt(intVal: Int)
case class TestIntN(intValN1: Option[Int], intValN2: Option[Int])
case class TestIntArray(intArray: Seq[Int])
case class TestIntArrayN(intArrayN: Seq[Option[Int]])
case class TestStr(strVal: String)
case class TestDouble(doubleVal: Double)
case class TestFloat(floatVal: Float)

class TestJsonSerializer extends FlatSpec with Matchers {


  /*"Test " should " should be able to deserialize" in {

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
*/
}

*/

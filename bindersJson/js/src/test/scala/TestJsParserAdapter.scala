import com.hypertino.binders.json.JsParserAdapter
import com.hypertino.binders.json.api._
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable
import scala.scalajs.js.JSON

case class J(
              currentToken: JsToken,
              fieldName: Option[String] = None,
              stringValue: Option[String] = None,
              numberValue: Option[BigDecimal] = None
            )

class TestJsParserAdapter extends FlatSpec with Matchers {
  def iterate(parser: JsParserAdapter): List[J] = {
    val builder = mutable.ArrayBuffer[J]()
    while(parser.nextToken() != JsUnknown) {
      val j = J(parser.currentToken, parser.fieldName, Option(parser.stringValue), Option(parser.numberValue))
      //println(j)
      builder += j
    }
    builder.toList
  }

  it should "parse null" in {
    val adapter = new JsParserAdapter(JSON.parse("null"))
    iterate(adapter) shouldBe List(J(JsNull))
  }

  it should "parse number" in {
    val adapter = new JsParserAdapter(JSON.parse("1"))
    iterate(adapter) shouldBe List(J(JsNumber, numberValue=Some(1)))
  }

  it should "parse boolean" in {
    val adapter = new JsParserAdapter(JSON.parse("true"))
    iterate(adapter) shouldBe List(J(JsTrue))
  }

  it should "parse object" in {
    val adapter = new JsParserAdapter(JSON.parse("""{"a":1,"b":false}"""))
    iterate(adapter) shouldBe List(
      J(JsStartObject),
        J(JsFieldName, fieldName=Some("a")), J(JsNumber, numberValue=Some(1)),
        J(JsFieldName, fieldName=Some("b")), J(JsFalse),
      J(JsEndObject)
    )
  }

  it should "parse array" in {
    val adapter = new JsParserAdapter(JSON.parse("""[1,true]"""))
    iterate(adapter) shouldBe List(
      J(JsStartArray),
      J(JsNumber, numberValue=Some(1)),
      J(JsTrue),
      J(JsEndArray)
    )
  }

  it should "parse inner object" in {
    val adapter = new JsParserAdapter(JSON.parse("""{"a":1,"b":{"x":true},"c":false}"""))
    iterate(adapter) shouldBe List(
      J(JsStartObject),
      J(JsFieldName, fieldName=Some("a")), J(JsNumber, numberValue=Some(1)),
      J(JsFieldName, fieldName=Some("b")),
        J(JsStartObject),
        J(JsFieldName, fieldName=Some("x")), J(JsTrue),
        J(JsEndObject),
      J(JsFieldName, fieldName=Some("c")), J(JsFalse),
      J(JsEndObject)
    )
  }

  it should "parse inner array + object" in {
    val adapter = new JsParserAdapter(JSON.parse("""{"a":1,"b":[false,{"x":true},3.4],"c":false}"""))
    iterate(adapter) shouldBe List(
      J(JsStartObject),
      J(JsFieldName, fieldName=Some("a")), J(JsNumber, numberValue=Some(1)),
      J(JsFieldName, fieldName=Some("b")),
      J(JsStartArray),
      J(JsFalse),
      J(JsStartObject),
      J(JsFieldName, fieldName=Some("x")), J(JsTrue),
      J(JsEndObject),
      J(JsNumber, numberValue=Some(3.4)),
      J(JsEndArray),
      J(JsFieldName, fieldName=Some("c")), J(JsFalse),
      J(JsEndObject)
    )
  }
}



import java.io.{StringReader, StringWriter}

import com.fasterxml.jackson.core.{JsonFactory, JsonParser}
import com.hypertino.binders.json.{JacksonGeneratorAdapter, JacksonParserAdapter, JsonBinders, JsonBindersFactory}
import org.scalatest.{FlatSpec, Matchers}

case class TestStream(id: Double)

class TestStreamSerializer extends FlatSpec with Matchers {
  import JsonBinders._

  it should " serialize sequentially" in {
    val writer = new StringWriter()
    10.writeJson(writer)
    "hey".writeJson(writer)
    TestStream(10.2).writeJson(writer)
    writer.toString should equal("""10"hey"{"id":10.2}""")
  }

  it should " deserialize sequentially (with JacksonParser)" in {
    val reader = new StringReader(
      """
        10
        "hey"
        {"id":10.2}
      """)

    val jacksonFactory = new JsonFactory()
    jacksonFactory.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE)

    val jp = jacksonFactory.createParser(reader)
    try {
      val adapter = new JacksonParserAdapter(jp)
      JsonBindersFactory.findFactory().withJsonParserApi(adapter) { jpa ⇒
        jpa.unbind[Int] should equal(10)
      }
      JsonBindersFactory.findFactory().withJsonParserApi(adapter) { jpa ⇒
        jpa.unbind[String] should equal("hey")
      }
      JsonBindersFactory.findFactory().withJsonParserApi(adapter) { jpa ⇒
        jpa.unbind[TestStream] should equal(TestStream(10.2))
      }
    }
    finally {
      jp.close()
    }

    reader.close()
  }
}

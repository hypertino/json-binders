import java.io.{ByteArrayInputStream, ByteArrayOutputStream, StringReader, StringWriter}

import com.hypertino.binders.json.JsonBinders
import org.scalatest.{FlatSpec, Matchers}

class TestReaderWriterSerializer extends FlatSpec with Matchers {

  import JsonBinders._

  "Json " should " serialize to OutputStream" in {
    val wr = new StringWriter()
    val i:Int = 55667
    i.writeJson(wr)
    wr.close()
    wr.toString should equal("55667")
  }

  "Json " should " deserialize from InputStream" in {
    val str = "55667"
    val rd = new StringReader(str)
    val i = rd.readJson[Int]
    i should equal(55667)
  }
}


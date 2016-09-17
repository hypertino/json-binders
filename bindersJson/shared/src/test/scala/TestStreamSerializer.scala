
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}

import org.scalatest.{FlatSpec, Matchers}

class TestStreamSerializer extends FlatSpec with Matchers {

  import com.hypertino.binders.json._

  "Json " should " serialize to OutputStream" in {
    val ba = new ByteArrayOutputStream()
    val i:Int = 55667
    i.writeJson(ba)
    ba.close()
    ba.toString("UTF8") should equal("55667")
  }

  "Json " should " deserialize from InputStream" in {
    val str = "55667"
    val is = new ByteArrayInputStream(str.getBytes("UTF8"))
    val i = is.readJson[Int]
    i should equal(55667)
  }
}

import com.hypertino.binders.json._
import org.scalatest.{FlatSpec, Matchers}
import java.time.Instant

class TestInstant13Serializer extends FlatSpec with Matchers {
  import JsonBinders._
  import JsonTimeBinders._

  it should " serialize instant" in {
    val instantJson = Instant.parse("2016-10-01T00:12:42.007Z").toJson
    instantJson shouldBe "1475280762007"
  }

  it should " deserialize instant" in {
    val instant = "1475280762007".parseJson[Instant]
    instant shouldBe Instant.parse("2016-10-01T00:12:42.007Z")
  }
}

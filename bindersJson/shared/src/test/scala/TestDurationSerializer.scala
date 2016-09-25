import org.scalatest.{FlatSpec, Matchers}

import scala.concurrent.duration._

class TestDurationSerializer extends FlatSpec with Matchers {
  import com.hypertino.binders.json._

  it should " serialize FiniteDuration as number" in {
    val d = 10.seconds
    val str = d.toJson
    str shouldBe "10000"
  }

  it should " serialize Option[FiniteDuration] as number" in {
    val d = Some(10.seconds)
    val str = d.toJson
    str shouldBe "10000"

    val d2: Option[FiniteDuration] = None
    val str2 = d2.toJson
    str2 shouldBe "null"
  }

  it should " deserialize FiniteDuration from a number" in {
    val o = "10000".parseJson[FiniteDuration]
    o shouldBe 10.seconds
  }

  it should " deserialize Option[FiniteDuration] from a number" in {
    val o = "10000".parseJson[Option[FiniteDuration]]
    o shouldBe Some(10.seconds)

    val o2 = "null".parseJson[Option[FiniteDuration]]
    o2 shouldBe None
  }

  it should " serialize Duration as string" in {
    val d: Duration = 10.seconds
    val str = d.toJson
    str shouldBe "\"10 seconds\""

    val d2: Duration = Duration.Inf
    val str2 = d2.toJson
    str2 shouldBe "\"Inf\""
  }

  it should " deserialize Duration from a string" in {
    val o = "\"10s\"".parseJson[Duration]
    o shouldBe 10.seconds

    val o2 = "\"Inf\"".parseJson[Duration]
    o2 shouldBe Duration.Inf
  }
}

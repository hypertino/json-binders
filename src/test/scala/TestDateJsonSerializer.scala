
import java.util.{TimeZone, Date}

import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}

case class TestDate(dateVal: Date)
case class TestDateN(dateValN1: Option[Date], dateValN2: Option[Date])
case class TestDateArray(dateArray: Seq[Date])
case class TestDateArrayN(dateArrayN: Seq[Option[Date]])

class TestDateJsonSerializer extends FlatSpec with Matchers with BeforeAndAfter {

  import eu.inn.binders.json._

  before {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
  }

  "Json " should " serialize class with Date" in {
    val t = TestDate(new Date(1418402444000l))
    val str = t.toJson
    assert (str === """{"dateVal":"2014-12-12T16:40:44+00:00"}""")
  }

  "Json " should " serialize class with array of Date" in {
    val t = TestDateArray(List(new Date(1418402444000l),new Date(1418402445000l)))
    val str = t.toJson
    assert (str === """{"dateArray":["2014-12-12T16:40:44+00:00","2014-12-12T16:40:45+00:00"]}""")
  }

  "Json " should " serialize class with array of Option[Date]" in {
    val t = TestDateArrayN(List(Some(new Date(1418402444000l)),None,Some(new Date(1418402445000l))))
    val str = t.toJson
    assert (str === """{"dateArrayN":["2014-12-12T16:40:44+00:00",null,"2014-12-12T16:40:45+00:00"]}""")
  }

  "Json " should " serialize class with Nullable Date" in {
    val t = TestDateN(Some(new Date(1418402444000l)),Some(new Date(1418402445000l)))
    val str = t.toJson
    assert (str === """{"dateValN1":"2014-12-12T16:40:44+00:00","dateValN2":"2014-12-12T16:40:45+00:00"}""")

    val t2 = TestDateN(Some(new Date(1418402444000l)),None)
    val str2 = t2.toJson
    assert (str2 === """{"dateValN1":"2014-12-12T16:40:44+00:00"}""")
  }
}
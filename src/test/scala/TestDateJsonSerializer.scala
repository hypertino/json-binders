
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
    assert (str === """{"dateVal":1418402444000}""")
  }

  "Json " should " deserialize class with Date" in {
    val o = """{"dateVal":1418402444000}""".parseJson[TestDate]
    val t = TestDate(new Date(1418402444000l))
    assert (o === t)
  }

  "Json " should " serialize class with array of Date" in {
    val t = TestDateArray(List(new Date(1418402444000l),new Date(1418402445000l)))
    val str = t.toJson
    assert (str === """{"dateArray":[1418402444000,1418402445000]}""")
  }

  "Json " should " deserialize class with array of Date" in {
    val o = """{"dateArray":[1418402444000,1418402445000]}""".parseJson[TestDateArray]
    val t = TestDateArray(List(new Date(1418402444000l),new Date(1418402445000l)))
    assert (o === t)
  }

  "Json " should " serialize class with array of Option[Date]" in {
    val t = TestDateArrayN(List(Some(new Date(1418402444000l)),None,Some(new Date(1418402445000l))))
    val str = t.toJson
    assert (str === """{"dateArrayN":[1418402444000,null,1418402445000]}""")
  }

  "Json " should " deserialize class with array of Option[Date]" in {
    val o = """{"dateArrayN":[1418402444000,null,1418402445000]}""".parseJson[TestDateArrayN]
    val t = TestDateArrayN(List(Some(new Date(1418402444000l)),None,Some(new Date(1418402445000l))))
    assert (o === t)
  }

  "Json " should " serialize class with Nullable Date" in {
    val t = TestDateN(Some(new Date(1418402444000l)),Some(new Date(1418402445000l)))
    val str = t.toJson
    assert (str === """{"dateValN1":1418402444000,"dateValN2":1418402445000}""")

    val t2 = TestDateN(Some(new Date(1418402444000l)),None)
    val str2 = t2.toJson
    assert (str2 === """{"dateValN1":1418402444000,"dateValN2":null}""")
  }
}
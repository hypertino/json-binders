
import com.hypertino.binders.json.{DefaultJsonBindersFactory, JsonBinders}
import com.hypertino.inflector.naming.CamelCaseToSnakeCaseConverter
import org.scalatest.{FlatSpec, Matchers}


class TestConvertJsonSerializer extends FlatSpec with Matchers {

  import JsonBinders._

   "Json " should " serialize class with Int with Converter" in {
     implicit val factory = new DefaultJsonBindersFactory[CamelCaseToSnakeCaseConverter.type]

     val t = TestInt(1234)
     val str = t.toJson
     assert (str === """{"int_val":1234}""")
   }
}
